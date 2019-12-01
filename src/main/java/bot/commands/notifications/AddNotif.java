package bot.commands.notifications;

import bot.Mixcord;
import bot.utils.MixerQuery;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
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
        String query = commandEvent.getArgs().trim();

        // Empty args check
        if (query.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
        } else if (query.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
        } else {
            ArrayList list = Mixcord.getDatabase().filter(serverId);

            if (list.size() >= 10) {
                commandEvent.reply("This server has reached the limit for the number of notifications.");
                return;
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

            // Database handling
            String responseCode = Mixcord.getDatabase().insert(serverId, channelId, streamerName, streamerId);

            // Response to user
            if (responseCode.equals("1")) {
                commandEvent.reply("From now, you will receive notifications for " + streamerName + " in this channel.");
                commandEvent.reactSuccess();
            }
            if (responseCode.equals("0")) {
                commandEvent.reply("You have already set up a notification for this streamer.");
                commandEvent.reactSuccess();
            }
            if (responseCode.equals("-1")) {
                commandEvent.reply("Please provide a streamer!");
                commandEvent.reactError();
            }
        }
    }
}
