package bot.commands.mixer;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.utils.HelpUtil;
import bot.utils.MixerEmbedBuilder;
import bot.utils.MixerQuery;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * Sends information about a specific Mixer user's social accounts to the Discord user in a formatted embed.
 * Only available socials are displayed.
 */
@Slf4j
public class MixerUserSocials extends Command {

    public MixerUserSocials() {
        this.name = "MixerUserSocials";
        this.help = Locale.MIXER_USER_SOCIALS_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("MIXER"));
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

        final String streamerName = validateQueryParam(commandEvent);
        if (streamerName == null) return;

        final JSONObject channel = validateMixerQuery(commandEvent, streamerName);
        if (channel == null) return;

        final JSONObject socials = channel.getJSONObject("user").optJSONObject("social");
        StringBuilder description = generateDescription(socials);

        respond(commandEvent, channel, description);
    }

    @Nullable
    private String validateQueryParam(CommandEvent commandEvent) {
        final String streamerName = commandEvent.getArgs().trim();

        if (streamerName.isEmpty()) {
            commandEvent.reply(Locale.MIXER_USER_SOCIALS_COMMAND_NO_STREAMER_NAME);
            return null;
        }

        if (streamerName.length() > 20) {
            commandEvent.reply(Locale.MIXER_USER_SOCIALS_COMMAND_TOO_LONG_NAME);
            return null;
        }
        return streamerName;
    }

    @Nullable
    private JSONObject validateMixerQuery(CommandEvent commandEvent, String streamerName) {
        final JSONObject channel = MixerQuery.queryChannel(streamerName);

        if (channel == null) {
            commandEvent.reactError();
            commandEvent.reply(Locale.MIXER_USER_SOCIALS_COMMAND_JSON_WAS_NULL);
            return null;
        }

        if (channel.isEmpty()) {
            commandEvent.reply(Locale.MIXER_USER_SOCIALS_COMMAND_NO_SUCH_STREAMER);
            return null;
        }
        return channel;
    }

    @NotNull
    private StringBuilder generateDescription(JSONObject socials) {
        StringBuilder description = new StringBuilder();

        if (socials.has("facebook")) {
            description.append(String.format(
                    Locale.MIXER_USER_SOCIALS_COMMAND_FACEBOOK,
                    socials.optString("facebook")));

        }

        if (socials.has("instagram")) {
            description.append(String.format(
                    Locale.MIXER_USER_SOCIALS_COMMAND_INSTAGRAM,
                    socials.optString("instagram")));
        }

        if (socials.has("twitter")) {
            description.append(String.format(
                    Locale.MIXER_USER_SOCIALS_COMMAND_TWITTER,
                    socials.optString("twitter")));
        }

        if (socials.has("youtube")) {
            description.append(String.format(
                    Locale.MIXER_USER_SOCIALS_COMMAND_YOUTUBE,
                    socials.optString("youtube")));
        }

        if (socials.has("discord")) {
            description.append(String.format(
                    Locale.MIXER_USER_SOCIALS_COMMAND_DISCORD,
                    socials.optString("discord")));
        }

        if (socials.has("patreon")) {
            description.append(String.format(
                    Locale.MIXER_USER_SOCIALS_COMMAND_PATREON,
                    socials.optString("patreon")));
        }

        if (socials.has("player")) {
            description.append(String.format(
                    Locale.MIXER_USER_SOCIALS_COMMAND_PLAYER,
                    socials.optString("player")));
        }

        if (socials.has("soundcloud")) {
            description.append(String.format(
                    Locale.MIXER_USER_SOCIALS_COMMAND_SOUNDCLOUD,
                    socials.optString("soundcloud")));
        }

        if (socials.has("steam")) {
            description.append(String.format(
                    Locale.MIXER_USER_SOCIALS_COMMAND_STEAM,
                    socials.optString("steam")));
        }
        // TODO: FIND A SPREADSHIRT LINKED ACCOUNT
        return description;
    }

    private void respond(CommandEvent commandEvent, JSONObject channel, StringBuilder description) {
        if (description.toString().isEmpty()) {
            description.append(Locale.MIXER_USER_SOCIALS_COMMAND_NO_SOCIALS);
            commandEvent.reply(new MixerEmbedBuilder(channel)
                    .setCustomAuthor()
                    .setDescription(description)
                    .build());
        } else {
            commandEvent.reply(new MixerEmbedBuilder(channel)
                    .setCustomAuthor()
                    .setCustomThumbnail()
                    .setDescription(description)
                    .build());
        }
    }
}
