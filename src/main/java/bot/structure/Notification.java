package bot.structure;

public class Notification {

    private String id;
    private String serverId;
    private String channelId;
    private String streamerId;
    private String streamerName;
    private boolean embed;
    private String embedColor;
    private String message;
    private int streamEndAction;
    private String streamEndMessage;

    public Notification() {
    }

    public Notification(String id, String serverId, String channelId, String streamerId, String streamerName, boolean embed, String embedColor, String message, int streamEndAction, String streamEndMessage) {
        this.id = id;
        this.serverId = serverId;
        this.channelId = channelId;
        this.streamerId = streamerId;
        this.streamerName = streamerName;
        this.embed = embed;
        this.embedColor = embedColor;
        this.message = message;
        this.streamEndAction = streamEndAction;
        this.streamEndMessage = streamEndMessage;
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

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getStreamerId() {
        return streamerId;
    }

    public void setStreamerId(String streamerId) {
        this.streamerId = streamerId;
    }

    public String getStreamerName() {
        return streamerName;
    }

    public void setStreamerName(String streamerName) {
        this.streamerName = streamerName;
    }

    public boolean isEmbed() {
        return embed;
    }

    public void setEmbed(boolean embed) {
        this.embed = embed;
    }

    public String getEmbedColor() {
        return embedColor;
    }

    public void setEmbedColor(String embedColor) {
        this.embedColor = embedColor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStreamEndAction() {
        return streamEndAction;
    }

    public void setStreamEndAction(int streamEndAction) {
        this.streamEndAction = streamEndAction;
    }

    public String getStreamEndMessage() {
        return streamEndMessage;
    }

    public void setStreamEndMessage(String streamEndMessage) {
        this.streamEndMessage = streamEndMessage;
    }

    @Override
    public String toString() {
        return ("id: " + id + ", " +
                "serverId: " + serverId + ", " +
                "channelId: " + channelId + ", " +
                "streamerId " + streamerId + ", " +
                "streamerName: " + streamerName + ", " +
                "embed: " + embed + ", " +
                "embedColor: " + embedColor + ", " +
                "message: " + message + ", " +
                "streamEndAction: " + streamEndAction + ", " +
                "streamEndMessage: " + streamEndMessage);
    }
}
