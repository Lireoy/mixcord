package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.structures.MixcordCommand;
import bot.utils.CommandUtil;
import bot.utils.MixerEmbedBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class RoleInfo extends MixcordCommand {

    public RoleInfo() {
        this.name = "RoleInfo";
        this.help = Locale.ROLE_INFO_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("OWNER"));
        this.arguments = "<role>";
        this.guildOnly = true;
        this.ownerCommand = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name + " testers"};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;

        if (commandEvent.getArgs().trim().isEmpty()) {
            commandEvent.replyError(Locale.ROLE_INFO_COMMAND_NO_ROLE);
            return;
        }

        final List<Role> found = FinderUtil.findRoles(commandEvent.getArgs().trim(), commandEvent.getGuild());
        if (found.isEmpty()) {
            commandEvent.replyError(Locale.ROLE_INFO_COMMAND_NO_SUCH_ROLE);
            return;
        }

        if (found.size() > 1) {
            StringBuilder reply = new StringBuilder();
            reply.append(
                    String.format(
                            Locale.ROLE_INFO_COMMAND_MULTIPLE_ROLES_FOUND,
                            commandEvent.getArgs().trim()));

            for (Role item : found) {
                reply.append(
                        String.format(
                                Locale.ROLE_INFO_COMMAND_ROLE_LINE,
                                item.getName(),
                                item.getId()));
            }
            commandEvent.replyWarning(reply.toString());
            return;
        }

        final Role role = found.get(0);
        final String title = String.format(Locale.ROLE_INFO_COMMAND_REPLy_TITLE, role.getName());
        StringBuilder permissions = new StringBuilder();
        final List<Member> list;

        if (role.isPublicRole()) {
            list = commandEvent.getGuild().getMembers();
        } else {
            list = commandEvent.getGuild().getMembersWithRoles(role);
        }

        if (role.getPermissions().isEmpty()) {
            permissions.append(Locale.ROLE_INFO_COMMAND_NONE);
        } else {
            StringBuilder acc = new StringBuilder();
            for (Permission permission : role.getPermissions()) {
                acc.append(String.format(
                        Locale.ROLE_INFO_COMMAND_PERMISSION_LINE,
                        permission.getName()));
            }
            permissions.append(acc
                    .substring(3)).append("`");

            /* Old version
            permissions.append(role.getPermissions().stream().map(
                    p -> "`, `" + p.getName())
                    .reduce("", String::concat)
                    .substring(3)).append("`");
             */
        }

        commandEvent.reply(new MixerEmbedBuilder()
                .setColor(role.getColor())
                .setTitle(title)
                .addField(
                        Locale.ROLE_INFO_COMMAND_ID_TITLE,
                        role.getId(),
                        false)
                .addField(
                        Locale.ROLE_INFO_COMMAND_CREATION_TITLE,
                        role.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                        false)
                .addField(
                        Locale.ROLE_INFO_COMMAND_COLOR_TITLE,
                        (role.getColor() == null ? "000000" : Integer.toHexString(role.getColor().getRGB()).toUpperCase().substring(2)),
                        false)
                .addField(
                        Locale.ROLE_INFO_COMMAND_POSITION_TITLE,
                        String.valueOf(role.getPosition()),
                        false)
                .addField(
                        Locale.ROLE_INFO_COMMAND_MENTIONABLE_TITLE,
                        String.valueOf(role.isMentionable()),
                        false)
                .addField(
                        Locale.ROLE_INFO_COMMAND_HOISTED_TITLE,
                        String.valueOf(role.isHoisted()),
                        false)
                .addField(
                        Locale.ROLE_INFO_COMMAND_MEMBERS_TITLE,
                        String.valueOf(list.size()),
                        false)
                .addBlankField(false)
                .addField(
                        Locale.ROLE_INFO_COMMAND_PERMISSIONS_TITLE,
                        permissions.toString(),
                        false)
                .build());
    }
}