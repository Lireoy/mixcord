package bot.utils;

import bot.Constants;
import bot.DatabaseDriver;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

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

        while (running.get()) {
            Cursor streamers = getDatabaseDriver().selectAllStreamers();
            for (Object streamer : streamers) {
                // Checking one streamer at a time
                JSONObject dbStreamer = new JSONObject(streamer.toString());

                // Data from database
                String dbDocumentId = dbStreamer.getString("id");
                String dbStreamerName = dbStreamer.getString("streamerName");
                String dbStreamerId = dbStreamer.getString("streamerId");
                boolean dbStreamingStatus = dbStreamer.getBoolean("isStreaming");

                // Mixer query
                JSONObject queryJson = MixerQuery.queryChannel(dbStreamerName);
                assert queryJson != null;
                boolean queryIsOnline = queryJson.getBoolean("online");

                if (queryIsOnline && !dbStreamingStatus) {
                    // Was offline, is now online

                    log.info("{} ({}) is streaming. Processing...", dbStreamerName, dbStreamerId);
                    getDatabaseDriver().updateIsStreaming(dbDocumentId, true);
                    log.info("Updated streaming to TRUE for {} ({})", dbStreamerName, dbStreamerId);
                    log.info("Queueing notifications...");

                    // Select all notifications for this streamer from database
                    Cursor notifications = getDatabaseDriver().selectStreamerNotifs(dbStreamerId);
                    for (Object notification : notifications) {
                        JSONObject dbNotification = new JSONObject(notification.toString());

                        boolean dbEmbed = dbNotification.getBoolean("embed");
                        if (dbEmbed) {
                            NotifSender.sendEmbed(dbNotification, queryJson);
                        } else {
                            NotifSender.sendNonEmbed(dbNotification);
                        }
                        metrics.incrementNotifsSent();
                    }
                    notifications.close();
                }

                if (!queryIsOnline && dbStreamingStatus) {
                    // Was online, is now offline

                    log.info("{} ({}) is not streaming. Processing...", dbStreamerName, dbStreamerId);
                    getDatabaseDriver().updateIsStreaming(dbDocumentId, false);
                    log.info("Updated streaming to FALSE for {} ({})", dbStreamerName, dbStreamerId);
                    log.info("Queueing event end message...");

                    // Select all notifications for this streamer from database
                    Cursor notifications = getDatabaseDriver().selectStreamerNotifs(dbStreamerId);
                    for (Object notification : notifications) {
                        JSONObject dbNotification = new JSONObject(notification.toString());
                        NotifSender.sendOfflineMsg(dbNotification);
                        metrics.incrementNotifsSent();
                    }
                    notifications.close();
                }
                metrics.incrementNotifsProcessed();
                streamers.close();
            }

            metrics.incrementCycle();

            if (metrics.getCycle() == 10) {
                metrics.stopTimer();
                metrics.postMetrics(Constants.METRICS_GUILD, Constants.METRICS_CHANNEL);
                log.info("Posting metrics to {} - {}", Constants.METRICS_GUILD, Constants.METRICS_CHANNEL);
                log.info("Looped {} notifications in {}s", metrics.getNotifsProcessed(), metrics.getSecs());
                metrics.initReset();
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

    private DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }
}
