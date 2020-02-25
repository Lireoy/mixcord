package bot.commands.owner;

import bot.constants.BasicConstants;
import bot.constants.HelpConstants;
import bot.services.WorkStatus;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class StopNotifService extends Command {

    public StopNotifService() {
        this.name = "StopNotifService";
        this.help = HelpConstants.STOP_NOTIF_SERVICE_HELP;
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
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);
        
        final String commandExample = BasicConstants.PREFIX + this.name;

        boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExample);
        if (helpResponse) return;

        WorkStatus.getInstance().markFinished();
        commandEvent.reactSuccess();
    }
}

