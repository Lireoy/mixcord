package bot.commands.owner;

import bot.structure.CommandCategory;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;

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
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);


        Guild guild = commandEvent.getGuild();
        String title = "Information about `" + guild.getName() + "`:";

        String owner = guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator();
        String id = guild.getId();
        String location = guild.getRegion().getEmoji() + " " + guild.getRegion().getName();
        String creation = guild.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME);


        long botCount = guild.getMembers().stream().filter(m -> m.getUser().isBot()).count();
        long userCount = guild.getMembers().size() - botCount;
        long onlineCount = guild.getMembers().stream().filter((u)
                -> (u.getOnlineStatus() == OnlineStatus.ONLINE)).count();
        long idleCount = guild.getMembers().stream().filter((u)
                -> (u.getOnlineStatus() == OnlineStatus.IDLE)).count();
        long dndCount = guild.getMembers().stream().filter((u)
                -> (u.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB)).count();
        long offlineCount = guild.getMembers().stream().filter((u)
                -> (u.getOnlineStatus() == OnlineStatus.OFFLINE)).count();

        StringBuilder membersString = new StringBuilder();
        membersString
                .append(onlineCount).append(" Online\n")
                .append(idleCount).append(" Idle\n")
                .append(dndCount).append(" Do not Disturb\n")
                .append(offlineCount).append(" Offline\nAltogether ")
                .append(userCount).append(" users and ").append(botCount).append(" bots.");


        // TODO: Embed, Server Features, Splash attachment, Footer

        String footer = commandEvent.getAuthor().getName() + "#" + commandEvent.getAuthor().getDiscriminator();
        String footerImg = commandEvent.getAuthor().getAvatarUrl();

        EmbedBuilder builder = new EmbedBuilder();


        commandEvent.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(guild.getOwner().getColor())
                        .setTitle(title)
                        .addField("ID", id, false)
                        .addField("Owner", owner, false)
                        .addField("Location", location, false)
                        .addField("Creation", creation, false)
                        .addField("Members", membersString.toString(), false)
                        .setFooter(footer, footerImg)
                        .build()
        ).queue();
    }
}