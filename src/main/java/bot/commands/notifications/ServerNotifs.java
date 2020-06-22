package bot.commands.notifications;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.constants.MixerConstants;
import bot.database.DatabaseDriver;
import bot.structures.MixcordCommand;
import bot.structures.Notification;
import bot.utils.CommandUtil;
import bot.utils.MixerEmbedBuilder;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;

import java.util.ArrayList;

/**
 * Lists all notifications already set up in a specific Discord guild.
 */
@Slf4j
public class ServerNotifs extends MixcordCommand {

    public ServerNotifs() {
        this.name = "ServerNotifs";
        this.aliases = new String[]{"ListAllNotifs", "ListAllNotifications"};
        this.help = Locale.SERVER_NOTIFS_COMMAND_HELP;
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
        final ArrayList list = DatabaseDriver.getInstance().selectServerNotifsOrdered(serverId);

        String prevChannel = "";
        if (list.isEmpty()) {
            commandEvent.reactError();
            commandEvent.reply(Locale.SERVER_NOTIFS_COMMAND_NO_NOTIFICATIONS);
            return;
        }

        StringBuilder description = new StringBuilder();
        for (Object doc : list) {
            Notification notif = new Gson().fromJson(doc.toString(), Notification.class);

            if (!prevChannel.equals(notif.getChannelId())) {
                description.append(
                        String.format(
                                Locale.SERVER_NOTIFS_COMMAND_CHANNEL_LINE,
                                notif.getChannelId()));
            }
            prevChannel = notif.getChannelId();
            description.append(
                    String.format(
                            Locale.SERVER_NOTIFS_COMMAND_STREAMER_LINE,
                            notif.getStreamerName(),
                            MixerConstants.HTTPS_MIXER_COM,
                            notif.getStreamerName()));
        }

        if (list.size() == 1) {
            commandEvent.reply(Locale.SERVER_NOTIFS_COMMAND_ONLY_ONE);
            commandEvent.reply(new MixerEmbedBuilder()
                    .setTitle(Locale.SERVER_NOTIFS_COMMAND_SERVER_NOTIFS_TITLE)
                    .setDescription(description)
                    .build());
        }

        if (list.size() > 1) {
            commandEvent.reply(
                    String.format(
                            Locale.SERVER_NOTIFS_COMMAND_N_AMOUNT,
                            list.size()));
            commandEvent.reply(new MixerEmbedBuilder()
                    .setTitle(Locale.SERVER_NOTIFS_COMMAND_SERVER_NOTIFS_TITLE)
                    .setDescription(description.toString())
                    .build());
        }
    }
}
