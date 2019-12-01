package bot;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.ReqlExpr;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseDriver {

    private final RethinkDB rethink = RethinkDB.r;
    private Connection connection;
    private Table database;

    DatabaseDriver(String databaseIp, int databasePort, String databaseName, String databaseTable, String databaseUser, String databasePassword) {
        this.connection = getRethink().connection().hostname(databaseIp).port(databasePort)
                .user(databaseUser, databasePassword).connect();
        this.database = getRethink().db(databaseName).table(databaseTable);
    }

    public Cursor selectAll() {
        return getDatabase().map(ReqlExpr::toJson).run(getConnection());
    }

    public ArrayList filter(String serverId) {
        return getDatabase().filter(getRethink().hashMap("serverId", serverId))
                .orderBy("channelId").map(ReqlExpr::toJson).run(getConnection());
    }

    public Cursor filter(String serverId, String channelId) {
        return getDatabase().filter(getRethink().hashMap("serverId", serverId)
                .with("channelId", channelId)).map(ReqlExpr::toJson).run(getConnection());
    }

    // Retrieve all notification with name "William" (case insensitive).
    // .match("(?i)^william$")
    // (?i) is canse insestitive mode
    // ^ start of the string
    // $ end of the string
    public Cursor filter(String serverId, String channelId, String streamerName) {
        return getDatabase().filter(row -> row.g("streamerName").match("(?i)" + streamerName + "$")
                .and(row.g("serverId").eq(serverId))
                .and(row.g("channelId").eq(channelId)))
                .map(ReqlExpr::toJson).run(getConnection());
    }

    // This method is used by the NotifierService
    // Changes the isStreaming status in the database
    // Required to keep track of stream endings
    public void updateIsStreaming(String documentId, boolean isStreaming) {
        getDatabase().get(documentId).update(
                getRethink().hashMap("isStreaming", isStreaming)
        ).run(getConnection());
    }

    public void updateMessage(String documentId, String newMessage) {
        getDatabase().get(documentId).update(
                getRethink().hashMap("message", newMessage)
        ).run(getConnection());
    }

    public void updateColor(String documentId, String newColor) {
        getDatabase().get(documentId).update(
                getRethink().hashMap("embedColor", newColor)
        ).run(getConnection());
    }

    // Insert a new document into the database
    public String insert(String serverId, String channelId, String streamerName, String streamerId) {

        // Returns "-1" which means empty arguments
        if (serverId.isEmpty() || channelId.isEmpty() || streamerName.isEmpty() || streamerId == null) {
            return "-1";
        }

        String defaultMsg = "<" + Constants.MIXER_COM + streamerName + "> is now live on Mixer!";
        String defaultEndMsg = streamerName + " finished streaming.";
        // Check if entry is in the database
        if (getDocumentId(serverId, channelId, streamerId).equals("-1")) {
            // If not in database, insert the data.
            getDatabase().insert(getRethink().hashMap("serverId", serverId)
                    .with("channelId", channelId)
                    .with("streamerId", streamerId)
                    .with("streamerName", streamerName)
                    .with("embed", true)
                    .with("embedColor", "ffffff")
                    .with("isStreaming", false)
                    .with("message", defaultMsg)
                    .with("streamEndAction", "0")
                    .with("streamEndMessage", defaultEndMsg)
            ).run(getConnection());

            // Return successful database insertion.
            return "1";
        } else {
            // The entry is in the database.
            return "0";
        }
    }

    // Deletes a document from the database
    public String delete(String serverId, String channelId, String streamerId) {
        String documentId = getDocumentId(serverId, channelId, streamerId);
        if (documentId.equals("-1")) {
            return "Could not delete entry, because it is not in the database.";
        } else {
            delete(documentId);
            return "1";
        }
    }

    // Deletes a document from the database
    public void delete(String dbEntryId) {
        getDatabase().get(dbEntryId).delete().run(getConnection());
    }


    // Searches for a specific document and gets it's ID
    // Checks if the supplied data exists in the database (all of them in one single document)
    // Not in database -> "-1"
    // In database -> Return the unique document ID
    private String getDocumentId(String serverId, String channelId, String streamerId) {
        Cursor cursor = getDatabase().filter(row -> row.g("serverId").eq(serverId)
                .and(row.g("channelId").eq(channelId))
                .and(row.g("streamerId").eq(streamerId)))
                .map(ReqlExpr::toJson).run(getConnection());

        if (cursor.hasNext()) {
            JSONObject document = new JSONObject(cursor.next().toString());
            return document.getString("id");
        }

        return "-1";
    }

    private RethinkDB getRethink() {
        return rethink;
    }

    private Connection getConnection() {
        return connection;
    }

    private Table getDatabase() {
        return database;
    }
}
