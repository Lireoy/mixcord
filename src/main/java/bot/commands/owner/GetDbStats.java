package bot.commands.owner;

import bot.constants.BotConstants;
import bot.database.DatabaseDriver;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
import bot.utils.MixerEmbedBuilder;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class GetDbStats extends Command {

    public GetDbStats() {
        this.name = "GetDbStats";
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

        String message = "· Guilds: %s\n· Streamers: %s\n· Notifications: %s";
        message = String.format(message,
                DatabaseDriver.getInstance().countAllGuilds(),
                DatabaseDriver.getInstance().countAllStreamers(),
                DatabaseDriver.getInstance().countAllNotifs());

        commandEvent.reply(new MixerEmbedBuilder()
                .setTitle("Database statistics")
                .setDescription(message)
                .build());
    }
}
