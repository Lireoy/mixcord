package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.structures.MixcordCommand;
import bot.structures.Notification;
import bot.utils.CommandUtil;
import bot.utils.MixerEmbedBuilder;
import bot.utils.MixerQuery;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import org.json.JSONObject;

/**
 * Sends a complete notification preview for a specific streamer
 * in a specific Discord channel with the already specified options.
 */
@Slf4j
public class NotifPreview extends MixcordCommand {

    public NotifPreview() {
        this.name = "NotifPreview";
        this.aliases = new String[]{"Preview", "NotificationPreview"};
        this.help = Locale.NOTIF_PREVIEW_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
        this.arguments = "<streamer name>";
        this.guildOnly = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name + " shroud"};
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String streamerName = commandEvent.getArgs().trim();

        if (streamerName.isEmpty()) {
            commandEvent.reply(Locale.NOTIF_PREVIEW_COMMAND_NO_STREAMER_NAME);
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply(Locale.NOTIF_PREVIEW_COMMAND_TOO_LONG_NAME);
            return;
        }

        Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, streamerName);
        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.NOTIF_PREVIEW_COMMAND_NO_SUCH_NOTIFICATION);
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();

        if (notif.isEmbed()) {
            final JSONObject queryJson = MixerQuery.queryChannel(notif.getStreamerName());

            if (queryJson == null) {
                commandEvent.reactError();
                commandEvent.reply(Locale.NOTIF_PREVIEW_COMMAND_JSON_WAS_NULL);
                return;
            }

            if (queryJson.isEmpty()) {
                commandEvent.reply(Locale.NOTIF_PREVIEW_COMMAND_WAS_NOT_FOUND_ON_MIXER);
                log.info(Locale.NOTIF_PREVIEW_COMMAND_WAS_NOT_FOUND_ON_MIXER);
                return;
            }

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
