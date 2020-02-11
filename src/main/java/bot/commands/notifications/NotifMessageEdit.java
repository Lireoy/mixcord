package bot.commands.notifications;

import bot.Constants;
import bot.Mixcord;
import bot.structure.CommandCategory;
import bot.structure.Notification;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

/**
 * Changes the notification message for a specific notification.
 */
@Slf4j
public class NotifMessageEdit extends Command {

    public NotifMessageEdit() {
        this.name = "NotifMessageEdit";
        this.aliases = new String[]{"MessageEdit", "EditMessage"};
        this.help = "Edits the notification's message.";
        this.category = new Category(CommandCategory.NOTIFICATIONS.toString());
        this.arguments = "<streamer name>, <new message>";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String serverId = commandEvent.getMessage().getGuild().getId();
        String channelId = commandEvent.getMessage().getChannel().getId();
        String[] args = StringUtil.separateArgs(commandEvent.getArgs());

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
            commandEvent.reply("There is no such notification in this channel");
            return;
        }

        Notification notif = new Gson().fromJson(new JSONObject(cursor.next().toString()).toString(), Notification.class);
        cursor.close();

        if (notif.getMessage().equals(newMessage)) {
            commandEvent.reply("Your new message is same as the old one!");
            return;
        }

        String MIXER_PATTERN = "https://mixer.com/" + notif.getStreamerName();

        if (notif.isEmbed()) {
            if (StringUtil.containsIgnoreCase(newMessage, Constants.MIXER_COM)) {
                if (!StringUtil.containsIgnoreCase(newMessage, MIXER_PATTERN)) {
                    commandEvent.reply("Your notification message contains a link to a different steamer.");
                    return;
                }
            }
            updateMsgAndRespond(commandEvent, newMessage, notif.getId(), notif.getStreamerName(), notif.getMessage());
        } else {
            if (!StringUtil.containsIgnoreCase(newMessage, Constants.MIXER_COM)) {
                commandEvent.reply("Your notification message does not contain a link to the streamer.");
                return;
            }
            if (!StringUtil.containsIgnoreCase(newMessage, MIXER_PATTERN)) {
                commandEvent.reply("Your notification message contains a link to a different steamer.");
                return;
            }
            updateMsgAndRespond(commandEvent, newMessage, notif.getId(), notif.getStreamerName(), notif.getMessage());
        }
    }

    private void updateMsgAndRespond(CommandEvent event, String newMessage, String docId, String streamerName, String oldMessage) {
        Mixcord.getDatabase().updateMessage(docId, newMessage);

        StringBuilder response = new StringBuilder();

        response.append("Notification message was changed for the following notification: `").append(streamerName).append("`");
        response.append("\nOld message:\n```").append(oldMessage).append("```\n\n");
        response.append("New message:\n```").append(newMessage).append("```");

        event.reply(response.toString());
    }
}
