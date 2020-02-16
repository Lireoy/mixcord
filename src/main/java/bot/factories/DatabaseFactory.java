package bot.factories;

import bot.DatabaseConnectionBuilder;
import bot.DatabaseDriver;
import bot.structure.Credentials;
import com.rethinkdb.net.Connection;

public class DatabaseFactory {

    private static DatabaseDriver database;

    private DatabaseFactory() {
        database = new DatabaseDriver().setConnection(buildConnection(CredentialsFactory.getCredentials()));
    }

    public static DatabaseDriver getDatabase() {
        if (database == null) {
            new DatabaseFactory();
        }

        return database;
    }

    private Connection buildConnection(Credentials credentials) {
        DatabaseConnectionBuilder connectionBuilder = new DatabaseConnectionBuilder()
                .setDatabaseIp(credentials.getDatabaseIp())
                .setDatabasePort(credentials.getDatabasePort())
                .setDatabaseUser(credentials.getDatabaseUser())
                .setDatabasePassword(credentials.getDatabasePassword());
        return connectionBuilder.build();
    }
}
