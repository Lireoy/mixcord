package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.structures.MixcordCommand;
import bot.structures.Server;
import bot.utils.CommandUtil;
import bot.utils.MixerQuery;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Adds a notification entry to the database for a specific Mixer user.
 * The notification limit is checked here as case by case basis.
 */
@Slf4j
public class AddNotif extends MixcordCommand {

    public AddNotif() {
        this.name = "AddNotif";
        this.aliases = new String[]{"CreateNotif"};
        this.help = Locale.ADD_NOTIF_COMMAND_HELP;
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
        if (!isValidQueryParam(commandEvent)) return;

        if (checkAvailableNotificationSlots(commandEvent, commandEvent.getMessage().getGuild().getId())) return;

        final JSONObject channel = validateMixerQuery(commandEvent, commandEvent.getArgs().trim());
        if (channel == null) return;

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
        final boolean response = DatabaseDriver.getInstance().addNotif(
                commandEvent.getMessage().getGuild().getId(),
                commandEvent.getMessage().getChannel().getId(),
                streamerName,
                streamerId);

        respond(commandEvent, streamerName, response);
    }

    private boolean isValidQueryParam(CommandEvent commandEvent) {
        if (commandEvent.getArgs().trim().isEmpty()) {
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_NO_STREAMER_NAME);
            return false;
        }

        if (commandEvent.getArgs().trim().length() > 20) {
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_TOO_LONG_NAME);
            return false;
        }
        return true;
    }

    @Nullable
    private JSONObject validateMixerQuery(CommandEvent commandEvent, String query) {
        final JSONObject channel = MixerQuery.queryChannel(query);
        if (channel == null) {
            commandEvent.reactError();
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_JSON_WAS_NULL);
            return null;
        }

        if (channel.isEmpty()) {
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_NO_SUCH_STREAMER);
            return null;
        }
        return channel;
    }

    private boolean checkAvailableNotificationSlots(CommandEvent commandEvent, String serverId) {
        Cursor cursor = DatabaseDriver.getInstance().selectOneServer(serverId);
        if (!cursor.hasNext()) {
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_SERVER_DOES_NOT_EXIST);
            return false;
        }

        final Server server = new Gson().fromJson(cursor.next().toString(), Server.class);
        cursor.close();
        final ArrayList list = DatabaseDriver.getInstance().selectServerNotifsOrdered(serverId);
        if (server.isWhitelisted()) {
            if (list.size() >= 25) {
                commandEvent.reply(Locale.ADD_NOTIF_COMMAND_FREE_LIMIT_REACHED);
                return true;
            }
        } else {
            if (list.size() >= 10) {
                commandEvent.reply(Locale.ADD_NOTIF_COMMAND_TIER_ONE_LIMIT_REACHED);
                return true;
            }
        }
        return false;
    }

    private void respond(CommandEvent commandEvent, String streamerName, boolean response) {
        if (response) {
            commandEvent.reply(String.format(Locale.ADD_NOTIF_COMMAND_SUCCESSFUL, streamerName));
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply(Locale.ADD_NOTIF_COMMAND_ALREADY_EXISTS);
        }
    }
}
