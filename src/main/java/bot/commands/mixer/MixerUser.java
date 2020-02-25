package bot.commands.mixer;

import bot.Constants;
import bot.structure.enums.CommandCategory;
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
 * Sends information about a specific Mixer user to the Discord user in a formatted embed.
 * Information varies based on the current state of the channel. (Live or offline)
 */
@Slf4j
public class MixerUser extends Command {

    public MixerUser() {
        this.name = "MixerUser";
        this.help = "Displays a Mixer user's data.";
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

        boolean helpResponse = HelpUtil.getInstance().sendCommandHelp(this, commandEvent);
        if (helpResponse) return;

        final String query = commandEvent.getArgs().trim();

        // Empty args check
        if (query.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
        } else if (query.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
        } else {
            final JSONObject channel = MixerQuery.queryChannel(query);
            if (channel == null) {
                commandEvent.reactError();
                commandEvent.reply("Query response JSON was null, when requesting data for a user, " +
                        "please contact the developer: <@" + Constants.OWNER_ID + ">");
                return;
            }

            final int id = channel.getInt("id");
            final String liveThumbnail = Constants.MIXER_THUMB_PRE + id + Constants.MIXER_THUMB_POST;
            final String username = channel.getString("token");

            final JSONObject user = channel.getJSONObject("user");
            final Object avatarObject = user.get("avatarUrl");
            final String avatarUrl = avatarObject == JSONObject.NULL ? Constants.MIXER_PROFILE_PICTURE_DEFAULT : avatarObject.toString();
            final Object bio = user.get("bio") == JSONObject.NULL ? "No bio available." : user.get("bio");
            final String isVerified = user.getBoolean("verified") ? Constants.SUCCESS : Constants.ERROR;
            final String isPartnered = channel.getBoolean("partnered") ? Constants.SUCCESS : Constants.ERROR;
            final boolean streaming = channel.getBoolean("online");
            final String isOnline = channel.getBoolean("online") ? Constants.SUCCESS : Constants.ERROR;
            final String isFeatured = channel.getBoolean("featured") ? Constants.SUCCESS : Constants.ERROR;

            final String trusted =
                    "Verified: " + isVerified + "\n" +
                            "Partnered: " + isPartnered + "\n";

            final String status =
                    "Online: " + isOnline + "\n" +
                            "Featured: " + isFeatured;

            final String followers = String.valueOf(channel.getInt("numFollowers"));
            final String streamTitle = channel.getString("name");
            final String currentGame = channel.getJSONObject("type").getString("name");
            final String language = channel.getString("languageId").toUpperCase();
            final String targetAudience = channel.getString("audience").toUpperCase();
            final String viewersCurrent = String.valueOf(channel.getInt("viewersCurrent"));
            final String channelUrl = Constants.HTTPS_MIXER_COM + username;
            final String liveStreamLink = "[Click here to watch on Mixer](" + channelUrl + ")";


            if (streaming) {
                // Live
                commandEvent.reply(new MixerEmbedBuilder(channel)
                        .setCustomAuthor()
                        .setThumbnail(avatarUrl)
                        .addField("Bio", bio.toString(), false)
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
                        .addField("Bio", bio.toString(), false)
                        .addField("Trusted", trusted, true)
                        .addField("Status", status, true)
                        .addField("Followers", followers, true)
                        .build());
            }
        }
    }
}
