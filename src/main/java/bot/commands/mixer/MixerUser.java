package bot.commands.mixer;

import bot.Constants;
import bot.Mixcord;
import bot.utils.MixerQuery;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.time.Instant;

@Slf4j
public class MixerUser extends Command {

    public MixerUser() {
        this.name = "MixerUser";
        this.help = "Displays a Mixer user's data.";
        this.arguments = "<streamer name>";
        this.guildOnly = true;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String query = commandEvent.getArgs().trim();

        // Empty args check
        if (query.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
        } else if (query.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
        } else {
            JSONObject channel = MixerQuery.queryChannel(query);
            if (channel == null) {
                commandEvent.reactError();
                commandEvent.reply("Query response JSON was null, when requesting data for a user, " +
                        "please contact the developer: <@331756964801544202>");
                return;
            }

            // This line avoids lots of spaghetti code
            JSONObject user = channel.getJSONObject("user");

            String username = channel.getString("token");
            int id = channel.getInt("id");
            String liveThumbnail = Constants.MIXER_THUMB_PRE + id + Constants.MIXER_THUMB_POST;
            String avatarUrl = user.getString("avatarUrl");
            Object bio = user.get("bio") == JSONObject.NULL ? "No bio available." : user.get("bio");
            String isVerified = user.getBoolean("verified") ? Constants.SUCCESS : Constants.ERROR;
            String isPartnered = channel.getBoolean("partnered") ? Constants.SUCCESS : Constants.ERROR;
            boolean streaming = channel.getBoolean("online");
            String isOnline = channel.getBoolean("online") ? Constants.SUCCESS : Constants.ERROR;
            String isFeatured = channel.getBoolean("featured") ? Constants.SUCCESS : Constants.ERROR;

            String trusted =
                    "Verified: " + isVerified + "\n" +
                            "Partnered: " + isPartnered + "\n";

            String status =
                    "Online: " + isOnline + "\n" +
                            "Featured: " + isFeatured;

            String followers = String.valueOf(channel.getInt("numFollowers"));
            String streamTitle = channel.getString("name");
            String currentGame = channel.getJSONObject("type").getString("name");
            String language = channel.getString("languageId").toUpperCase();
            String targetAudience = channel.getString("audience").toUpperCase();
            String viewersCurrent = String.valueOf(channel.getInt("viewersCurrent"));
            Object bannerUrl = channel.get("bannerUrl");
            String channelUrl = Constants.MIXER_COM + username;
            String liveStreamLink = "[Click here to watch on Mixer](" + channelUrl + ")";
            String image = bannerUrl == JSONObject.NULL ? Constants.MIXER_BANNER_DEFAULT : bannerUrl.toString();


            String footer = commandEvent.getAuthor().getName() + "#" +
                    commandEvent.getAuthor().getDiscriminator();
            String footerImg = commandEvent.getAuthor().getAvatarUrl();

            if (streaming) {
                // Live
                commandEvent.reply(
                        new EmbedBuilder()
                                .setAuthor(username, channelUrl, avatarUrl)
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
                                .setFooter(footer, footerImg)
                                .setTimestamp(Instant.now())
                                .build()
                );
            } else {
                // Offline
                commandEvent.reply(
                        new EmbedBuilder()
                                .setAuthor(username, channelUrl, avatarUrl)
                                .setThumbnail(avatarUrl)
                                .addField("Bio", bio.toString(), false)
                                .addField("Trusted", trusted, true)
                                .addField("Status", status, true)
                                .addField("Followers", followers, true)
                                .setImage(image)
                                .setFooter(footer, footerImg)
                                .setTimestamp(Instant.now())
                                .build()
                );
            }
        }
    }
}
