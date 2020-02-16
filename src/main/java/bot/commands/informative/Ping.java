package bot.commands.informative;

import bot.structure.enums.CommandCategory;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Sends the bot's latency information to the user in a regular message.
 * The latency is determined by measuring the gateway ping
 * and the time it took Discord to send our message.
 * The gateway ping is our connection latency to Discord's Api.
 * The true ping is the time difference between message creation and current time.
 */
@Slf4j
public class Ping extends Command {

    public Ping() {
        this.name = "Ping";
        this.aliases = new String[]{"Pong", "Pingpong", "Latency"};
        this.help = "Shows the current latency of the bot.";
        this.category = new Category(CommandCategory.INFORMATIVE.toString());
        this.guildOnly = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        commandEvent.reply("Ping: ...", (m) -> {
            final long ping = commandEvent.getMessage().getTimeCreated().until(m.getTimeCreated(),
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
