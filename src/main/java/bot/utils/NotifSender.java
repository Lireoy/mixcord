package bot.utils;

import bot.Constants;
import bot.DatabaseDriver;
import bot.services.ShardService;
import bot.structure.Notification;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;

import java.util.Objects;

@Slf4j
public class NotifSender {

    /**
     * Sends the specified message to the specified address in an embed when a streamer comes online.
     * If the channel or guild does not exist, the notification is deleted.
     *
     * @param notif     {@link Notification} object which contains data for the notification from the database
     * @param queryJson {@link JSONObject} which contains data for the streamer from Mixer
     */
    public static void sendEmbed(Notification notif, JSONObject queryJson) {
        final String queryChId = String.valueOf(queryJson.getInt("id"));
        final String embLiveThumbnail = Constants.MIXER_THUMB_PRE + queryChId + Constants.MIXER_THUMB_POST;

        final Guild guild = ShardService.getInstance().getGuildById(notif.getServerId());
        final TextChannel textChannel = ShardService.getInstance().getTextChannelById(notif.getChannelId());

        if (!isGuildReachable(notif, guild)) return;
        if (!isChannelReachable(notif, textChannel)) return;

        Objects.requireNonNull(textChannel).sendMessage(notif.getMessage()).queue();
        textChannel.sendMessage(new MixerEmbedBuilder(notif, queryJson)
                .setCustomAuthor()
                .setCustomTitle()
                .setCustomDescription()
                .setImage(embLiveThumbnail)
                .build()).queue();
        log.info("Sent notification to G:{} C:{}", notif.getServerId(), notif.getChannelId());
    }

    /**
     * Sends the specified message to the specified address as a regular message when a streamer comes online.
     * If the channel or guild does not exist, the notification is deleted.
     *
     * @param notif {@link Notification} object which contains data for the notification from the database
     */
    public static void sendNonEmbed(Notification notif) {
        final Guild guild = ShardService.getInstance().getGuildById(notif.getServerId());
        final TextChannel textChannel = ShardService.getInstance().getTextChannelById(notif.getChannelId());

        if (!isGuildReachable(notif, guild)) return;
        if (!isChannelReachable(notif, textChannel)) return;

        Objects.requireNonNull(textChannel).sendMessage(notif.getMessage()).queue();
        textChannel.sendMessage(notif.getMessage()).queue();
        log.info("Sent notification to G:{} C:{}", notif.getServerId(), notif.getChannelId());
    }

    /**
     * Sends the specified offline message to the specified address.
     * If the channel or guild does not exist, the notification is deleted.
     *
     * @param notif {@link Notification} object which contains data for the notification from the database
     */
    public static void sendOfflineMsg(Notification notif) {
        final Guild guild = ShardService.getInstance().getGuildById(notif.getServerId());
        final TextChannel textChannel = ShardService.getInstance().getTextChannelById(notif.getChannelId());

        if (!isGuildReachable(notif, guild)) return;
        if (!isChannelReachable(notif, textChannel)) return;

        Objects.requireNonNull(textChannel).sendMessage(notif.getStreamEndMessage()).queue();
        log.info("Sent stream end message to G:{} C:{}", notif.getServerId(), notif.getChannelId());
    }

    private static boolean isGuildReachable(Notification notif, Guild guild) {
        if (!ShardService.getInstance().getGuilds().contains(guild)) {
            log.info("Guild is not reachable. G:{}", notif.getServerId());
            return false;
        }

        return true;
    }

    private static boolean isChannelReachable(Notification notif, TextChannel textChannel) {
        if (!Objects.requireNonNull(ShardService.getInstance().getGuildById(notif.getServerId()))
                .getTextChannels().contains(textChannel)) {
            log.info("Channel does not exits. G:{} C:{}", notif.getServerId(), notif.getChannelId());
            DatabaseDriver.getInstance().deleteNotif(notif.getId());
            log.info("Deleted the notification in G:{} C:{} for {} ({})",
                    notif.getServerId(), notif.getChannelId(), notif.getStreamerName(), notif.getStreamerId());

            final Cursor cursor = DatabaseDriver.getInstance().selectStreamerNotifs(notif.getStreamerId());
            if (!cursor.hasNext()) {
                DatabaseDriver.getInstance().deleteStreamer(notif.getStreamerId());
                log.info("There are no more notifications for {} - {}. Deleted from database.",
                        notif.getStreamerName(), notif.getStreamerId());
            }
            cursor.close();
            return false;
        }

        return true;
    }
}
