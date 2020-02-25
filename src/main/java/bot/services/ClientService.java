package bot.services;

import bot.Constants;
import bot.commands.Commands;
import bot.structure.enums.CommandCategory;
import bot.utils.StringUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class ClientService {

    private static CommandClient instance;

    private ClientService() {
        instance = new CommandClientBuilder()
                .setPrefix(Constants.PREFIX)
                .setAlternativePrefix("@mention")
                .setOwnerId(Constants.OWNER_ID)
                .setCoOwnerIds(Constants.CO_OWNER_ID, Constants.CO_OWNER_ID2)
                .setEmojis(Constants.SUCCESS, Constants.WARNING, Constants.ERROR)
                .setServerInvite(Constants.DISCORD)
                .addCommands(Commands.getCommands())
                .setHelpConsumer(ClientService::help)
                .build();
    }

    public static CommandClient getInstance() {
        if (instance == null) {
            new ClientService();
        }
        return instance;
    }

    public static void help(CommandEvent event) {
        // TODO: Write HelpUtil, and implement it in each command
        StringBuilder helpMessage = new StringBuilder();
        String botName = event.getSelfUser().getName();

        String intro = "**%s**\nYou can `%s` me or `%s` as a command prefix.\n\n**__Available commands__**:\n";
        String formattedIntro = String.format(intro, botName, instance.getAltPrefix(), instance.getPrefix());
        helpMessage.append(formattedIntro);

        helpMessage.append("For command specific help, put `--help` after any command, " +
                "and I will help you out with a detailed help, and a very nice example.\n");
        helpMessage.append("Here's an example for requesting command help:");
        helpMessage.append("`").append(instance.getPrefix()).append("Info --help`\n");

        for (CommandCategory category : CommandCategory.values()) {
            helpMessage.append("\n  **").append(category.getText()).append("**\n");

            StringBuilder builder = new StringBuilder();
            for (Command command : instance.getCommands()) {
                if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
                    if (command.getCategory().getName().equalsIgnoreCase(category.getText())) {
                        builder
                                .append("`")
                                .append(instance.getPrefix())
                                .append(command.getName())
                                .append("`")
                                .append(", ");
                    }
                }
            }

            helpMessage.append(StringUtil.replaceLastComma(builder.toString())).append("\n");
        }

        try {
            if (event.isFromType(ChannelType.TEXT)) {
                event.replyFormatted(helpMessage.toString());
                event.reactSuccess();
            }

            if (!event.isOwner()) {
                User owner = event.getJDA().getUserById(instance.getOwnerId());
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

    /*
    public static void help(CommandEvent event) {
        //TODO: group commands by category, and not by order
        StringBuilder helpBuilder = new StringBuilder("**" + event.getSelfUser().getName() + "** commands:\n");
        Command.Category category = null;
        for (Command command : ClientService.instance.getCommands()) {
            if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
                // nem rejtett, nem tulajparancs nem tulaj
                // true && (true || false) -> true

                // nem rejtett, tulajparancs, nem tulaj
                // true && (false || false) -> false
                if (!Objects.equals(category, command.getCategory())) {
                    category = command.getCategory();
                    helpBuilder
                            .append("\n\n  __")
                            .append(category == null ? "No Category" : category.getName())
                            .append("__:\n");
                }
                helpBuilder
                        .append("\n`")
                        .append(ClientService.instance.getPrefix())
                        .append(ClientService.instance.getPrefix() == null ? " " : "")
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

                User owner = event.getJDA().getUserById(ClientService.instance.getOwnerId());
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
    }*/
}