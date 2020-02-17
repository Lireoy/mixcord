package bot;

import bot.services.ClientService;
import bot.services.ShardService;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class Mixcord {

    public static void main(String[] args) {
        displayAscii();

        ClientService.getClient().getCommands().forEach(
                (command) -> log.info("Added command: {}", command.getName()));
        log.info("Total number of commands available: {}", ClientService.getClient().getCommands().size());

        log.info("Bot prefix set to {}", ClientService.getClient().getPrefix());
        log.info("Bot alternate prefix set to {}", ClientService.getClient().getAltPrefix());

        log.info("Added owner: {}", ClientService.getClient().getOwnerId());
        Arrays.stream(ClientService.getClient().getCoOwnerIds()).forEach(
                coOwnerId -> log.info("Added co-owner: {}", coOwnerId));

        ShardService.manager();

        // Starts the automatic notification system
        // If you delete this, then you have to start
        // the notifier service manually on every startup
        //NotifServiceFactory.getNotifService().run();
        log.info("Notifier service was started.");
        log.info("Posting metrics to G:{} - C:{}", Constants.METRICS_GUILD, Constants.METRICS_CHANNEL);
    }

    private static void displayAscii() {
        try (FileReader fr = new FileReader("MixcordASCII.txt")) {
            int i;
            while ((i = fr.read()) != -1) {
                System.out.print((char) i);
            }
        } catch (IOException e) {
            log.warn("Could not find ASCII art.");
        }
    }
}