package bot.commands.informative;

import bot.Constants;
import bot.Mixcord;
import bot.utils.EmbedSender;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

@Slf4j
public class Commands extends Command {

    public Commands() {
        this.name = "Commands";
        this.help = "Shows all the commands for the bot.";
        this.guildOnly = true;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        List<Command> commands = Mixcord.getClient().getCommands();

        StringBuilder description = new StringBuilder();

        description.append("**For specific command help, please use <insert whatever command here>**\n\n");
        if (commandEvent.getAuthor().getId().equals(Constants.OWNER_ID) ||
                commandEvent.getAuthor().getId().equals(Constants.CO_OWNER_ID)) {

            for (Command command : commands) {
                description.append("· **").append(command.getName()).append("**\n")
                        .append(command.getHelp())
                        .append("\n\n");
            }
        } else {
            for (Command command : commands) {
                if (!command.isOwnerCommand()) {

                    description.append("· **").append(command.getName()).append("**\n")
                            .append(command.getHelp())
                            .append("\n\n");
                }
            }
        }

        commandEvent.reply(
                new EmbedSender()
                        .setTitle("Commands")
                        .setDescription(description.toString())
                        .build());
    }
}
