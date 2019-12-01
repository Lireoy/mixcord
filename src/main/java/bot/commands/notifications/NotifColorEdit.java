package bot.commands.notifications;

import bot.Mixcord;
import bot.utils.HexUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

@Slf4j
public class NotifColorEdit extends Command {

    public NotifColorEdit() {
        this.name = "NotifColorEdit";
        this.aliases = new String[]{"ColorEdit", "EditColor", "Color"};
        this.help = "Edits the notification's embed color.";
        this.arguments = "<streamer name>, <hex color code>";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String serverId = commandEvent.getMessage().getGuild().getId();
        String channelId = commandEvent.getMessage().getChannel().getId();
        String[] args = commandEvent.getArgs().trim().split(",", 2);


        String streamerName = "";
        String newColor = "";

        if (args.length > 1) {
            streamerName = args[0].trim();
            newColor = args[1].trim();
        }

        if (streamerName.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one! (max 20 chars)");
            return;
        }

        HexUtil hexValidator = new HexUtil();

        if (hexValidator.validateHex(newColor.trim())) {
            Cursor cursor = Mixcord.getDatabase().filter(serverId, channelId, streamerName);
            if (cursor.hasNext()) {
                JSONObject dbNotification = new JSONObject(cursor.next().toString());

                String dbDocumentId = dbNotification.getString("id");
                String dbStreamerName = dbNotification.getString("streamerName");
                String dbEmbedColor = dbNotification.getString("embedColor");

                if (dbEmbedColor.equals(newColor)) {
                    commandEvent.reply("Your new color is same as the old one!");
                } else {
                    Mixcord.getDatabase().updateColor(dbDocumentId, newColor);

                    String response = "";

                    response += "Notification color was changed for the following notification: `" + dbStreamerName + "`";
                    response += "\nOld color:\n```" + dbEmbedColor + "```\n\n";
                    response += "New message:\n```" + newColor + "```";

                    commandEvent.reply(response);
                }

            } else {
                commandEvent.reply("There are no notifications in this channel");
            }

        } else {
            commandEvent.reply("Please provide a valid hex color.");
        }
    }
}
