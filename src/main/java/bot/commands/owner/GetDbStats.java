package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.structures.MixcordCommand;
import bot.utils.CommandUtil;
import bot.utils.MixerEmbedBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;

@Slf4j
public class GetDbStats extends MixcordCommand {

    public GetDbStats() {
        this.name = "GetDbStats";
        this.help = Locale.GET_DB_STATS_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("OWNER"));
        this.guildOnly = false;
        this.ownerCommand = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;

        String message = String.format(
                Locale.GET_DB_STATS_COMMAND_STATISTICS,
                DatabaseDriver.getInstance().countAllServers(),
                DatabaseDriver.getInstance().countAllStreamers(),
                DatabaseDriver.getInstance().countAllNotifs());

        commandEvent.reply(new MixerEmbedBuilder()
                .setTitle(Locale.GET_DB_STATS_COMMAND_STATISTICS_TITLE)
                .setDescription(message)
                .build());
    }
}
