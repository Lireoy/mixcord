package bot;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

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

    public DatabaseConnectionBuilder setDatabaseIp(String databaseIp) {
        this.databaseIp = databaseIp;
        return this;
    }

    public DatabaseConnectionBuilder setDatabasePort(int databasePort) {
        this.databasePort = databasePort;
        return this;
    }

    public DatabaseConnectionBuilder setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
        return this;
    }

    public DatabaseConnectionBuilder setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
        return this;
    }

    public Connection build() {
        return RethinkDB.r.connection().hostname(databaseIp).port(databasePort)
                .user(databaseUser, databasePassword).connect();
    }
}
