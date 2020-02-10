package bot.commands.notifications;

import bot.Mixcord;
import bot.structure.CommandCategory;
import bot.structure.Notification;
import bot.utils.EmbedSender;
import bot.utils.MixerQuery;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

/**
 * Sends a complete notification preview for a specific streamer
 * in a specific Discord channel with the already specified options.
 */
@Slf4j
public class NotifPreview extends Command {

    public NotifPreview() {
        this.name = "NotifPreview";
        this.aliases = new String[]{"Preview", "NotificationPreview"};
        this.help = "Sends a preview for a notification.";
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
            Gson gson = new Gson();
            Notification notif = gson.fromJson(new JSONObject(cursor.next().toString()).toString(), Notification.class);
            JSONObject queryJson = MixerQuery.queryChannel(notif.getStreamerName());

            if (notif.isEmbed()) {
                commandEvent.reply(notif.getMessage());
                commandEvent.reply(
                        new EmbedSender(notif, queryJson)
                                .setCustomAuthor()
                                .setCustomTitle()
                                .setCustomDescription()
                                .build());
            } else {
                commandEvent.reply(notif.getMessage());
            }

        } else {
            commandEvent.reply("There is no such notification in this channel.");
        }
        cursor.close();
    }
}
