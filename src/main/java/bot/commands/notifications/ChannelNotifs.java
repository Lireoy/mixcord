package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.constants.MixerConstants;
import bot.database.DatabaseDriver;
import bot.structures.MixcordCommand;
import bot.structures.Streamer;
import bot.utils.CommandUtil;
import bot.utils.MixerEmbedBuilder;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;

/**
 * Lists all notifications already set up in a specific Discord channel.
 */
@Slf4j
public class ChannelNotifs extends MixcordCommand {

    public ChannelNotifs() {
        this.name = "ChannelNotifs";
        this.aliases = new String[]{"ListNotifs", "ListNotifications"};
        this.help = Locale.CHANNEL_NOTIFS_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("NOTIFICATIONS"));
        this.guildOnly = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name};
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        final Cursor cursor = DatabaseDriver.getInstance().selectChannelNotifs(serverId, channelId);

        if (!cursor.hasNext()) {
            commandEvent.reactError();
            commandEvent.reply(Locale.CHANNEL_NOTIFS_COMMAND_NO_NOTIFICATIONS);
            return;
        }

        StringBuilder description = generateDescription(cursor);
        cursor.close();

        respond(commandEvent, description);
    }

    private StringBuilder generateDescription(Cursor cursor) {
        StringBuilder description = new StringBuilder();
        int notifCount = 0;

        for (Object doc : cursor) {
            Streamer streamer = new Gson().fromJson(doc.toString(), Streamer.class);
            description.append(
                    String.format(
                            Locale.CHANNEL_NOTIFS_COMMAND_LINE,
                            streamer.getStreamerName(),
                            MixerConstants.HTTPS_MIXER_COM,
                            streamer.getStreamerName()));
            notifCount++;
            if (notifCount % 5 == 0) {
                description.append("\n");
            }
        }
        return description;
    }

    private void respond(CommandEvent commandEvent, StringBuilder description) {
        commandEvent.reply(new MixerEmbedBuilder()
                .setTitle(Locale.CHANNEL_NOTIFS_COMMAND_CHANNEL_NOTIFS_TITLE)
                .setDescription(description)
                .build());
    }
}