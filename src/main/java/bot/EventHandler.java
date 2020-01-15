package bot;

import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class EventHandler extends ListenerAdapter {

    @Override
    public void onResume(@Nonnull ResumedEvent event) {
        //TODO: Restart notifier service upon resume.
        super.onResume(event);
    }

    @Override
    public void onReconnect(@Nonnull ReconnectedEvent event) {
        //TODO: Restart notifier service upon reconnect.
        super.onReconnect(event);
    }

    @Override
    public void onDisconnect(@Nonnull DisconnectEvent event) {
        //TODO: Stop notifier service upon disconnect.
        //TODO: Upon disconnect still keep checking the Mixer data, however store it locally, to push it later to Discord
        super.onDisconnect(event);
    }


}
