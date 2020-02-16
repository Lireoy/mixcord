package bot.factories;

import bot.structure.Credentials;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class CredentialsFactory {

    private static Credentials credentials;

    private CredentialsFactory() {
        if (new File("credentials.json").exists()) {
            try {
                final String text = new String(Files.readAllBytes(Paths.get("credentials.json")), StandardCharsets.UTF_8);
                credentials = new Gson().fromJson(text, Credentials.class);
            } catch (IOException e) {
                log.error("Failed to read 'credentials.json'.");
            }
        } else {
            log.error("Could not find 'credentials.json' file.");
            log.info("Shutting down application...");
            System.exit(0);
        }
    }

    public static Credentials getCredentials() {
        if (credentials == null) {
            new CredentialsFactory();
        }

        return credentials;
    }
}
