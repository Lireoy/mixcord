package bot.structure;

public class Streamer {

    private String id;
    private String streamerName;
    private String streamerId;
    private boolean isStreaming;

    public Streamer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreamerName() {
        return streamerName;
    }

    public void setStreamerName(String streamerName) {
        this.streamerName = streamerName;
    }

    public String getStreamerId() {
        return streamerId;
    }

    public void setStreamerId(String streamerId) {
        this.streamerId = streamerId;
    }

    public boolean isStreaming() {
        return isStreaming;
    }

    public void setStreaming(boolean streaming) {
        isStreaming = streaming;
    }

    @Override
    public String toString() {
        return ("id: " + id + ", " +
                "streamerName: " + streamerName + ", " +
                "streamerId: " + streamerId + ", " +
                "isStreaming: " + isStreaming);
    }
}
