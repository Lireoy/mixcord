package bot.commands.owner;

import bot.Mixcord;
import bot.structure.CommandCategory;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class StopNotifService extends Command {

    public StopNotifService() {
        this.name = "StopNotifService";
        this.help = "Stops the notifier service.";
        this.category = new Category(CommandCategory.OWNER.toString());
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
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

