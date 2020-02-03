package bot.structure;

public class Streamer {

    private String id;
    private String streamerName;
    private String streamerId;
    private boolean isStreaming;

    public Streamer() {
    }

    public Streamer(String id, String streamerName, String streamerId, boolean isStreaming) {
        this.id = id;
        this.streamerName = streamerName;
        this.streamerId = streamerId;
        this.isStreaming = isStreaming;
    }

    public String getId() {
        return id;
    }

    public String getStreamerName() {
        return streamerName;
    }

    public String getStreamerId() {
        return streamerId;
    }

    public boolean isStreaming() {
        return isStreaming;
    }

    @Override
    public String toString() {
        return ("id: " + id + ", " +
                "streamerName: " + streamerName + ", " +
                "streamerId: " + streamerId + ", " +
                "isStreaming: " + isStreaming);
    }
}
