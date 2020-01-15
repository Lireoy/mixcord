package bot.commands.notifications;

import bot.Constants;
import bot.DatabaseDriver;
import bot.Mixcord;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.time.Instant;

@Slf4j
public class ChannelNotifs extends Command {

    private DatabaseDriver database;

    public ChannelNotifs() {
        this.name = "ChannelNotifs";
        this.aliases = new String[]{"ListNotifs", "ListNotifications"};
        this.help = "Lists all available notifications for this channel.";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.database = Mixcord.getDatabase();
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String serverId = commandEvent.getMessage().getGuild().getId();
        String channelId = commandEvent.getMessage().getChannel().getId();
        Cursor cursor = getDatabase().selectChannelNotifs(serverId, channelId);

        StringBuilder description = new StringBuilder();
        int notifCount = 0;

        if (cursor.hasNext()) {
            for (Object doc : cursor) {
                JSONObject entry = new JSONObject(doc.toString());
                String streamer = entry.getString("streamerName");
                description
                        .append("· [")
                        .append(streamer)
                        .append("](")
                        .append(Constants.MIXER_COM)
                        .append(streamer).append(")\n");
                notifCount++;
                if (notifCount % 5 == 0) {
                    description.append("\n");
                }
            }
        } else {
            description = new StringBuilder("There are no notifications in this channel");
        }

        String footer = commandEvent.getAuthor().getName() + "#"
                + commandEvent.getAuthor().getDiscriminator();
        String footerImg = commandEvent.getAuthor().getAvatarUrl();

        if (notifCount > 1) {
            commandEvent.reply("There's a total of " + notifCount + " notifications in this channel.");
        } else if (notifCount == 1) {
            commandEvent.reply("There's only 1 notification in this channel.");
        }
        commandEvent.reply(new EmbedBuilder()
                .setTitle("Channel Notifications")
                .setDescription(description)
                .setFooter(footer, footerImg)
                .setTimestamp(Instant.now())
                .build());
        cursor.close();
    }

    private DatabaseDriver getDatabase() {
        return database;
    }
}
