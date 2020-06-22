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
 * Deletes a specific notification entry from the database.
 */
@Slf4j
public class DeleteNotif extends Command {

    public DeleteNotif() {
        this.name = "DeleteNotif";
        this.aliases = new String[]{"DeleteNotif", "DelNotif", "RemoveNotif"};
        this.help = Locale.DELETE_NOTIF_COMMAND_HELP;
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
        final String username = commandEvent.getArgs().trim();

        // Empty args check
        if (username.isEmpty()) {
            commandEvent.reply(Locale.DELETE_NOTIF_COMMAND_NO_STREAMER_NAME);
            return;
        }

        if (username.length() > 20) {
            commandEvent.reply(Locale.DELETE_NOTIF_COMMAND_TOO_LONG_NAME);
            return;
        }

        final Cursor notificationCursor = DatabaseDriver.getInstance()
                .selectOneNotification(serverId, channelId, username);
        if (!notificationCursor.hasNext()) {
            commandEvent.reply(Locale.DELETE_NOTIF_COMMAND_NO_SUCH_NOTIFICATION);
            return;
        }

        final Notification notif = new Gson().fromJson(notificationCursor.next().toString(), Notification.class);
        notificationCursor.close();

        DatabaseDriver.getInstance().deleteNotif(notif.getId());
        log.info("Deleted the notification in G:{} C:{} for {} ({})",
                notif.getServerId(), notif.getChannelId(), notif.getStreamerName(), notif.getStreamerId());
        commandEvent.reply(Locale.DELETE_NOTIF_COMMAND_SUCCESSFUL);
        commandEvent.reactSuccess();

        Cursor cursor = DatabaseDriver.getInstance().selectStreamerNotifs(notif.getStreamerId());
        if (!cursor.hasNext()) {
            final boolean streamerDeleteResponse = DatabaseDriver.getInstance().deleteStreamer(notif.getStreamerId());

            if (streamerDeleteResponse) {
                log.info("There are no more notifications for {} - {}. Deleted from database.",
                        notif.getStreamerName(), notif.getStreamerId());
            }
        }
        cursor.close();
    }
}
