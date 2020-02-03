package bot.utils;

import bot.Constants;
import bot.DatabaseDriver;
import bot.Mixcord;
import bot.structure.Notification;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;

import java.util.Objects;

// TODO: UPDATE DOCS
@Slf4j
public class NotifSender {

    static DatabaseDriver database = Mixcord.getDatabase();

    /**
     * Sends the specified message to the specified address in an embed when a streamer comes online.
     * If the channel or guild does not exist, the notification is deleted.
     *
     * @param notif JSON object which contains data for the notification from the database
     * @param queryJson      JSON object which contains data for the streamer from Mixer
     */
    public static void sendEmbed(Notification notif, JSONObject queryJson) {
        String queryChId = String.valueOf(queryJson.getInt("id"));
        String embLiveThumbnail = Constants.MIXER_THUMB_PRE + queryChId + Constants.MIXER_THUMB_POST;

        Guild guild = Mixcord.getJda().getGuildById(notif.getServerId());
        TextChannel textChannel = Mixcord.getJda().getTextChannelById(notif.getChannelId());

        if (Mixcord.getJda().getGuilds().contains(guild)) {
            if (Objects.requireNonNull(Mixcord.getJda().getGuildById(notif.getServerId()))
                    .getTextChannels().contains(textChannel)) {
                Objects.requireNonNull(textChannel).sendMessage(notif.getMessage()).queue();
                textChannel.sendMessage(
                        new EmbedSender(notif, queryJson)
                                .setCustomAuthor()
                                .setCustomTitle()
                                .setCustomDescription()
                                .setImage(embLiveThumbnail)
                                .build()).queue();
                log.info("Sent notification to G:{} C:{}", notif.getServerId(), notif.getChannelId());
            } else {
                log.info("Channel does not exits. G:{} C:{}", notif.getServerId(), notif.getChannelId());
                database.deleteNotif(notif.getId());
                log.info("Deleted the notification in G:{} C:{} for {} ({})",
                        notif.getServerId(), notif.getChannelId(), notif.getStreamerName(), notif.getStreamerId());
                if (!database.selectStreamerNotifs(notif.getStreamerId()).hasNext()) {
                    database.deleteStreamer(notif.getStreamerName(), notif.getStreamerId());
                }
            }
        } else {
            log.info("Guild is not available. G:{}", notif.getServerId());
        }
    }

    /**
     * Sends the specified message to the specified address as a regular message when a streamer comes online.
     * If the channel or guild does not exist, the notification is deleted.
     *
     * @param notif JSON object which contains data for the notification from the database
     */
    public static void sendNonEmbed(Notification notif) {
        Guild guild = Mixcord.getJda().getGuildById(notif.getServerId());

        TextChannel textChannel = Mixcord.getJda().getTextChannelById(notif.getChannelId());

        if (Mixcord.getJda().getGuilds().contains(guild)) {
            if (Objects.requireNonNull(Mixcord.getJda().getGuildById(notif.getServerId()))
                    .getTextChannels().contains(textChannel)) {
                Objects.requireNonNull(textChannel).sendMessage(notif.getMessage()).queue();
                log.info("Sent notification to G:{} C:{}", notif.getServerId(), notif.getChannelId());
            } else {
                log.info("Channel does not exits. G:{} C:{}", notif.getServerId(), notif.getChannelId());
                database.deleteNotif(notif.getId());
                log.info("Deleted the notification in G:{} C:{} for {} ({})",
                        notif.getServerId(), notif.getChannelId(), notif.getStreamerName(), notif.getStreamerId());
                if (!database.selectStreamerNotifs(notif.getStreamerId()).hasNext()) {
                    database.deleteStreamer(notif.getStreamerName(), notif.getStreamerId());
                }
            }
        } else {
            log.info("Guild is not available. G:{}", notif.getServerId());
        }
    }

    /**
     * Sends the specified offline message to the specified address.
     * If the channel or guild does not exist, the notification is deleted.
     *
     * @param notif JSON object which contains data for the notification from the database
     */
    public static void sendOfflineMsg(Notification notif) {
        Guild guild = Mixcord.getJda().getGuildById(notif.getServerId());
        TextChannel textChannel = Mixcord.getJda().getTextChannelById(notif.getChannelId());

        if (Mixcord.getJda().getGuilds().contains(guild)) {
            if (Objects.requireNonNull(Mixcord.getJda().getGuildById(notif.getServerId()))
                    .getTextChannels().contains(textChannel)) {
                Objects.requireNonNull(textChannel).sendMessage(notif.getStreamEndMessage()).queue();
                log.info("Sent event end message to G:{} C:{}", notif.getServerId(), notif.getChannelId());
            } else {
                log.info("Channel does not exits. G:{} C:{}", notif.getServerId(), notif.getChannelId());
                database.deleteNotif(notif.getId());
                log.info("Deleted the notification in G:{} C:{} for {} ({})",
                        notif.getServerId(), notif.getChannelId(), notif.getStreamerName(), notif.getStreamerId());
            }
        } else {
            log.info("Guild does not exits. G:{}", notif.getServerId());
        }
    }
}
