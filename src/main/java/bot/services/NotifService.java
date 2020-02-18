package bot.services;

import bot.Constants;
import bot.factories.DatabaseFactory;
import bot.structure.Notification;
import bot.structure.Streamer;
import bot.utils.MetricsUtil;
import bot.utils.MixerQuery;
import bot.utils.NotifSender;
import com.google.gson.Gson;
import com.rethinkdb.gen.exc.ReqlOpFailedError;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class NotifService implements Runnable {

    private static volatile AtomicBoolean running = new AtomicBoolean(false);

    public NotifService() {
    }

    public void run() {
        log.info("Notifier service was started.");
        running.set(true);
        MetricsUtil metrics = new MetricsUtil();
        try {
            while (running.get()) {
                Cursor streamers = DatabaseFactory.getDatabase().selectAllStreamers();
                for (Object streamerObj : streamers) {
                    // Checking one streamer at a time
                    Streamer streamer = new Gson().fromJson(streamerObj.toString(), Streamer.class);

                    // Mixer query
                    JSONObject queryJson = MixerQuery.queryChannel(streamer.getStreamerName());
                    assert queryJson != null;
                    boolean queryIsOnline = queryJson.getBoolean("online");

                    if (queryIsOnline && !streamer.isStreaming()) {
                        // Was offline, is now online

                        log.info("{} ({}) is streaming. Processing...", streamer.getStreamerName(), streamer.getStreamerId());
                        DatabaseFactory.getDatabase().updateIsStreaming(streamer.getId(), true);
                        log.info("Updated streaming to TRUE for {} ({})", streamer.getStreamerName(), streamer.getStreamerId());
                        log.info("Queueing notifications...");

                        // Select all notifications for this streamer from database
                        Cursor notifications = DatabaseFactory.getDatabase().selectStreamerNotifs(streamer.getStreamerId());
                        for (Object notificationObj : notifications) {

                            Notification notif = new Gson().fromJson(notificationObj.toString(), Notification.class);

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
                        DatabaseFactory.getDatabase().updateIsStreaming(streamer.getId(), false);
                        log.info("Updated streaming to FALSE for {} ({})", streamer.getStreamerName(), streamer.getStreamerId());
                        log.info("Queueing event end message...");

                        // Select all notifications for this streamer from database
                        Cursor notifications = DatabaseFactory.getDatabase().selectStreamerNotifs(streamer.getStreamerId());
                        for (Object notificationObj : notifications) {
                            Notification notif = new Gson().fromJson(notificationObj.toString(), Notification.class);
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
        } catch (Exception ex) {
            if (ex instanceof InterruptedException) {
                this.stop();
                log.info("Interrupted Exception: {}", ex.getMessage());

                String message = Constants.WARNING + Constants.WARNING + Constants.WARNING +
                        "Interrupted exception.\nTerminating the notifier service." +
                        Constants.WARNING + Constants.WARNING + Constants.WARNING;

                sendReportInDm(Constants.OWNER_ID, message);
            } else if (ex instanceof ReqlOpFailedError) {
                this.stop();
                log.info("Database error: {}", ex.getMessage());

                String message = Constants.WARNING + Constants.WARNING + Constants.WARNING +
                        "There is a database issue.\nTerminating the notifier service." +
                        Constants.WARNING + Constants.WARNING + Constants.WARNING;

                sendReportInDm(Constants.OWNER_ID, message);
            } else {
                log.info("General exception: {}", ex.getMessage());
            }
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

    public boolean getState() {
        return running.get();
    }

    private void sendReportInDm(String userId, String message) {
        User owner = ShardService.manager().getUserById(userId);
        assert owner != null;
        owner.openPrivateChannel().queue(
                channel -> channel.sendMessage(message).queue());
    }
}
