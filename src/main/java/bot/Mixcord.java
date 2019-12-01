package bot;

import bot.commands.informative.*;
import bot.commands.misc.Shutdown;
import bot.commands.mixer.MixerUser;
import bot.commands.mixer.MixerUserSocials;
import bot.commands.notifications.*;
import bot.commands.notifierservice.NotifServiceStatus;
import bot.commands.notifierservice.StartNotifService;
import bot.commands.notifierservice.StopNotifService;
import bot.utils.NotifierService;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class Mixcord {

    /////////////////////////////////////////
    //                                     //
    // Load DotENV first or else the token //
    // and the database will get exception //
    //                                     //
    /////////////////////////////////////////
    private static Dotenv dotenv;
    private static CommandClient client;
    private static JDA jda;
    private static DatabaseDriver database;
    private static NotifierService notifierService;

    public static void main(String[] args) {

        try (FileReader fr = new FileReader("MixcordASCII.txt")) {
            int i;
            while ((i = fr.read()) != -1) {
                System.out.print((char) i);
            }
        } catch (IOException e) {
            log.info("Could not find ASCII art.");
        }

        if (new File(".env").exists()) {
            dotenv = Dotenv.load();
        } else {
            log.info("Could not find .env file.");
            log.info("Shutting down application...");
            System.exit(0);
        }


        String databaseIp = Objects.requireNonNull(dotenv.get("DB_IP"));
        int databasePort = Integer.parseInt(Objects.requireNonNull(dotenv.get("DB_PORT")));
        String databaseName = Objects.requireNonNull(dotenv.get("DB_NAME"));
        String databaseTable = Objects.requireNonNull(dotenv.get("DB_TABLE"));
        String databaseUser = Objects.requireNonNull(dotenv.get("DB_USER"));
        String databasePassword = Objects.requireNonNull(dotenv.get("DB_PASS"));
        String metricsGuildId = dotenv.get("METRICS_GUILD_ID");
        String metricsChannelId = dotenv.get("METRICS_CHANNEL_ID");
        //String mixerApiClientId = dotenv.get("MIXER_API_CLIENT_ID");

        database = new DatabaseDriver(databaseIp, databasePort,
                databaseName, databaseTable, databaseUser, databasePassword);

        if (Objects.requireNonNull(metricsGuildId).length() < 18 ||
                Objects.requireNonNull(metricsChannelId).length() < 18) {
            notifierService = new NotifierService(database, Constants.METRICS_GUILD, Constants.METRICS_CHANNEL);
            log.info("Notifier service was created with fallback for metrics.");
        } else {
            notifierService = new NotifierService(database);
            log.info("Custom notifier service was created for metrics.");
        }

        String DISCORD_BOT_TOKEN = dotenv.get("DISCORD_BOT_TOKEN");
        int NUMBER_OF_SHARDS = Integer.parseInt(Objects.requireNonNull(dotenv.get("NUMBER_OF_SHARDS")));

        CommandClientBuilder builder = new CommandClientBuilder();
        client = builder
                .setPrefix(Constants.PREFIX)
                .setAlternativePrefix(Constants.ALTERNATE_PREFIX)
                .setOwnerId(Constants.OWNER_ID)
                .setCoOwnerIds(Constants.CO_OWNER_ID)
                .setEmojis(Constants.SUCCESS, Constants.WARNING, Constants.ERROR)
                .addCommands(
                        // Informative
                        new Ping(),
                        new Info(),
                        new Invite(),
                        new RoleInfo(),
                        new ServerInfo(),
                        new Commands(),

                        // Notifications
                        new AddNotif(),
                        new DeleteNotif(),
                        new ChannelNotifs(),
                        new ServerNotifs(),
                        new NotifPreview(),
                        new NotifMessageEdit(),
                        new NotifColorEdit(),

                        // Notifier Service
                        new StartNotifService(),
                        new StopNotifService(),
                        new NotifServiceStatus(),

                        // Mixer
                        new MixerUser(),
                        new MixerUserSocials(),

                        // Misc
                        new Shutdown())
                .build();
        log.info("Bot prefix set to {}", Constants.PREFIX);
        log.info("Bot alternate prefix set to {}", Constants.ALTERNATE_PREFIX);
        log.info("Bot owner is {}", Constants.OWNER_ID);
        log.info("Bot co-owner is {}", Constants.CO_OWNER_ID);

        log.info("Total number of commands available: {}", client.getCommands().size());
        client.getCommands().forEach((command) -> log.info("Added command: {}", command.getName()));

        // Create client with token
        try {
            for (int i = 0; i < NUMBER_OF_SHARDS; i++) {
                JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT)
                        .setToken(DISCORD_BOT_TOKEN)
                        .useSharding(i, NUMBER_OF_SHARDS)
                        .setStatus(OnlineStatus.ONLINE)
                        .setActivity(Activity.playing("Type .help"))
                        .addEventListeners(client)
                        .setAutoReconnect(true);
                jda = jdaBuilder.build().awaitReady();

                log.info("Shard {} is ready!", i);

                // Discord has a rate limit of 5 seconds between separate identification
                if (NUMBER_OF_SHARDS > 1) {
                    Thread.sleep(5001);
                }
            }

            // Starts the automatic notification system
            // If you delete this, then you have to start
            // the notifier service manually on every startup
            //notifierService.run();
        } catch (LoginException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    public static Dotenv getDotenv() {
        return dotenv;
    }

    public static CommandClient getClient() {
        return client;
    }

    public static JDA getJda() {
        return jda;
    }

    public static DatabaseDriver getDatabase() {
        return database;
    }

    public static NotifierService getNotifierService() {
        return notifierService;
    }
}