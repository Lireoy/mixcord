package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.HelpConstants;
import bot.database.DatabaseDriver;
import bot.structures.Notification;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
import bot.utils.HexUtil;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 * Edits a specific notifications embed color.
 */
@Slf4j
public class NotifColorEdit extends Command {

    public NotifColorEdit() {
        this.name = "NotifColorEdit";
        this.aliases = new String[]{"ColorEdit", "EditColor", "Color"};
        this.help = HelpConstants.NOTIF_COLOR_EDIT_HELP;
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
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        final String commandExample = BotConstants.PREFIX + this.name + " shroud, f2ff00";

        boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExample);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String[] args = StringUtil.separateArgs(commandEvent.getArgs());
        final String example = "\nExample: `" + BotConstants.PREFIX + this.name + " shroud, 32a852`";

        String streamerName = "";
        String newColor = "";

        if (args.length < 2) {
            commandEvent.reply("Please provide a full configuration." + example);
            return;
        }

        streamerName = args[0].trim();
        newColor = args[1].trim();


        if (streamerName.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one! (max 20 chars)");
            return;
        }

        if (newColor.isEmpty()) {
            commandEvent.reply("Please provide a valid hex color.");
            return;
        }

        if (!HexUtil.getInstance().validateHex(newColor.trim())) {
            commandEvent.reply("Please provide a valid hex color.");
            return;
        }

        final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, streamerName);
        if (!cursor.hasNext()) {
            commandEvent.reply("There are no notifications in this channel");
            return;
        }

        Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();

        newColor = HexUtil.getInstance().formatHex(newColor).trim();
        if (notif.getEmbedColor().equals(newColor)) {
            commandEvent.reply("Your new color is same as the old one!");
            return;
        }

        DatabaseDriver.getInstance().updateColor(notif.getId(), newColor);

        String response = "";
        response += "Notification color was changed for the following notification: `" + notif.getStreamerName() + "`";
        response += "\nOld color:\n```" + notif.getEmbedColor() + "```\n\n";
        response += "New color:\n```" + newColor + "```";
        commandEvent.reply(response);
    }
}
