package bot.utils;

import bot.Constants;
import bot.Mixcord;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

import java.time.Instant;

public class EmbedSender extends EmbedBuilder {

    private JSONObject mixerInfo;
    private JSONObject dbInfo;

    public EmbedSender() {
        this.setFooter(Constants.MIXCORD_IO, Mixcord.getJda().getSelfUser().getAvatarUrl());
        this.setTimestamp(Instant.now());
    }

    public EmbedSender(JSONObject mixerInfo, JSONObject dbInfo) {
        this.mixerInfo = mixerInfo;
        this.dbInfo = dbInfo;
        this.setColor();
        this.setImage(Constants.MIXER_BANNER_DEFAULT);
        this.setFooter(Constants.MIXCORD_IO, Mixcord.getJda().getSelfUser().getAvatarUrl());
        this.setTimestamp(Instant.now());
    }

    public EmbedSender setAuthor() {
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

    public EmbedSender setTitle() {
        this.setTitle(mixerInfo.getString("name"), getChannelLink());
        return this;
    }

    public EmbedSender setDescription() {
        String gameName = mixerInfo.getJSONObject("type").getString("name");
        String currentViewers = String.valueOf(mixerInfo.getJSONObject("type")
                .getInt("viewersCurrent"));

        String description = "Playing " + gameName + " for " + currentViewers + " viewers.\n"
                + "[Click Here to Watch](" + getChannelLink() + ")";
        this.setDescription(description);
        return this;
    }

    private void setColor() {
        this.setColor(HexUtil.formatForEmbed(dbInfo.getString("embedColor")));
    }

    private String getChannelLink() {
        return Constants.MIXER_COM + dbInfo.getString("streamerName");
    }
}
