package bot.commands.informative;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.utils.HelpUtil;
import bot.utils.MixerEmbedBuilder;
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
        this.help = Locale.INFO_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("INFORMATIVE"));
        this.guildOnly = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        if (checkHelp(commandEvent)) return;

        final String upTime = calculateUpTime();
        final String usage = generateUsageSegment(commandEvent);
        final String version = generateVersioning();
        final String shards = generateShardInfo(commandEvent);
        final String systemInfo = generateSystemInfo();
        final String links = generateLinks();

        respond(commandEvent, upTime, usage, version, shards, systemInfo, links);
    }

    private boolean checkHelp(CommandEvent commandEvent) {
        final String[] commandExamples = {BotConstants.PREFIX + this.name};
        return HelpUtil.getInstance().sendCommandHelp(this, commandEvent, commandExamples);
    }

    private String calculateUpTime() {
        // Calculate uptime
        final long duration = ManagementFactory.getRuntimeMXBean().getUptime();
        final long days = duration / 86400000L % 30;
        final long hours = duration / 3600000L % 24;
        final long minutes = duration / 60000L % 60;
        final long seconds = duration / 1000L % 60;

        return String.format(
                Locale.INFO_COMMAND_UPTIME,
                days, hours, minutes, seconds);
    }

    private String generateUsageSegment(CommandEvent commandEvent) {
        // Usage segment
        final int guildCount = commandEvent.getJDA().getGuilds().size();
        int memberCount = 0;

        // Count members in each guild.
        for (int i = 0; i < guildCount; i++) {
            memberCount += commandEvent.getJDA().getGuilds().get(i).getMembers().size();
        }

        return String.format(
                Locale.INFO_COMMAND_USAGE,
                guildCount, memberCount);
    }

    private String generateVersioning() {
        // Get Java version
        return String.format(
                Locale.INFO_COMMAND_JAVA_VERSION,
                System.getProperty("java.version"));
    }

    private String generateShardInfo(CommandEvent commandEvent) {
        // Shards
        final long ping = commandEvent.getJDA().getGatewayPing();
        final int shardId = commandEvent.getJDA().getShardInfo().getShardId();
        final int totalShards = commandEvent.getJDA().getShardInfo().getShardTotal();
        return String.format(
                Locale.INFO_COMMAND_SHARDS,
                shardId, ping, totalShards);
    }

    private String generateSystemInfo() {
        // System
        Runtime rt = Runtime.getRuntime();
        return String.format(
                Locale.INFO_COMMAND_RAM_USAGE,
                rt.freeMemory() / 1024 / 1024,
                rt.maxMemory() / 1024 / 1024
        );
    }

    private String generateLinks() {
        // Links
        return String.format(
                Locale.INFO_COMMAND_LINKS,
                BotConstants.MIXCORD_XYZ,
                BotConstants.DISCORD
        );
    }

    private void respond(CommandEvent commandEvent, String upTime, String usage,
                         String version, String shards, String systemInfo, String links) {
        commandEvent.reply(new MixerEmbedBuilder()
                .setTitle(commandEvent.getJDA().getSelfUser().getName())
                .addField(
                        Locale.INFO_COMMAND_UPTIME_TITLE,
                        upTime,
                        false)
                .addField(
                        Locale.INFO_COMMAND_USAGE_TITLE,
                        usage,
                        true)
                .addField(
                        Locale.INFO_COMMAND_JAVA_VERSION_TITLE,
                        version,
                        true)
                .addField(
                        Locale.INFO_COMMAND_SHARDS_TITLE,
                        shards,
                        true)
                .addField(
                        Locale.INFO_COMMAND_RAM_USAGE_TITLE,
                        systemInfo,
                        false)
                .addField(
                        Locale.INFO_COMMAND_LINKS_TITLE,
                        links,
                        false)
                .addField(
                        Locale.INFO_COMMAND_DEVELOPER_TITLE,
                        Locale.INFO_COMMAND_DEVELOPER,
                        false)
                .build());
    }
}