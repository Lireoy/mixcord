package bot.commands.mixer;

import bot.constants.BotConstants;
import bot.constants.DevConstants;
import bot.constants.HelpConstants;
import bot.constants.MixerConstants;
import bot.structures.enums.CommandCategory;
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
        this.help = HelpConstants.MIXER_USER_HELP;
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

        final String query = commandEvent.getArgs().trim();

        if (query.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
            return;
        }

        if (query.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
            return;
        }

        final JSONObject channel = MixerQuery.queryChannel(query);

        if (channel == null) {
            commandEvent.reactError();
            commandEvent.reply("Query response JSON was null, when requesting data for a user, " +
                    "please contact the developer: <@" + DevConstants.OWNER_ID + ">");
            return;
        }

        if (channel.isEmpty()) {
            commandEvent.reply("There is no such streamer...");
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

        final String trusted =
                "Verified: " + isVerified + "\n" +
                        "Partnered: " + isPartnered + "\n";

        final String status =
                "Online: " + isOnline + "\n" +
                        "Featured: " + isFeatured;

        String followers = "No followers.";
        if (channel.optInt("numFollowers") != 0)
            followers = String.valueOf(channel.optInt("numFollowers"));

        String streamTitle = "No stream title available.";
        if (!channel.optString("name").isEmpty())
            streamTitle = channel.getString("name");

        String currentGame = "No game available.";
        if (channel.opt("type") != JSONObject.NULL)
            currentGame = channel.getJSONObject("type").getString("name");

        String language = "No language available.";
        if (channel.opt("languageId") != JSONObject.NULL)
            language = channel.getString("languageId").toUpperCase();

        String targetAudience = "No audience available.";
        if (!channel.optString("audience").isEmpty())
            targetAudience = channel.getString("audience").toUpperCase();

        String viewersCurrent = "No viewers.";
        if (channel.optInt("viewersCurrent") != 0)
            viewersCurrent = String.valueOf(channel.getInt("viewersCurrent"));

        final String channelUrl = MixerConstants.HTTPS_MIXER_COM + username;
        final String liveStreamLink = "[Click here to watch on Mixer](" + channelUrl + ")";


        if (streaming) {
            // Live
            commandEvent.reply(new MixerEmbedBuilder(channel)
                    .setCustomAuthor()
                    .setThumbnail(avatarUrl)
                    .addField("Bio", bio, false)
                    .addField("Trusted", trusted, true)
                    .addField("Status", status, true)
                    .addField("Followers", followers, false)
                    .addField("Currently live", streamTitle, false)
                    .addField("Game", currentGame, true)
                    .addField("Viewers", viewersCurrent, true)
                    .addField("Language", language, true)
                    .addField("Target audience", targetAudience, true)
                    .addField("Link", liveStreamLink, false)
                    .setImage(liveThumbnail)
                    .build());
        } else {
            // Offline
            commandEvent.reply(new MixerEmbedBuilder(channel)
                    .setCustomAuthor()
                    .setCustomImage()
                    .setThumbnail(avatarUrl)
                    .addField("Bio", bio, false)
                    .addField("Trusted", trusted, true)
                    .addField("Status", status, true)
                    .addField("Followers", followers, true)
                    .build());
        }
    }
}