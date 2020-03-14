package bot.services;

import bot.constants.BotConstants;
import bot.constants.DevConstants;
import bot.database.DatabaseDriver;
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
                final Cursor streamers = DatabaseDriver.getInstance().selectAllStreamers();

                for (Object streamerObj : streamers) {
                    if (!isRunning) break;
                    Streamer streamer = new Gson().fromJson(streamerObj.toString(), Streamer.class);
                    final JSONObject queryJson = MixerQuery.queryChannel(streamer.getStreamerName());

                    if (queryJson == null) {
                        log.info("queryJson was null.");
                        return;
                    }

                    if (queryJson.isEmpty()) {
                        log.info("Streamer not found.");
                        return;
                    }

                    final boolean queryIsOnline = queryJson.getBoolean("online");

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
                    MetricsUtil.getInstance().incrementStreamersProcessed();
                    streamers.close();
                }

                MetricsUtil.getInstance().stopTimer();
                MetricsUtil.getInstance().postMetrics(DevConstants.METRICS_CHANNEL);
                log.info("Posting metrics to {} - {}",
                        DevConstants.METRICS_GUILD, DevConstants.METRICS_CHANNEL);
                log.info("Checked {} streamers in {}s",
                        MetricsUtil.getInstance().getStreamersProcessed(),
                        MetricsUtil.getInstance().getSecs());

                if (MetricsUtil.getInstance().getSecs() <= 40) {
                    log.info("Sleeping the notifier service...");
                    long waiterStart = System.currentTimeMillis();
                    long desiredTime = waiterStart + 60000;

                    boolean toCheck = true;
                    while (toCheck) {
                        if (desiredTime == System.currentTimeMillis() || !isRunning) {
                            toCheck = false;
                        }
                    }
                }

                MetricsUtil.getInstance().reset();
            }
        } catch (Exception ex) {
            if (ex instanceof ReqlOpFailedError) {
                this.isRunning = false;
                log.info("ReqlOpFailedError");
                ex.printStackTrace();

                String message = BotConstants.WARNING + BotConstants.WARNING +
                        "There is a database issue. Stopping the notifier service." +
                        BotConstants.WARNING + BotConstants.WARNING;

                sendReportInDm(DevConstants.OWNER_ID, message);
            } else if (ex instanceof NullPointerException) {
                log.info("NullPointer in the loop, sleeping then continue.");
                NotifierThread.getInstance().start();
            } else {
                log.info("General exception");
                log.info("Message: {}", ex.getMessage());
                NotifierThread.getInstance().start();
            }
        }
    }

    public void terminate() {
        this.isRunning = false;
        log.info("Terminating...");
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void sendReportInDm(String userId, String message) {
        User owner = ShardService.getInstance().getUserById(userId);
        assert owner != null;
        owner.openPrivateChannel().queue(
                channel -> channel.sendMessage(message).queue());
    }
}
