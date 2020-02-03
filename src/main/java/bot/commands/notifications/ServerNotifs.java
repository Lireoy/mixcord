package bot.commands.notifications;

import bot.Constants;
import bot.DatabaseDriver;
import bot.Mixcord;
import bot.structure.CommandCategory;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;

@Slf4j
public class ServerNotifs extends Command {

    // TODO: Check message length limit for whitelisted servers (25 streamers max)
    public ServerNotifs() {
        this.name = "ServerNotifs";
        this.aliases = new String[]{"ListAllNotifs", "ListAllNotifications"};
        this.help = "Lists all available notifications for this server.";
        this.category = new Category(CommandCategory.NOTIFICATIONS.toString());
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String serverId = commandEvent.getMessage().getGuild().getId();
        ArrayList list = Mixcord.getDatabase().selectServerNotifs(serverId);

        StringBuilder description = new StringBuilder();
        String prevChannel = "";
        if (!list.isEmpty()) {
            for (Object doc : list) {
                JSONObject entry = new JSONObject(doc.toString());
                String streamer = entry.getString("streamerName");
                String channel = entry.getString("channelId");

                if (!prevChannel.equals(channel)) {
                    description
                            .append("\n")
                            .append("<#")
                            .append(channel)
                            .append(">\n");
                }
                prevChannel = channel;
                description
                        .append("· [")
                        .append(streamer)
                        .append("](")
                        .append(Constants.MIXER_COM)
                        .append(streamer).append(")\n");
            }
        } else {
            description = new StringBuilder("There are no notifications in this server");
        }

        String footer = commandEvent.getAuthor().getName() + "#"
                + commandEvent.getAuthor().getDiscriminator();
        String footerImg = commandEvent.getAuthor().getAvatarUrl();

        if (list.size() > 1) {
            String message = "There's a total of " + list.size() + " notifications in this server.";
            commandEvent.reply(message);
        } else if (list.size() == 1) {
            commandEvent.reply("There's only 1 notification in this server.");
        }
        commandEvent.reply(new EmbedBuilder()
                .setTitle("Server Notifications")
                .setDescription(description.toString())
                .setFooter(footer, footerImg)
                .setTimestamp(Instant.now())
                .build());
    }
}
