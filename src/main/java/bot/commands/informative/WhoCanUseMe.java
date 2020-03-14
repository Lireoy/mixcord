package bot.commands.informative;

import bot.constants.BotConstants;
import bot.constants.HelpConstants;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WhoCanUseMe extends Command {

    public WhoCanUseMe() {
        this.name = "WhoCanUseMe";
        this.help = HelpConstants.WHO_CAN_USE_ME_COMMAND_HELP;
        this.category = new Category(CommandCategory.INFORMATIVE.toString());
        this.guildOnly = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        final String[] commandExamples = {BotConstants.PREFIX + this.name};

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        List<Role> roleWithManageServer = new ArrayList<>();
        List<Role> roleToUse = new ArrayList<>();

        for (Role role : commandEvent.getGuild().getRoles()) {
            if (role.getPermissions().contains(Permission.MANAGE_SERVER) || role.getPermissions().contains(Permission.ADMINISTRATOR)) {
                roleWithManageServer.add(role);
            }

            if (role.getPermissions().contains(Permission.MESSAGE_WRITE)) {
                roleToUse.add(role);
            }
        }

        StringBuilder description = new StringBuilder();

        if (roleWithManageServer.size() < 1 && roleToUse.size() < 1) {
            commandEvent.reply("There are no roles which can use Mixcord.");
            return;
        }

        if (roleWithManageServer.size() > 0) {
            description.append("Roles which can manage notifications:\n\n");
            for (Role role : roleWithManageServer) {
                String simpleLine = "· <@&%s>\n";
                String advancedLine = "· <@&%s> (Managed by an integration)\n";

                if (role.isManaged()) {
                    description.append(String.format(advancedLine, role.getId()));
                } else {
                    description.append(String.format(simpleLine, role.getId()));
                }
            }
        }

        if (roleWithManageServer.size() > 0 && roleToUse.size() > 0)
            description.append("\n").append("=================================\n\n");

        if (roleToUse.size() > 0) {
            description.append("Roles which can use basic commands:\n\n");
            for (Role role : roleToUse) {
                String simpleLine = "· <@&%s>\n";
                String advancedLine = "· <@&%s> (Managed by an integration)\n";

                if (role.isManaged()) {
                    description.append(String.format(advancedLine, role.getId()));
                } else {
                    description.append(String.format(simpleLine, role.getId()));
                }
            }
        }

        commandEvent.reactSuccess();
        commandEvent.replyFormatted(description.toString());
    }
}
