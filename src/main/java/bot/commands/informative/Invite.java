package bot.commands.informative;

import bot.Mixcord;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.time.Instant;

@Slf4j
public class Invite extends Command {

    public Invite() {
        this.name = "Invite";
        this.aliases = new String[]{"Inv", "GetOverHere"};
        this.help = "Provides an invite link so you can add this bot to your server.";
        this.guildOnly = true;
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
    }

    /**
     * Sends the bot's invite link to the author in a formatted embed,
     * with the author's name and discriminator and profile picture in the footer.
     * The embed's timestamp is set to the time of execution.
     *
     * @param commandEvent the event which triggered this command
     */
    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String footer = commandEvent.getAuthor().getName() + "#"
                + commandEvent.getAuthor().getDiscriminator();
        String footerImg = commandEvent.getAuthor().getAvatarUrl();

        String clientId = Mixcord.getJda().getSelfUser().getId();

        commandEvent.reactSuccess();
        commandEvent.reply(new EmbedBuilder()
                .setAuthor("Invite")
                .setDescription("[Click Here to Invite Me](https://discordapp.com/oauth2/authorize?client_id=" + clientId + "&permissions=347200&scope=bot)")
                .setFooter(footer, footerImg)
                .setTimestamp(Instant.now())
                .build());
    }
}
