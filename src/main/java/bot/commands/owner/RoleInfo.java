package bot.commands.owner;

import bot.structure.enums.CommandCategory;
import bot.utils.HelpUtil;
import bot.utils.MixerEmbedBuilder;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import lombok.extern.slf4j.Slf4j;
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
        this.category = new Category(CommandCategory.OWNER.toString());
        this.arguments = "<role>";
        this.guildOnly = true;
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        boolean helpResponse = HelpUtil.getInstance().sendCommandHelp(this, commandEvent);
        if (helpResponse) return;

        if (commandEvent.getArgs().isEmpty()) {
            commandEvent.replyError("Please provide the name of a role!");
            return;
        }

        final List<Role> found = FinderUtil.findRoles(commandEvent.getArgs(), commandEvent.getGuild());
        if (found.isEmpty()) {
            commandEvent.replyError("I couldn't find the role you were looking for!");
            return;
        }

        if (found.size() > 1) {
            StringBuilder reply = new StringBuilder();
            reply.append("Multiple roles found matching `").append(commandEvent.getArgs()).append("`:\n");
            for (Role item : found) {
                reply.append(" - ").append(item.getName()).append(" (ID: `").append(item.getId()).append("`)\n");
            }
            commandEvent.replyWarning(reply.toString());
            return;
        }

        final Role role = found.get(0);

        String title = "Information about `" + role.getName() + "`:";
        StringBuilder permissions = new StringBuilder();
        final List<Member> list = role.isPublicRole() ? commandEvent.getGuild().getMembers() : commandEvent.getGuild().getMembersWithRoles(role);

        if (role.getPermissions().isEmpty()) {
            permissions.append("None");
        } else {
            permissions.append(role.getPermissions().stream().map(p -> "`, `" + p.getName()).reduce("", String::concat).substring(3)).append("`");
        }

        commandEvent.reply(new MixerEmbedBuilder()
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
                .build());
    }
}