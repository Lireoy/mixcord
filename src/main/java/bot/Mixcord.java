package bot;

import bot.factories.NotifServiceFactory;
import bot.services.ClientService;
import bot.services.ShardService;
import bot.utils.StringUtil;
import com.jagrosh.jdautilities.command.Command;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mixcord {

    public static void main(String[] args) {
        StringUtil.displayAscii();

        for (Command command : ClientService.getClient().getCommands())
            log.info("Added command: {}", command.getName());

        int numberOfCommands = ClientService.getClient().getCommands().size();
        log.info("Total number of commands available: {}", numberOfCommands);

        log.info("Bot prefix set to {}", ClientService.getClient().getPrefix());
        log.info("Bot alternate prefix set to {}", ClientService.getClient().getAltPrefix());

        log.info("Added owner: {}", ClientService.getClient().getOwnerId());
        for (String coOwnerId : ClientService.getClient().getCoOwnerIds())
            log.info("Added co-owner: {}", coOwnerId);

        ShardService.manager();

        // Starts the automatic notification system
        // If you delete this, then you have to start
        // the notifier service manually on every startup
        NotifServiceFactory.getNotifService().run();
        log.info("Notifier service was started.");
        log.info("Posting metrics to G:{} - C:{}", Constants.METRICS_GUILD, Constants.METRICS_CHANNEL);
    }
}