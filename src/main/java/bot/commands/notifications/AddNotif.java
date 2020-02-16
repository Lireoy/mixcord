package bot.commands.notifications;

import bot.Constants;
import bot.factories.DatabaseFactory;
import bot.structure.enums.CommandCategory;
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

/**
 * Adds a notification entry to the database for a specific Mixer user.
 * The notification limit is checked here as case by case basis.
 */
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
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String query = commandEvent.getArgs().trim();

        // Empty args check
        if (query.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (query.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
            return;
        }

        Cursor cursor = DatabaseFactory.getDatabase().selectOneServer(serverId);
        if (!cursor.hasNext()) {
            commandEvent.reply("This server does not exist in the database. Please contact the developer: <@" + Constants.OWNER_ID + ">");
            return;
        }


        Server server = new Gson().fromJson(cursor.next().toString(), Server.class);
        cursor.close();
        final ArrayList list = DatabaseFactory.getDatabase().selectServerNotifsOrdered(serverId);
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


        // Query Mixer to get case-correct streamer name, ID etc.
        final JSONObject channel = MixerQuery.queryChannel(query);
        if (channel == JSONObject.NULL) {
            commandEvent.reactError();
            commandEvent.reply("Query response JSON was null, when adding a notification, " +
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

        if (streamerName.isEmpty() || streamerId.isEmpty()) {
            commandEvent.reply("Streamer name or ID is empty. Please contact the developer: <@" + Constants.OWNER_ID + ">");
            log.info("Streamer name or ID was empty.");
            return;
        }

        if (DatabaseFactory.getDatabase().addStreamer(streamerName, streamerId)) {
            log.info("New streamer detected, added to database...");
        }

        final boolean response = DatabaseFactory.getDatabase().addNotif(serverId, channelId, streamerName, streamerId);

        if (response) {
            commandEvent.reply("From now, you will receive notifications for " + streamerName + " in this channel.");
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply("You have already set up a notification for this streamer.");
        }
    }
}
