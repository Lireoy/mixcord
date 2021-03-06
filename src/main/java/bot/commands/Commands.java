package bot.commands;

import bot.commands.informative.Info;
import bot.commands.informative.Invite;
import bot.commands.informative.Ping;
import bot.commands.informative.WhoCanUseMe;
import bot.commands.mixer.MixerUser;
import bot.commands.mixer.MixerUserSocials;
import bot.commands.notifications.*;
import bot.commands.owner.Shutdown;
import bot.commands.owner.*;
import com.jagrosh.jdautilities.command.Command;

public class Commands {

    public static Command[] getCommands() {
        return new Command[]{
                // Informative
                new Ping(),
                new Info(),
                new Invite(),
                new WhoCanUseMe(),

                // Mixer
                new MixerUser(),
                new MixerUserSocials(),

                // Notifications
                new AddNotif(),
                new DeleteNotif(),
                new ChannelNotifs(),
                new ServerNotifs(),
                new MakeDefault(),
                new NotifDetails(),
                new NotifPreview(),
                new NotifMessageEdit(),
                new NotifColorEdit(),
                new NotifEmbedConfig(),

                // Owner
                new Debug(),
                new Whitelist(),
                new GetServerShard(),
                new GetDbStats(),
                new NotifServiceStatus(),
                new StartNotifService(),
                new StopNotifService(),
                new RoleInfo(),
                new ServerInfo(),
                new Shutdown()};
    }
}
