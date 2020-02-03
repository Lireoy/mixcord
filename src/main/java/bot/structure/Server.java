package bot.structure;

public class Server {

    private String id;
    private String serverId;
    private boolean whitelisted;
    private String prefix;

    public Server() {
    }

    public Server(String id, String serverId, boolean whitelisted, String prefix) {
        this.id = id;
        this.serverId = serverId;
        this.whitelisted = whitelisted;
        this.prefix = prefix;
    }

    public String getId() {
        return id;
    }

    public String getServerId() {
        return serverId;
    }

    public boolean isWhitelisted() {
        return whitelisted;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return ("id: " + id + ", " +
                "serverId: " + serverId + ", " +
                "whitelisted: " + whitelisted + ", " +
                "prefix: " + prefix);
    }
}
