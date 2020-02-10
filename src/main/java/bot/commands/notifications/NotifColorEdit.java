package bot.commands.notifications;

import bot.Mixcord;
import bot.structure.CommandCategory;
import bot.utils.HexUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

/**
 * Edits a specific notifications embed color.
 */
@Slf4j
public class NotifColorEdit extends Command {

    public NotifColorEdit() {
        this.name = "NotifColorEdit";
        this.aliases = new String[]{"ColorEdit", "EditColor", "Color"};
        this.help = "Edits the notification's embed color.";
        this.category = new Category(CommandCategory.NOTIFICATIONS.toString());
        this.arguments = "<streamer name>, <new hex color code>";
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
        String newColor = "";

        if (args.length > 1) {
            streamerName = args[0].trim();
            newColor = HexUtil.formatHex(args[1].trim());
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
            Cursor cursor = Mixcord.getDatabase().selectOneNotification(serverId, channelId, streamerName);
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
                    response += "New color:\n```" + newColor + "```";

                    commandEvent.reply(response);
                }

            } else {
                commandEvent.reply("There are no notifications in this channel");
            }
            cursor.close();
        } else {
            commandEvent.reply("Please provide a valid hex color.");
        }
    }
}
