package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.constants.MixerConstants;
import bot.database.DatabaseDriver;
import bot.structures.Notification;
import bot.utils.CommandUtil;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class NotifEndMessageEdit extends Command {

    public NotifEndMessageEdit() {
        this.name = "NotifEndMessageEdit";
        this.aliases = new String[]{"EndMessageEdit", "EditEndMessage"};
        this.help = Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
        this.arguments = "<streamer name>, <new message>";
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

        final String[] commandExamples = {BotConstants.PREFIX + this.name +
                " shroud, shroud just finished streaming!"};

        final boolean helpResponse = CommandUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String[] args = StringUtil.separateArgs(commandEvent.getArgs(), 2);
        final String example = "\nExample: `" + BotConstants.PREFIX + this.name + " shroud, Shroud finished streaming, insert sad gif here`";

        if (args.length < 2) {
            commandEvent.reply(
                    String.format(
                            Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_NO_FULL_CONFIG,
                            example));
            return;
        }

        String streamerName = args[0].trim();
        String newMessage = args[1].trim();

        if (streamerName.isEmpty()) {
            commandEvent.reply(Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_NO_STREAMER_NAME);
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply(Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_TOO_LONG_NAME);
            return;
        }

        if (newMessage.isEmpty()) {
            commandEvent.reply(Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_NO_NEW_MESSAGE);
            return;
        }

        if (newMessage.length() > 300) {
            commandEvent.reply(Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_NEW_MESSAGE_TOO_LONG);
            return;
        }

        final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, streamerName);
        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_NO_SUCH_NOTIFICATION);
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();

        if (notif.getStreamEndMessage().equals(newMessage)) {
            commandEvent.reply(Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_SAME_MESSAGE);
            return;
        }

        final String MIXER_PATTERN = MixerConstants.HTTPS_MIXER_COM + notif.getStreamerName();

        if (notif.isEmbed()) {
            if (StringUtil.containsIgnoreCase(newMessage, MixerConstants.HTTPS_MIXER_COM)) {
                if (!StringUtil.containsIgnoreCase(newMessage, MIXER_PATTERN)) {
                    commandEvent.reply(Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_WRONG_LINK);
                    return;
                }
            }
            updateMsgAndRespond(commandEvent, newMessage, notif.getId(), notif.getStreamerName(), notif.getStreamEndMessage());
        } else {
            if (!StringUtil.containsIgnoreCase(newMessage, MixerConstants.HTTPS_MIXER_COM)) {
                commandEvent.reply(Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_NO_LINK);
                return;
            }
            if (!StringUtil.containsIgnoreCase(newMessage, MIXER_PATTERN)) {
                commandEvent.reply(Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_WRONG_LINK);
                return;
            }
            updateMsgAndRespond(commandEvent, newMessage, notif.getId(), notif.getStreamerName(), notif.getStreamEndMessage());
        }
    }

    private void updateMsgAndRespond(CommandEvent event, String newMessage, String docId, String streamerName, String oldMessage) {
        DatabaseDriver.getInstance().updateEndMessage(docId, newMessage);

        String response = "";
        response += String.format(
                Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_SUCCESSFUL,
                streamerName);
        response += String.format(
                Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_OLD_MESSAGE,
                oldMessage);
        response += String.format(
                Locale.NOTIF_END_MESSAGE_EDIT_COMMAND_NEW_MESSAGE,
                newMessage);

        event.reply(response);
    }
}
