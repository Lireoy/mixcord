package bot.commands.notifications;

import bot.Constants;
import bot.DatabaseDriver;
import bot.Mixcord;
import bot.structure.CommandCategory;
import bot.structure.Notification;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

@Slf4j
public class MakeDefault extends Command {

    public MakeDefault() {
        this.name = "MakeDefault";
        this.help = "Resets a notification's configuration to the defaults.";
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
        String streamerName = commandEvent.getArgs().trim();

        if (streamerName.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one! (max 20 chars)");
            return;
        }

        Cursor cursor = Mixcord.getDatabase().selectOneNotification(serverId, channelId, streamerName);
        if (cursor.hasNext()) {
            Notification notif = new Gson().fromJson(new JSONObject(cursor.next().toString()).toString(), Notification.class);

            String message = String.format(Constants.NOTIF_MESSAGE_DEFAULT, notif.getStreamerName());
            String endMessage = String.format(Constants.NOTIF_END_MESSAGE_DEFAULT, notif.getStreamerName());

            Mixcord.getDatabase().updateEmbed(notif.getId(), Constants.NOTIF_EMBED_DEFAULT);
            Mixcord.getDatabase().updateColor(notif.getId(), Constants.NOTIF_EMBED_COLOR_DEFAULT);
            Mixcord.getDatabase().updateMessage(notif.getId(), message);
            Mixcord.getDatabase().updateEndAction(notif.getId(), Constants.NOTIF_END_ACTION);
            Mixcord.getDatabase().updateEndMessage(notif.getId(), endMessage);

            commandEvent.reply("Notification configuration was reset for `" + notif.getStreamerName() + "`.");
        } else {
            commandEvent.reply("There is no such notification in this channel.");
        }
        cursor.close();
    }
}

