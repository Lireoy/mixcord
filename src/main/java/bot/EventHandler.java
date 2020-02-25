package bot;

import bot.services.NotifService;
import bot.structures.Notification;
import com.google.gson.Gson;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles specific events which are triggered.
 */
@Slf4j
public class EventHandler extends ListenerAdapter {

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        int shardId = event.getJDA().getShardInfo().getShardId();
        int total = event.getGuildTotalCount();
        int available = event.getGuildAvailableCount();
        int unAvailable = event.getGuildUnavailableCount();
        log.info("Ready event for shard {}. Total guilds {}, Available {}, Unavailable {}",
                shardId, total, available, unAvailable);
    }

    /**
     * Starts back up the notification service.
     *
     * @param event the event which triggered the listener
     */
    @Override
    public void onResume(@Nonnull ResumedEvent event) {
        log.info("Resumed session...");
        NotifService.getInstance();
    }

    /**
     * Starts back up the notification service.
     *
     * @param event the event which triggered the listener
     */
    @Override
    public void onReconnect(@Nonnull ReconnectedEvent event) {
        log.info("Reconnected to session...");
        NotifService.getInstance();
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
        NotifService.getInstance().stop();
        log.info("Stopping notifier service due to disconnect event...");
    }

    /**
     * Logs and adds the server to the guilds table with default values.
     *
     * @param event the event which triggered the listener
     */
    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        final boolean addResponse = DatabaseDriver.getInstance().addServer(event.getGuild().getId());
        if (addResponse) {
            log.info("Joined guild {}", event.getGuild().getId());
        } else {
            log.info("Failed to add G:{} on join event", event.getGuild().getId());
        }
    }

    /**
     * When the bot is kicked from a guild
     * their configuration is deleted from the database.
     *
     * @param event the event which triggered the listener
     */
    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        log.info("Leaving guild {}", event.getGuild().getId());
        DatabaseDriver.getInstance().deleteGuild(event.getGuild().getId());
        log.info("Deleting server configuration from database...");

        List<String> streamerIds = new ArrayList<>();
        Cursor notifs = DatabaseDriver.getInstance().selectServerNotifs(event.getGuild().getId());

        int deletedNotifs = 0;
        for (Object object : notifs) {
            Notification notif = new Gson().fromJson(object.toString(), Notification.class);
            DatabaseDriver.getInstance().deleteNotif(notif.getId());
            deletedNotifs++;

            if (!streamerIds.contains(notif.getStreamerId())) {
                streamerIds.add(notif.getStreamerId());
            }
        }
        log.info("Deleted {} notifications for G:{}", deletedNotifs, event.getGuild().getId());

        int deletedStreamers = 0;
        for (String streamerId : streamerIds) {
            Cursor cursor = DatabaseDriver.getInstance().selectStreamerNotifs(streamerId);
            if (!cursor.hasNext()) {
                DatabaseDriver.getInstance().deleteStreamer(streamerId);
                deletedStreamers++;
            }
            cursor.close();
        }

        log.info("Deleted {} streamers from the database.", deletedStreamers);
    }
}
