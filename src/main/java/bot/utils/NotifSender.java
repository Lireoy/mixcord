package bot.utils;

import bot.constants.MixerConstants;
import bot.database.DatabaseDriver;
import bot.services.ShardService;
import bot.structures.Notification;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.json.JSONObject;

@Slf4j
public class NotifSender {

    public static void sendEmbed(final Notification notif, final JSONObject queryJson) {
        final String queryChId = String.valueOf(queryJson.getInt("id"));
        final String embLiveThumbnail = MixerConstants.MIXER_THUMB_PRE + queryChId +
                MixerConstants.MIXER_THUMB_POST + "?" + StringUtil.generateRandomString(5, 6);

        if (!isAvailable(notif)) return;
        TextChannel textChannel = ShardService.getInstance().getTextChannelById(notif.getChannelId());

        assert textChannel != null;
        textChannel.sendMessage(notif.getMessage()).queue();
        textChannel.sendMessage(new MixerEmbedBuilder(notif, queryJson)
                .setCustomAuthor()
                .setCustomTitle()
                .setCustomDescription()
                .setImage(embLiveThumbnail)
                .build()).queue();
        log.info("Sent notification to G:{} C:{}", notif.getServerId(), notif.getChannelId());
    }

    public static void sendNonEmbed(final Notification notif) {
        if (!isAvailable(notif)) return;
        TextChannel textChannel = ShardService.getInstance().getTextChannelById(notif.getChannelId());

        assert textChannel != null;
        textChannel.sendMessage(notif.getMessage()).queue();
        log.info("Sent notification to G:{} C:{}", notif.getServerId(), notif.getChannelId());
    }

    public static void sendOfflineMsg(final Notification notif) {
        if (!isAvailable(notif)) return;
        TextChannel textChannel = ShardService.getInstance().getTextChannelById(notif.getChannelId());

        assert textChannel != null;

        if (notif.getStreamEndAction().equalsIgnoreCase("0")) {
            log.info("Do nothing to G:{} C:{}", notif.getServerId(), notif.getChannelId());
            return;
        }

        if (notif.getStreamEndAction().equalsIgnoreCase("1")) {
            textChannel.sendMessage(notif.getStreamEndMessage()).queue();
            log.info("Sent stream end message to G:{} C:{}", notif.getServerId(), notif.getChannelId());
        }
    }

    private static boolean isAvailable(final Notification notif) {
        final Guild guild = ShardService.getInstance().getGuildById(notif.getServerId());
        if (guild == null) {
            log.info("Guild is not reachable: {}", notif.getServerId());
            return false;
        }

        final TextChannel textChannel = ShardService.getInstance().getTextChannelById(notif.getChannelId());
        if (textChannel == null) {
            DatabaseDriver.getInstance().cleanStreamerAndNotifications(notif.getId(), notif.getStreamerId());
            log.info("Channel does not exist. Cleaned streamer and notifications in G:{} C:{} for {} ({})",
                    notif.getServerId(), notif.getChannelId(), notif.getStreamerName(), notif.getStreamerId());
            return false;
        }

        if (PermissionUtil.checkPermission(textChannel, guild.getSelfMember(),
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_EMBED_LINKS)) {

            return true;
        } else {
            final Member owner = textChannel.getGuild().getOwner();

            if (owner != null) {
                log.info("No talk power, notified the server owner ({}).", owner.getUser().getId());
                owner.getUser().openPrivateChannel().queue(privateChannel -> {
                    String template = "I don't have access to <#%s> channel in %s server, to send a notification for %s.\n" +
                            "Please give me `READ MESSAGES` and `SEND MESSAGES` permission in that channel.";
                    final String warningText = String.format(
                            template, textChannel.getId(), notif.getServerId(), notif.getStreamerName());
                    privateChannel.sendMessage(warningText).queue();
                });
            } else {
                DatabaseDriver.getInstance().cleanStreamerAndNotifications(notif.getId(), notif.getStreamerId());
                log.info("No talk power. Cleaned streamer and notifications in G:{} C:{} for {} ({})",
                        notif.getServerId(), notif.getChannelId(), notif.getStreamerName(), notif.getStreamerId());
            }
            return false;
        }
    }
}
