package bot.commands.owner;

import bot.factories.NotifServiceFactory;
import bot.structure.CommandCategory;
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
        this.category = new Category(CommandCategory.OWNER.toString());
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        NotifServiceFactory.getNotifService().stop();
        NotifServiceFactory.getNotifService().start();
        commandEvent.reactSuccess();
    }
}
