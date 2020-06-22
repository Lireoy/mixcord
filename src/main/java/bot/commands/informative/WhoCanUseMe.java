package bot.commands.informative;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.structures.MixcordCommand;
import bot.utils.CommandUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WhoCanUseMe extends MixcordCommand {

    public WhoCanUseMe() {
        this.name = "WhoCanUseMe";
        this.help = Locale.WHO_CAN_USE_ME_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("INFORMATIVE"));
        this.guildOnly = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;

        List<Role> roleWithManageServer = new ArrayList<>();
        List<Role> roleToUse = new ArrayList<>();
        collectRoleInfo(commandEvent, roleWithManageServer, roleToUse);

        StringBuilder description = new StringBuilder();
        if (roleWithManageServer.size() < 1 && roleToUse.size() < 1) {
            commandEvent.reply(Locale.WHO_CAN_USE_ME_COMMAND_NO_ROLES);
            return;
        }

        formatRolesWithManageServer(roleWithManageServer, description);
        if (roleWithManageServer.size() > 0 && roleToUse.size() > 0) {
            description.append("\n").append("=================================\n\n");
        }
        formatRolesWithSimpleUse(roleToUse, description);

        commandEvent.reactSuccess();
        commandEvent.replyFormatted(description.toString());
    }

    private void formatRolesWithSimpleUse(List<Role> roleToUse, StringBuilder description) {
        if (roleToUse.size() > 0) {
            description.append(Locale.WHO_CAN_USE_ME_COMMAND_BASIC_COMMANDS);
            for (Role role : roleToUse) {
                if (role.isManaged()) {
                    description.append(String.format(Locale.WHO_CAN_USE_ME_COMMAND_ADVANCED_LINE, role.getId()));
                } else {
                    description.append(String.format(Locale.WHO_CAN_USE_ME_COMMAND_SIMPLE_LINE, role.getId()));
                }
            }
        }
    }

    private void formatRolesWithManageServer(List<Role> roleWithManageServer, StringBuilder description) {
        if (roleWithManageServer.size() > 0) {
            description.append(Locale.WHO_CAN_USE_ME_COMMAND_MANAGE_NOTIFS);
            for (Role role : roleWithManageServer) {
                if (role.isManaged()) {
                    description.append(String.format(Locale.WHO_CAN_USE_ME_COMMAND_ADVANCED_LINE, role.getId()));
                } else {
                    description.append(String.format(Locale.WHO_CAN_USE_ME_COMMAND_SIMPLE_LINE, role.getId()));
                }
            }
        }
    }

    private void collectRoleInfo(CommandEvent commandEvent, List<Role> roleWithManageServer, List<Role> roleToUse) {
        for (Role role : commandEvent.getGuild().getRoles()) {
            if (role.getPermissions().contains(Permission.MANAGE_SERVER) ||
                    role.getPermissions().contains(Permission.ADMINISTRATOR)) {
                roleWithManageServer.add(role);
            }

            if (role.getPermissions().contains(Permission.MESSAGE_WRITE)) {
                roleToUse.add(role);
            }
        }
    }
}
