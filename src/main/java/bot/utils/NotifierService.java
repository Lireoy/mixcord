package bot.utils;

import bot.Constants;
import bot.DatabaseDriver;
import bot.Mixcord;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class NotifierService implements Runnable {

    private String metricsGuildId;
    private String metricsChannelId;
    private DatabaseDriver databaseDriver;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public NotifierService(DatabaseDriver driver, String metricsGuildId, String metricsChannelId) {
        this.databaseDriver = driver;
        this.metricsGuildId = metricsGuildId;
        this.metricsChannelId = metricsChannelId;
    }

    public NotifierService(DatabaseDriver driver) {
        this.databaseDriver = driver;
        this.metricsGuildId = Constants.METRICS_GUILD;
        this.metricsChannelId = Constants.METRICS_CHANNEL;
    }

    public void run() {
        log.info("Notifier service was started.");
        running.set(true);
        MetricsUtil metrics = new MetricsUtil();

        while (running.get()) {
            if (metrics.getCycle() == 0) {
                metrics.startTimer();
            }

            Cursor cursor = getDatabaseDriver().selectAll();
            //log.info("Looping {} notifications...", cursor.bufferedItems().size());
            for (Object doc : cursor) {
                JSONObject dbNotification = new JSONObject(doc.toString());

                String dbDocumentId = dbNotification.getString("id");
                String dbStreamerName = dbNotification.getString("streamerName");
                String dbStreamerId = dbNotification.getString("streamerId");
                boolean dbStreamingStatus = dbNotification.getBoolean("isStreaming");
                String dbServerId = dbNotification.getString("serverId");
                String dbChannelId = dbNotification.getString("channelId");

                JSONObject queryJson = MixerQuery.queryChannel(dbStreamerName);
                assert queryJson != null;
                boolean queryIsOnline = queryJson.getBoolean("online");
                String queryChId = String.valueOf(queryJson.getInt("id"));

                Guild guild = Mixcord.getJda().getGuildById(dbServerId);
                TextChannel textChannel = Mixcord.getJda().getTextChannelById(dbChannelId);

                if (queryIsOnline && !dbStreamingStatus) {
                    if (Mixcord.getJda().getGuilds().contains(guild)) {
                        if (Objects.requireNonNull(Mixcord.getJda().getGuildById(dbServerId))
                                .getTextChannels().contains(textChannel)) {

                            log.info("{} ({}) is streaming. Processing...", dbStreamerName, dbChannelId);
                            getDatabaseDriver().updateIsStreaming(dbDocumentId, true);
                            log.info("Updated streaming to TRUE for {} ({})", dbStreamerName, dbStreamerId);

                            String message = dbNotification.getString("message");
                            String embLiveThumbnail = Constants.MIXER_THUMB_PRE + queryChId + Constants.MIXER_THUMB_POST;

                            log.info("Queueing notification...");
                            Objects.requireNonNull(textChannel).sendMessage(message).queue();
                            textChannel.sendMessage(
                                    new EmbedSender(queryJson, dbNotification)
                                            .setAuthor()
                                            .setTitle()
                                            .setDescription()
                                            .setImage(embLiveThumbnail)
                                            .build()).queue();
                            log.info("Sent notification to G:{} C:{}", dbServerId, dbChannelId);
                            metrics.incrementNotifsSent();
                        } else {
                            log.info("Channel does not exits. G:{} C:{}", dbServerId, dbChannelId);
                            databaseDriver.delete(dbDocumentId);
                            log.info("Deleted the notification in G:{} C:{} for {} ({})",
                                    dbServerId, dbChannelId, dbStreamerName, dbStreamerId);
                        }
                    } else {
                        log.info("Guild does not exits. G:{}", dbServerId);
                    }
                }

                if (!queryIsOnline && dbStreamingStatus) {
                    if (Mixcord.getJda().getGuilds().contains(guild)) {
                        if (Objects.requireNonNull(Mixcord.getJda().getGuildById(dbServerId))
                                .getTextChannels().contains(textChannel)) {

                            log.info("{} ({}) is not streaming. Processing...", dbStreamerName, dbChannelId);
                            getDatabaseDriver().updateIsStreaming(dbDocumentId, false);
                            log.info("Updated streaming to FALSE for {} ({})", dbStreamerName, dbStreamerId);

                            String endMsg = dbNotification.getString("streamEndMessage");
                            log.info("Queueing event end message...");

                            Objects.requireNonNull(textChannel).sendMessage(endMsg).queue();
                            log.info("Sent event end message to G:{} C:{}", dbServerId, dbChannelId);
                        } else {
                            log.info("Channel does not exits. G:{} C:{}", dbServerId, dbChannelId);
                            databaseDriver.delete(dbDocumentId);
                            log.info("Deleted the notification in G:{} C:{} for {} ({})",
                                    dbServerId, dbChannelId, dbStreamerName, dbStreamerId);
                        }
                    } else {
                        log.info("Guild does not exits. G:{}", dbServerId);
                    }
                }
                metrics.incrementNotifsProcessed();

            }
            metrics.incementCycle();

            if (metrics.getCycle() == 20) {
                metrics.stopTimer();
                metrics.postMetrics(getMetricsGuildId(), getMetricsChannelId());
                log.info("Posting metrics to {} - {}", getMetricsGuildId(), getMetricsChannelId());
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

    private String getMetricsGuildId() {
        return metricsGuildId;
    }

    private String getMetricsChannelId() {
        return metricsChannelId;
    }
}

