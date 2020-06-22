package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.structures.MixcordCommand;
import bot.utils.CommandUtil;
import bot.utils.StringUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;

@Slf4j
public class GetServerShard extends MixcordCommand {

    public GetServerShard() {
        this.name = "GetServerShard";
        this.category = new Category(Locale.CATEGORIES.get("OWNER"));
        this.arguments = "<server ID>, <total number of shards>";
        this.guildOnly = false;
        this.ownerCommand = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name + " 348110542667251712, 12"};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;

        final String[] args = StringUtil.separateArgs(commandEvent.getArgs(), 2);

        if (args.length < 2) {
            commandEvent.reply(Locale.GET_SERVER_SHARD_COMMAND_NO_FULL_CONFIG);
            return;
        }

        String serverId = args[0].trim();
        String numberOfShards = args[1].trim();

        if (serverId.isEmpty()) {
            commandEvent.reply(Locale.GET_SERVER_SHARD_COMMAND_NO_SERVER_ID);
            return;
        }

        if (numberOfShards.isEmpty()) {
            commandEvent.reply(Locale.GET_SERVER_SHARD_COMMAND_NO_SHARD_NUMBER);
            return;
        }
        long shardId;
        try {
            shardId = (Long.parseLong(serverId) >> 22) % Long.parseLong(numberOfShards);
        } catch (NumberFormatException ex) {
            commandEvent.reply(Locale.GET_SERVER_SHARD_COMMAND_EXCEPTION);
            return;
        }

        commandEvent.reply(String.valueOf(shardId));
    }
}
