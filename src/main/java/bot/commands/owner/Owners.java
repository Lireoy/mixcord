package bot.commands.owner;

import bot.constants.BotConstants;
import bot.services.ClientService;
import bot.structures.enums.CommandCategory;
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
        this.category = new Category(CommandCategory.OWNER.toString());
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

        String ownerOne = ClientService.getInstance().getOwnerId();
        StringBuilder stringBuilder = new StringBuilder("Owners of this bot are:\n");
        stringBuilder.append("· <@").append(ownerOne).append(">\n");
        for (String owner : ClientService.getInstance().getCoOwnerIds()) {
            stringBuilder.append("· <@").append(owner).append(">\n");
        }

        commandEvent.reply(stringBuilder.toString());
    }
}
