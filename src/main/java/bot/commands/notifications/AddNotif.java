package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.structures.Server;
import bot.utils.HelpUtil;
import bot.utils.MixerQuery;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Adds a notification entry to the database for a specific Mixer user.
 * The notification limit is checked here as case by case basis.
 */
@Slf4j
public class AddNotif extends Command {

    public AddNotif() {
        this.name = "AddNotif";
        this.aliases = new String[]{"CreateNotif"};
        this.help = Locale.ADD_NOTIF_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
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

        final String[] commandExamples = {BotConstants.PREFIX + this.name + " shroud"};

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final String query = commandEvent.getArgs().trim();

        // Empty args check
        if (query.isEmpty()) {
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_NO_STREAMER_NAME);
            return;
        }

        if (query.length() > 20) {
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_TOO_LONG_NAME);
            return;
        }

        Cursor cursor = DatabaseDriver.getInstance().selectOneServer(serverId);
        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_SERVER_DOES_NOT_EXIST);
            return;
        }


        final Server server = new Gson().fromJson(cursor.next().toString(), Server.class);
        cursor.close();
        final ArrayList list = DatabaseDriver.getInstance().selectServerNotifsOrdered(serverId);
        if (server.isWhitelisted()) {
            if (list.size() >= 25) {
                commandEvent.reply(Locale.ADD_NOTIF_COMMAND_FREE_LIMIT_REACHED);
                return;
            }
        } else {
            if (list.size() >= 10) {
                commandEvent.reply(Locale.ADD_NOTIF_COMMAND_TIER_ONE_LIMIT_REACHED);
                return;
            }
        }


        // Query Mixer to get case-correct streamer name, ID etc.
        final JSONObject channel = MixerQuery.queryChannel(query);

        if (channel == null) {
            commandEvent.reactError();
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_JSON_WAS_NULL);

            return;
        }

        if (channel.isEmpty()) {
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_NO_SUCH_STREAMER);
            return;
        }

        final String streamerId = String.valueOf(channel.getInt("userId"));
        final String streamerName = channel.getString("token");

        if (streamerName.isEmpty() || streamerId.isEmpty()) {
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_EMPTY_STREAMER);
            log.info("Streamer name or ID was empty.");
            return;
        }

        if (DatabaseDriver.getInstance().addStreamer(streamerName, streamerId)) {
            log.info("New streamer detected, added to database...");
        }

        final boolean response = DatabaseDriver.getInstance().addNotif(serverId, channelId, streamerName, streamerId);

        if (response) {
            commandEvent.reply(
                    String.format(
                            Locale.ADD_NOTIF_COMMAND_SUCCESSFUL,
                            streamerName));
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_ALREADY_EXISTS);
        }
    }
}
