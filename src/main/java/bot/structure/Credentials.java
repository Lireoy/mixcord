package bot.structure;

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

    public boolean isProductionBuild() {
        return isProductionBuild;
    }

    public void setProductionBuild(boolean productionBuild) {
        isProductionBuild = productionBuild;
    }

    public String getDiscordBotToken() {
        return discordBotToken;
    }

    public void setDiscordBotToken(String discordBotToken) {
        this.discordBotToken = discordBotToken;
    }

    public String getDiscordBotTokenCanary() {
        return discordBotTokenCanary;
    }

    public void setDiscordBotTokenCanary(String discordBotTokenCanary) {
        this.discordBotTokenCanary = discordBotTokenCanary;
    }

    public String getMixerApiClientId() {
        return mixerApiClientId;
    }

    public void setMixerApiClientId(String mixerApiClientId) {
        this.mixerApiClientId = mixerApiClientId;
    }

    public String getMixerApiClientSecret() {
        return mixerApiClientSecret;
    }

    public void setMixerApiClientSecret(String mixerApiClientSecret) {
        this.mixerApiClientSecret = mixerApiClientSecret;
    }

    public String getDatabaseIp() {
        return databaseIp;
    }

    public void setDatabaseIp(String databaseIp) {
        this.databaseIp = databaseIp;
    }

    public int getDatabasePort() {
        return databasePort;
    }

    public void setDatabasePort(int databasePort) {
        this.databasePort = databasePort;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public int getNumberOfShards() {
        return numberOfShards;
    }

    public void setNumberOfShards(int numberOfShards) {
        this.numberOfShards = numberOfShards;
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
