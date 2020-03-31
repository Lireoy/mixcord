package bot.database;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import lombok.extern.slf4j.Slf4j;

/**
 * This class helps build a connection for RethinkDB.
 * If no settings are suplied the connection will be set to
 * the values which RethinkDB uses as default.
 * <p>
 * This class was written with method chaining in mind.
 * <p>
 * To create the connection
 * you have to use {@link DatabaseConnectionBuilder#build()}
 * regardless of provided connection parameters.
 */
@Slf4j
public class DatabaseConnectionBuilder {

    private String databaseIp;
    private int databasePort;
    private String databaseUser;
    private String databasePassword;

    public DatabaseConnectionBuilder() {
        this.databaseIp = "127.0.0.1";
        this.databasePort = 28015;
        this.databaseUser = "admin";
        this.databasePassword = "";
    }

    /**
     * Sets the IP address to connect to.
     *
     * @param databaseIp the IP of the database
     * @return this instance
     */
    public DatabaseConnectionBuilder setDatabaseIp(String databaseIp) {
        this.databaseIp = databaseIp;
        return this;
    }

    /**
     * Sets the Port number to be used for connection.
     *
     * @param databasePort the number of the Port
     * @return this instance
     */
    public DatabaseConnectionBuilder setDatabasePort(int databasePort) {
        this.databasePort = databasePort;
        return this;
    }

    /**
     * Sets the username to be used for connection.
     *
     * @param databaseUser the username
     * @return this instance
     */
    public DatabaseConnectionBuilder setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
        return this;
    }

    /**
     * Sets the password for the user for connection.
     *
     * @param databasePassword the password for the user
     * @return this instance
     */
    public DatabaseConnectionBuilder setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
        return this;
    }

    /**
     * Builds a {@link Connection} with the set details.
     *
     * @return a RethinkDB {@link Connection}
     */
    public Connection build() {
        try {
            return RethinkDB.r.connection()
                    .hostname(databaseIp)
                    .port(databasePort)
                    .user(databaseUser, databasePassword)
                    .connect();
        } catch (ReqlDriverError ex) {
            log.info("ReqlDriverError: Could not connect to the database.");
            log.info(ex.getCause().toString());
            log.info(ex.getMessage());
            //TODO: Report in DM to owner
            System.exit(0);
        }
        return new Connection(new Connection.Builder());
    }
}
