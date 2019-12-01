package bot.commands.notifierservice;

import bot.Mixcord;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class StartNotifService extends Command {

    public StartNotifService() {
        this.name = "StartNotifService";
        this.help = "Starts the notifier service.";
        this.guildOnly = true;
        this.ownerCommand = true;
    }

    // This command start the notifier service manually
    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        if (Mixcord.getNotifierService().getState()) {
            commandEvent.reply("The notifier service is already running.");
            commandEvent.reactError();
        } else {
            Mixcord.getNotifierService().start();
            commandEvent.reactSuccess();
        }
    }
}

