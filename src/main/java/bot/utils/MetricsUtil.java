package bot.utils;

import bot.Mixcord;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;

/**
 * A utility class which collects and supplies statistical information
 * about the starting and stopping time of data gathering,
 * completed processing cycles, the number of processed notifications
 * and the number of sent notifications.
 * <p>
 * For the time component this class uses nanoTime for accuracy.
 */
@Slf4j
public class MetricsUtil {

    private int cycle;
    private long startTime;
    private long endTime;
    private int notifsProcessed;
    private int notifsSent;

    MetricsUtil() {
        initReset();
    }


    /**
     * Starts the {@link MetricsUtil} timer
     */
    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    /**
     * Stops the {@link MetricsUtil} timer
     */
    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    /**
     * Resets all values for {@link MetricsUtil}
     */
    public void initReset() {
        this.cycle = 0;
        this.startTime = 0;
        this.endTime = 0;
        this.notifsProcessed = 0;
        this.notifsSent = 0;
    }

    /**
     * Increments the number of processed notifications
     */
    public void incrementNotifsProcessed() {
        this.notifsProcessed++;
    }

    /**
     * Increments the number of sent notifications
     */
    public void incrementNotifsSent() {
        this.notifsSent++;
    }

    /**
     * Increments the number of completed cycles
     */
    public void incrementCycle() {
        this.cycle++;
    }

    /**
     * @return the number of completed cycles
     */
    public int getCycle() {
        return this.cycle;
    }

    /**
     * @return the elapsed time from start to finish in milliseconds
     */
    public long getMilliSecs() {
        return (endTime - startTime) / 1000000;
    }

    /**
     * @return the elapsed time from start to finish in seconds with double precision
     */
    public double getSecs() {
        return (double) (getMilliSecs() / 1000000);
    }

    /**
     * @return the number of processed notifications
     */
    public int getNotifsProcessed() {
        return notifsProcessed;
    }


    /**
     * Posts the metrics gathered by {@link MetricsUtil} to the specified Discord guild and channel.
     * The specified guild should contain the specified channel.
     *
     * @param guildId   Discord guild ID
     * @param channelId Discord channel ID
     */
    public void postMetrics(String guildId, String channelId) {
        Guild guild = Mixcord.getJda().getGuildById(guildId);
        TextChannel channel = Mixcord.getJda().getTextChannelById(channelId);

        if (Mixcord.getJda().getGuilds().contains(guild)) {
            if (Objects.requireNonNull(Mixcord.getJda().getGuildById(guildId))
                    .getTextChannels().contains(channel)) {

                String stringBuilder = "· Cycles: " + cycle + "\n" +
                        "· Notifications Processed: " + notifsProcessed + "\n" +
                        "· NotifsSent: " + notifsSent + "\n" +
                        "· Time: " + getSecs() + "sec";
                Objects.requireNonNull(channel).sendMessage(
                        new EmbedSender()
                                .setTitle("Metrics")
                                .setDescription(stringBuilder)
                                .build()).queue();
            } else {
                log.info("Channel does not exits. G:{} C:{}", guildId, channelId);
            }
        } else {
            log.info("Guild does not exits. G:{}", guildId);
        }
    }
}
