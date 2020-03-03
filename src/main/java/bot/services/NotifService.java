package bot.services;

import bot.constants.BotConstants;
import bot.constants.DeveloperConstants;
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

import java.util.concurrent.TimeUnit;

@Slf4j
public class NotifService implements Runnable {

    private static NotifService instance;
    private Thread worker;

    private NotifService() {
        log.info("NotifService constructor called.");
        worker = new Thread(this);
        WorkStatus.getInstance().markStarted();
        this.start();
    }

    public static NotifService getInstance() {
        if (instance == null)
            instance = new NotifService();

        return instance;
    }

    public void run() {
        log.info("run()");


        try {
            while (WorkStatus.getInstance().isRunning()) {
                MetricsUtil.getInstance().startTimer();
                Cursor streamers = DatabaseDriver.getInstance().selectAllStreamers();

                for (Object streamerObj : streamers) {
                    Streamer streamer = new Gson().fromJson(streamerObj.toString(), Streamer.class);
                    JSONObject queryJson = MixerQuery.queryChannel(streamer.getStreamerName());

                    assert queryJson != null;
                    boolean queryIsOnline = queryJson.getBoolean("online");

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
                MetricsUtil.getInstance().postMetrics(DeveloperConstants.METRICS_CHANNEL);
                log.info("Posting metrics to {} - {}",
                        DeveloperConstants.METRICS_GUILD, DeveloperConstants.METRICS_CHANNEL);
                log.info("Checked {} streamers in {}s",
                        MetricsUtil.getInstance().getStreamersProcessed(),
                        MetricsUtil.getInstance().getSecs());

                if (MetricsUtil.getInstance().getSecs() <= 40) {
                    log.info("Sleeping the notifier service...");
                    TimeUnit.SECONDS.sleep(60);
                }

                MetricsUtil.getInstance().reset();
            }
        } catch (Exception ex) {
            if (ex instanceof InterruptedException) {
                //TODO: create a central class to support app-wide DM report sending to owners
                this.stop();
                log.info("InterruptedException");
                ex.printStackTrace();

                String message = BotConstants.WARNING + BotConstants.WARNING +
                        "The notifier service was interrupted. Terminating the notifier service." +
                        BotConstants.WARNING + BotConstants.WARNING;

                sendReportInDm(DeveloperConstants.OWNER_ID, message);
            } else if (ex instanceof ReqlOpFailedError) {
                this.stop();
                log.info("ReqlOpFailedError");
                ex.printStackTrace();

                String message = BotConstants.WARNING + BotConstants.WARNING +
                        "There is a database issue. Stopping the notifier service." +
                        BotConstants.WARNING + BotConstants.WARNING;

                sendReportInDm(DeveloperConstants.OWNER_ID, message);
            } else {
                this.stop();
                log.info("General exception");
                ex.printStackTrace();
            }
        }
    }

    public void start() {
        String msg;

        if (worker.getState().equals(Thread.State.NEW)) {
            worker.start();
            worker.setName("NotifierService");
            msg = "Started worker...";
            log.info(msg);
        }

        if (worker.isInterrupted()) {
            WorkStatus.getInstance().markStarted();
            worker.start();
            msg = "Thread is in interrupted state. Started worker...";
            log.info(msg);
        }

        if (worker.isAlive()) {
            if (WorkStatus.getInstance().isRunning()) {
                msg = "Thread is alive and is running.";
                log.info(msg);
            } else {
                WorkStatus.getInstance().markStarted();
                msg = "Thread is alive but not running. Started it.";
                log.info(msg);
            }
        }
    }

    public void stop() {
        try {
            WorkStatus.getInstance().markFinished();
            log.info("Stopping notifier service...");
        } catch (SecurityException ex) {
            log.info("Security exception.");
            ex.printStackTrace();
        }
    }

    public String getState() {
        return worker.getState().name();
    }

    private void sendReportInDm(String userId, String message) {
        User owner = ShardService.getInstance().getUserById(userId);
        assert owner != null;
        owner.openPrivateChannel().queue(
                channel -> channel.sendMessage(message).queue());
    }
}
