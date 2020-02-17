package bot;

import bot.commands.Commands;
import bot.factories.CredentialsFactory;
import bot.structure.Credentials;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class Mixcord {

    private static CommandClient client;
    private static ShardManager shards;

    public static void main(String[] args) {
        displayAscii();

        Credentials credentials = CredentialsFactory.getCredentials();
        String BOT_TOKEN = credentials.isProductionBuild() ?
                credentials.getDiscordBotToken() : credentials.getDiscordBotTokenCanary();

        log.info("Notifier service was started.");
        log.info("Posting metrics to G:{} - C:{}", Constants.METRICS_GUILD, Constants.METRICS_CHANNEL);

        client = new CommandClientBuilder()
                .setPrefix(Constants.PREFIX)
                .setAlternativePrefix("@mention")
                .setOwnerId(Constants.OWNER_ID)
                .setCoOwnerIds(Constants.CO_OWNER_ID, Constants.CO_OWNER_ID2)
                .setEmojis(Constants.SUCCESS, Constants.WARNING, Constants.ERROR)
                .setServerInvite(Constants.DISCORD)
                .addCommands(Commands.getCommands())
                .setHelpConsumer(Mixcord::accept)
                .build();
        client.getCommands().forEach((command) -> log.info("Added command: {}", command.getName()));
        log.info("Total number of commands available: {}", client.getCommands().size());

        log.info("Bot prefix set to {}", client.getPrefix());
        log.info("Bot alternate prefix set to {}", client.getAltPrefix());

        log.info("Added owner: {}", client.getOwnerId());
        Arrays.stream(client.getCoOwnerIds()).forEach(coOwnerId -> log.info("Added co-owner: {}", coOwnerId));
        EventHandler eventHandler = new EventHandler();

        try {
            shards = new DefaultShardManagerBuilder()
                    .setToken(BOT_TOKEN)
                    .setShardsTotal(credentials.getNumberOfShards())
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(Activity.playing("Type .help"))
                    .addEventListeners(client)
                    .addEventListeners(eventHandler)
                    .setAutoReconnect(true)
                    .build();

            // Starts the automatic notification system
            // If you delete this, then you have to start
            // the notifier service manually on every startup
            //NotifServiceFactory.getNotifService().run();
        } catch (LoginException e) {
            log.error(e.getMessage());
        }
    }

    private static void displayAscii() {
        try (FileReader fr = new FileReader("MixcordASCII.txt")) {
            int i;
            while ((i = fr.read()) != -1) {
                System.out.print((char) i);
            }
        } catch (IOException e) {
            log.warn("Could not find ASCII art.");
        }
    }

    //TODO: Move to / create a service this.
    public static ShardManager getShards() {
        return shards;
    }

    private static void accept(CommandEvent event) {
        //TODO: group commands by category, and not by order
        StringBuilder helpBuilder = new StringBuilder("**" + event.getSelfUser().getName() + "** commands:\n");
        Command.Category category = null;
        for (Command command : client.getCommands()) {
            if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
                if (!Objects.equals(category, command.getCategory())) {
                    category = command.getCategory();
                    helpBuilder
                            .append("\n\n  __")
                            .append(category == null ? "No Category" : category.getName())
                            .append("__:\n");
                }
                helpBuilder
                        .append("\n`")
                        .append(client.getPrefix())
                        .append(client.getPrefix() == null ? " " : "")
                        .append(command.getName())
                        .append(command.getArguments() == null ? "`" : " " + command.getArguments() + "`")
                        .append(" - ")
                        .append(command.getHelp());
            }
        }
        try {
            if (event.isFromType(ChannelType.TEXT)) {
                event.reply(helpBuilder.toString());
                event.reactSuccess();

                User owner = event.getJDA().getUserById(client.getOwnerId());
                if (owner != null) {
                    String contact = "For additional help, contact **" +
                            owner.getName() +
                            "**#" +
                            owner.getDiscriminator() +
                            " or join " +
                            Constants.DISCORD;
                    event.reply(contact);
                }
            }
        } catch (InsufficientPermissionException ex) {
            event.reactError();
            event.replyInDm("Help cannot be sent. I don't have permission to write in that channel.");
        }
    }
}