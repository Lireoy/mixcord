package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.services.NotifService;
import bot.structures.MixcordCommand;
import bot.utils.CommandUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;

@Slf4j
public class NotifServiceStatus extends MixcordCommand {

    public NotifServiceStatus() {
        this.name = "NotifServiceStatus";
        this.aliases = new String[]{"Status", "NotifierServiceStatus"};
        this.help = Locale.NOTIF_SERVICE_STATUS_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("OWNER"));
        this.guildOnly = false;
        this.ownerCommand = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name + " shroud"};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;

        if (NotifService.getInstance().isRunning()) {
            commandEvent.reply(Locale.NOTIF_SERVICE_STATUS_COMMAND_RUNNING);
            log.info(Locale.NOTIF_SERVICE_STATUS_COMMAND_RUNNING);
        } else {
            commandEvent.reply(Locale.NOTIF_SERVICE_STATUS_COMMAND_NOT_RUNNING);
            log.info(Locale.NOTIF_SERVICE_STATUS_COMMAND_NOT_RUNNING);
        }
    }
}
