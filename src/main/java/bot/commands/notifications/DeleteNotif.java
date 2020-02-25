package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.HelpConstants;
import bot.database.DatabaseDriver;
import bot.structures.Notification;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
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
        this.help = HelpConstants.DELETE_NOTIF_HELP;
        this.category = new Category(CommandCategory.NOTIFICATIONS.toString());
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

        boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String username = commandEvent.getArgs().trim();

        // Empty args check
        if (username.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (username.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
            return;
        }

        final Cursor notificationCursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, username);
        if (!notificationCursor.hasNext()) {
            commandEvent.reply("There is no such notification...");
            return;
        }

        /*
        // Query Mixer to get case-correct streamer name, ID etc.
        final JSONObject channel = MixerQuery.queryChannel(username);
        if (channel == JSONObject.NULL) {
            commandEvent.reactError();
            commandEvent.reply("Query response JSON was null, when deleting a notification, " +
                    "please contact the developer: <@" + Constants.OWNER_ID + ">");
            return;
        }

        // Non existent streamer queries return with null from Mixer API
        if (channel == null) {
            commandEvent.reply("There is no such streamer...");
            return;
        }

        final String streamerId = String.valueOf(channel.getInt("userId"));
        final String streamerName = channel.getString("token");
         */

        Notification notif = new Gson().fromJson(notificationCursor.next().toString(), Notification.class);
        notificationCursor.close();

        DatabaseDriver.getInstance().deleteNotif(notif.getId());
        log.info("Deleted the notification in G:{} C:{} for {} ({})",
                notif.getServerId(), notif.getChannelId(), notif.getStreamerName(), notif.getStreamerId());
        commandEvent.reply("Notification was deleted.");
        commandEvent.reactSuccess();

        Cursor cursor = DatabaseDriver.getInstance().selectStreamerNotifs(notif.getStreamerId());
        if (!cursor.hasNext()) {
            boolean streamerDeleteResponse = DatabaseDriver.getInstance().deleteStreamer(notif.getStreamerId());

            if (streamerDeleteResponse) {
                log.info("There are no more notifications for {} - {}. Deleted from database.",
                        notif.getStreamerName(), notif.getStreamerId());
            }
        }
        cursor.close();
    }
}
