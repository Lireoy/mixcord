package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.HelpConstants;
import bot.services.NotifierThread;
import bot.services.ShardService;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class Shutdown extends Command {

    public Shutdown() {
        this.name = "Shutdown";
        this.help = HelpConstants.SHUTDOWN_HELP;
        this.category = new Category(CommandCategory.OWNER.toString());
        this.arguments = "<reason>";
        this.guildOnly = true;
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    /**
     * Shuts down the application with a specified reason.
     *
     * @param commandEvent the event which triggered this command
     */
    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);
        final String reason = commandEvent.getArgs();

        final String[] commandExamples = {BotConstants.PREFIX + this.name + " This is an example reason."};

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        if (reason.isEmpty()) {
            log.info("{} attempted to shutdown the bot, but failed because there was no reason provided.", commandAuthor);
            commandEvent.reply("You need to provide a reason.");
        } else {
            log.info("Command ran by {}. Reason: {}", commandAuthor, reason);
            NotifierThread.getInstance().stop();
            log.info("Notifier service was shut down due to system shutdown request...");

            ShardService.getInstance().shutdown();
            log.info("JDA instance was shutdown due to system shutdown request...");

            log.info("Shutting down application...");
            System.exit(0);
        }
    }
}
