package bot.commands.owner;

import bot.services.NotifService;
import bot.structure.enums.CommandCategory;
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
        this.help = "Creates a new notification for a Mixer streamer in the channel where the command is used.";
        this.category = new Category(CommandCategory.OWNER.toString());
        this.arguments = "<streamer name>";
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

        boolean helpResponse = HelpUtil.getInstance().sendCommandHelp(this, commandEvent);
        if (helpResponse) return;

        commandEvent.reply(message);
        log.info(message);
    }
}
