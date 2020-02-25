package bot.commands.notifications;

import bot.Constants;
import bot.DatabaseDriver;
import bot.structure.Notification;
import bot.structure.enums.CommandCategory;
import bot.utils.HelpUtil;
import bot.utils.HexUtil;
import bot.utils.MixerEmbedBuilder;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class NotifDetails extends Command {

    public NotifDetails() {
        this.name = "NotifDetails";
        this.help = "Lists all settings for a notifications.";
        this.category = new Command.Category(CommandCategory.NOTIFICATIONS.toString());
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

        boolean helpResponse = HelpUtil.getInstance().sendCommandHelp(this, commandEvent);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();


        if (commandAuthor.getId().equals(Constants.OWNER_ID) ||
                commandAuthor.getId().equals(Constants.CO_OWNER_ID) ||
                commandAuthor.getId().equals(Constants.CO_OWNER_ID2)) {

            final String[] args = StringUtil.separateArgs(commandEvent.getArgs());
            final ArrayList<String> argList = new ArrayList<>(Arrays.asList(args));

            String streamerName = "";
            String json = "";

            if (argList.size() > 1) {
                streamerName = argList.get(0).trim();
                json = argList.get(1).trim();
            } else {
                streamerName = argList.get(0).trim();
            }

            if (streamerName.isEmpty()) {
                commandEvent.reply("Please provide a streamer name!");
                return;
            }

            if (streamerName.length() > 20) {
                commandEvent.reply("This name is too long! Please provide a shorter one! (max 20 chars)");
                return;
            }

            final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, streamerName);
            if (!cursor.hasNext()) {
                commandEvent.reply("There is no such notification...");
                return;
            }

            if (json.equalsIgnoreCase("json")) {
                String reply = "```json\n" + new JSONObject(cursor.next().toString()).toString(2) + "```";
                commandEvent.reply(reply);
                cursor.close();
                return;
            }
        }

        final String username = commandEvent.getArgs();

        // Empty args check
        if (username.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
        }

        if (username.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
            return;
        }

        final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, username);
        if (!cursor.hasNext()) {
            commandEvent.reply("There is no such notification...");
            return;
        }

        final Notification notif = new Gson().fromJson(cursor.next().toString(), Notification.class);
        cursor.close();

        commandEvent.reply(new MixerEmbedBuilder()
                .setTitle("Notification details")
                .setColor(HexUtil.getInstance().formatForEmbed(notif.getEmbedColor()))
                .addField("Name", notif.getStreamerName(), false)
                .addField("Send in embed", String.valueOf(notif.isEmbed()), false)
                .addField("Embed color", "#" + notif.getEmbedColor(), false)
                .addField("Stream start message", notif.getMessage(), false)
                .addField("Stream end message", notif.getStreamEndMessage(), false)
                .build());
    }
}
