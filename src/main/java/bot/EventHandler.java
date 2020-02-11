package bot;

import bot.structure.Notification;
import com.google.gson.Gson;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles specific events which are triggered.
 */
@Slf4j
public class EventHandler extends ListenerAdapter {

    /**
     * Starts back up the notification service if it was running
     * before the disconnect event.
     *
     * @param event the event which triggered the listener
     */
    @Override
    public void onResume(@Nonnull ResumedEvent event) {
        log.info("Resumed session...");
        if (Mixcord.getNotifierServiceStateArchive()) {
            Mixcord.getNotifierService().start();
            log.info("Resume event: Starting notifier service...");
        }
    }

    /**
     * Starts back up the notification service if it was running
     * before the disconnect event.
     *
     * @param event the event which triggered the listener
     */
    @Override
    public void onReconnect(@Nonnull ReconnectedEvent event) {
        log.info("Reconnected to session...");
        if (Mixcord.getNotifierServiceStateArchive()) {
            Mixcord.getNotifierService().start();
            log.info("Reconnect event: Starting notifier service...");
        }
    }

    /**
     * Saves the current state of the notifier service.
     * If the notifier service was running, it is stopped.
     *
     * @param event the event which triggered the listener
     */
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

    /**
     * Logs and adds the server to the guilds table with default values.
     *
     * @param event the event which triggered the listener
     */
    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        Mixcord.getDatabase().addServer(event.getGuild().getId());
        log.info("Joined guild {}", event.getGuild().getId());
    }

    /**
     * When the bot is kicked from a guild
     * their configuration is deleted from the database.
     *
     * @param event the event which triggered the listener
     */
    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        Gson gson = new Gson();

        log.info("Leaving guild {}", event.getGuild().getId());
        Mixcord.getDatabase().deleteGuild(event.getGuild().getId());
        log.info("Deleting server configuration from database...");

        List<String> streamerIds = new ArrayList<>();
        Cursor notifs = Mixcord.getDatabase().selectServerNotifs(event.getGuild().getId());

        int deletedNotifs = 0;
        for (Object object : notifs) {
            Notification notif = gson.fromJson(new JSONObject(object.toString()).toString(), Notification.class);
            Mixcord.getDatabase().deleteNotif(notif.getId());
            deletedNotifs++;

            if (!streamerIds.contains(notif.getStreamerId())) {
                streamerIds.add(notif.getStreamerId());
            }
        }
        log.info("Deleted {} notifications for G:{}", deletedNotifs, event.getGuild().getId());

        int deletedStreamers = 0;
        for (String streamerId : streamerIds) {
            if (!Mixcord.getDatabase().selectStreamerNotifs(streamerId).hasNext()) {
                Mixcord.getDatabase().deleteStreamer(streamerId);
                deletedStreamers++;
            }
        }

        log.info("Deleted {} streamers from the database.", deletedStreamers);
    }
}
