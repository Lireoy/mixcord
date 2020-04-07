package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.MixerConstants;
import bot.database.DatabaseDriver;
import bot.services.ShardService;
import bot.structures.Notification;
import bot.structures.Streamer;
import bot.structures.enums.CommandCategory;
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
        this.category = new Category(CommandCategory.OWNER.toString());
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
            commandEvent.reply("Please provide some arguments.");
            return;
        }


        final String[] args = StringUtil.separateArgs(commandEvent.getArgs(), 3);

        if (args.length == 0) {
            commandEvent.reply("Please provide some arguments.");
            return;
        }

        // Server
        if (args.length == 1) {
            final String serverId = args[0].trim();

            if (serverId.isEmpty()) {
                commandEvent.reply("Empty serverId argument.");
                return;
            }

            if (!DatabaseDriver.getInstance().selectOneServer(serverId).hasNext()) {
                commandEvent.reply("I'm not in this server.");
                return;
            }

            final ArrayList list = DatabaseDriver.getInstance().selectServerNotifsOrdered(serverId);

            if (list.isEmpty()) {
                commandEvent.reactError();
                commandEvent.reply("There are no notifications in this server.");
                return;
            }

            String prevChannel = "";
            StringBuilder description = new StringBuilder();
            for (Object doc : list) {
                Notification notif = new Gson().fromJson(doc.toString(), Notification.class);

                String channelTemplate = "\n%s - <#%s>\n";
                String streamerTemplate = "· [%s](%s%s)\n";

                if (!prevChannel.equals(notif.getChannelId())) {
                    description.append(
                            String.format(
                                    channelTemplate,
                                    notif.getChannelId(),
                                    notif.getChannelId()));
                }

                prevChannel = notif.getChannelId();
                description.append(
                        String.format(
                                streamerTemplate,
                                notif.getStreamerName(),
                                MixerConstants.HTTPS_MIXER_COM,
                                notif.getStreamerName()));
            }

            String titleTemplate = "Notifications in G:%s";
            commandEvent.reply(new MixerEmbedBuilder()
                    .setTitle(String.format(titleTemplate, serverId))
                    .setDescription(description.toString())
                    .build());
            return;
        }

        // Server + Channel
        if (args.length == 2) {
            final String serverId = args[0].trim();
            final String channelId = args[1].trim();


            if (serverId.isEmpty()) {
                commandEvent.reply("Empty serverId argument.");
                return;
            }

            if (channelId.isEmpty()) {
                commandEvent.reply("Empty channelId argument.");
                return;
            }

            if (!DatabaseDriver.getInstance().selectOneServer(serverId).hasNext()) {
                commandEvent.reply("I'm not in this server.");
                return;
            }

            final ArrayList list = DatabaseDriver.getInstance().selectServerNotifsOrdered(serverId);

            if (list.isEmpty()) {
                commandEvent.reactError();
                commandEvent.reply("There are no notifications in this server.");
                return;
            }

            TextChannel textChannel = ShardService.getInstance().getTextChannelById(channelId);

            if (textChannel == null) {
                commandEvent.reactError();
                commandEvent.reply("There is no such channel.");
                return;
            }

            if (!textChannel.canTalk()) {
                commandEvent.reactError();
                commandEvent.reply("No talk power in that channel.");
                return;
            }

            StringBuilder description = new StringBuilder();
            final Cursor cursor = DatabaseDriver.getInstance().selectChannelNotifs(serverId, channelId);

            if (!cursor.hasNext()) {
                commandEvent.reactError();
                commandEvent.reply("There are no notifications in this channel.");
                return;
            }

            for (Object doc : cursor) {
                Streamer streamer = new Gson().fromJson(doc.toString(), Streamer.class);

                String streamerTemplate = "· [%s](%s%s)\n";

                description.append(
                        String.format(
                                streamerTemplate,
                                streamer.getStreamerName(),
                                MixerConstants.HTTPS_MIXER_COM,
                                streamer.getStreamerName()));
            }
            cursor.close();

            String titleTemplate = "Notifications in G:%s C:%s";
            commandEvent.reply(new MixerEmbedBuilder()
                    .setTitle(String.format(titleTemplate, serverId, channelId))
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
                commandEvent.reply("Empty serverId argument.");
                return;
            }

            if (channelId.isEmpty()) {
                commandEvent.reply("Empty channelId argument.");
                return;
            }

            if (streamerName.isEmpty()) {
                commandEvent.reply("Empty streamerName argument.");
                return;
            }

            if (!DatabaseDriver.getInstance().selectOneServer(serverId).hasNext()) {
                commandEvent.reply("I'm not in this server.");
                return;
            }

            final ArrayList list = DatabaseDriver.getInstance().selectServerNotifsOrdered(serverId);

            if (list.isEmpty()) {
                commandEvent.reactError();
                commandEvent.reply("There are no notifications in this server.");
                return;
            }

            TextChannel textChannel = ShardService.getInstance().getTextChannelById(channelId);

            if (textChannel == null) {
                commandEvent.reactError();
                commandEvent.reply("There is no such channel.");
                return;
            }

            if (!textChannel.canTalk()) {
                commandEvent.reactError();
                commandEvent.reply("No talk power in that channel.");
                return;
            }

            final Cursor cursor = DatabaseDriver.getInstance().selectOneNotification(serverId, channelId, streamerName);
            if (!cursor.hasNext()) {
                commandEvent.reply("There is no such notification in this channel");
                return;
            }

            String jsonReplyTemplate = "```json\n%s```";
            String reply = String.format(
                    jsonReplyTemplate,
                    new JSONObject(
                            cursor.next().toString()
                    ).toString(2));
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
