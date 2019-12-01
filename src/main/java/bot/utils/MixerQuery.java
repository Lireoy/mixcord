package bot.utils;

import bot.Constants;
import bot.Mixcord;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

@Slf4j
public class MixerQuery {

    private static Dotenv dotenv = Mixcord.getDotenv();
    private static String MIXER_API_CLIENT_ID = dotenv.get("MIXER_API_CLIENT_ID");
    //private static String MIXER_API_CLIENT_SECRET = dotenv.get("MIXER_API_CLIENT_SECRET");

    // Requires legit string
    // Creates a custom Http client
    // Custom Timout values to avoid long stuck commands
    // setCookieSpec.STANDARD to avoid "Invalid 'expires' attribute" and "Invalid cookie header"
    // Returns response JSON when successful
    // Returns null when failed
    public static JSONObject queryChannel(String queryParam) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String uri = Constants.MIXER_API_CHANNELS_PATH + "/" + queryParam;

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD)
                .setConnectionRequestTimeout(10000)
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .build();
        HttpUriRequest request = RequestBuilder.get().setUri(uri)
                .setHeader("Client-ID", MIXER_API_CLIENT_ID)
                .setConfig(requestConfig)
                .build();

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return new JSONObject(EntityUtils.toString(entity));
            } else {
                log.info("Request failed: {}", uri);
                log.info("Unexpected response status: {}", status);
                EntityUtils.consumeQuietly(response.getEntity());
                //log.info("Full response: {}", response);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}