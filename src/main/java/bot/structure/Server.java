package bot.structure;

public class Server {

    private String id;
    private String serverId;
    private boolean whitelisted;
    private String prefix;

    public Server() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public boolean isWhitelisted() {
        return whitelisted;
    }

    public void setWhitelisted(boolean whitelisted) {
        this.whitelisted = whitelisted;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return ("id: " + id + ", " +
                "serverId: " + serverId + ", " +
                "whitelisted: " + whitelisted + ", " +
                "prefix: " + prefix);
    }
}
