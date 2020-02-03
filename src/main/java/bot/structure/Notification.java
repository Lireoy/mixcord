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

    public String getServerId() {
        return serverId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getStreamerId() {
        return streamerId;
    }

    public String getStreamerName() {
        return streamerName;
    }

    public boolean isEmbed() {
        return embed;
    }

    public String getEmbedColor() {
        return embedColor;
    }

    public String getMessage() {
        return message;
    }

    public int getStreamEndAction() {
        return streamEndAction;
    }

    public String getStreamEndMessage() {
        return streamEndMessage;
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
