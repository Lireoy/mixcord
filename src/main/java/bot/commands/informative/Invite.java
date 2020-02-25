package bot.commands.informative;

import bot.structure.enums.CommandCategory;
import bot.utils.HelpUtil;
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
public class Invite extends Command {

    public Invite() {
        this.name = "Invite";
        this.aliases = new String[]{"Inv", "GetOverHere"};
        this.help = "Provides an invite link so you can add this bot to your server.";
        this.category = new Category(CommandCategory.INFORMATIVE.toString());
        this.guildOnly = true;
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

        boolean helpResponse = HelpUtil.getInstance().sendCommandHelp(this, commandEvent);
        if (helpResponse) return;

        final String clientId = commandEvent.getSelfUser().getId();

        commandEvent.reactSuccess();
        commandEvent.reply(new MixerEmbedBuilder()
                .setAuthor("Invite")
                .setDescription("[Click Here to Invite Me](https://discordapp.com/oauth2/authorize?client_id=" + clientId + "&permissions=347200&scope=bot)")
                .build());
    }
}
