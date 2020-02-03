package bot.commands.notifierservice;

import bot.Mixcord;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class StopNotifService extends Command {

    public StopNotifService() {
        this.name = "StopNotifService";
        this.help = "Stops the notifier service.";
        this.ownerCommand = true;
    }

    // This command stops the notifier service manually
    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        if(Mixcord.getNotifierService().getState()) {
            Mixcord.getNotifierService().stop();
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply("The notifier service is not running.");
            commandEvent.reactError();
        }
    }
}

