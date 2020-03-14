package bot.utils;

import bot.constants.BotConstants;
import bot.constants.MixerConstants;
import bot.services.ShardService;
import bot.structures.Notification;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

import java.time.Instant;

/**
 * This class extends {@link EmbedBuilder} and provides additional methods, which are customized
 * for Mixcord's use case.
 */
public class MixerEmbedBuilder extends EmbedBuilder {

    private JSONObject mixerInfo;
    private Notification notif;

    /**
     * Sets the embed time and footer.
     */
    public MixerEmbedBuilder() {
        this.setFooter(BotConstants.MIXCORD_IO_EMBED_FOOTER, ShardService.getInstance().getShards().get(0).getSelfUser().getAvatarUrl());
        this.setTimestamp(Instant.now());
    }

    public MixerEmbedBuilder(JSONObject mixerInfo) {
        this.mixerInfo = mixerInfo;
        this.setCustomColor();
        this.setFooter(BotConstants.MIXCORD_IO_EMBED_FOOTER, ShardService.getInstance().getShards().get(0).getSelfUser().getAvatarUrl());
        this.setTimestamp(Instant.now());
    }

    /**
     * Sets the embed color, image, footer and time.
     * It also provides method options to set additional details
     * with the specified parameters.
     *
     * @param notif     the JSON object which has information from the database
     * @param mixerInfo the JSON object which has information about the streamer from Mixer
     */
    public MixerEmbedBuilder(Notification notif, JSONObject mixerInfo) {
        this.mixerInfo = mixerInfo;
        this.notif = notif;
        this.setCustomColor();
        this.setImage(MixerConstants.MIXER_BANNER_DEFAULT);
        this.setFooter(BotConstants.MIXCORD_IO_EMBED_FOOTER,
                ShardService.getInstance().getShards().get(0).getSelfUser().getAvatarUrl());
        this.setTimestamp(Instant.now());
    }

    /**
     * Sets the embed author and it's image based on the information in {@link MixerEmbedBuilder#mixerInfo}.
     * If there's no profile picture, this method provides a default picture instead.
     *
     * @return The EmbedSender instance. Useful for chaining.
     */
    public MixerEmbedBuilder setCustomAuthor() {
        if (mixerInfo == null) return this;

        String authorImg = MixerConstants.MIXER_PROFILE_PICTURE_DEFAULT;
        JSONObject user = mixerInfo.getJSONObject("user");
        if (user.opt("avatarUrl") != JSONObject.NULL)
            authorImg = String.valueOf(user.opt("avatarUrl"));

        this.setAuthor(mixerInfo.getString("token"), getChannelLink(), authorImg);
        return this;
    }

    /**
     * Sets the embed title based on the information in {@link MixerEmbedBuilder#mixerInfo}.
     *
     * @return The EmbedSender instance. Useful for chaining.
     */
    public MixerEmbedBuilder setCustomTitle() {
        if (mixerInfo == null) return this;


        String title = "No title available.";
        if (mixerInfo.opt("name") != JSONObject.NULL)
            title = mixerInfo.getString("name");

        this.setTitle(title, getChannelLink());
        return this;
    }

    public MixerEmbedBuilder setCustomImage() {
        String bannerUrl = MixerConstants.MIXER_BANNER_DEFAULT;
        if (mixerInfo != null) {
            if (mixerInfo.opt("bannerUrl") != JSONObject.NULL)
                bannerUrl = mixerInfo.getString("bannerUrl");
        }

        this.setImage(bannerUrl);
        return this;
    }

    public MixerEmbedBuilder setCustomThumbnail() {
        String avatarUrl = MixerConstants.MIXER_PROFILE_PICTURE_DEFAULT;
        if (mixerInfo != null) {
            JSONObject user = mixerInfo.getJSONObject("user");
            if (user.opt("avatarUrl") != JSONObject.NULL)
                avatarUrl = user.getString("avatarUrl");
        }

        this.setThumbnail(avatarUrl);
        return this;
    }

    /**
     * Sets the embed description based on the information in {@link MixerEmbedBuilder#mixerInfo}.
     * The description includes game name, current viewers and a link.
     *
     * @return The EmbedSender instance. Useful for chaining.
     */
    public MixerEmbedBuilder setCustomDescription() {
        if (mixerInfo == null) return this;

        String currentGame = "No game available.";
        if (mixerInfo.opt("type") != JSONObject.NULL)
            currentGame = mixerInfo.getJSONObject("type").getString("name");

        String viewersCurrent = "No viewers.";
        if (mixerInfo.optInt("viewersCurrent") != 0)
            viewersCurrent = String.valueOf(mixerInfo.getInt("viewersCurrent"));

        String description = "Playing %s for %s viewers.\n[Click Here to Watch](%s)";
        String finalDescription = String.format(description, currentGame, viewersCurrent, getChannelLink());

        this.setDescription(finalDescription);
        return this;
    }

    /**
     * Sets the embed color based on the information in {@link MixerEmbedBuilder#mixerInfo}.
     */
    private void setCustomColor() {
        if (notif == null) return;

        this.setColor(HexUtil.getInstance().formatForEmbed(notif.getEmbedColor()));
    }

    /**
     * Constructs a valid link for a streamer by concatenating a static link and the streamer's name.
     * The streamer name is from {@link MixerEmbedBuilder#notif}.
     *
     * @return a String, which is a valid link for the streamer.
     */
    private String getChannelLink() {
        String streamerName = notif == null ? mixerInfo.getString("token") : notif.getStreamerName();
        return MixerConstants.HTTPS_MIXER_COM + streamerName;
    }
}
