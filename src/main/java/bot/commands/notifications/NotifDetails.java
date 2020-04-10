package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.structures.Notification;
import bot.utils.HelpUtil;
import bot.utils.HexUtil;
import bot.utils.MixerEmbedBuilder;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class NotifDetails extends Command {

    public NotifDetails() {
        this.name = "NotifDetails";
        this.help = Locale.NOTIF_DETAILS_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
        this.arguments = "<streamer name>";
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

        final String[] commandExamples = {BotConstants.PREFIX + this.name + " shroud"};

        boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String username = commandEvent.getArgs();

        // Empty args check
        if (username.isEmpty()) {
            commandEvent.reply(Locale.NOTIF_DETAILS_COMMAND_NO_STREAMER_NAME);
        }

        if (username.length() > 20) {
            commandEvent.reply(Locale.NOTIF_DETAILS_COMMAND_TOO_LONG_NAME);
            return;
        }

        final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, username);
        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.NOTIF_DETAILS_COMMAND_NO_SUCH_NOTIFICATION);
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();

        commandEvent.reply(new MixerEmbedBuilder()
                .setTitle(Locale.NOTIF_DETAILS_COMMAND_NOTIFICATION_DETAILS_TITLE)
                .setColor(HexUtil.getInstance().formatForEmbed(notif.getEmbedColor()))
                .addField(
                        Locale.NOTIF_DETAILS_COMMAND_NAME_TITLE,
                        notif.getStreamerName(),
                        false)
                .addField(
                        Locale.NOTIF_DETAILS_COMMAND_SEND_EMBED_TITLE,
                        String.valueOf(notif.isEmbed()),
                        false)
                .addField(
                        Locale.NOTIF_DETAILS_COMMAND_EMBED_COLOR_TITLE,
                        String.format(
                                Locale.NOTIF_DETAILS_COMMAND_EMBED_COLOR,
                                notif.getEmbedColor()),
                        false)
                .addField(
                        Locale.NOTIF_DETAILS_COMMAND_START_MESSAGE_TITLE,
                        notif.getMessage(),
                        false)
                .addField(
                        Locale.NOTIF_DETAILS_COMMAND_END_MESSAGE_TITLE,
                        notif.getStreamEndMessage(),
                        false)
                .build());
    }
}
