package bot.services;

import bot.EventHandler;
import bot.factories.CredentialsFactory;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;

@Slf4j
public class ShardService {

    private static ShardManager shards;

    private ShardService() throws LoginException {
        shards = new DefaultShardManagerBuilder()
                .setToken(ShardService.getBotToken())
                .setShardsTotal(ShardService.getShardNumber())
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("Type .help"))
                .addEventListeners(ClientService.getClient())
                .addEventListeners(new EventHandler())
                .setAutoReconnect(true)
                .build();
    }

    public static ShardManager manager() {
        if (shards == null) {
            try {
                new ShardService();
            } catch (LoginException e) {
                log.error(e.getMessage());
            }
        }
        return shards;
    }

    private static String getBotToken() {
        if (CredentialsFactory.getCredentials().isProductionBuild()) {
            return CredentialsFactory.getCredentials().getDiscordBotToken();
        } else {
            return CredentialsFactory.getCredentials().getDiscordBotTokenCanary();
        }
    }

    private static int getShardNumber() {
        return CredentialsFactory.getCredentials().getNumberOfShards();
    }
}