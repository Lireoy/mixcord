package bot.utils;

import bot.Mixcord;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;

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

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    public void initReset() {
        this.cycle = 0;
        this.startTime = 0;
        this.endTime = 0;
        this.notifsProcessed = 0;
        this.notifsSent = 0;
    }

    public void incrementNotifsProcessed() {
        this.notifsProcessed++;
    }

    public void incrementNotifsSent() {
        this.notifsSent++;
    }

    public void incementCycle() {
        this.cycle++;
    }

    public int getCycle() {
        return this.cycle;
    }

    public long getMillisecs() {
        return (endTime - startTime) / 1000000;
    }

    public double getSecs() {
        return (double) getMillisecs() / 1000;
    }

    public int getNotifsProcessed() {
        return notifsProcessed;
    }


    public void postMetrics(String guildId, String channelId) {
        if (Objects.requireNonNull(Mixcord.getJda().getGuildById(guildId)).isAvailable() &&
                Objects.requireNonNull(Mixcord.getJda().getTextChannelById(channelId)).canTalk()) {

            TextChannel channel = Mixcord.getJda().getTextChannelById(channelId);

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
            log.info("Could not post metrics to Guild {} -> Channel {}",
                    guildId, channelId);
        }
    }
}
