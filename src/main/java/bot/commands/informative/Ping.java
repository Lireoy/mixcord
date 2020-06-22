package bot.commands.informative;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.structures.MixcordCommand;
import bot.utils.CommandUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
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
public class Ping extends MixcordCommand {

    public Ping() {
        this.name = "Ping";
        this.aliases = new String[]{"Pong", "Pingpong", "Latency"};
        this.help = Locale.PING_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("INFORMATIVE"));
        this.guildOnly = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.getInstance().checkHelp(this, commandEvent)) return;
        respond(commandEvent);
    }

    private void respond(CommandEvent commandEvent) {
        commandEvent.reply(Locale.PING_COMMAND_CALCULATING, (m) -> {
            final long ping = commandEvent.getMessage().getTimeCreated()
                    .until(m.getTimeCreated(), ChronoUnit.MILLIS);
            final long gatewayPing = commandEvent.getJDA().getGatewayPing();

            m.editMessage(String.format(Locale.PING_COMMAND_REPLY, ping, gatewayPing)).queue();
        });
    }
}
