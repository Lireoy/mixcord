package bot.services;

import bot.constants.BotConstants;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;

@Slf4j
public class EventEmitter {

    public static void emitGenericExceptionEvent(final User user, final Exception ex) {
        String message = "General exception in NotifService.";
        emitInDm(user, message);
        emitInDm(user, ex.getMessage());
        emitInDm(user, Arrays.toString(ex.getCause().getStackTrace()));
    }

    public static void emitGenericExceptionEvent(final TextChannel textChannel, final Exception ex) {
        String message = "General exception in NotifService.";
        emitToChannel(textChannel, message);
        emitToChannel(textChannel, ex.getMessage());
        emitToChannel(textChannel, Arrays.toString(ex.getCause().getStackTrace()));
    }

    public static void emitDatabaseExceptionEvent(final User user, final Exception ex) {
        String message = BotConstants.WARNING + BotConstants.WARNING +
                "There is a database issue. Stopping the notifier service." +
                BotConstants.WARNING + BotConstants.WARNING;

        emitInDm(user, message);
        emitInDm(user, ex.getMessage());
        emitInDm(user, Arrays.toString(ex.getCause().getStackTrace()));
    }

    public static void emitDatabaseExceptionEvent(final TextChannel textChannel, final Exception ex) {
        String message = BotConstants.WARNING + BotConstants.WARNING +
                "There is a database issue. Stopping the notifier service." +
                BotConstants.WARNING + BotConstants.WARNING;

        emitToChannel(textChannel, message);
        emitToChannel(textChannel, ex.getMessage());
        emitToChannel(textChannel, Arrays.toString(ex.getCause().getStackTrace()));
    }

    private static boolean emitInDm(final User user, final String message) {
        if (user == null) {
            log.info("User was null.");
            return false;
        }

        if (message.trim().isEmpty() || message.trim().length() > 2000) {
            log.info("Emitted message was too empty or too long.");
            return false;
        }

        log.info("Sent a message to {}. Content: {}.", user.getId(), message.trim());
        user.openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage(message.trim()).queue()
        );
        return true;
    }

    private static boolean emitInDm(final User user, final MessageEmbed embed) {
        if (user == null) {
            log.info("User was null.");
            return false;
        }

        if (embed.isEmpty()) {
            log.info("Emitted embed was empty.");
            return false;
        }

        log.info("Sent a message to {}. Content: embed.", user.getId());
        user.openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage(embed).queue()
        );
        return true;
    }

    private static boolean emitToChannel(final TextChannel textChannel, final String message) {
        if (!textChannel.canTalk()) {
            log.info("No talk power in C:{}.", textChannel.getId());
            return false;
        }

        if (message.trim().isEmpty() || message.trim().length() > 2000) {
            log.info("Emitted message was too empty or too long.");
            return false;
        }

        log.info("Sent a message to C:{}. Content: {}.", textChannel.getId(), message.trim());
        textChannel.sendMessage(message.trim()).queue();
        return true;
    }

    private static boolean emitToChannel(final TextChannel textChannel, final MessageEmbed embed) {
        if (!textChannel.canTalk()) {
            log.info("No talk power in C:{}.", textChannel.getId());
            return false;
        }

        if (embed.isEmpty()) {
            log.info("Emitted embed was empty.");
            return false;
        }

        log.info("Sent a embed to C:{}. Content: embed.", textChannel.getId());
        textChannel.sendMessage(embed).queue();
        return true;
    }
}

