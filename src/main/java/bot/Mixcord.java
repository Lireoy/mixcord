package bot;

import bot.commands.informative.*;
import bot.commands.owner.Shutdown;
import bot.commands.owner.Whitelist;
import bot.commands.mixer.MixerUser;
import bot.commands.mixer.MixerUserSocials;
import bot.commands.notifications.*;
import bot.commands.owner.NotifServiceStatus;
import bot.commands.owner.RestartNotifService;
import bot.commands.owner.StartNotifService;
import bot.commands.owner.StopNotifService;
import bot.commands.owner.RoleInfo;
import bot.commands.owner.ServerInfo;
import bot.structure.Credentials;
import bot.utils.NotifService;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
public class Mixcord {

    private static Credentials credentials;
    private static CommandClient client;
    private static JDA jda;
    private static DatabaseDriver database;
    private static NotifService notifierService;
    private static boolean notifierServiceStateArchive;

    public static void main(String[] args) {
        displayAscii();
        loadCredentials();

        String BOT_TOKEN = credentials.isProductionBuild() ?
                credentials.getDiscordBotToken() : credentials.getDiscordBotTokenCanary();

        DatabaseConnectionBuilder connectionBuilder = new DatabaseConnectionBuilder()
                .setDatabaseIp(credentials.getDatabaseIp())
                .setDatabasePort(credentials.getDatabasePort())
                .setDatabaseUser(credentials.getDatabaseUser())
                .setDatabasePassword(credentials.getDatabasePassword());

        database = new DatabaseDriver().setConnection(connectionBuilder.build());
        notifierService = new NotifService(database);
        log.info("Notifier service was started.");
        log.info("Posting metrics to G:{} - C:{}", Constants.METRICS_GUILD, Constants.METRICS_CHANNEL);

        CommandClientBuilder builder = new CommandClientBuilder();
        client = builder
                .setPrefix(Constants.PREFIX)
                .setAlternativePrefix(Constants.ALTERNATE_PREFIX)
                .setOwnerId(Constants.OWNER_ID)
                .setCoOwnerIds(Constants.CO_OWNER_ID, Constants.CO_OWNER_ID2)
                .setEmojis(Constants.SUCCESS, Constants.WARNING, Constants.ERROR)
                .addCommands(
                        // Informative
                        new Ping(),
                        new Info(),
                        new Invite(),

                        // Notifications
                        new AddNotif(),
                        new DeleteNotif(),
                        new ChannelNotifs(),
                        new ServerNotifs(),
                        new MakeDefault(),
                        new NotifPreview(),
                        new NotifMessageEdit(),
                        new NotifColorEdit(),
                        new NotifEmbedConfig(),

                        // Mixer
                        new MixerUser(),
                        new MixerUserSocials(),

                        // Owner
                        new Whitelist(),
                        new NotifServiceStatus(),
                        new StartNotifService(),
                        new StopNotifService(),
                        new RestartNotifService(),
                        new RoleInfo(),
                        new ServerInfo(),
                        new Shutdown())
                .build();
        client.getCommands().forEach((command) -> log.info("Added command: {}", command.getName()));
        log.info("Total number of commands available: {}", client.getCommands().size());

        log.info("Bot prefix set to {}", client.getPrefix());
        log.info("Bot alternate prefix set to {}", client.getAltPrefix());

        log.info("Added owner: {}", client.getOwnerId());
        Arrays.stream(client.getCoOwnerIds()).forEach(coOwnerId -> log.info("Added co-owner: {}", coOwnerId));
        EventHandler eventHandler = new EventHandler();

        try {
            for (int i = 0; i < credentials.getNumberOfShards(); i++) {
                JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT)
                        .setToken(BOT_TOKEN)
                        .useSharding(i, credentials.getNumberOfShards())
                        .setStatus(OnlineStatus.ONLINE)
                        .setActivity(Activity.playing("Type .help"))
                        .addEventListeners(client)
                        .addEventListeners(eventHandler)
                        .setAutoReconnect(true);
                jda = jdaBuilder.build().awaitReady();

                log.info("Shard {} is ready!", i);

                // Discord has a rate limit of 5 seconds between separate identification
                if (credentials.getNumberOfShards() > 1) {
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

    private static void loadCredentials() {
        if (new File("credentials.json").exists()) {
            try {
                String text = new String(Files.readAllBytes(Paths.get("credentials.json")), StandardCharsets.UTF_8);
                credentials = new Gson().fromJson(new JSONObject(text).toString(), Credentials.class);
            } catch (IOException e) {
                log.error("Failed to read 'credentials.json'.");
            }
        } else {
            log.error("Could not find 'credentials.json' file.");
            log.info("Shutting down application...");
            System.exit(0);
        }
    }

    public static Credentials getCredentials() {
        return credentials;
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

    public static NotifService getNotifierService() {
        return notifierService;
    }

    public static boolean getNotifierServiceStateArchive() {
        return notifierServiceStateArchive;
    }

    public static void setNotifierServiceStateArchive(boolean notifierServiceStateArchive) {
        Mixcord.notifierServiceStateArchive = notifierServiceStateArchive;
    }
}