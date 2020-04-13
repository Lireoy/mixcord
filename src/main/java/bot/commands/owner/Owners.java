package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.services.ClientService;
import bot.services.ShardService;
import bot.utils.HelpUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class Owners extends Command {

    public Owners() {
        this.name = "Owners";
        this.help = Locale.OWNERS_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("OWNER"));
        this.guildOnly = false;
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        final String[] commandExamples = {BotConstants.PREFIX + this.name};

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        StringBuilder response = new StringBuilder(Locale.OWNERS_COMMAND_TITLE);

        User ownerOne = ShardService.getInstance().getUserById(
                ClientService.getInstance().getOwnerId());

        if (ownerOne == null) {
            commandEvent.reply(Locale.OWNERS_COMMAND_NO_OWNER);
            return;
        }

        response.append(
                String.format(
                        Locale.OWNERS_COMMAND_OWNER_LINE,
                        ownerOne.getName(),
                        ownerOne.getDiscriminator()));

        for (String ownerId : ClientService.getInstance().getCoOwnerIds()) {
            User ownerN = ShardService.getInstance().getUserById(ownerId);
            if (ownerN == null) {
                return;
            }

            response.append(
                    String.format(
                            Locale.OWNERS_COMMAND_OWNER_LINE,
                            ownerN.getName(),
                            ownerN.getDiscriminator()));
        }

        commandEvent.reply(response.toString());
    }
}
