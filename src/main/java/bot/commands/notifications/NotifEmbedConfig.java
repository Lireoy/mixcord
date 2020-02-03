package bot.commands.notifications;

import bot.Mixcord;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

@Slf4j
public class NotifEmbedConfig extends Command {

    public NotifEmbedConfig() {
        this.name = "NotifEmbedConfig";
        this.help = "Edits the notification format. Set it to true for embed, false for non-embed notification.";
        this.category = new Category("notifications");
        this.arguments = "<streamer name>, <true | false>";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String serverId = commandEvent.getMessage().getGuild().getId();
        String channelId = commandEvent.getMessage().getChannel().getId();
        String[] args = commandEvent.getArgs().trim().split(",", 2);

        String streamerName = "";
        String sendAsEmbed = "";
        boolean newEmbedValue = true;
        boolean booleanConvetSuccess = false;

        if (args.length > 1) {
            streamerName = args[0].trim();
            sendAsEmbed = args[1].trim();
        }

        if (streamerName.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one! (max 20 chars)");
            return;
        }

        if (sendAsEmbed.equalsIgnoreCase("true")) {
            booleanConvetSuccess = true;
        }

        if (sendAsEmbed.equalsIgnoreCase("false")) {
            newEmbedValue = false;
            booleanConvetSuccess = true;
        }

        if (!booleanConvetSuccess) {
            return;
        }

        Cursor cursor = Mixcord.getDatabase().selectOneNotification(serverId, channelId, streamerName);
        if (cursor.hasNext()) {
            JSONObject dbNotification = new JSONObject(cursor.next().toString());

            String dbDocumentId = dbNotification.getString("id");
            String dbStreamerName = dbNotification.getString("streamerName");
            String dbNotifMessage = dbNotification.getString("message");
            boolean dbEmbed = dbNotification.getBoolean("embed");

            String MIXER_PATTERN = "https://mixer.com/" + dbStreamerName;

            if (dbNotifMessage.contains(MIXER_PATTERN)) {
                if (dbEmbed == newEmbedValue) {
                    commandEvent.reply("This embed configuration is already set.");
                } else {
                    Mixcord.getDatabase().updateEmbed(dbDocumentId, newEmbedValue);

                    StringBuilder response = new StringBuilder();

                    response.append("Notification format was changed for the following notification: `").append(dbStreamerName).append("`");
                    if (newEmbedValue) {
                        response.append("\nThis notification will be sent as an embed in the future.");
                    } else {
                        response.append("\nThis notification will be sent without an embed in the future.");
                    }

                    commandEvent.reply(response.toString());
                }
            } else {
                commandEvent.reply("Your notification message does not contain a link to the steamer. Please include one, and try again.");
            }
            cursor.close();
        } else {
            commandEvent.reply("There is no such notification in this channel");
        }

    }
}