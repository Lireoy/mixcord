package bot.commands.informative;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Ping extends Command {

    public Ping() {
        this.name = "Ping";
        this.aliases = new String[]{"Pong", "Pingpong", "Latency"};
        this.help = "Shows the current latency of the bot.";
        this.guildOnly = true;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        commandEvent.reply("Ping: ...", (m) -> {
            long ping = commandEvent.getMessage().getTimeCreated().until(m.getTimeCreated(),
                    ChronoUnit.MILLIS);

            m.editMessage("Calculating...").queue();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            m.editMessage("Websocket: " + commandEvent.getJDA().getGatewayPing() + "ms").queue();
            m.editMessage("Ping: " + ping + "ms | Websocket: " +
                    commandEvent.getJDA().getGatewayPing() + "ms").queue();
        });
    }
}
