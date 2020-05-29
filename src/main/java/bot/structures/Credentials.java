package bot.structures;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class Credentials {

    private static final String credentialsFileName = "credentials.json";
    private static Credentials instance;
    private boolean isProductionBuild;
    private String discordBotToken;
    private String discordBotTokenCanary;
    private String mixerApiClientId;
    private String mixerApiClientSecret;
    private String databaseIp;
    private int databasePort;
    private String databaseUser;
    private String databasePassword;
    private int numberOfShards;
    private String ownerOne;
    private String metricsGuild;
    private String metricsChannel;

    public Credentials() {
    }

    public static Credentials getInstance() {
        if (instance == null) {
            File credentialsJson = new File(credentialsFileName);

            if (!credentialsJson.exists()) {
                log.info("Could not find 'credentials.json' file.");
                log.info("Shutting down application...");
                System.exit(0);
            }

            String text = null;
            try {
                text = new String(Files.readAllBytes(Paths.get(credentialsFileName)), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            instance = new Gson().fromJson(text, Credentials.class);
        }
        return instance;
    }

    public static String getCredentialsFileName() {
        return credentialsFileName;
    }

    public boolean isProductionBuild() {
        return isProductionBuild;
    }

    public String getDiscordBotToken() {
        return discordBotToken;
    }

    public String getDiscordBotTokenCanary() {
        return discordBotTokenCanary;
    }

    public String getMixerApiClientId() {
        return mixerApiClientId;
    }

    public String getMixerApiClientSecret() {
        return mixerApiClientSecret;
    }

    public String getDatabaseIp() {
        return databaseIp;
    }

    public int getDatabasePort() {
        return databasePort;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public int getNumberOfShards() {
        return numberOfShards;
    }

    public String getOwnerOne() {
        return ownerOne;
    }

    public String getMetricsGuild() {
        return metricsGuild;
    }

    public String getMetricsChannel() {
        return metricsChannel;
    }

    @Override
    public String toString() {
        return ("isProductionBuild: " + isProductionBuild + ", " +
                "discordBotToken: " + discordBotToken + ", " +
                "discordBotTokenCanary: " + discordBotTokenCanary + ", " +
                "mixerApiClientId: " + mixerApiClientId + ", " +
                "mixerApiClientSecret: " + mixerApiClientSecret + ", " +
                "databaseIp: " + databaseIp + ", " +
                "databasePort: " + databasePort + ", " +
                "databaseUser: " + databaseUser + ", " +
                "databasePassword: " + databasePassword + ", " +
                "numberOfShards: " + numberOfShards);
    }
}
