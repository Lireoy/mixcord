package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.constants.MixerConstants;
import bot.database.DatabaseDriver;
import bot.services.ShardService;
import bot.structures.Notification;
import bot.structures.Streamer;
import bot.utils.HelpUtil;
import bot.utils.MixerEmbedBuilder;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.util.ArrayList;

@Slf4j
public class Debug extends Command {

    public Debug() {
        this.name = "Debug";
        this.help = Locale.DEBUG_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("OWNER"));
        this.guildOnly = false;
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        final String[] commandExamples = {
                BotConstants.PREFIX + this.name + " <server ID>",
                BotConstants.PREFIX + this.name + " 348110542667251712",
                BotConstants.PREFIX + this.name + " <server ID>, <channel ID>",
                BotConstants.PREFIX + this.name + " 348110542667251712, 346474378466164736",
                BotConstants.PREFIX + this.name + " <server ID>, <channel ID>, <streamer name>",
                BotConstants.PREFIX + this.name + " 348110542667251712, 346474378466164736, shroud",
        };

        boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        if (commandEvent.getArgs().trim().isEmpty()) {
            commandEvent.reply(Locale.DEBUG_COMMAND_NO_ARGUMENTS);
            return;
        }


        final String[] args = StringUtil.separateArgs(commandEvent.getArgs(), 3);

        if (args.length == 0) {
            commandEvent.reply(Locale.DEBUG_COMMAND_NO_ARGUMENTS);
            return;
        }

        // Server
        if (args.length == 1) {
            final String serverId = args[0].trim();

            if (serverId.isEmpty()) {
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_SERVER_ID);
                return;
            }

            if (!DatabaseDriver.getInstance().selectOneServer(serverId).hasNext()) {
                commandEvent.reply(Locale.DEBUG_COMMAND_NOT_IN_SERVER);
                return;
            }

            final ArrayList list = DatabaseDriver.getInstance().selectServerNotifsOrdered(serverId);

            if (list.isEmpty()) {
                commandEvent.reactError();
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_NOTIFS_IN_SERVER);
                return;
            }

            String prevChannel = "";
            StringBuilder description = new StringBuilder();
            for (Object doc : list) {
                Notification notif = new Gson().fromJson(doc.toString(), Notification.class);

                if (!prevChannel.equals(notif.getChannelId())) {
                    description.append(
                            String.format(
                                    Locale.DEBUG_COMMAND_CHANNEL_LINE,
                                    notif.getChannelId(),
                                    notif.getChannelId()));
                }

                prevChannel = notif.getChannelId();
                description.append(
                        String.format(
                                Locale.DEBUG_COMMAND_STREAMER_LINE,
                                notif.getStreamerName(),
                                MixerConstants.HTTPS_MIXER_COM,
                                notif.getStreamerName()));
            }

            commandEvent.reply(new MixerEmbedBuilder()
                    .setTitle(String.format(Locale.DEBUG_COMMAND_SERVER_NOTIFS_TITLE, serverId))
                    .setDescription(description.toString())
                    .build());
            return;
        }

        // Server + Channel
        if (args.length == 2) {
            final String serverId = args[0].trim();
            final String channelId = args[1].trim();


            if (serverId.isEmpty()) {
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_SERVER_ID);
                return;
            }

            if (channelId.isEmpty()) {
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_CHANNEL_ID);
                return;
            }

            if (!DatabaseDriver.getInstance().selectOneServer(serverId).hasNext()) {
                commandEvent.reply(Locale.DEBUG_COMMAND_NOT_IN_SERVER);
                return;
            }

            final ArrayList list = DatabaseDriver.getInstance().selectServerNotifsOrdered(serverId);

            if (list.isEmpty()) {
                commandEvent.reactError();
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_NOTIFS_IN_SERVER);
                return;
            }

            TextChannel textChannel = ShardService.getInstance().getTextChannelById(channelId);

            if (textChannel == null) {
                commandEvent.reactError();
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_SUCH_CHANNEL);
                return;
            }

            if (!textChannel.canTalk()) {
                commandEvent.reactError();
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_TALK_POWER);
                return;
            }

            StringBuilder description = new StringBuilder();
            final Cursor cursor = DatabaseDriver.getInstance().selectChannelNotifs(serverId, channelId);

            if (!cursor.hasNext()) {
                commandEvent.reactError();
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_NOTIFS_IN_CHANNEL);
                return;
            }

            for (Object doc : cursor) {
                Streamer streamer = new Gson().fromJson(doc.toString(), Streamer.class);

                description.append(
                        String.format(
                                Locale.DEBUG_COMMAND_STREAMER_LINE,
                                streamer.getStreamerName(),
                                MixerConstants.HTTPS_MIXER_COM,
                                streamer.getStreamerName()));
            }
            cursor.close();


            commandEvent.reply(new MixerEmbedBuilder()
                    .setTitle(String.format(Locale.DEBUG_COMMAND_CHANNEL_NOTIFS_TITLE, serverId, channelId))
                    .setDescription(description)
                    .build());
            return;
        }

        // Server + Channel + Notification
        if (args.length == 3) {
            final String serverId = args[0].trim();
            final String channelId = args[1].trim();
            final String streamerName = args[2].trim();

            if (serverId.isEmpty()) {
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_SERVER_ID);
                return;
            }

            if (channelId.isEmpty()) {
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_CHANNEL_ID);
                return;
            }

            if (streamerName.isEmpty()) {
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_STREAMER_NAME);
                return;
            }

            if (!DatabaseDriver.getInstance().selectOneServer(serverId).hasNext()) {
                commandEvent.reply(Locale.DEBUG_COMMAND_NOT_IN_SERVER);
                return;
            }

            final ArrayList list = DatabaseDriver.getInstance().selectServerNotifsOrdered(serverId);

            if (list.isEmpty()) {
                commandEvent.reactError();
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_NOTIFS_IN_SERVER);
                return;
            }

            TextChannel textChannel = ShardService.getInstance().getTextChannelById(channelId);

            if (textChannel == null) {
                commandEvent.reactError();
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_SUCH_CHANNEL);
                return;
            }

            if (!textChannel.canTalk()) {
                commandEvent.reactError();
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_TALK_POWER);
                return;
            }

            final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, streamerName);
            if (!cursor.hasNext()) {
                commandEvent.reply(Locale.DEBUG_COMMAND_NO_SUCH_STREAMER);
                return;
            }

            String jsonReplyTemplate = "```json\n%s```";
            String reply = String.format(
                    jsonReplyTemplate,
                    new JSONObject(cursor.next().toString())
                            .toString(2));
            cursor.close();
            commandEvent.reply(reply);

            Guild guild = ShardService.getInstance().getGuildById(serverId);
            if (guild != null) {

                StringBuilder stringBuilder = new StringBuilder("{\n");
                for (Permission perm : guild.getSelfMember().getPermissions()) {
                    stringBuilder.append("  \"").append(perm.name()).append("\", \n");
                }
                stringBuilder.append("}");
                String output = StringUtil.replaceLastComma(stringBuilder.toString());

                commandEvent.reply("```json\n" + output + "```");
            }
        }

        commandEvent.reply("Heha");
    }
}
