package bot.commands.notifications;

import bot.Constants;
import bot.Mixcord;
import bot.utils.StringUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

@Slf4j
public class NotifMessageEdit extends Command {

    public NotifMessageEdit() {
        this.name = "NotifMessageEdit";
        this.aliases = new String[]{"MessageEdit", "EditMessage"};
        this.help = "Edits the notification's message.";
        this.arguments = "<streamer name>, <new message>";
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
        String[] args = commandEvent.getArgs().trim().split(",", 2);

        String streamerName = "";
        String newMessage = "";

        if (args.length > 1) {
            streamerName = args[0].trim();
            newMessage = args[1].trim();
        }

        if (streamerName.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one! (max 20 chars)");
            return;
        }

        if (newMessage.isEmpty()) {
            commandEvent.reply("Please provide a new notification message!");
            return;
        }

        if (newMessage.length() > 300) {
            commandEvent.reply("Your new notification message is too long! (max 300 chars)");
            return;
        }

        Cursor cursor = Mixcord.getDatabase().selectOneNotification(serverId, channelId, streamerName);
        if (cursor.hasNext()) {
            JSONObject dbNotification = new JSONObject(cursor.next().toString());

            String dbDocumentId = dbNotification.getString("id");
            String dbStreamerName = dbNotification.getString("streamerName");
            String dbNotifMessage = dbNotification.getString("message");
            boolean dbEmbed = dbNotification.getBoolean("embed");

            if (dbNotifMessage.equals(newMessage)) {
                commandEvent.reply("Your new message is same as the old one!");
                return;
            }

            String MIXER_PATTERN = "https://mixer.com/" + dbStreamerName;

            if (dbEmbed) {
                if (StringUtil.containsIgnoreCase(newMessage, Constants.MIXER_COM)) {
                    if (StringUtil.containsIgnoreCase(newMessage, MIXER_PATTERN)) {
                        updateMsgAndRespond(commandEvent, newMessage, dbDocumentId, dbStreamerName, dbNotifMessage);
                    } else {
                        commandEvent.reply("Your notification message contains a link to a different steamer.");
                    }
                } else {
                    updateMsgAndRespond(commandEvent, newMessage, dbDocumentId, dbStreamerName, dbNotifMessage);
                }
            } else {
                if (StringUtil.containsIgnoreCase(newMessage, Constants.MIXER_COM)) {
                    if (StringUtil.containsIgnoreCase(newMessage, MIXER_PATTERN)) {
                        updateMsgAndRespond(commandEvent, newMessage, dbDocumentId, dbStreamerName, dbNotifMessage);
                    } else {
                        commandEvent.reply("Your notification message contains a link to a different steamer.");
                    }
                } else {
                    commandEvent.reply("Your notification message does not contain a link to the streamer.");
                }
            }
        } else {
            commandEvent.reply("There is no such notification in this channel");
        }
        cursor.close();
    }

    private void updateMsgAndRespond(CommandEvent commandEvent, String newMessage, String dbDocumentId, String dbStreamerName, String dbNotifMessage) {
        Mixcord.getDatabase().updateMessage(dbDocumentId, newMessage);

        StringBuilder response = new StringBuilder();

        response.append("Notification message was changed for the following notification: `").append(dbStreamerName).append("`");
        response.append("\nOld message:\n```").append(dbNotifMessage).append("```\n\n");
        response.append("New message:\n```").append(newMessage).append("```");

        commandEvent.reply(response.toString());
    }
}
