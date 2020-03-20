package bot.commands.mixer;

import bot.constants.BotConstants;
import bot.constants.HelpConstants;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
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
        this.help = HelpConstants.MIXER_USER_SOCIALS_HELP;
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

        final String[] commandExamples = {BotConstants.PREFIX + this.name + " shroud"};

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        final String streamerName = commandEvent.getArgs().trim();


        if (streamerName.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
            return;
        }

        final JSONObject channel = MixerQuery.queryChannel(streamerName);

        if (channel == null) {
            commandEvent.reactError();
            commandEvent.reply("Query response JSON was null, when requesting data for a user, " +
                    "please contact the developer: **Lireoy#4444**");
            return;
        }

        if (channel.isEmpty()) {
            commandEvent.reply("There is no such streamer...");
            return;
        }

        final JSONObject user = channel.getJSONObject("user");
        final JSONObject socials = user.optJSONObject("social");

        StringBuilder description = new StringBuilder();
        boolean hasSocial = false;

        if (socials.has("facebook")) {
            description.append("· [Facebook](").append(socials.optString("facebook")).append(")\n");
            hasSocial = true;
        }
        if (socials.has("instagram")) {
            description.append("· [Instagram](").append(socials.optString("instagram")).append(")\n");
            hasSocial = true;
        }
        if (socials.has("twitter")) {
            description.append("· [Twitter](").append(socials.optString("twitter")).append(")\n");
            hasSocial = true;
        }
        if (socials.has("youtube")) {
            description.append("· [Youtube](").append(socials.optString("youtube")).append(")\n");
            hasSocial = true;
        }
        if (socials.has("discord")) {
            description.append("· [Discord](").append(socials.optString("discord")).append(")\n");
            hasSocial = true;
        }
        if (socials.has("patreon")) {
            description.append("· [Patreon](").append(socials.optString("patreon")).append(")\n");
            hasSocial = true;
        }
        if (socials.has("player")) {
            description.append("· [Player](").append(socials.optString("player")).append(")\n");
            hasSocial = true;
        }
        if (socials.has("soundcloud")) {
            description.append("· [Soundcloud](").append(socials.optString("soundcloud")).append(")\n");
            hasSocial = true;
        }
        if (socials.has("steam")) {
            description.append("· [Steam](").append(socials.optString("steam")).append(")\n");
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
