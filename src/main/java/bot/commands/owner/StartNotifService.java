package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.services.NotifierThread;
import bot.structures.MixcordCommand;
import bot.utils.CommandUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;

@Slf4j
public class StartNotifService extends MixcordCommand {

    public StartNotifService() {
        this.name = "StartNotifService";
        this.help = Locale.START_NOTIF_SERVICE_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("OWNER"));
        this.guildOnly = false;
        this.ownerCommand = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;
        NotifierThread.getInstance().start();
        commandEvent.reactSuccess();
    }
}