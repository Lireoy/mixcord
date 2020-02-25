package bot.commands.informative;

import bot.Constants;
import bot.structure.enums.CommandCategory;
import bot.utils.HelpUtil;
import bot.utils.MixerEmbedBuilder;
import bot.utils.StringUtil;
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
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        boolean helpResponse = HelpUtil.getInstance().sendCommandHelp(this, commandEvent);
        if (helpResponse) return;

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

        uptime = StringUtil.replaceLastComma(uptime);

        // Usage segment
        final int guildCount = commandEvent.getJDA().getGuilds().size();
        int memberCount = 0;

        // Count members in each guild.
        for (int i = 0; i < guildCount; i++) {
            memberCount += commandEvent.getJDA().getGuilds().get(i).getMembers().size();
        }
        String usage = "· " + guildCount + " servers\n· " + memberCount + " members";

        // Get Java version
        String version = "· Java " + System.getProperty("java.version");

        // Shards
        final long ping = commandEvent.getJDA().getGatewayPing();
        final int shardId = commandEvent.getJDA().getShardInfo().getShardId();
        final int totalShards = commandEvent.getJDA().getShardInfo().getShardTotal();
        String shards = "· Current shard: " + shardId + "\n· Shard latency: "
                + ping + "ms\n· Total shards: " + totalShards;

        // System
        Runtime rt = Runtime.getRuntime();
        final String systemInfo = rt.freeMemory() / 1024 / 1024 + "MB / " + rt.maxMemory() / 1024 / 1024 + "MB";

        // Links
        final String links = "· Website: " + Constants.MIXCORD_IO + " \n" +
                "· Discord: " + Constants.DISCORD;

        // Developers
        final String developers = "Lead Dev: Lireoy#4444\nConsultant: Akira#8185";

        // Infrastructure
        final String infrastructure = "Provided by Akira#8185";

        commandEvent.reply(new MixerEmbedBuilder()
                .setTitle("Mixcord")
                .addField("Uptime", uptime, false)
                .addField("Usage", usage, true)
                .addField("Version", version, true)
                .addField("Shards", shards, true)
                .addField("System", systemInfo, false)
                .addField("Links", links, false)
                .addField("Developers", developers, false)
                .addField("Infrastructure", infrastructure, false)
                .build());
    }
}