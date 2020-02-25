package bot.commands.notifications;

import bot.DatabaseDriver;
import bot.constants.BasicConstants;
import bot.constants.BotConstants;
import bot.constants.HelpConstants;
import bot.structures.Notification;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 * Changes in what format a specific notification is going to be sent.
 */
@Slf4j
public class NotifEmbedConfig extends Command {

    public NotifEmbedConfig() {
        this.name = "NotifEmbedConfig";
        this.help = HelpConstants.NOTIF_EMBED_CONFIG_HELP;
        this.category = new Category(CommandCategory.NOTIFICATIONS.toString());
        this.arguments = "<streamer name>, <true | false>";
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

        final String commandExample = BotConstants.PREFIX + this.name + " shroud, true";

        boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExample);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String[] args = StringUtil.separateArgs(commandEvent.getArgs());
        final String example = "\nExample: `" + BotConstants.PREFIX + this.name + " shroud, true`";

        String streamerName = "";
        String sendAsEmbed = "";
        boolean newEmbedValue = true;
        boolean booleanConvetSuccess = false;

        if (args.length < 2) {
            commandEvent.reply("Please provide a full configuration." + example);
            return;
        }

        streamerName = args[0].trim();
        sendAsEmbed = args[1].trim();

        if (streamerName.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one! (max 20 chars)");
            return;
        }

        if (sendAsEmbed.isEmpty()) {

            commandEvent.reply("Please provide a full configuration." + example);
            return;
        }

        if (sendAsEmbed.equalsIgnoreCase("true")) {
            booleanConvetSuccess = true;
        }

        if (sendAsEmbed.equalsIgnoreCase("false")) {
            newEmbedValue = false;
            booleanConvetSuccess = true;
        }

        if (!booleanConvetSuccess) {
            commandEvent.reply("Please provide a valid embed value." + example);
            return;
        }

        Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, streamerName);
        if (!cursor.hasNext()) {
            commandEvent.reply("There is no such notification in this channel");
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();

        final String MIXER_PATTERN = BasicConstants.HTTPS_MIXER_COM + notif.getStreamerName();
        final String MIXER_PATTERN2 = BasicConstants.HTTP_MIXER_COM + notif.getStreamerName();
        boolean containsLink = false;

        if (notif.getMessage().contains(MIXER_PATTERN)) containsLink = true;
        if (notif.getMessage().contains(MIXER_PATTERN2)) containsLink = true;

        if (!containsLink) {
            commandEvent.reply("Your notification message does not contain a link to the streamer. " +
                    "Please include one, and try again.");
            return;
        }

        if (notif.isEmbed() == newEmbedValue) {
            commandEvent.reply("This embed configuration is already set.");
            return;
        }

        DatabaseDriver.getInstance().updateEmbed(notif.getId(), newEmbedValue);

        StringBuilder response = new StringBuilder();
        response.append("Notification format was changed for the following notification: `")
                .append(notif.getStreamerName()).append("`");
        if (newEmbedValue) {
            response.append("\nThis notification will be sent as an embed in the future.");
        } else {
            response.append("\nThis notification will be sent without an embed in the future.");
        }

        commandEvent.reply(response.toString());
    }
}