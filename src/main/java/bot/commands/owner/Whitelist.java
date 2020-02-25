package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.HelpConstants;
import bot.database.DatabaseDriver;
import bot.services.ShardService;
import bot.structures.Server;
import bot.structures.enums.CommandCategory;
import bot.utils.HelpUtil;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

@Slf4j
public class Whitelist extends Command {

    public Whitelist() {
        this.name = "Whitelist";
        this.help = HelpConstants.WHITELIST_HELP;
        this.category = new Category(CommandCategory.OWNER.toString());
        this.arguments = "<server ID>, <true | false> || 'all'";
        this.guildOnly = true;
        this.ownerCommand = true;
        this.botPermissions = new Permission[]{
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION};
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);
        final String commandExample = "\nExample: `" + BotConstants.PREFIX + this.name + " 637724317672669184, true`"
                + "\nExample: `" + BotConstants.PREFIX + this.name + " all`";

        boolean helpResponse = HelpUtil.getInstance().sendCommandHelp(this, commandEvent, commandExample);
        if (helpResponse) return;

        if (commandEvent.getArgs().trim().isEmpty()) {
            commandEvent.reply("Please provide the required parameters.");
            return;
        }

        final String[] args = StringUtil.separateArgs(commandEvent.getArgs());

        if (args.length == 1) {
            if (!args[0].trim().equalsIgnoreCase("all")) {
                commandEvent.reply("Please provide a full configuration." + commandExample);
                return;
            }

            final Cursor cursor = DatabaseDriver.getInstance().selectAllGuilds();
            StringBuilder serversDetails = new StringBuilder();
            for (Object o : cursor) {
                final Server server = new Gson().fromJson(o.toString(), Server.class);
                final Guild guild = ShardService.getInstance().getGuildById(server.getServerId());

                String guildOwnerId = guild != null ? guild.getOwnerId() : "(Could not retrieve owner ID)";
                String guildName = guild != null ? guild.getName() : "(Could not retrieve name)";
                String guildMemberCount = guild != null ? String.valueOf(guild.getMembers().size()) : "-1";

                String line = "· <@%s> - `%s` - `%s` - `%s members`\n";
                String formattedLine = String.format(line,
                        guildOwnerId, guildName, server.getServerId(), guildMemberCount);
                serversDetails.append(formattedLine);
            }

            cursor.close();
            commandEvent.replyFormatted(serversDetails.toString());
            return;
        }

        if (args.length == 2) {
            if (args[0].trim().isEmpty()) {
                commandEvent.reply("First parameter was empty." + commandExample);
                return;
            }

            if (args[1].trim().isEmpty()) {
                commandEvent.reply("Second parameter was empty." + commandExample);
                return;
            }
        }

        final String serverId = args[0].trim();
        boolean newWhitelistVal = false;
        if (args[1].trim().equalsIgnoreCase("true")) {
            newWhitelistVal = true;
        }

        final Guild guild = ShardService.getInstance().getGuildById(serverId);

        if (!ShardService.getInstance().getGuilds().contains(guild)) {
            commandEvent.reply("The bot is not in that server.");
            commandEvent.reactError();

            String docId = DatabaseDriver.getInstance().getGuildDocId(serverId);
            DatabaseDriver.getInstance().deleteGuild(docId);

            log.info("Guild is not reachable. G:{}. Deleted from database.", serverId);
            commandEvent.reply("Deleted G:`" + serverId + "` from database.");
            return;
        }

        final Cursor whitelistCursor = DatabaseDriver.getInstance().selectOneServer(serverId);
        if (whitelistCursor.hasNext()) {
            final Server server = new Gson().fromJson(whitelistCursor.next().toString(), Server.class);
            boolean oldWhitelistVal = server.isWhitelisted();

            DatabaseDriver.getInstance().updateWhitelist(server.getId(), newWhitelistVal);

            if (oldWhitelistVal == newWhitelistVal) {
                commandEvent.reply("`" + serverId + "` is already set to `" + newWhitelistVal + "`");
                commandEvent.reactError();
                return;
            }

            DatabaseDriver.getInstance().updateWhitelist(server.getId(), newWhitelistVal);
            commandEvent.reply("Successfully updated `" + serverId + "` to `" + newWhitelistVal + "`.");
            commandEvent.reactSuccess();
        } else {
            DatabaseDriver.getInstance().addServer(serverId);
            final Server server = new Gson().fromJson(
                    DatabaseDriver.getInstance().selectOneServer(serverId).next().toString(), Server.class);

            DatabaseDriver.getInstance().updateWhitelist(server.getId(), newWhitelistVal);
            commandEvent.reply("Successfully whitelisted `" + serverId + "`.");
            commandEvent.reactSuccess();
        }
    }
}
