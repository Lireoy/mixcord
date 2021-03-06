package bot;

import bot.services.ClientService;
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

        ShardService.getInstance();
    }
}