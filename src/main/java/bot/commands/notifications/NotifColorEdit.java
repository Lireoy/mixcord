package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.structures.Notification;
import bot.utils.HelpUtil;
import bot.utils.HexUtil;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 * Edits a specific notifications embed color.
 */
@Slf4j
public class NotifColorEdit extends Command {

    public NotifColorEdit() {
        this.name = "NotifColorEdit";
        this.aliases = new String[]{"ColorEdit", "EditColor", "Color"};
        this.help = Locale.NOTIF_COLOR_EDIT_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
        this.arguments = "<streamer name>, <new hex color code>";
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

        final String[] commandExamples = {BotConstants.PREFIX + this.name + " shroud, f2ff00"};

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String[] args = StringUtil.separateArgs(commandEvent.getArgs(), 2);
        final String example = "\nExample: `" + BotConstants.PREFIX + this.name + " shroud, 32a852`";

        if (args.length < 2) {
            commandEvent.reply(
                    String.format(
                            Locale.NOTIF_COLOR_EDIT_COMMAND_NO_FULL_CONFIG,
                            example));
            return;
        }

        String streamerName = args[0].trim();
        String newColor = args[1].trim();

        if (streamerName.isEmpty()) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_NO_STREAMER_NAME);
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_TOO_LONG_NAME);
            return;
        }

        if (newColor.isEmpty()) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_INVALID_HEX);
            return;
        }

        if (!HexUtil.getInstance().validateHex(newColor.trim())) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_INVALID_HEX);
            return;
        }

        final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, streamerName);
        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_NO_NOTIFICATIONS);
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();

        newColor = HexUtil.getInstance().formatHex(newColor).trim();
        if (notif.getEmbedColor().equals(newColor)) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_SAME_COLOR);
            return;
        }

        DatabaseDriver.getInstance().updateColor(notif.getId(), newColor);

        String response = "";
        response += String.format(
                Locale.NOTIF_COLOR_EDIT_COMMAND_SUCCESSFUL,
                notif.getStreamerName());
        response += String.format(
                Locale.NOTIF_COLOR_EDIT_COMMAND_OLD_COLOR,
                notif.getEmbedColor());
        response += String.format(
                Locale.NOTIF_COLOR_EDIT_COMMAND_NEW_COLOR,
                newColor);

        commandEvent.reply(response);
    }
}
