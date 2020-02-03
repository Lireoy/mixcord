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
public class MixerUserSocials extends Command {

    public MixerUserSocials() {
        this.name = "MixerUserSocials";
        this.help = "Displays a Mixer user's social profiles.";
        this.category = new Category("mixer");
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
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String streamerName = commandEvent.getArgs().trim();

        // Empty args check
        if (streamerName.isEmpty()) {
            commandEvent.reply("Please provide a streamer name!");
        } else if (streamerName.length() > 20) {
            commandEvent.reply("This name is too long! Please provide a shorter one!");
        } else {
            JSONObject channel = MixerQuery.queryChannel(streamerName);
            if (channel == null) {
                commandEvent.reactError();
                return;
            }

            String username = channel.getString("token");
            String channelUrl = Constants.MIXER_COM + username;
            JSONObject user = channel.getJSONObject("user");
            JSONObject socials = user.getJSONObject("social");
            Object avatarUrl = user.get("avatarUrl");

            // Handles "null" as a profile picture exception
            if (avatarUrl == JSONObject.NULL) {
                avatarUrl = Constants.MIXER_PROFILE_PICTURE_DEFAULT;
            }

            StringBuilder description = new StringBuilder();
            boolean hasSocial = false;

            if (socials.has("facebook")) {
                description.append("· [Facebook](").append(socials.getString("facebook")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("instagram")) {
                description.append("· [Instagram](").append(socials.getString("instagram")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("twitter")) {
                description.append("· [Twitter](").append(socials.getString("twitter")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("youtube")) {
                description.append("· [Youtube](").append(socials.getString("youtube")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("discord")) {
                description.append("· [Discord](").append(socials.getString("discord")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("patreon")) {
                description.append("· [Patreon](").append(socials.getString("patreon")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("player")) {
                description.append("· [Player](").append(socials.getString("player")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("soundcloud")) {
                description.append("· [Soundcloud](").append(socials.getString("soundcloud")).append(")\n");
                hasSocial = true;
            }
            if (socials.has("steam")) {
                description.append("· [Steam](").append(socials.getString("steam")).append(")\n");
                hasSocial = true;
            }

            String footer = commandEvent.getAuthor().getName() + "#"
                    + commandEvent.getAuthor().getDiscriminator();
            String footerImg = commandEvent.getAuthor().getAvatarUrl();

            if (hasSocial) {
                commandEvent.reply(new EmbedBuilder()
                        .setAuthor(username, channelUrl, avatarUrl.toString())
                        .setDescription(description)
                        .setFooter(footer, footerImg)
                        .setTimestamp(Instant.now())
                        .build());
            } else {
                description = new StringBuilder("No socials are available.");
                commandEvent.reply(new EmbedBuilder()
                        .setAuthor(username, channelUrl, avatarUrl.toString())
                        .setDescription(description)
                        .setFooter(footer, footerImg)
                        .setTimestamp(Instant.now())
                        .build());
            }
        }
    }
}
