package bot.utils;

import bot.Constants;
import bot.DatabaseDriver;
import bot.Mixcord;
import bot.structure.Notification;
import bot.structure.Streamer;
import com.google.gson.Gson;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class NotifService implements Runnable {

    private DatabaseDriver databaseDriver;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public NotifService(DatabaseDriver driver) {
        this.databaseDriver = driver;
    }

    public void run() {
        log.info("Notifier service was started.");
        running.set(true);
        MetricsUtil metrics = new MetricsUtil();
        try {
            while (running.get()) {
                Gson gson = new Gson();
                Cursor streamers = getDatabaseDriver().selectAllStreamers();
                for (Object streamerObj : streamers) {
                    // Checking one streamer at a time
                    Streamer streamer = gson.fromJson(new JSONObject(streamerObj.toString()).toString(), Streamer.class);

                    // Mixer query
                    JSONObject queryJson = MixerQuery.queryChannel(streamer.getStreamerName());
                    assert queryJson != null;
                    boolean queryIsOnline = queryJson.getBoolean("online");

                    if (queryIsOnline && !streamer.isStreaming()) {
                        // Was offline, is now online

                        log.info("{} ({}) is streaming. Processing...", streamer.getStreamerName(), streamer.getStreamerId());
                        getDatabaseDriver().updateIsStreaming(streamer.getId(), true);
                        log.info("Updated streaming to TRUE for {} ({})", streamer.getStreamerName(), streamer.getStreamerId());
                        log.info("Queueing notifications...");

                        // Select all notifications for this streamer from database
                        Cursor notifications = getDatabaseDriver().selectStreamerNotifs(streamer.getStreamerId());
                        for (Object notificationObj : notifications) {

                            Notification notif = gson.fromJson(
                                    new JSONObject(notificationObj.toString()).toString(), Notification.class);

                            boolean dbEmbed = notif.isEmbed();
                            if (dbEmbed) {
                                NotifSender.sendEmbed(notif, queryJson);
                            } else {
                                NotifSender.sendNonEmbed(notif);
                            }
                            metrics.incrementNotifsSent();
                        }
                        notifications.close();
                    }

                    if (!queryIsOnline && streamer.isStreaming()) {
                        // Was online, is now offline

                        log.info("{} ({}) is not streaming. Processing...", streamer.getStreamerName(), streamer.getStreamerId());
                        getDatabaseDriver().updateIsStreaming(streamer.getId(), false);
                        log.info("Updated streaming to FALSE for {} ({})", streamer.getStreamerName(), streamer.getStreamerId());
                        log.info("Queueing event end message...");

                        // Select all notifications for this streamer from database
                        Cursor notifications = getDatabaseDriver().selectStreamerNotifs(streamer.getStreamerId());
                        for (Object notificationObj : notifications) {
                            Notification notif = gson.fromJson(
                                    new JSONObject(notificationObj.toString()).toString(), Notification.class);
                            NotifSender.sendOfflineMsg(notif);
                            metrics.incrementNotifsSent();
                        }
                        notifications.close();
                    }
                    metrics.incrementStreamersProcessed();
                    streamers.close();
                }

                metrics.stopTimer();
                metrics.postMetrics(Constants.METRICS_CHANNEL);
                log.info("Posting metrics to {} - {}", Constants.METRICS_GUILD, Constants.METRICS_CHANNEL);
                log.info("Checked {} streamers in {}s", metrics.getStreamersProcessed(), metrics.getSecs());

                if (metrics.getSecs() <= 40) {
                    log.info("Sleeping the notifier service...");
                    TimeUnit.SECONDS.sleep(60);
                }

                metrics.initReset();
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("Okay so we caught some ugly exception, we should carry on, no stopping with the notifs.");
            Mixcord.getNotifierService().restart();
            // lmao
        }
    }

    public void start() {
        Thread worker = new Thread(this);
        worker.start();
        log.info("Starting notifier service...");
    }

    public void stop() {
        running.set(false);
        log.info("Stopping notifier service...");
    }

    public void restart() {
        log.info("Restarting notifier service...");
        stop();
        start();
    }

    public boolean getState() {
        return running.get();
    }

    private DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }
}
