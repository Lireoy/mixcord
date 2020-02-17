package bot.commands.owner;

import bot.factories.NotifServiceFactory;
import bot.services.ShardService;
import bot.structure.enums.CommandCategory;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class Shutdown extends Command {

    public Shutdown() {
        this.name = "Shutdown";
        this.help = "Kills the bot. You know what shutdown means, don't ya? Cool. Be aware.";
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
        final String reason = commandEvent.getArgs();

        if (reason.isEmpty()) {
            log.info("{} attempted to shutdown the bot, but failed because there was no reason provided.", commandAuthor);
            commandEvent.reply("You need to provide a reason.");
        } else {
            log.info("Command ran by {}. Reason: {}", commandAuthor, reason);
            NotifServiceFactory.getNotifService().stop();
            log.info("Notifier service was shut down due to system shutdown request...");

            ShardService.manager().shutdown();
            log.info("JDA instance was shutdown due to system shutdown request...");

            log.info("Shutting down application...");
            System.exit(0);
        }
    }
}
