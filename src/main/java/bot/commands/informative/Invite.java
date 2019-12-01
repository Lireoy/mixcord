package bot.commands.informative;

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

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String footer = commandEvent.getAuthor().getName() + "#"
                + commandEvent.getAuthor().getDiscriminator();
        String footerImg = commandEvent.getAuthor().getAvatarUrl();

        commandEvent.reactSuccess();
        commandEvent.reply(new EmbedBuilder()
                .setAuthor("Invite")
                .setDescription("[Click Here to Invite Me](https://discordapp.com/oauth2/authorize?client_id=551114019327574026&permissions=347200&scope=bot)")
                .setFooter(footer, footerImg)
                .setTimestamp(Instant.now())
                .build());
    }
}
