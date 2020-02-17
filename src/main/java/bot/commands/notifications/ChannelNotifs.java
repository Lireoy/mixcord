package bot.commands.notifications;

import bot.Constants;
import bot.factories.DatabaseFactory;
import bot.structure.Streamer;
import bot.structure.enums.CommandCategory;
import bot.utils.MixerEmbedBuilder;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 * Lists all notifications already set up in a specific Discord channel.
 */
@Slf4j
public class ChannelNotifs extends Command {

    public ChannelNotifs() {
        this.name = "ChannelNotifs";
        this.aliases = new String[]{"ListNotifs", "ListNotifications"};
        this.help = "Lists all available notifications for this channel.";
        this.category = new Category(CommandCategory.NOTIFICATIONS.toString());
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

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final String channelId = commandEvent.getMessage().getChannel().getId();
        Cursor cursor = DatabaseFactory.getDatabase().selectChannelNotifs(serverId, channelId);

        StringBuilder description = new StringBuilder();
        int notifCount = 0;

        if (!cursor.hasNext()) {
            commandEvent.reactError();
            commandEvent.reply("There are no notifications in this channel");
            return;
        }

        for (Object doc : cursor) {
            Streamer streamer = new Gson().fromJson(doc.toString(), Streamer.class);
            String line = "· [%s](" + Constants.HTTPS_MIXER_COM + "%s)\n";
            description.append(String.format(line, streamer.getStreamerName(), streamer.getStreamerName()));
            notifCount++;
            if (notifCount % 5 == 0) {
                description.append("\n");
            }
        }
        cursor.close();

        if (notifCount == 1) {
            commandEvent.reply("There's only 1 notification in this channel.");
            commandEvent.reply(new MixerEmbedBuilder()
                    .setTitle("Channel Notifications")
                    .setDescription(description)
                    .build());
        }

        if (notifCount > 1) {
            commandEvent.reply("There's a total of " + notifCount + " notifications in this channel.");
            commandEvent.reply(new MixerEmbedBuilder()
                    .setTitle("Channel Notifications")
                    .setDescription(description)
                    .build());
        }
    }
}
