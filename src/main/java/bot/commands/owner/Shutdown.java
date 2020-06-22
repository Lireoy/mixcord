package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.services.NotifierThread;
import bot.services.ShardService;
import bot.structures.MixcordCommand;
import bot.utils.CommandUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class Shutdown extends MixcordCommand {

    public Shutdown() {
        this.name = "Shutdown";
        this.help = Locale.SHUTDOWN_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("OWNER"));
        this.arguments = "<reason>";
        this.guildOnly = false;
        this.ownerCommand = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name + " This is an example reason."};
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
        if (CommandUtil.checkHelp(this, commandEvent)) return;

        User commandAuthor = commandEvent.getAuthor();
        final String reason = commandEvent.getArgs();
        if (reason.isEmpty()) {
            log.info("{} attempted to shutdown the bot, but failed because there was no reason provided.", commandAuthor);
            commandEvent.reply(Locale.SHUTDOWN_COMMAND_NO_REASON);
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
