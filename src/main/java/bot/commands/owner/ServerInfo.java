package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.HelpConstants;
import bot.services.ShardService;
import bot.structures.enums.CommandCategory;
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
        this.help = HelpConstants.SERVER_INFO_COMMAND_HELP;
        this.category = new Category(CommandCategory.OWNER.toString());
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
            commandEvent.reply("Guild was null.");
            return;
        }

        final Member member = guild.getOwner();
        if (member == null) {
            commandEvent.reply("Owner was null.");
            return;
        }

        final String title = "Information about `" + guild.getName() + "`:";
        final String owner = member.getUser().getName() + "#" + member.getUser().getDiscriminator();
        final String id = guild.getId();
        final String location = guild.getRegion().getEmoji() + " " + guild.getRegion().getName();
        final String creation = guild.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME);

        StringBuilder stringBuilder = new StringBuilder();
        if (guild.getFeatures().size() < 1) {
            stringBuilder.append("None");
        } else {
            for (String feature : guild.getFeatures()) {
                for (ServerFeatures serverFeature : ServerFeatures.values()) {
                    if (feature.equalsIgnoreCase(serverFeature.name())) {
                        stringBuilder.append(serverFeature.getText()).append(", ");
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

        final String line = "%d Online\n%d Idle\n%d Do not Disturb\n%d Offline\nAltogether %d users and %d bots.";
        final String membersString = String.format(line, onlineCount, idleCount, dndCount, offlineCount, userCount, botCount);

        commandEvent.reply(new MixerEmbedBuilder()
                .setColor(guild.getOwner().getColor())
                .setTitle(title)
                .addField("ID", id, false)
                .addField("Owner", owner, false)
                .addField("Location", location, false)
                .addField("Creation", creation, false)
                .addField("Features", features, false)
                .addField("Members", membersString, false)
                .build());
    }
}