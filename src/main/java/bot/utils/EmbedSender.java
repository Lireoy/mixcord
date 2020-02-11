package bot.utils;

import bot.Constants;
import bot.Mixcord;
import bot.structure.Notification;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

import java.time.Instant;

/**
 * This class extends {@link EmbedBuilder} and provides additional methods, which are customized
 * for Mixcord's use case.
 */
public class EmbedSender extends EmbedBuilder {

    private JSONObject mixerInfo;
    private Notification notif;

    /**
     * Sets the embed time and footer.
     */
    public EmbedSender() {
        this.setFooter(Constants.MIXCORD_IO_EMBED_FOOTER, Mixcord.getJda().getSelfUser().getAvatarUrl());
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
    public EmbedSender(Notification notif, JSONObject mixerInfo) {
        this.mixerInfo = mixerInfo;
        this.notif = notif;
        this.setCustomColor();
        this.setImage(Constants.MIXER_BANNER_DEFAULT);
        this.setFooter(Constants.MIXCORD_IO, Mixcord.getJda().getSelfUser().getAvatarUrl());
        this.setTimestamp(Instant.now());
    }

    /**
     * Sets the embed author and it's image based on the information in {@link EmbedSender#mixerInfo}.
     * If there's no profile picture, this method provides a default picture instead.
     *
     * @return The EmbedSender instance. Useful for chaining.
     */
    public EmbedSender setCustomAuthor() {
        if (mixerInfo == null) return this;

        Object authorImg = null;
        try {
            authorImg = mixerInfo.getJSONObject("user").get("avatarUrl");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // Handles "null" as a profile picture exception
        if (authorImg == JSONObject.NULL || authorImg == null) {
            authorImg = Constants.MIXER_PROFILE_PICTURE_DEFAULT;
        }
        this.setAuthor(mixerInfo.getString("token"), getChannelLink(), authorImg.toString());
        return this;
    }

    /**
     * Sets the embed title based on the information in {@link EmbedSender#mixerInfo}.
     *
     * @return The EmbedSender instance. Useful for chaining.
     */
    public EmbedSender setCustomTitle() {
        if (mixerInfo == null) return this;

        this.setTitle(mixerInfo.getString("name"), getChannelLink());
        return this;
    }

    /**
     * Sets the embed description based on the information in {@link EmbedSender#mixerInfo}.
     * The description includes game name, current viewers and a link.
     *
     * @return The EmbedSender instance. Useful for chaining.
     */
    public EmbedSender setCustomDescription() {
        if (mixerInfo == null) return this;

        String gameName = mixerInfo.getJSONObject("type").getString("name");
        String currentViewers = String.valueOf(mixerInfo.getJSONObject("type")
                .getInt("viewersCurrent"));

        String description = "Playing " + gameName + " for " + currentViewers + " viewers.\n"
                + "[Click Here to Watch](" + getChannelLink() + ")";
        this.setDescription(description);
        return this;
    }

    /**
     * Sets the embed color based on the information in {@link EmbedSender#mixerInfo}.
     */
    private void setCustomColor() {
        if (notif == null) return;

        this.setColor(HexUtil.formatForEmbed(notif.getEmbedColor()));
    }

    /**
     * Constructs a valid link for a streamer by concatenating a static link and the streamer's name.
     * The streamer name is from {@link EmbedSender#notif}.
     *
     * @return a String, which is a valid link for the streamer.
     */
    private String getChannelLink() {
        String streamerName = notif == null ? mixerInfo.getString("token") : notif.getStreamerName();
        return Constants.MIXER_COM + streamerName;
    }
}
