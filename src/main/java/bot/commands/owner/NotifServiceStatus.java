package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.HelpConstants;
import bot.services.NotifService;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;

@Slf4j
public class NotifServiceStatus extends Command {

    public NotifServiceStatus() {
        this.name = "NotifServiceStatus";
        this.aliases = new String[]{"Status", "NotifierServiceStatus"};
        this.help = HelpConstants.NOTIF_SERVICE_STATUS_HELP;
        this.category = new Category(CommandCategory.OWNER.toString());
        this.guildOnly = true;
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final String state = NotifService.getInstance().getState();
        String message = "Notifier service state: " + state;

        final String[] commandExamples = {BotConstants.PREFIX + this.name};

        boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        commandEvent.reply(message);
        log.info(message);
    }
}
