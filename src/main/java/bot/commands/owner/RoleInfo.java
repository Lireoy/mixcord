package bot.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class RoleInfo extends Command {

    public RoleInfo() {
        this.name = "RoleInfo";
        this.help = "Shows information about a role.";
        this.category = new Category("owner");
        this.arguments = "<role>";
        this.guildOnly = true;
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        Role role;
        if (commandEvent.getArgs().isEmpty()) {
            commandEvent.replyError("Please provide the name of a role!");
            return;
        } else {
            List<Role> found = FinderUtil.findRoles(commandEvent.getArgs(), commandEvent.getGuild());
            if (found.isEmpty()) {
                commandEvent.replyError("I couldn't find the role you were looking for!");
                return;
            } else if (found.size() > 1) {
                StringBuilder reply;
                reply = new StringBuilder("Multiple roles found matching `" + commandEvent.getArgs() + "`:\n");
                for (Role item : found) {
                    reply.append(" - ").append(item.getName()).append(" (ID: `").append(item.getId()).append("`)\n");
                }
                commandEvent.replyWarning(reply.toString());
                return;
            } else {
                role = found.get(0);
            }
        }

        String title = "Information about `" + role.getName() + "`:";
        StringBuilder permissions = new StringBuilder();
        List<Member> list = role.isPublicRole() ? commandEvent.getGuild().getMembers() : commandEvent.getGuild().getMembersWithRoles(role);

        if (role.getPermissions().isEmpty()) {
            permissions.append("None");
        } else {
            permissions.append(role.getPermissions().stream().map(p -> "`, `" + p.getName()).reduce("", String::concat).substring(3)).append("`");
        }

        String footer = commandEvent.getAuthor().getName() + "#" + commandEvent.getAuthor().getDiscriminator();
        String footerImg = commandEvent.getAuthor().getAvatarUrl();

        commandEvent.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(role.getColor())
                        .setTitle(title)
                        .addField("ID", role.getId(), false)
                        .addField("Creation", role.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), false)
                        .addField("Color", (role.getColor() == null ? "000000" : Integer.toHexString(role.getColor().getRGB()).toUpperCase().substring(2)), false)
                        .addField("Postion", String.valueOf(role.getPosition()), false)
                        .addField("Mentionable", String.valueOf(role.isMentionable()), false)
                        .addField("Hoisted", String.valueOf(role.isHoisted()), false)
                        .addField("Members", String.valueOf(list.size()), false)
                        .addBlankField(false)
                        .addField("Permissions", permissions.toString(), false)
                        .setFooter(footer, footerImg)
                        .build()
        ).queue();
    }
}