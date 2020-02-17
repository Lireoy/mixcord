package bot.commands.notifications;

import bot.factories.DatabaseFactory;
import bot.structure.Notification;
import bot.structure.enums.CommandCategory;
import bot.utils.MixerEmbedBuilder;
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
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String streamerName = commandEvent.getArgs().trim();

        if (streamerName.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one! (max 20 chars)");
            return;
        }

        Cursor cursor = DatabaseFactory.getDatabase().selectOneNotification(serverId, channelId, streamerName);
        if (!cursor.hasNext()) {
            commandEvent.reply("There is no such notification in this channel.");
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();

        if (notif.isEmbed()) {
            final JSONObject queryJson = MixerQuery.queryChannel(notif.getStreamerName());
            commandEvent.reply(notif.getMessage());
            commandEvent.reply(new MixerEmbedBuilder(notif, queryJson)
                    .setCustomAuthor()
                    .setCustomTitle()
                    .setCustomDescription()
                    .build());
        } else {
            commandEvent.reply(notif.getMessage());
        }
    }
}
