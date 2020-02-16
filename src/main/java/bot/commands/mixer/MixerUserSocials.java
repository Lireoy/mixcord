package bot.commands.mixer;

import bot.structure.CommandCategory;
import bot.utils.MixerEmbedBuilder;
import bot.utils.MixerQuery;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

/**
 * Sends information about a specific Mixer user's social accounts to the Discord user in a formatted embed.
 * Only available socials are displayed.
 */
@Slf4j
public class MixerUserSocials extends Command {

    public MixerUserSocials() {
        this.name = "MixerUserSocials";
        this.help = "Displays a Mixer user's social profiles.";
        this.category = new Category(CommandCategory.MIXER.toString());
        this.arguments = "<streamer name>";
        this.guildOnly = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        final String streamerName = commandEvent.getArgs().trim();

        // Empty args check
        if (streamerName.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
        } else if (streamerName.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
        } else {
            final JSONObject channel = MixerQuery.queryChannel(streamerName);
            if (channel == null) {
                commandEvent.reply("There is no such streamer...");
                commandEvent.reactError();
                return;
            }

            final JSONObject user = channel.getJSONObject("user");
            final JSONObject socials = user.getJSONObject("social");


            StringBuilder description = new StringBuilder();
            boolean hasSocial = false;

            if (socials.has("facebook")) {
                description.append("· [Facebook](").append(socials.getString("facebook")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("instagram")) {
                description.append("· [Instagram](").append(socials.getString("instagram")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("twitter")) {
                description.append("· [Twitter](").append(socials.getString("twitter")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("youtube")) {
                description.append("· [Youtube](").append(socials.getString("youtube")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("discord")) {
                description.append("· [Discord](").append(socials.getString("discord")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("patreon")) {
                description.append("· [Patreon](").append(socials.getString("patreon")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("player")) {
                description.append("· [Player](").append(socials.getString("player")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("soundcloud")) {
                description.append("· [Soundcloud](").append(socials.getString("soundcloud")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("steam")) {
                description.append("· [Steam](").append(socials.getString("steam")).append(")\n");
                hasSocial = true;
            }
            // TODO: FIND A SPREADSHIRT LINKED ACCOUNT

            if (hasSocial) {
                commandEvent.reply(new MixerEmbedBuilder(channel)
                        .setCustomAuthor()
                        .setCustomThumbnail()
                        .setDescription(description)
                        .build());
            } else {
                description = new StringBuilder("No socials are available.");
                commandEvent.reply(new MixerEmbedBuilder(channel)
                        .setCustomAuthor()
                        .setDescription(description)
                        .build());
            }
        }
    }
}
