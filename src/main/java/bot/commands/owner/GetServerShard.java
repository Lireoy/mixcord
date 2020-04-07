package bot.commands.owner;

import bot.constants.BotConstants;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
import bot.utils.StringUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class GetServerShard extends Command {

    public GetServerShard() {
        this.name = "GetServerShard";
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

        final String[] commandExamples = {BotConstants.PREFIX + this.name + " 348110542667251712"};

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        final String[] args = StringUtil.separateArgs(commandEvent.getArgs(), 2);

        if (args.length < 2) {
            commandEvent.reply("Please provide a full configuration.");
            return;
        }

        String serverId = args[0].trim();
        String numberOfShards = args[1].trim();

        if (serverId.isEmpty()) {
            commandEvent.reply("Please provide a server ID.");
            return;
        }

        if (numberOfShards.isEmpty()) {
            commandEvent.reply("Please provide the number of shards.");
            return;
        }
        long shardId;
        try {
            shardId = (Long.parseLong(serverId) >> 22) % Long.parseLong(numberOfShards);
        } catch (NumberFormatException ex) {
            commandEvent.reply("Number format exception.");
            return;
        }

        commandEvent.reply(String.valueOf(shardId));
    }
}
