package bot.utils;

import bot.Constants;
import bot.DatabaseDriver;
import bot.Mixcord;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;

import java.util.Objects;

@Slf4j
public class NotifSender {

    static DatabaseDriver database = Mixcord.getDatabase();

    /**
     * Sends the specified message to the specified address in an embed when a streamer comes online.
     * If the channel or guild does not exist, the notification is deleted.
     *
     * @param dbNotification JSON object which contains data for the notification from the database
     * @param queryJson      JSON object which contains data for the streamer from Mixer
     */
    public static void sendEmbed(JSONObject dbNotification, JSONObject queryJson) {
        String documentId = dbNotification.getString("id");
        String streamerName = dbNotification.getString("streamerName");
        String streamerId = dbNotification.getString("streamerId");
        String serverId = dbNotification.getString("serverId");
        String channelId = dbNotification.getString("channelId");
        String message = dbNotification.getString("message");
        String queryChId = String.valueOf(queryJson.getInt("id"));
        String embLiveThumbnail = Constants.MIXER_THUMB_PRE + queryChId + Constants.MIXER_THUMB_POST;

        Guild guild = Mixcord.getJda().getGuildById(serverId);
        TextChannel textChannel = Mixcord.getJda().getTextChannelById(channelId);

        if (Mixcord.getJda().getGuilds().contains(guild)) {
            if (Objects.requireNonNull(Mixcord.getJda().getGuildById(serverId))
                    .getTextChannels().contains(textChannel)) {
                Objects.requireNonNull(textChannel).sendMessage(message).queue();
                textChannel.sendMessage(
                        new EmbedSender(dbNotification, queryJson)
                                .setCustomAuthor()
                                .setCustomTitle()
                                .setCustomDescription()
                                .setImage(embLiveThumbnail)
                                .build()).queue();
                log.info("Sent notification to G:{} C:{}", serverId, channelId);
            } else {
                log.info("Channel does not exits. G:{} C:{}", serverId, channelId);
                database.deleteNotif(documentId);
                log.info("Deleted the notification in G:{} C:{} for {} ({})",
                        serverId, channelId, streamerName, streamerId);
                if (!database.selectStreamerNotifs(streamerId).hasNext()) {
                    database.deleteStreamer(streamerName, streamerId);
                }
            }
        } else {
            log.info("Guild is not available. G:{}", serverId);
        }
    }

    /**
     * Sends the specified message to the specified address as a regular message when a streamer comes online.
     * If the channel or guild does not exist, the notification is deleted.
     *
     * @param dbNotification JSON object which contains data for the notification from the database
     */
    public static void sendNonEmbed(JSONObject dbNotification) {
        String documentId = dbNotification.getString("id");
        String streamerName = dbNotification.getString("streamerName");
        String streamerId = dbNotification.getString("streamerId");
        String serverId = dbNotification.getString("serverId");
        String channelId = dbNotification.getString("channelId");
        String message = dbNotification.getString("message");

        Guild guild = Mixcord.getJda().getGuildById(serverId);

        TextChannel textChannel = Mixcord.getJda().getTextChannelById(channelId);

        if (Mixcord.getJda().getGuilds().contains(guild)) {
            if (Objects.requireNonNull(Mixcord.getJda().getGuildById(serverId))
                    .getTextChannels().contains(textChannel)) {
                Objects.requireNonNull(textChannel).sendMessage(message).queue();
                log.info("Sent notification to G:{} C:{}", serverId, channelId);
            } else {
                log.info("Channel does not exits. G:{} C:{}", serverId, channelId);
                database.deleteNotif(documentId);
                log.info("Deleted the notification in G:{} C:{} for {} ({})",
                        serverId, channelId, streamerName, streamerId);
                if (!database.selectStreamerNotifs(streamerId).hasNext()) {
                    database.deleteStreamer(streamerName, streamerId);
                }
            }
        } else {
            log.info("Guild is not available. G:{}", serverId);
        }
    }

    /**
     * Sends the specified offline message to the specified address.
     * If the channel or guild does not exist, the notification is deleted.
     *
     * @param dbNotification JSON object which contains data for the notification from the database
     */
    public static void sendOfflineMsg(JSONObject dbNotification) {
        String documentId = dbNotification.getString("id");
        String streamerName = dbNotification.getString("streamerName");
        String streamerId = dbNotification.getString("streamerId");
        String serverId = dbNotification.getString("serverId");
        String channelId = dbNotification.getString("channelId");
        String endMsg = dbNotification.getString("streamEndMessage");

        Guild guild = Mixcord.getJda().getGuildById(serverId);
        TextChannel textChannel = Mixcord.getJda().getTextChannelById(channelId);

        if (Mixcord.getJda().getGuilds().contains(guild)) {
            if (Objects.requireNonNull(Mixcord.getJda().getGuildById(serverId))
                    .getTextChannels().contains(textChannel)) {
                Objects.requireNonNull(textChannel).sendMessage(endMsg).queue();
                log.info("Sent event end message to G:{} C:{}", serverId, channelId);
            } else {
                log.info("Channel does not exits. G:{} C:{}", serverId, channelId);
                database.deleteNotif(documentId);
                log.info("Deleted the notification in G:{} C:{} for {} ({})",
                        serverId, channelId, streamerName, streamerId);
            }
        } else {
            log.info("Guild does not exits. G:{}", serverId);
        }
    }
}
