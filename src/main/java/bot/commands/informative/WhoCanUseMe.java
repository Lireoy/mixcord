package bot.commands.informative;

import bot.structure.CommandCategory;
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
        this.help = "Displays those roles which can use Mixcord.";
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
        description.append("Roles which can manage notifications:\n\n");
        for (Role role : roleWithManageServer) {
            description.append("· <@&").append(role.getId());
            if (role.isManaged()) {
                description.append("> (Managed by an integration)\n");
            } else {
                description.append(">\n");
            }
        }
        description.append("\n").append("=================================\n\n");
        description.append("Roles which can use basic commands:\n\n");

        for (Role role : roleToUse) {
            description.append("· <@&").append(role.getId());
            if (role.isManaged()) {
                description.append("> (Managed by an integration)\n");
            } else {
                description.append(">\n");
            }
        }


        commandEvent.reactSuccess();
        commandEvent.replyFormatted(description.toString());
    }
}
