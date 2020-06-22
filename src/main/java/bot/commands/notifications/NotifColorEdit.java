package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.structures.MixcordCommand;
import bot.structures.Notification;
import bot.utils.CommandUtil;
import bot.utils.HexUtil;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;

/**
 * Edits a specific notifications embed color.
 */
@Slf4j
public class NotifColorEdit extends MixcordCommand {

    public NotifColorEdit() {
        this.name = "NotifColorEdit";
        this.aliases = new String[]{"ColorEdit", "EditColor", "Color"};
        this.help = Locale.NOTIF_COLOR_EDIT_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
        this.arguments = "<streamer name>, <new hex color code>";
        this.guildOnly = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name + " shroud, f2ff00"};
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String[] args = StringUtil.separateArgs(commandEvent.getArgs(), 2);

        if (!isValidCommand(commandEvent, args)) return;

        String query = args[0].trim();
        String newColor = args[1].trim();
        if (!isValidQueryParam(commandEvent, query)) return;
        if (!isValidColor(commandEvent, newColor)) return;

        handleColorUpdate(commandEvent, args);
    }

    private boolean isValidCommand(CommandEvent commandEvent, String[] args) {
        if (args.length != 2) {
            commandEvent.reply(String.format(Locale.NOTIF_COLOR_EDIT_COMMAND_NO_FULL_CONFIG, commandExamples[0]));
        }
        return true;
    }

    private boolean isValidQueryParam(CommandEvent commandEvent, String query) {
        if (query.isEmpty()) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_NO_STREAMER_NAME);
            return false;
        }

        if (query.length() > 20) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_TOO_LONG_NAME);
            return false;
        }
        return true;
    }

    private boolean isValidColor(CommandEvent commandEvent, String newColor) {
        if (newColor.isEmpty()) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_INVALID_HEX);
            return false;
        }

        if (!HexUtil.getInstance().validateHex(newColor.trim())) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_INVALID_HEX);
            return false;
        }
        return true;
    }

    private void handleColorUpdate(CommandEvent commandEvent, String[] args) {
        final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(
                commandEvent.getGuild().getId(),
                commandEvent.getChannel().getId(),
                args[0].trim());
        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_NO_NOTIFICATIONS);
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();

        String newColor = HexUtil.getInstance().formatHex(args[1].trim()).trim();
        if (notif.getEmbedColor().equals(newColor)) {
            commandEvent.reply(Locale.NOTIF_COLOR_EDIT_COMMAND_SAME_COLOR);
            return;
        }

        DatabaseDriver.getInstance().updateColor(notif.getId(), newColor);

        respond(commandEvent, newColor, notif);
    }

    private void respond(CommandEvent commandEvent, String newColor, Notification notif) {
        StringBuilder response = new StringBuilder()
                .append(String.format(
                        Locale.NOTIF_COLOR_EDIT_COMMAND_SUCCESSFUL,
                        notif.getStreamerName()))
                .append(String.format(
                        Locale.NOTIF_COLOR_EDIT_COMMAND_OLD_COLOR,
                        notif.getEmbedColor()))
                .append(String.format(
                        Locale.NOTIF_COLOR_EDIT_COMMAND_NEW_COLOR,
                        newColor));

        commandEvent.reply(response.toString());
    }
}
