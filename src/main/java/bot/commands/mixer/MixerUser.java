package bot.commands.mixer;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.constants.MixerConstants;
import bot.utils.HelpUtil;
import bot.utils.MixerEmbedBuilder;
import bot.utils.MixerQuery;
import bot.utils.StringUtil;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

/**
 * Sends information about a specific Mixer user to the Discord user in a formatted embed.
 * Information varies based on the current state of the channel. (Live or offline)
 */
@Slf4j
public class MixerUser extends Command {

    public MixerUser() {
        this.name = "MixerUser";
        this.help = Locale.MIXER_USER_COMMAND_HELP;
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

        final String query = commandEvent.getArgs().trim();

        if (query.isEmpty()) {
            commandEvent.reply(Locale.MIXER_USER_COMMAND_NO_STREAMER_NAME);
            return;
        }

        if (query.length() > 20) {
            commandEvent.reply(Locale.MIXER_USER_COMMAND_TOO_LONG_NAME);
            return;
        }

        final JSONObject channel = MixerQuery.queryChannel(query);

        if (channel == null) {
            commandEvent.reactError();
            commandEvent.reply(Locale.MIXER_USER_COMMAND_JSON_WAS_NULL);
            return;
        }

        if (channel.isEmpty()) {
            commandEvent.reply(Locale.MIXER_USER_COMMAND_NO_SUCH_STREAMER);
            return;
        }

        final int id = channel.getInt("id");
        final String liveThumbnail = MixerConstants.MIXER_THUMB_PRE + id +
                MixerConstants.MIXER_THUMB_POST + "?" + StringUtil.generateRandomString(5, 6);
        final String username = channel.getString("token");
        final JSONObject user = channel.getJSONObject("user");

        String avatarUrl = MixerConstants.MIXER_PROFILE_PICTURE_DEFAULT;
        if (user.opt("avatarUrl") != JSONObject.NULL)
            avatarUrl = user.getString("avatarUrl");

        String bio = "No bio available.";
        if (!user.optString("bio").isEmpty())
            bio = user.getString("bio");

        String isVerified = BotConstants.ERROR;
        if (user.optBoolean("verified"))
            isVerified = BotConstants.SUCCESS;

        String isPartnered = BotConstants.ERROR;
        if (user.optBoolean("partnered"))
            isPartnered = BotConstants.SUCCESS;

        final boolean streaming = channel.optBoolean("online");

        String isOnline = BotConstants.ERROR;
        if (channel.optBoolean("online"))
            isOnline = BotConstants.SUCCESS;

        String isFeatured = BotConstants.ERROR;
        if (channel.optBoolean("featured"))
            isFeatured = BotConstants.SUCCESS;

        final String trusted = String.format(
                Locale.MIXER_USER_COMMAND_TRUSTED,
                isVerified,
                isPartnered);

        final String status = String.format(
                Locale.MIXER_USER_COMMAND_STATUS,
                isOnline,
                isFeatured);


        String followers = Locale.MIXER_USER_COMMAND_NO_FOLLOWERS;
        if (channel.optInt("numFollowers") != 0)
            followers = String.valueOf(channel.optInt("numFollowers"));

        String streamTitle = Locale.MIXER_USER_COMMAND_NO_STREAM_TITLE;
        if (!channel.optString("name").isEmpty())
            streamTitle = channel.getString("name");

        String currentGame = Locale.MIXER_USER_COMMAND_NO_GAME;
        if (channel.opt("type") != JSONObject.NULL)
            currentGame = channel.getJSONObject("type").getString("name");

        String language = Locale.MIXER_USER_COMMAND_NO_LANGUAGE;
        if (channel.opt("languageId") != JSONObject.NULL)
            language = channel.getString("languageId").toUpperCase();

        String targetAudience = Locale.MIXER_USER_COMMAND_NO_AUIDIENCE;
        if (!channel.optString("audience").isEmpty())
            targetAudience = channel.getString("audience").toUpperCase();

        String viewersCurrent = "0";
        if (channel.optInt("viewersCurrent") != 0)
            viewersCurrent = String.valueOf(channel.getInt("viewersCurrent"));

        final String channelUrl = MixerConstants.HTTPS_MIXER_COM + username;
        final String liveStreamLink = String.format(
                Locale.MIXER_USER_COMMAND_LIVE_STREAM_LINK,
                channelUrl);


        if (streaming) {
            // Live
            commandEvent.reply(new MixerEmbedBuilder(channel)
                    .setCustomAuthor()
                    .setThumbnail(avatarUrl)
                    .addField(
                            Locale.MIXER_USER_COMMAND_BIO_TITLE,
                            bio,
                            false)
                    .addField(
                            Locale.MIXER_USER_COMMAND_TRUSTED_TITLE,
                            trusted,
                            true)
                    .addField(
                            Locale.MIXER_USER_COMMAND_STATUS_TITLE,
                            status,
                            true)
                    .addField(
                            Locale.MIXER_USER_COMMAND_FOLLOWERS_TITLE,
                            followers,
                            false)
                    .addField(
                            Locale.MIXER_USER_COMMAND_CURRENTLY_LIVE_TITLE,
                            streamTitle,
                            false)
                    .addField(
                            Locale.MIXER_USER_COMMAND_GAME_TITLE,
                            currentGame,
                            true)
                    .addField(
                            Locale.MIXER_USER_COMMAND_VIEWERS_TITLE,
                            viewersCurrent,
                            true)
                    .addField(
                            Locale.MIXER_USER_COMMAND_LANGUAGE_TITLE,
                            language,
                            true)
                    .addField(
                            Locale.MIXER_USER_COMMAND_TARGET_AUDIENCE_TITLE,
                            targetAudience,
                            true)
                    .addField(
                            Locale.MIXER_USER_COMMAND_LINK_TITLE,
                            liveStreamLink,
                            false)
                    .setImage(liveThumbnail)
                    .build());
        } else {
            // Offline
            commandEvent.reply(new MixerEmbedBuilder(channel)
                    .setCustomAuthor()
                    .setCustomImage()
                    .setThumbnail(avatarUrl)
                    .addField(
                            Locale.MIXER_USER_COMMAND_BIO_TITLE,
                            bio,
                            false)
                    .addField(
                            Locale.INFO_COMMAND_INFRASTRUCTURE_TITLE,
                            trusted,
                            true)
                    .addField(
                            Locale.MIXER_USER_COMMAND_STATUS_TITLE,
                            status,
                            true)
                    .addField(
                            Locale.MIXER_USER_COMMAND_FOLLOWERS_TITLE,
                            followers,
                            true)
                    .build());
        }
    }
}