package bot;

import bot.constants.DeveloperConstants;
import bot.services.ClientService;
import bot.services.NotifService;
import bot.services.ShardService;
import bot.utils.StringUtil;
import com.jagrosh.jdautilities.command.Command;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mixcord {

    public static void main(String[] args) {
        StringUtil.displayAscii();

        for (Command command : ClientService.getInstance().getCommands())
            log.info("Added command: {}", command.getName());

        int numberOfCommands = ClientService.getInstance().getCommands().size();
        log.info("Total number of commands available: {}", numberOfCommands);

        log.info("Bot prefix set to {}", ClientService.getInstance().getPrefix());
        log.info("Bot alternate prefix set to {}", ClientService.getInstance().getAltPrefix());

        log.info("Added owner: {}", ClientService.getInstance().getOwnerId());
        for (String coOwnerId : ClientService.getInstance().getCoOwnerIds())
            log.info("Added co-owner: {}", coOwnerId);

        ShardService.getInstance();

        // Starts the automatic notification system
        // If you delete this, then you have to start
        // the notifier service manually on every startup
        NotifService.getInstance();
        log.info("Notifier service was started.");
        log.info("Posting metrics to G:{} - C:{}", DeveloperConstants.METRICS_GUILD, DeveloperConstants.METRICS_CHANNEL);
    }
}