package bot.commands.notifications;

import bot.Mixcord;
import bot.structure.CommandCategory;
import bot.structure.Server;
import bot.utils.MixerQuery;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.util.ArrayList;

@Slf4j
public class AddNotif extends Command {

    public AddNotif() {
        this.name = "AddNotif";
        this.aliases = new String[]{"CreateNotif"};
        this.help = "Creates a new notification for a Mixer streamer in the channel where the command is used.";
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
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String serverId = commandEvent.getMessage().getGuild().getId();
        String channelId = commandEvent.getMessage().getChannel().getId();
        String query = commandEvent.getArgs().trim();

        // Empty args check
        if (query.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (query.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
            return;
        }

        Cursor cursor = Mixcord.getDatabase().selectOneServer(serverId);
        Gson gson = new Gson();
        if (cursor.hasNext()) {
            Server server = gson.fromJson(new JSONObject(cursor.next().toString()).toString(), Server.class);
            ArrayList list = Mixcord.getDatabase().selectServerNotifs(serverId);
            if (server.isWhitelisted()) {
                if (list.size() >= 25) {
                    commandEvent.reply("This server has reached the limit for the number of notifications.");
                    return;
                }
            } else {
                if (list.size() >= 10) {
                    commandEvent.reply("This server has reached the limit for the number of notifications.");
                    return;
                }
            }
        }

        // Query Mixer to get case-correct streamer name, ID etc.
        JSONObject channel = MixerQuery.queryChannel(query);
        if (channel == JSONObject.NULL) {
            commandEvent.reactError();
            commandEvent.reply("Query response JSON was null, when adding a notification, " +
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

        if (streamerName.isEmpty() || streamerId.isEmpty()) {
            commandEvent.reply("Streamer name or ID is empty. Please contact the developer: <@331756964801544202>");
            log.info("Streamer name or ID was empty.");
            return;
        }

        if (Mixcord.getDatabase().addStreamer(streamerName, streamerId)) {
            log.info("New streamer detected, added to database...");
        }

        boolean responseCode = Mixcord.getDatabase().addNotif(serverId, channelId, streamerName, streamerId);

        if (responseCode) {
            commandEvent.reply("From now, you will receive notifications for " + streamerName + " in this channel.");
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply("You have already set up a notification for this streamer.");
        }

    }
}
