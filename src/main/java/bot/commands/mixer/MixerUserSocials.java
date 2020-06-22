package bot.commands.mixer;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.structures.MixcordCommand;
import bot.utils.CommandUtil;
import bot.utils.MixerEmbedBuilder;
import bot.utils.MixerQuery;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * Sends information about a specific Mixer user's social accounts to the Discord user in a formatted embed.
 * Only available socials are displayed.
 */
@Slf4j
public class MixerUserSocials extends MixcordCommand {

    public MixerUserSocials() {
        this.name = "MixerUserSocials";
        this.help = Locale.MIXER_USER_SOCIALS_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("MIXER"));
        this.arguments = "<streamer name>";
        this.guildOnly = true;
        this.commandExamples = new String[]{BotConstants.PREFIX + this.name + " shroud"};
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (CommandUtil.checkHelp(this, commandEvent)) return;
        if (!isValidQueryParam(commandEvent)) return;

        final JSONObject channel = validateMixerQuery(commandEvent, commandEvent.getArgs().trim());
        if (channel == null) return;

        final JSONObject socials = channel.getJSONObject("user").optJSONObject("social");
        StringBuilder description = generateDescription(socials);

        respond(commandEvent, channel, description);
    }

    private boolean isValidQueryParam(CommandEvent commandEvent) {
        if (commandEvent.getArgs().trim().isEmpty()) {
            commandEvent.reply(Locale.MIXER_USER_SOCIALS_COMMAND_NO_STREAMER_NAME);
            return false;
        }

        if (commandEvent.getArgs().trim().length() > 20) {
            commandEvent.reply(Locale.MIXER_USER_SOCIALS_COMMAND_TOO_LONG_NAME);
            return false;
        }
        return true;
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
