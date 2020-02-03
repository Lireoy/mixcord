package bot;

import bot.structure.Server;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import javax.annotation.Nonnull;

@Slf4j
public class EventHandler extends ListenerAdapter {

    @Override
    public void onResume(@Nonnull ResumedEvent event) {
        log.info("Resumed session...");
        if (Mixcord.getNotifierServiceStateArchive()) {
            Mixcord.getNotifierService().start();
            log.info("Resume event: Starting notifier service...");
        }
    }

    @Override
    public void onReconnect(@Nonnull ReconnectedEvent event) {
        log.info("Reconnected to session...");
        if (Mixcord.getNotifierServiceStateArchive()) {
            Mixcord.getNotifierService().start();
            log.info("Reconnect event: Starting notifier service...");
        }
    }

    @Override
    public void onDisconnect(@Nonnull DisconnectEvent event) {
        log.info("Disconnected from session...");
        Mixcord.setNotifierServiceStateArchive(Mixcord.getNotifierService().getState());
        log.info("Saved notifier service state");
        if (Mixcord.getNotifierService().getState()) {
            Mixcord.getNotifierService().stop();
            log.info("Stopping notifier service due to disconnect event...");
        }
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        Mixcord.getDatabase().addServer(event.getGuild().getId(), false);
        log.info("Joined guild {}", event.getGuild().getId());
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        log.info("Leaving guild {}", event.getGuild().getId());
        Object guildObj = Mixcord.getDatabase().selectOneServer(event.getGuild().getId()).next();
        JSONObject guildJson = new JSONObject(guildObj.toString());
        Server server = new Gson().fromJson(guildJson.toString(), Server.class);
        Mixcord.getDatabase().deleteGuild(server.getId());
        log.info("Deleting server configuration from database...");
    }
}
