package bot.commands.notifications;

import bot.Mixcord;
import bot.utils.MixerQuery;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.util.ArrayList;

@Slf4j
public class DeleteNotif extends Command {

    public DeleteNotif() {
        this.name = "DeleteNotif";
        this.aliases = new String[]{"DelNotif"};
        this.help = "Deletes a streamer notification in the channel where the command is used.";
        this.arguments = "<streamer name>";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String serverId = commandEvent.getMessage().getGuild().getId();
        String channelId = commandEvent.getMessage().getChannel().getId();
        String username = commandEvent.getArgs().trim();

        // Empty args check
        if (username.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
        }
        if (username.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
        } else {
            //TODO: CHECK IN DATABASE, BEFORE ANY ONLINE QUERY - in theory done, but idk why I wrote this todo
            if (!Mixcord.getDatabase().selectOneNotification(serverId, channelId, username).hasNext()) {
                return;
            }

            // Query Mixer to get case-correct streamer name, ID etc.
            JSONObject channel = MixerQuery.queryChannel(username);
            if (channel == JSONObject.NULL) {
                commandEvent.reactError();
                commandEvent.reply("Query response JSON was null, when deleting a notification, " +
                        "please contact the developer: <@331756964801544202>");
                return;
            }

            // Non existent streamer queries return with null from Mixer API
            if (channel == null) {
                commandEvent.reply("There is no such streamer...");
                return;
            }
            String streamerId = String.valueOf(channel.getInt("userId"));
            String streamerName = channel.getString("token");

            boolean response = Mixcord.getDatabase().deleteNotif(serverId, channelId, streamerId);

            Cursor cursor = Mixcord.getDatabase().selectStreamerNotifs(streamerId);
            if (!cursor.hasNext()) {
                Mixcord.getDatabase().deleteStreamer(streamerName, streamerId);
                log.info("There are no more notifications for {} - {}. Deleted from database.", streamerName, streamerId);
            }

            // Response to user
            if (response) {
                commandEvent.reply("Notification was deleted.");
                commandEvent.reactSuccess();
            } else {
                commandEvent.reply("Something went wrong. Could not delete the notification.");
                commandEvent.reactError();
            }
        }
    }
}
