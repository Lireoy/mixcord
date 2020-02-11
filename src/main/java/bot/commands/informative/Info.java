package bot.commands.informative;

import bot.Constants;
import bot.structure.CommandCategory;
import bot.utils.EmbedSender;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.lang.management.ManagementFactory;

/**
 * Sends information about the bot to the user in a formatted embed.
 * <p>
 * Current uptime, guild count, member count,
 * current Java version, number of shards, used and free memory,
 * Mixcord website link, Discord server invite link,
 * Discord user names of developers.
 */
@Slf4j
public class Info extends Command {

    public Info() {
        this.name = "Info";
        this.help = "Shows information about the bot.";
        this.category = new Category(CommandCategory.INFORMATIVE.toString());
        this.guildOnly = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        // Calculate uptime
        // Taken from Almighty Alpaca
        //https://github.com/Java-Discord-Bot-System/Plugin-Uptime/blob/master/src/main/java/com/almightyalpaca/discord/bot/plugin/uptime/UptimePlugin.java#L28-L42
        final long duration = ManagementFactory.getRuntimeMXBean().getUptime();

        final long years = duration / 31104000000L;
        final long months = duration / 2592000000L % 12;
        final long days = duration / 86400000L % 30;
        final long hours = duration / 3600000L % 24;
        final long minutes = duration / 60000L % 60;
        final long seconds = duration / 1000L % 60;

        String uptime =
                (years == 0 ? "" : years + " years, ") +
                        (months == 0 ? "" : months + " months, ") +
                        (days == 0 ? "" : days + " days, ") +
                        (hours == 0 ? "" : hours + " hours, ") +
                        (minutes == 0 ? "" : minutes + " minutes, ") +
                        (seconds == 0 ? "" : seconds + " seconds, ");

        uptime = replaceLast(uptime);

        // Usage segment
        int guildCount = commandEvent.getJDA().getGuilds().size();
        int memberCount = 0;

        // Count members in each guild.
        for (int i = 0; i < guildCount; i++) {
            memberCount += commandEvent.getJDA().getGuilds().get(i).getMembers().size();
        }
        String usage = "· " + guildCount + " servers\n· " + memberCount + " members";

        // Get Java version
        String version = "· Java " + System.getProperty("java.version");

        // Shards
        long ping = commandEvent.getJDA().getGatewayPing();
        int shardId = commandEvent.getJDA().getShardInfo().getShardId();
        int totalShards = commandEvent.getJDA().getShardInfo().getShardTotal();
        String shards = "· Current shard: " + shardId + "\n· Shard latency: "
                + ping + "ms\n· Total shards: " + totalShards;

        // System
        Runtime rt = Runtime.getRuntime();
        String systemInfo = rt.freeMemory() / 1024 / 1024 + "MB / " + rt.maxMemory() / 1024 / 1024 + "MB";

        // Links
        String links = "· Website: " + Constants.WEBSITE + " \n" +
                "· Discord: " + Constants.DISCORD;

        // Developers
        String developers = "Lead Dev: Lireoy#4444\nConsultant: Akira#8185";

        // Infrastructure
        String infrastructure = "Provided by Akira#8185";

        // Footer
        String footer = commandEvent.getAuthor().getName() + "#" + commandEvent.getAuthor().getDiscriminator();
        String footerImg = commandEvent.getAuthor().getAvatarUrl();


        commandEvent.getTextChannel().sendMessage(
                new EmbedSender()
                        .setTitle("Mixcord")
                        .addField("Uptime", uptime, false)
                        .addField("Usage", usage, true)
                        .addField("Version", version, true)
                        .addField("Shards", shards, true)
                        .addField("System", systemInfo, false)
                        .addField("Links", links, false)
                        .addField("Developers", developers, false)
                        .addField("Infrastructure", infrastructure, false)
                        .build()
        ).queue();
    }

    private String replaceLast(final String text) {
        return text.replaceFirst("(?s)(.*)" + ", ", "$1" + "");
    }
}