package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.structures.Notification;
import bot.utils.HelpUtil;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class NotifEndActionConfig extends Command {

    public NotifEndActionConfig() {
        this.name = "NotifEndActionConfig";
        this.help = Locale.NOTIF_END_ACTION_CONFIG_COMMAND_HELP;
        this.category = new Command.Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
        this.arguments = "<streamer name>, <end action number>";
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

        final String[] commandExamples = {BotConstants.PREFIX + this.name + " shroud, 2"};

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String[] args = StringUtil.separateArgs(commandEvent.getArgs(), 2);
        final String example = "\nExample: `" + BotConstants.PREFIX + this.name + " shroud, 2`";

        if (args.length < 2) {
            commandEvent.reply(
                    String.format(
                            Locale.NOTIF_END_ACTION_CONFIG_COMMAND_NO_FULL_CONFIG,
                            example));
            return;
        }

        String streamerName = args[0].trim();
        String endAction = args[1].trim();

        boolean validEndAction = false;

        if (streamerName.isEmpty()) {
            commandEvent.reply(Locale.NOTIF_END_ACTION_CONFIG_COMMAND_NO_STREAMER_NAME);
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply(Locale.NOTIF_END_ACTION_CONFIG_COMMAND_TOO_LONG_NAME);
            return;
        }


        if (endAction.equalsIgnoreCase("0")) {
            validEndAction = true;
        }

        if (endAction.equalsIgnoreCase("1")) {
            validEndAction = true;
        }


        if (!validEndAction) {
            commandEvent.reply(
                    String.format(
                            Locale.NOTIF_END_ACTION_CONFIG_COMMAND_INVALID_END_ACTION_VALUE,
                            example));
            return;
        }

        final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, streamerName);
        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.NOTIF_END_ACTION_CONFIG_COMMAND_NO_SUCH_NOTIFICATION);
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();

        if (notif.getStreamEndAction().equalsIgnoreCase(endAction)) {
            commandEvent.reply(Locale.NOTIF_END_ACTION_CONFIG_COMMAND_ALREADY_SET);
            return;
        }

        DatabaseDriver.getInstance().updateEndAction(notif.getId(), endAction);
        String response = "";
        response += String.format(
                Locale.NOTIF_END_ACTION_CONFIG_COMMAND_SUCCESSFUL,
                notif.getStreamerName());

        if (endAction.equalsIgnoreCase("0")) {
            response += Locale.NOTIF_END_ACTION_CONFIG_COMMAND_DO_NOTHING;
        }

        if (endAction.equalsIgnoreCase("1")) {
            response += Locale.NOTIF_END_ACTION_CONFIG_COMMAND_END_NOTIFICATION;
        }

        commandEvent.reply(response);
    }
}
