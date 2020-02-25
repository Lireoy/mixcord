package bot.services;

import bot.EventHandler;
import bot.structures.Credentials;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;

@Slf4j
public class ShardService {

    private static ShardManager instance;

    private ShardService() throws LoginException {
        instance = new DefaultShardManagerBuilder()
                .setToken(ShardService.getBotToken())
                .setShardsTotal(ShardService.getShardNumber())
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("Type .help"))
                .addEventListeners(ClientService.getInstance())
                .addEventListeners(new EventHandler())
                .setAutoReconnect(true)
                .build();
    }

    public static ShardManager getInstance() {
        if (instance == null) {
            try {
                new ShardService();
            } catch (LoginException e) {
                log.error(e.getMessage());
            }
        }
        return instance;
    }

    private static String getBotToken() {
        if (Credentials.getInstance().isProductionBuild()) {
            return Credentials.getInstance().getDiscordBotToken();
        } else {
            return Credentials.getInstance().getDiscordBotTokenCanary();
        }
    }

    private static int getShardNumber() {
        return Credentials.getInstance().getNumberOfShards();
    }
}