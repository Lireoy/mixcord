package bot.commands.notifications;

import bot.Mixcord;
import bot.utils.EmbedSender;
import bot.utils.MixerQuery;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

@Slf4j
public class NotifPreview extends Command {

    public NotifPreview() {
        this.name = "NotifPreview";
        this.aliases = new String[]{"Preview", "NotificationPreview"};
        this.help = "Sends a preview for a notification.";
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
        String streamerName = commandEvent.getArgs().trim();

        if (streamerName.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one! (max 20 chars)");
            return;
        }

        Cursor cursor = Mixcord.getDatabase().filter(serverId, channelId, streamerName);
        if (cursor.hasNext()) {
            JSONObject dbNotification = new JSONObject(cursor.next().toString());

            String dbStreamerName = dbNotification.getString("streamerName");
            String dbNotifMessage = dbNotification.getString("message");

            JSONObject queryJson = MixerQuery.queryChannel(dbStreamerName);

            commandEvent.reply(dbNotifMessage);
            commandEvent.reply(
                    new EmbedSender(queryJson, dbNotification)
                            .setAuthor()
                            .setTitle()
                            .setDescription()
                            .build());

            queryJson = null;
        } else {
            commandEvent.reply("There is no such notification in this channel.");
        }
    }
}
