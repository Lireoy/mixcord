package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.structures.Notification;
import bot.utils.CommandUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 * Resets all the fields in a database entry to the default values.
 */
@Slf4j
public class MakeDefault extends Command {

    public MakeDefault() {
        this.name = "MakeDefault";
        this.help = Locale.MAKE_DEFAULT_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
        this.arguments = "<streamer name>";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        final String[] commandExamples = {BotConstants.PREFIX + this.name + " shroud"};

        final boolean helpResponse = CommandUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String streamerName = commandEvent.getArgs().trim();

        if (streamerName.isEmpty()) {
            commandEvent.reply(Locale.MAKE_DEFAULT_COMMAND_NO_STREAMER_NAME);
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply(Locale.MAKE_DEFAULT_COMMAND_TOO_LONG_NAME);
            return;
        }

        final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, streamerName);
        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.MAKE_DEFAULT_COMMAND_NO_SUCH_NOTIFICATION);
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        DatabaseDriver.getInstance().resetNotification(notif.getId(), notif.getStreamerName());
        cursor.close();

        commandEvent.reply(
                String.format(
                        Locale.MAKE_DEFAULT_COMMAND_SUCCESSFUL,
                        notif.getStreamerName()));
    }
}

