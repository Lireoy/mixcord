package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.structures.MixcordCommand;
import bot.structures.Notification;
import bot.utils.CommandUtil;
import bot.utils.HexUtil;
import bot.utils.MixerEmbedBuilder;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;

@Slf4j
public class NotifDetails extends MixcordCommand {

    public NotifDetails() {
        this.name = "NotifDetails";
        this.help = Locale.NOTIF_DETAILS_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
        this.arguments = "<streamer name>";
        this.guildOnly = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name + " shroud"};
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;
        if (!isValidQueryParam(commandEvent)) return;

        generateNotifDetails(commandEvent);
    }

    private void generateNotifDetails(CommandEvent commandEvent) {
        final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(
                commandEvent.getGuild().getId(),
                commandEvent.getChannel().getId(),
                commandEvent.getArgs().trim());

        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.NOTIF_DETAILS_COMMAND_NO_SUCH_NOTIFICATION);
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();
        respond(commandEvent, notif);
    }

    private boolean isValidQueryParam(CommandEvent commandEvent) {
        if (commandEvent.getArgs().trim().isEmpty()) {
            commandEvent.reply(Locale.NOTIF_DETAILS_COMMAND_NO_STREAMER_NAME);
            return false;
        }

        if (commandEvent.getArgs().trim().length() > 20) {
            commandEvent.reply(Locale.NOTIF_DETAILS_COMMAND_TOO_LONG_NAME);
            return false;
        }
        return true;
    }

    private void respond(CommandEvent commandEvent, Notification notif) {
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
