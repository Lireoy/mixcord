package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.HelpConstants;
import bot.constants.MixerConstants;
import bot.database.DatabaseDriver;
import bot.structures.Notification;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
import bot.utils.MixerEmbedBuilder;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

/**
 * Lists all notifications already set up in a specific Discord guild.
 */
@Slf4j
public class ServerNotifs extends Command {

    public ServerNotifs() {
        this.name = "ServerNotifs";
        this.aliases = new String[]{"ListAllNotifs", "ListAllNotifications"};
        this.help = HelpConstants.SERVER_NOTIFS_COMMAND_HELP;
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

        final String[] commandExamples = {BotConstants.PREFIX + this.name};

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        final String serverId = commandEvent.getMessage().getGuild().getId();
        final ArrayList list = DatabaseDriver.getInstance().selectServerNotifsOrdered(serverId);

        String prevChannel = "";
        if (list.isEmpty()) {
            commandEvent.reactError();
            commandEvent.reply("There are no notifications in this server.");
            return;
        }

        StringBuilder description = new StringBuilder();
        for (Object doc : list) {
            Notification notif = new Gson().fromJson(doc.toString(), Notification.class);
            String channelLine = "\n<#%s>\n";
            String streamerLine = "· [%s](" + MixerConstants.HTTPS_MIXER_COM + "%s)\n";

            if (!prevChannel.equals(notif.getChannelId())) {
                description.append(String.format(channelLine, notif.getChannelId()));
            }
            prevChannel = notif.getChannelId();
            description.append(String.format(streamerLine, notif.getStreamerName(), notif.getStreamerName()));
        }

        if (list.size() == 1) {
            commandEvent.reply("There's only 1 notification in this server.");
            commandEvent.reply(new MixerEmbedBuilder()
                    .setTitle("Channel Notifications")
                    .setDescription(description)
                    .build());
        }

        if (list.size() > 1) {
            String message = "There's a total of " + list.size() + " notifications in this server.";
            commandEvent.reply(message);
            commandEvent.reply(new MixerEmbedBuilder()
                    .setTitle("Server Notifications")
                    .setDescription(description.toString())
                    .build());
        }
    }
}
