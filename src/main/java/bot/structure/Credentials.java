package bot.structure;

public class Credentials {

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
