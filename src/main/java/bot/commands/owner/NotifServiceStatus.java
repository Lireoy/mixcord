package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.services.NotifService;
import bot.utils.CommandUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class NotifServiceStatus extends Command {

    public NotifServiceStatus() {
        this.name = "NotifServiceStatus";
        this.aliases = new String[]{"Status", "NotifierServiceStatus"};
        this.help = Locale.NOTIF_SERVICE_STATUS_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("OWNER"));
        this.guildOnly = false;
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

        final String[] commandExamples = {BotConstants.PREFIX + this.name + " shroud"};

        boolean helpResponse = CommandUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        if (NotifService.getInstance().isRunning()) {
            commandEvent.reply(Locale.NOTIF_SERVICE_STATUS_COMMAND_RUNNING);
            log.info(Locale.NOTIF_SERVICE_STATUS_COMMAND_RUNNING);
        } else {
            commandEvent.reply(Locale.NOTIF_SERVICE_STATUS_COMMAND_NOT_RUNNING);
            log.info(Locale.NOTIF_SERVICE_STATUS_COMMAND_NOT_RUNNING);
        }
    }
}
