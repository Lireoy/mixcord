package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.services.ShardService;
import bot.structures.enums.ServerFeatures;
import bot.utils.HelpUtil;
import bot.utils.MixerEmbedBuilder;
import bot.utils.StringUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;

@Slf4j
public class ServerInfo extends Command {

    public ServerInfo() {
        this.name = "ServerInfo";
        this.help = Locale.SERVER_INFO_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("OWNER"));
        this.guildOnly = true;
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        final String[] commandExamples = {
                BotConstants.PREFIX + this.name,
                BotConstants.PREFIX + this.name + " 348110542667251712"};

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        Guild guild = commandEvent.getGuild();
        if (!commandEvent.getArgs().trim().isEmpty()) {
            guild = ShardService.getInstance().getGuildById(commandEvent.getArgs().trim());
        }

        if (guild == null) {
            commandEvent.reply(Locale.SERVER_INFO_COMMAND_GUILD_NULL);
            return;
        }

        final Member member = guild.getOwner();
        if (member == null) {
            commandEvent.reply(Locale.SERVER_INFO_COMMAND_OWNER_NULL);
            return;
        }

        final String title = String.format(
                Locale.SERVER_INFO_COMMAND_TITLE,
                guild.getName());
        final String owner = String.format(
                Locale.SERVER_INFO_COMMAND_OWNER_LINE,
                member.getUser().getName(),
                member.getUser().getDiscriminator());
        final String id = guild.getId();
        final String location = String.format(
                Locale.SERVER_INFO_COMMAND_LOCATION,
                guild.getRegion().getEmoji(),
                guild.getRegion().getName());
        final String creation = guild.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME);

        StringBuilder stringBuilder = new StringBuilder();
        if (guild.getFeatures().size() < 1) {
            stringBuilder.append(Locale.SERVER_INFO_COMMAND_FEATURES_NONE);
        } else {
            for (String feature : guild.getFeatures()) {
                for (ServerFeatures serverFeature : ServerFeatures.values()) {
                    if (feature.equalsIgnoreCase(serverFeature.name())) {
                        stringBuilder.append(
                                String.format(
                                        Locale.SERVER_INFO_COMMAND_FEATURE_LINE,
                                        serverFeature.getText()));
                    }
                }
            }
        }
        final String features = StringUtil.replaceLastComma(stringBuilder.toString());

        log.info("Features size {}", guild.getFeatures().size());
        for (String feature : guild.getFeatures()) {
            log.info("feature: {}", feature);
        }


        final long botCount = guild.getMembers().stream().filter(m -> m.getUser().isBot()).count();
        final long userCount = guild.getMembers().size() - botCount;
        final long onlineCount = guild.getMembers().stream()
                .filter((u) -> (u.getOnlineStatus() == OnlineStatus.ONLINE)).count();
        final long idleCount = guild.getMembers().stream()
                .filter((u) -> (u.getOnlineStatus() == OnlineStatus.IDLE)).count();
        final long dndCount = guild.getMembers().stream()
                .filter((u) -> (u.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB)).count();
        final long offlineCount = guild.getMembers().stream()
                .filter((u) -> (u.getOnlineStatus() == OnlineStatus.OFFLINE)).count();

        final String membersString = String.format(
                Locale.SERVER_INFO_COMMAND_SUMMARY,
                onlineCount,
                idleCount,
                dndCount,
                offlineCount,
                userCount,
                botCount);

        commandEvent.reply(new MixerEmbedBuilder()
                .setColor(guild.getOwner().getColor())
                .setTitle(title)
                .addField(
                        Locale.SERVER_INFO_COMMAND_ID_TITLE,
                        id,
                        false)
                .addField(
                        Locale.SERVER_INFO_COMMAND_OWNER_TITLE,
                        owner,
                        false)
                .addField(
                        Locale.SERVER_INFO_COMMAND_LOCATION_TITLE,
                        location,
                        false)
                .addField(
                        Locale.SERVER_INFO_COMMAND_CREATION_TITLE,
                        creation,
                        false)
                .addField(
                        Locale.SERVER_INFO_COMMAND_FEATURES_TITLE,
                        features,
                        false)
                .addField(
                        Locale.SERVER_INFO_COMMAND_MEMBERS_TITLE,
                        membersString,
                        false)
                .build());
    }
}