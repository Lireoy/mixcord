package bot.services;

import bot.database.DatabaseDriver;
import bot.structures.Credentials;
import bot.structures.Notification;
import bot.structures.Streamer;
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

@Slf4j
public class NotifService implements Runnable {

    private static NotifService instance;
    private volatile boolean isRunning;

    private NotifService() {
    }

    public static NotifService getInstance() {
        if (instance == null)
            instance = new NotifService();

        return instance;
    }

    @Override
    public void run() {
        log.info("Notifier service was started.");
        this.isRunning = true;

        try {
            while (isRunning) {
                MetricsUtil.getInstance().startTimer();
                processAllStreamers();

                MetricsUtil.getInstance().stopTimer();
                MetricsUtil.getInstance().postMetrics(Credentials.getInstance().getMetricsChannel());

                if (MetricsUtil.getInstance().getSecs() <= 40) {
                    for (int i = 0; i < 9; i++) {
                        if (isRunning) {
                            TimeUnit.SECONDS.sleep(5);
                        }
                    }
                }

                MetricsUtil.getInstance().reset();
            }
        } catch (Exception ex) {
            if (ex instanceof ReqlOpFailedError) {
                handleDatabaseException(ex);
            } else {
                handleGenericException(ex);
            }
        }
    }

    private void processAllStreamers() {
        final Cursor streamers = DatabaseDriver.getInstance().selectAllStreamers();

        for (Object streamerObj : streamers) {
            if (!isRunning) break;
            final Streamer streamer = new Gson().fromJson(streamerObj.toString(), Streamer.class);
            final JSONObject queryJson = MixerQuery.queryChannel(streamer.getStreamerName());

            processStreamer(streamers, streamer, queryJson);
        }
    }

    private void cleanUpStreamer(Streamer streamer) {
        // If the JSON is empty then the streamer was deleted on Mixer or changed name
        // We delete all the correlating data to the streamer
        Cursor notifications = DatabaseDriver.getInstance().selectStreamerNotifs(streamer.getStreamerId());
        for (Object notificationObj : notifications) {
            Notification notif = new Gson().fromJson(
                    notificationObj.toString(), Notification.class);
            DatabaseDriver.getInstance().deleteNotif(notif.getId());
        }

        DatabaseDriver.getInstance().deleteStreamer(streamer.getStreamerId());
    }

    private void processStreamer(Cursor streamers, Streamer streamer, JSONObject queryJson) {
        if (queryJson == null) {
            // Check if it's null or not. Idk what this would do if it's null
            log.info("Query JSON was null.");
        } else if (queryJson.isEmpty()) {
            cleanUpStreamer(streamer);
        } else {
            // Do the status check, and notify if necessary
            final boolean queryIsOnline = queryJson.getBoolean("online");

            wasOfflineNowOnline(streamer, queryJson, queryIsOnline);
            wasOnlineNowOffline(streamer, queryIsOnline);

            MetricsUtil.getInstance().incrementStreamersProcessed();
            streamers.close();
        }
    }

    private void wasOfflineNowOnline(Streamer streamer, JSONObject queryJson, boolean queryIsOnline) {
        if (queryIsOnline && !streamer.isStreaming()) {
            // Was offline, is now online

            log.info("{} ({}) is streaming. Processing...",
                    streamer.getStreamerName(), streamer.getStreamerId());
            DatabaseDriver.getInstance().updateIsStreaming(streamer.getId(), true);

            log.info("Updated streaming to TRUE for {} ({})",
                    streamer.getStreamerName(), streamer.getStreamerId());
            log.info("Queueing notifications...");

            // Select all notifications for this streamer from database
            Cursor notifications = DatabaseDriver.getInstance()
                    .selectStreamerNotifs(streamer.getStreamerId());

            for (Object notificationObj : notifications) {
                Notification notif = new Gson().fromJson(
                        notificationObj.toString(), Notification.class);

                boolean dbEmbed = notif.isEmbed();
                if (dbEmbed) NotifSender.sendEmbed(notif, queryJson);
                else NotifSender.sendNonEmbed(notif);
                MetricsUtil.getInstance().incrementNotifsSent();
            }
            notifications.close();
        }
    }

    private void wasOnlineNowOffline(Streamer streamer, boolean queryIsOnline) {
        if (!queryIsOnline && streamer.isStreaming()) {
            // Was online, is now offline

            log.info("{} ({}) is not streaming. Processing...",
                    streamer.getStreamerName(), streamer.getStreamerId());

            DatabaseDriver.getInstance().updateIsStreaming(streamer.getId(), false);

            log.info("Updated streaming to FALSE for {} ({})",
                    streamer.getStreamerName(), streamer.getStreamerId());
            log.info("Queueing event end message...");

            // Select all notifications for this streamer from database
            Cursor notifications = DatabaseDriver.getInstance()
                    .selectStreamerNotifs(streamer.getStreamerId());

            for (Object notificationObj : notifications) {
                Notification notif = new Gson().fromJson(
                        notificationObj.toString(), Notification.class);
                NotifSender.sendOfflineMsg(notif);
                MetricsUtil.getInstance().incrementNotifsSent();
            }
            notifications.close();
        }
    }

    private void handleDatabaseException(Exception ex) {
        this.isRunning = false;
        log.info("ReqlOpFailedError");
        log.info("Message: {}", ex.getMessage());
        ex.printStackTrace();

        User owner = ShardService.getInstance().getUserById(Credentials.getInstance().getOwnerOne());
        EventEmitter.emitDatabaseExceptionEvent(owner, ex);
    }

    private void handleGenericException(Exception ex) {
        log.info("General exception");
        log.info("Message: {}", ex.getMessage());
        ex.printStackTrace();

        User owner = ShardService.getInstance().getUserById(Credentials.getInstance().getOwnerOne());
        EventEmitter.emitGenericExceptionEvent(owner, ex);

        NotifierThread.getInstance().start();
    }

    public void terminate() {
        this.isRunning = false;
        log.info("Terminating...");
    }

    public boolean isRunning() {
        return isRunning;
    }
}
