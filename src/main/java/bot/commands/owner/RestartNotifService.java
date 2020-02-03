package bot.commands.owner;

import bot.Mixcord;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class RestartNotifService extends Command {

    public RestartNotifService() {
        this.name = "RestartNotifService";
        this.aliases = new String[]{"RestartNotifierService"};
        this.help = "Restarts the notifier service.";
        this.category = new Category("owner");
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

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
