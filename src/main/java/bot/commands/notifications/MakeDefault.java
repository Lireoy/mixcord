package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.structures.MixcordCommand;
import bot.structures.Notification;
import bot.utils.CommandUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;

/**
 * Resets all the fields in a database entry to the default values.
 */
@Slf4j
public class MakeDefault extends MixcordCommand {

    public MakeDefault() {
        this.name = "MakeDefault";
        this.help = Locale.MAKE_DEFAULT_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
        this.arguments = "<streamer name>";
        this.guildOnly = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name + " shroud"};
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;
        if (!isValidQueryParam(commandEvent)) return;

        handleMakeDefault(commandEvent);
    }

    private boolean isValidQueryParam(CommandEvent commandEvent) {
        if (commandEvent.getArgs().trim().isEmpty()) {
            commandEvent.reply(Locale.MAKE_DEFAULT_COMMAND_NO_STREAMER_NAME);
            return false;
        }

        if (commandEvent.getArgs().trim().length() > 20) {
            commandEvent.reply(Locale.MAKE_DEFAULT_COMMAND_TOO_LONG_NAME);
            return false;
        }
        return true;
    }

    private void handleMakeDefault(CommandEvent commandEvent) {
        final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(
                commandEvent.getMessage().getGuild().getId(),
                commandEvent.getMessage().getChannel().getId(),
                commandEvent.getArgs().trim());

        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.MAKE_DEFAULT_COMMAND_NO_SUCH_NOTIFICATION);
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        DatabaseDriver.getInstance().resetNotification(notif.getId(), notif.getStreamerName());
        cursor.close();

        repsond(commandEvent, notif.getStreamerName());
    }

    private void repsond(CommandEvent commandEvent, String streamerName) {
        commandEvent.reply(String.format(Locale.MAKE_DEFAULT_COMMAND_SUCCESSFUL, streamerName));
    }
}

