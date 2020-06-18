package bot.utils;

import bot.services.ShardService;
import bot.structures.Credentials;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.TextChannel;


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

    private static MetricsUtil instance;

    private long startTime;
    private long endTime;
    private int notifsSent;
    private int streamersProcessed;

    private MetricsUtil() {
        this.startTime = 0;
        this.endTime = 0;
        this.notifsSent = 0;
        this.streamersProcessed = 0;
    }

    public static MetricsUtil getInstance() {
        if (instance == null)
            instance = new MetricsUtil();

        return instance;
    }

    /**
     * Resets all values for {@link MetricsUtil}
     */
    public void reset() {
        this.startTime = 0;
        this.endTime = 0;
        this.notifsSent = 0;
        this.streamersProcessed = 0;
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
     * Increments the number of sent notifications
     */
    public void incrementNotifsSent() {
        this.notifsSent++;
    }

    /**
     * Increments the number of processed streamers.
     */
    public void incrementStreamersProcessed() {
        this.streamersProcessed++;
    }

    /**
     * @return the elapsed time from start to finish in milliseconds
     */
    public long getMilliSecs() {
        return (this.endTime - this.startTime) / 1000000;
    }

    /**
     * @return the elapsed time from start to finish in seconds with double precision
     */
    public double getSecs() {
        return getMilliSecs() / 1000.0;
    }

    /**
     * @return the number of processed streamers
     */
    public int getStreamersProcessed() {
        return streamersProcessed;
    }

    /**
     * Posts the metrics gathered by {@link MetricsUtil} to the specified Discord guild and channel.
     * The specified guild should contain the specified channel.
     *
     * @param channelId Discord channel ID
     */
    public void postMetrics(final String channelId) {
        log.info("Posting metrics to {} - {}",
                Credentials.getInstance().getMetricsGuild(), Credentials.getInstance().getMetricsChannel());
        log.info("Checked {} streamers in {}s", getStreamersProcessed(), getSecs());

        final TextChannel channel = ShardService.getInstance().getTextChannelById(channelId);

        final String line = "· Streamers Processed: %d\n· Notifications Sent: %d\n· Time: %.2f sec";
        final String description = String.format(line, streamersProcessed, notifsSent, getSecs());

        if (channel == null) {
            log.info("Could not post metrics to {}", channelId);
            return;
        }

        channel.sendMessage(new MixerEmbedBuilder()
                .setTitle("Metrics")
                .setDescription(description)
                .build()).queue();
    }
}
