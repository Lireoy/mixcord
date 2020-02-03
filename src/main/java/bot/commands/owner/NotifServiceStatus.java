package bot.commands.owner;

import bot.Mixcord;
import bot.structure.CommandCategory;
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
        boolean state = Mixcord.getNotifierService().getState();

        if (state) {
            commandEvent.reply("Notifier service is running. ✅");
            log.info("Notifier service is running.");
        } else {
            commandEvent.reply("The notifier service is not running. ❌");
            log.info("The notifier service is not running.");
        }
    }
}
