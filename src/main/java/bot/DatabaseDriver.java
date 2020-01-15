package bot;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.ReqlExpr;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseDriver {

    private final RethinkDB rethink = RethinkDB.r; // RethinkDB instance for all operations
    private Connection connection; // connection link
    private Table notifications; // notifications table
    private Table streamers; // streamers table

    DatabaseDriver(String databaseIp, int databasePort, String databaseUser, String databasePassword) {
        this.connection = getRethink().connection().hostname(databaseIp).port(databasePort)
                .user(databaseUser, databasePassword).connect();
        this.notifications = getRethink().db("Mixcord").table("notifications");
        this.streamers = getRethink().db("Mixcord").table("streamers");
    }

    /**
     * Adds a streamer to the streamers table with the specified parameters.
     *
     * @param streamerName the name of the streamer
     * @param streamerId   the id of the streamer
     * @return true if the insertion is successful
     */
    public boolean addStreamer(String streamerName, String streamerId) {
        // Check if entry is in the database
        if (getStreamerDocId(streamerName, streamerId).equals("-1")) {
            // If not in database, insert the data.
            getStreamers().insert(getRethink().hashMap("streamerName", streamerName)
                    .with("streamerId", streamerId)
                    .with("isStreaming", false))
                    .run(getConnection());
            // Return successful database insertion.
            return true;
        } else {
            // The entry is in the database.
            return false;
        }
    }

    /**
     * Selects all streamers from the streamers table mapped to JSON.
     *
     * @return a {@link Cursor} with all the documents in the streamers table
     */
    public Cursor selectAllStreamers() {
        return getStreamers().map(ReqlExpr::toJson).run(getConnection());
    }

    /**
     * Selects all notifications from the notifications table
     * for the specified streamer mapped to JSON.
     *
     * @param streamerId the streamer ID to look for in the notifications
     * @return a {@link Cursor} with all the notifications for the streamer
     */
    public Cursor selectStreamerNotifs(String streamerId) {
        return getNotifications().filter(row -> row.g("streamerId").eq(streamerId))
                .map(ReqlExpr::toJson).run(getConnection());
    }

    /**
     * Selects one streamer from the streamers table with the specified parameters.
     *
     * @param streamerName the name of the streamer
     * @param streamerId   the id of the streamer
     * @return a {@link Cursor} with the selected streamer
     */
    public Cursor selectOneStreamer(String streamerName, String streamerId) {
        return getStreamers().filter(row -> row.g("streamerName").match("(?i)" + streamerName + "$")
                .and(row.g("streamerId").eq(streamerId)))
                .map(ReqlExpr::toJson).run(getConnection());
    }

    /**
     * Gets a streamer entry's document ID with the specified parameters.
     *
     * @param streamerName the name of the streamer
     * @param streamerId   the id of the streamer
     * @return a {@link String} containing the document ID
     */
    private String getStreamerDocId(String streamerName, String streamerId) {
        Cursor cursor = getStreamers().filter(row -> row.g("streamerName").eq(streamerName)
                .and(row.g("streamerId").eq(streamerId)))
                .map(ReqlExpr::toJson).run(getConnection());

        if (cursor.hasNext()) {
            JSONObject document = new JSONObject(cursor.next().toString());
            return document.getString("id");
        }
        cursor.close();
        return "-1";
    }

    /**
     * Deletes a document from the streamers table with the specified parameters.
     *
     * @param streamerName the name of the streamer
     * @param streamerId   the id of the streamer
     * @return true if deletion is successful
     */
    public boolean deleteStreamer(String streamerName, String streamerId) {
        String documentId = getStreamerDocId(streamerName, streamerId);
        if (documentId.equals("-1")) {
            return false;
        } else {
            deleteStreamer(documentId);
            return true;
        }
    }

    /**
     * Deletes the specified document from the streamers table.
     *
     * @param dbEntryId the ID of the document you want to delete
     */
    public void deleteStreamer(String dbEntryId) {
        getStreamers().get(dbEntryId).delete().run(getConnection());
    }

    /**
     * Adds a notification to the notifications table with the specified parameters.
     * Some rows are inserted with a default value.
     *
     * @param serverId     the server ID where the notification should be sent
     * @param channelId    the channel ID where the notification should be sent
     * @param streamerName the name of the streamer
     * @param streamerId   the id of the streamer
     * @return true if the insertion is successful
     */
    public boolean addNotif(String serverId, String channelId, String streamerName, String streamerId) {
        String defaultMsg = "<" + Constants.MIXER_COM + streamerName + "> is now live on Mixer!";
        String defaultEndMsg = streamerName + " finished streaming.";
        // Check if entry is in the database
        if (getNotifDocId(serverId, channelId, streamerId).equals("-1")) {
            // If not in database, insert the data.
            getNotifications().insert(getRethink().hashMap("serverId", serverId)
                    .with("channelId", channelId)
                    .with("streamerId", streamerId)
                    .with("streamerName", streamerName)
                    .with("embed", true)
                    .with("embedColor", "ffffff")
                    .with("message", defaultMsg)
                    .with("streamEndAction", "0")
                    .with("streamEndMessage", defaultEndMsg)
            ).run(getConnection());

            return true;
        } else {
            return false;
        }
    }

    /**
     * Selects all notifications from the streamers table mapped to JSON.
     *
     * @return a {@link Cursor} with all the documents in the notifications table
     */
    public Cursor selectAllNotifs() {
        return getNotifications().map(ReqlExpr::toJson).run(getConnection());
    }

    /**
     * Selects all notifications from the notifications table
     * with the specified server ID mapped to JSON.
     *
     * @param serverId the server ID to look for in the notifications
     * @return an {@link ArrayList} with all the notifications for the server
     */
    public ArrayList selectServerNotifs(String serverId) {
        return getNotifications().filter(getRethink().hashMap("serverId", serverId))
                .orderBy("channelId").map(ReqlExpr::toJson).run(getConnection());
    }

    /**
     * Selects all notifications from the notifications table
     * with the specified channel ID mapped to JSON.
     *
     * @param serverId the server ID to look for in the notifications
     * @return an {@link ArrayList} with all the notifications for the channel
     */
    public Cursor selectChannelNotifs(String serverId, String channelId) {
        return getNotifications().filter(getRethink().hashMap("serverId", serverId)
                .with("channelId", channelId)).map(ReqlExpr::toJson).run(getConnection());
    }

    /**
     * Selects one notification from the notification table
     * with the specified parameters mapped to JSON.
     *
     * @param serverId     the server ID to look for in the notifications
     * @param channelId    the channel ID to look for in the notifications
     * @param streamerName the name of the streamer
     * @return a {@link Cursor} with a single notification if found
     */
    public Cursor selectOneNotification(String serverId, String channelId, String streamerName) {
        // Retrieve all notification with name "William" (case insensitive).
        // .match("(?i)^william$")
        // (?i) is canse insestitive mode
        // ^ start of the string
        // $ end of the string
        return getNotifications().filter(row -> row.g("streamerName").match("(?i)" + streamerName + "$")
                .and(row.g("serverId").eq(serverId))
                .and(row.g("channelId").eq(channelId)))
                .map(ReqlExpr::toJson).run(getConnection());
    }

    /**
     * Updates the specified streamer's streaming status in the streamers table
     * with the specified parameter.
     *
     * @param documentId  the ID of the document you want to update
     * @param isStreaming the new value of the field
     */
    public void updateIsStreaming(String documentId, boolean isStreaming) {
        getStreamers().get(documentId).update(
                getRethink().hashMap("isStreaming", isStreaming)
        ).run(getConnection());
    }

    /**
     * Updates the specified notification's message in the notifications table
     * with the specified parameter.
     *
     * @param documentId the ID of the document you want to update
     * @param newMessage the new value of the field
     */
    public void updateMessage(String documentId, String newMessage) {
        getNotifications().get(documentId).update(
                getRethink().hashMap("message", newMessage)
        ).run(getConnection());
    }

    /**
     * Updates the specified notification's embed color in the notifications table
     * with the specified parameter.
     *
     * @param documentId the ID of the document you want to update
     * @param newColor   the new value of the field
     */
    public void updateColor(String documentId, String newColor) {
        getNotifications().get(documentId).update(
                getRethink().hashMap("embedColor", newColor)
        ).run(getConnection());
    }

    /**
     * Updates the specified notification's embed mode in the notifications table
     * with the specified parameter.
     *
     * @param documentId    the ID of the document you want to update
     * @param newEmbedValue set true for embed, false for non-embed
     */
    public void updateEmbed(String documentId, boolean newEmbedValue) {
        getNotifications().get(documentId).update(
                getRethink().hashMap("embed", newEmbedValue)
        ).run(getConnection());
    }

    /**
     * Deletes a document from the notification table with the specified parameters.
     *
     * @param serverId   the server ID to look for in the notifications
     * @param channelId  the channel ID to look for in the notifications
     * @param streamerId the id of the streamer
     * @return true if the deletion is successful
     */
    public boolean deleteNotif(String serverId, String channelId, String streamerId) {
        String documentId = getNotifDocId(serverId, channelId, streamerId);
        if (documentId == null) {
            //return "Could not delete entry, because it is not in the database.";
            return false;
        } else {
            deleteNotif(documentId);
            return true;
        }
    }

    /**
     * Deletes a specific document from the notifications table.
     *
     * @param dbEntryId the ID of the document you want to delete
     */
    public void deleteNotif(String dbEntryId) {
        getNotifications().get(dbEntryId).delete().run(getConnection());
    }

    /**
     * Retrieves a document's ID with the specified parameters.
     *
     * @param serverId   the server ID to look for in the notifications
     * @param channelId  the channel ID to look for in the notifications
     * @param streamerId the id of the streamer
     * @return the unique ID of the document if found, otherwise null
     */
    private String getNotifDocId(String serverId, String channelId, String streamerId) {
        Cursor cursor = getNotifications().filter(row -> row.g("serverId").eq(serverId)
                .and(row.g("channelId").eq(channelId))
                .and(row.g("streamerId").eq(streamerId)))
                .map(ReqlExpr::toJson).run(getConnection());

        if (cursor.hasNext()) {
            JSONObject document = new JSONObject(cursor.next().toString());
            return document.getString("id");
        }
        cursor.close();
        return null;
    }

    /**
     * @return the {@link RethinkDB} instance
     */
    private RethinkDB getRethink() {
        return this.rethink;
    }

    /**
     * @return the RethinkDB {@link Connection} instance
     */
    private Connection getConnection() {
        return this.connection;
    }

    /**
     * @return the path for the notifications {@link Table}
     */
    private Table getNotifications() {
        return this.notifications;
    }

    /**
     * @return the path for the streamers {@link Table}
     */
    private Table getStreamers() {
        return this.streamers;
    }
}
