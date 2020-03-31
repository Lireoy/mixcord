package bot.commands.informative;

import bot.constants.BotConstants;
import bot.constants.HelpConstants;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.time.temporal.ChronoUnit;

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
        this.help = HelpConstants.PING_COMMAND_HELP;
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

        final String[] commandExamples = {BotConstants.PREFIX + this.name};

        boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        commandEvent.reply("Calculating...", (m) -> {
            final long ping =
                    commandEvent.getMessage()
                            .getTimeCreated()
                            .until(m.getTimeCreated(),
                                    ChronoUnit.MILLIS);

            m.editMessage("Ping: " + ping + "ms | Websocket: " +
                    commandEvent.getJDA().getGatewayPing() + "ms").queue();
        });
    }
}
