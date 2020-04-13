package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.constants.MixerConstants;
import bot.database.DatabaseDriver;
import bot.structures.Notification;
import bot.utils.HelpUtil;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 * Changes in what format a specific notification is going to be sent.
 */
@Slf4j
public class NotifEmbedConfig extends Command {

    public NotifEmbedConfig() {
        this.name = "NotifEmbedConfig";
        this.help = Locale.NOTIF_EMBED_CONFIG_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
        this.arguments = "<streamer name>, <true | false>";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        final String[] commandExamples = {BotConstants.PREFIX + this.name + " shroud, true"};

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String[] args = StringUtil.separateArgs(commandEvent.getArgs(), 2);
        final String example = "\nExample: `" + BotConstants.PREFIX + this.name + " shroud, true`";

        boolean newEmbedValue = true;
        boolean booleanConvetSuccess = false;

        if (args.length < 2) {
            commandEvent.reply(
                    String.format(
                            Locale.NOTIF_EMBED_CONFIG_COMMAND_NO_FULL_CONFIG,
                            example));
            return;
        }

        String streamerName = args[0].trim();
        String sendAsEmbed = args[1].trim();

        if (streamerName.isEmpty()) {
            commandEvent.reply(Locale.NOTIF_EMBED_CONFIG_COMMAND_NO_STREAMER_NAME);
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply(Locale.NOTIF_EMBED_CONFIG_COMMAND_TOO_LONG_NAME);
            return;
        }

        if (sendAsEmbed.isEmpty()) {
            commandEvent.reply(
                    String.format(
                            Locale.NOTIF_EMBED_CONFIG_COMMAND_NO_FULL_CONFIG,
                            example));
            return;
        }

        if (sendAsEmbed.equalsIgnoreCase("true")) {
            booleanConvetSuccess = true;
        }

        if (sendAsEmbed.equalsIgnoreCase("false")) {
            newEmbedValue = false;
            booleanConvetSuccess = true;
        }

        if (!booleanConvetSuccess) {
            commandEvent.reply(Locale.NOTIF_EMBED_CONFIG_COMMAND_INVALID_EMBED_VALUE);
            return;
        }

        final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, streamerName);
        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.NOTIF_EMBED_CONFIG_COMMAND_NO_SUCH_NOTIFICATION);
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();

        final String MIXER_PATTERN = MixerConstants.HTTPS_MIXER_COM + notif.getStreamerName();
        final String MIXER_PATTERN2 = MixerConstants.HTTP_MIXER_COM + notif.getStreamerName();
        boolean containsLink = false;

        if (StringUtil.containsIgnoreCase(notif.getMessage(), MIXER_PATTERN)) containsLink = true;
        if (StringUtil.containsIgnoreCase(notif.getMessage(), MIXER_PATTERN2)) containsLink = true;

        if (!containsLink) {
            commandEvent.reply(Locale.NOTIF_EMBED_CONFIG_COMMAND_NO_LINK);
            return;
        }

        if (notif.isEmbed() == newEmbedValue) {
            commandEvent.reply(Locale.NOTIF_EMBED_CONFIG_COMMAND_ALREADY_SET);
            return;
        }

        DatabaseDriver.getInstance().updateEmbed(notif.getId(), newEmbedValue);

        String response = "";
        response += String.format(
                Locale.NOTIF_EMBED_CONFIG_COMMAND_SUCCESSFUL,
                notif.getStreamerName());

        if (newEmbedValue) {
            response += Locale.NOTIF_EMBED_CONFIG_COMMAND_SEND_AS_EMBED;
        } else {
            response += Locale.NOTIF_EMBED_CONFIG_COMMAND_SEND_AS_NON_EMBED;
        }

        commandEvent.reply(response);
    }
}