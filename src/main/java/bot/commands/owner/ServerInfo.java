package bot.commands.owner;

import bot.structure.enums.CommandCategory;
import bot.structure.enums.ServerFeatures;
import bot.utils.HelpUtil;
import bot.utils.MixerEmbedBuilder;
import bot.utils.StringUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
public class ServerInfo extends Command {
    public ServerInfo() {
        this.name = "ServerInfo";
        this.help = "Shows information about the server.";
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

        boolean helpResponse = HelpUtil.getInstance().sendCommandHelp(this, commandEvent);
        if (helpResponse) return;

        //TODO: Request info about a specific server
        //TODO: Optional arg for server ID, and look it with JDA if the bot is in the server
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        final Guild guild = commandEvent.getGuild();
        User user = Objects.requireNonNull(guild.getOwner()).getUser();
        String title = "Information about `" + guild.getName() + "`:";

        final String owner = user.getName() + "#" + user.getDiscriminator();
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
        String features = StringUtil.replaceLastComma(stringBuilder.toString());

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

        String line = "%d Online\n%d Idle\n%d Do not Disturb\n%d Offline\nAltogether %d users and %d bots.";
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