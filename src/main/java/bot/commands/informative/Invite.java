package bot.commands.informative;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.structures.MixcordCommand;
import bot.utils.CommandUtil;
import bot.utils.MixerEmbedBuilder;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 * Sends the bot's invite link to the user in a formatted embed.
 */
@Slf4j
public class Invite extends MixcordCommand {

    public Invite() {
        this.name = "Invite";
        this.aliases = new String[]{"Inv", "GetOverHere"};
        this.help = Locale.INVITE_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("INFORMATIVE"));
        this.guildOnly = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.getInstance().checkHelp(this, commandEvent)) return;
        final String finalDescription = generateDescription(commandEvent);
        respond(commandEvent, finalDescription);
    }

    private String generateDescription(CommandEvent commandEvent) {
        final String clientId = commandEvent.getSelfUser().getId();
        return String.format(Locale.INVITE_COMMAND_INVITE_LINK, clientId);
    }

    private void respond(CommandEvent commandEvent, String finalDescription) {
        commandEvent.reactSuccess();
        commandEvent.reply(new MixerEmbedBuilder()
                .setAuthor(Locale.INVITE_COMMAND_INVITE_TITLE)
                .setDescription(finalDescription)
                .build());
    }
}
