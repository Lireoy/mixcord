package bot.commands.notifications;

import bot.Constants;
import bot.Mixcord;
import bot.structure.CommandCategory;
import bot.utils.EmbedSender;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Lists all notifications already set up in a specific Discord guild.
 */
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
        ArrayList list = Mixcord.getDatabase().selectServerNotifsOrdered(serverId);

        String prevChannel = "";
        if (list.isEmpty()) {
            commandEvent.reactError();
            commandEvent.reply("There are no notifications in this channel");
            return;
        }

        StringBuilder description = new StringBuilder();
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

        if (list.size() == 1) {
            commandEvent.reply("There's only 1 notification in this server.");
            commandEvent.reply(
                    new EmbedSender()
                            .setTitle("Channel Notifications")
                            .setDescription(description)
                            .build());
        }

        if (list.size() > 1) {
            String message = "There's a total of " + list.size() + " notifications in this server.";
            commandEvent.reply(message);
            commandEvent.reply(
                    new EmbedSender()
                            .setTitle("Server Notifications")
                            .setDescription(description.toString())
                            .build());
        }
    }
}
