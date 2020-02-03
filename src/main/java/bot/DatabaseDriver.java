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
    private Table guilds; // servers table

    DatabaseDriver() {
        this.notifications = rethink.db("Mixcord").table("notifications");
        this.streamers = rethink.db("Mixcord").table("streamers");
        this.guilds = rethink.db("Mixcord").table("guilds");
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
            streamers.insert(rethink.hashMap("streamerName", streamerName)
                    .with("streamerId", streamerId)
                    .with("isStreaming", false))
                    .run(connection);
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
        return streamers.map(ReqlExpr::toJson).run(connection);
    }

    /**
     * Selects all notifications from the notifications table
     * for the specified streamer mapped to JSON.
     *
     * @param streamerId the streamer ID to look for in the notifications
     * @return a {@link Cursor} with all the notifications for the streamer
     */
    public Cursor selectStreamerNotifs(String streamerId) {
        return notifications.filter(row -> row.g("streamerId").eq(streamerId))
                .map(ReqlExpr::toJson).run(connection);
    }

    /**
     * Selects one streamer from the streamers table with the specified parameters.
     *
     * @param streamerName the name of the streamer
     * @param streamerId   the id of the streamer
     * @return a {@link Cursor} with the selected streamer
     */
    public Cursor selectOneStreamer(String streamerName, String streamerId) {
        return streamers.filter(row -> row.g("streamerName").match("(?i)" + streamerName + "$")
                .and(row.g("streamerId").eq(streamerId)))
                .map(ReqlExpr::toJson).run(connection);
    }

    /**
     * Gets a streamer entry's document ID with the specified parameters.
     *
     * @param streamerName the name of the streamer
     * @param streamerId   the id of the streamer
     * @return a {@link String} containing the document ID
     */
    private String getStreamerDocId(String streamerName, String streamerId) {
        Cursor cursor = streamers.filter(row -> row.g("streamerName").eq(streamerName)
                .and(row.g("streamerId").eq(streamerId)))
                .map(ReqlExpr::toJson).run(connection);

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
        streamers.get(dbEntryId).delete().run(connection);
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
        //String defaultMsg = "<" + Constants.MIXER_COM + streamerName + "> is now live on Mixer!";
        String defaultMsg = String.format(Constants.NOTIF_MESSAGE_DEFAULT, streamerName);
        String defaultEndMsg = String.format(Constants.NOTIF_END_MESSAGE_DEFAULT, streamerName);
        // Check if entry is in the database
        if (getNotifDocId(serverId, channelId, streamerId) == null) {
            // If not in database, insert the data.
            notifications.insert(rethink.hashMap("serverId", serverId)
                    .with("channelId", channelId)
                    .with("streamerId", streamerId)
                    .with("streamerName", streamerName)
                    .with("embed", Constants.NOTIF_EMBED_DEFAULT)
                    .with("embedColor", Constants.NOTIF_EMBED_COLOR_DEFAULT)
                    .with("message", defaultMsg)
                    .with("streamEndAction", Constants.NOTIF_END_ACTION)
                    .with("streamEndMessage", defaultEndMsg)
            ).run(connection);

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
        return notifications.map(ReqlExpr::toJson).run(connection);
    }

    /**
     * Selects all notifications from the notifications table
     * with the specified server ID mapped to JSON.
     *
     * @param serverId the server ID to look for in the notifications
     * @return an {@link ArrayList} with all the notifications for the server
     */
    public ArrayList selectServerNotifs(String serverId) {
        return notifications.filter(rethink.hashMap("serverId", serverId))
                .orderBy("channelId").map(ReqlExpr::toJson).run(connection);
    }

    /**
     * Selects all notifications from the notifications table
     * with the specified channel ID mapped to JSON.
     *
     * @param serverId the server ID to look for in the notifications
     * @return an {@link ArrayList} with all the notifications for the channel
     */
    public Cursor selectChannelNotifs(String serverId, String channelId) {
        return notifications.filter(rethink.hashMap("serverId", serverId)
                .with("channelId", channelId)).map(ReqlExpr::toJson).run(connection);
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
        return notifications.filter(row -> row.g("streamerName")
                .match("(?i)" + streamerName + "$")
                .and(row.g("serverId").eq(serverId))
                .and(row.g("channelId").eq(channelId)))
                .map(ReqlExpr::toJson).run(connection);
    }

    /**
     * Updates the specified streamer's streaming status in the streamers table
     * with the specified parameter.
     *
     * @param documentId  the ID of the document you want to update
     * @param isStreaming the new value of the field
     */
    public void updateIsStreaming(String documentId, boolean isStreaming) {
        streamers.get(documentId).update(
                rethink.hashMap("isStreaming", isStreaming)
        ).run(connection);
    }

    /**
     * Updates the specified notification's message in the notifications table
     * with the specified parameter.
     *
     * @param documentId the ID of the document you want to update
     * @param newMessage the new value of the field
     */
    public void updateMessage(String documentId, String newMessage) {
        notifications.get(documentId).update(
                rethink.hashMap("message", newMessage)
        ).run(connection);
    }

    /**
     * Updates the specified notification's embed color in the notifications table
     * with the specified parameter.
     *
     * @param documentId the ID of the document you want to update
     * @param newColor   the new value of the field
     */
    public void updateColor(String documentId, String newColor) {
        notifications.get(documentId).update(
                rethink.hashMap("embedColor", newColor)
        ).run(connection);
    }

    /**
     * Updates the specified notification's embed mode in the notifications table
     * with the specified parameter.
     *
     * @param documentId    the ID of the document you want to update
     * @param newEmbedValue set true for embed, false for non-embed
     */
    public void updateEmbed(String documentId, boolean newEmbedValue) {
        notifications.get(documentId).update(
                rethink.hashMap("embed", newEmbedValue)
        ).run(connection);
    }

    public void updateEndAction(String documentId, String newEndAction) {
        notifications.get(documentId).update(
                rethink.hashMap("streamEndAction", newEndAction)
        ).run(connection);
    }

    public void updateEndMessage(String documentId, String newEndMessage) {
        notifications.get(documentId).update(
                rethink.hashMap("streamEndMessage", newEndMessage)
        ).run(connection);
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
        notifications.get(dbEntryId).delete().run(connection);
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
        Cursor cursor = notifications.filter(row -> row.g("serverId").eq(serverId)
                .and(row.g("channelId").eq(channelId))
                .and(row.g("streamerId").eq(streamerId)))
                .map(ReqlExpr::toJson).run(connection);

        if (cursor.hasNext()) {
            JSONObject document = new JSONObject(cursor.next().toString());
            return document.getString("id");
        }
        cursor.close();
        return null;
    }

    public boolean addServer(String serverId, boolean whitelisted) {
        if (getGuildDocId(serverId) == null) {
            guilds.insert(rethink.hashMap("serverId", serverId)
                    .with("whitelisted", whitelisted)
                    .with("prefix", "."))
                    .run(connection);
            return true;
        } else {
            return false;
        }
    }

    public Cursor selectOneServer(String serverId) {
        return guilds.filter(rethink
                .hashMap("serverId", serverId))
                .map(ReqlExpr::toJson).run(connection);
    }

    public Cursor selectAllGuilds() {
        return guilds.map(ReqlExpr::toJson).run(connection);
    }

    public void updateWhitelist(String documentId, boolean whitelisted) {
        guilds.get(documentId).update(rethink.hashMap("whitelisted", whitelisted)).run(connection);
    }

    public void deleteGuild(String docId) {
        guilds.get(docId).delete().run(connection);
    }

    private String getGuildDocId(String serverId) {
        Cursor cursor = guilds.filter(row -> row.g("serverId").eq(serverId))
                .map(ReqlExpr::toJson).run(connection);

        if (cursor.hasNext()) {
            JSONObject document = new JSONObject(cursor.next().toString());
            return document.getString("id");
        }
        cursor.close();
        return null;
    }

    public DatabaseDriver setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }
}