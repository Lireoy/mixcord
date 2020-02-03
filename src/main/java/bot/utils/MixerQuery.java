package bot.utils;

import bot.Constants;
import bot.Mixcord;
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

    /**
     * Queries Mixer's Api with the specified parameter for information about the channel.
     * Uses the 'channels' api endpoint.
     *
     * @param queryParam the parameter which the query should be executed with
     * @return the JSON response, or null if failed
     */
    public static JSONObject queryChannel(String queryParam) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String uri = Constants.MIXER_API_CHANNELS_PATH + "/" + queryParam;

        // Custom Timout values to avoid long stuck commands
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD) // avoids "Invalid 'expires' attribute" and "Invalid cookie header"
                .setConnectionRequestTimeout(10000)
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .build();

        HttpUriRequest request = RequestBuilder.get().setUri(uri)
                .setHeader("Client-ID", Mixcord.getCredentials().getMixerApiClientId())
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