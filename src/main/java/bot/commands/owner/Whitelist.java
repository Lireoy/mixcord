package bot.commands.owner;

import bot.constants.BotConstants;
import bot.constants.Locale;
import bot.database.DatabaseDriver;
import bot.services.ShardService;
import bot.structures.Server;
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
        this.help = Locale.WHITELIST_COMMAND_HELP;
        this.category = new Category(Locale.CATEGORIES.get("OWNER"));
        this.arguments = "<server ID>, <true | false> || 'all'";
        this.guildOnly = false;
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

        final String[] commandExamples = {
                BotConstants.PREFIX + this.name + " 637724317672669184, true",
                BotConstants.PREFIX + this.name + " --all"
        };

        final boolean helpResponse = HelpUtil.getInstance()
                .sendCommandHelp(this, commandEvent, commandExamples);
        if (helpResponse) return;

        if (commandEvent.getArgs().trim().isEmpty()) {
            commandEvent.reply(Locale.WHITELIST_COMMAND_NO_ARGUMENTS);
            return;
        }

        final String[] args = StringUtil.separateArgs(commandEvent.getArgs(), 2);

        if (args.length == 0) {
            commandEvent.reply(Locale.WHITELIST_COMMAND_NO_ARGUMENTS);
            return;
        }

        // List all whitelisted server
        if (args.length == 1) {
            if (!args[0].trim().equalsIgnoreCase("--all")) {
                commandEvent.reply(Locale.WHITELIST_COMMAND_NO_FULL_CONFIG);
                return;
            }

            final Cursor cursor = DatabaseDriver.getInstance().selectAllGuilds();
            StringBuilder serversDetails = new StringBuilder();
            for (Object o : cursor) {
                final Server server = new Gson().fromJson(o.toString(), Server.class);
                final Guild guild = ShardService.getInstance().getGuildById(server.getServerId());

                final String guildOwnerId = guild != null ? guild.getOwnerId() : Locale.WHITELIST_COMMAND_OWNER_NOT_AVAILABLE;
                final String guildName = guild != null ? guild.getName() : Locale.WHITELIST_COMMAND_NAME_NOT_AVAILABLE;
                final String guildMemberCount = guild != null ? String.valueOf(guild.getMembers().size()) : "-1";

                final String formattedLine = String.format(
                        Locale.WHITELIST_COMMAND_LINE,
                        guildOwnerId, guildName, server.getServerId(), guildMemberCount);
                serversDetails.append(formattedLine);
            }

            cursor.close();
            commandEvent.replyFormatted(serversDetails.toString());
            return;
        }

        // Whitelist configurer
        if (args.length == 2) {
            if (args[0].trim().isEmpty()) {
                commandEvent.reply(
                        String.format(
                                Locale.WHITELIST_COMMAND_NO_FIRST_ARG,
                                commandExamples[1]));
                return;
            }

            if (args[1].trim().isEmpty()) {
                commandEvent.reply(
                        String.format(
                                Locale.WHITELIST_COMMAND_NO_SECOND_ARG,
                                commandExamples[0]));
                return;
            }
        }

        final String serverId = args[0].trim();
        boolean newWhitelistVal = false;
        boolean successfulConvert = false;
        if (args[1].trim().equalsIgnoreCase("true")) {
            newWhitelistVal = true;
            successfulConvert = true;
        }

        if (args[1].trim().equalsIgnoreCase("false")) {
            successfulConvert = true;
        }

        if (!successfulConvert) {
            commandEvent.reply(Locale.WHITELIST_COMMAND_INVALID_SECOND_ARG);
            return;
        }

        final Guild guild = ShardService.getInstance().getGuildById(serverId);

        if (!ShardService.getInstance().getGuilds().contains(guild)) {
            commandEvent.reply(Locale.WHITELIST_COMMAND_NOT_IN_SERVER);
            commandEvent.reactError();

            String docId = DatabaseDriver.getInstance().getGuildDocId(serverId);
            DatabaseDriver.getInstance().deleteGuild(docId);

            log.info("Guild is not reachable. G:{}. Deleted from database.", serverId);
            commandEvent.reply(
                    String.format(
                            Locale.WHITELIST_COMMAND_DELETED,
                            serverId));
            return;
        }

        final Cursor whitelistCursor = DatabaseDriver.getInstance().selectOneServer(serverId);
        if (whitelistCursor.hasNext()) {
            final Server server = new Gson().fromJson(whitelistCursor.next().toString(), Server.class);
            boolean oldWhitelistVal = server.isWhitelisted();

            DatabaseDriver.getInstance().updateWhitelist(server.getId(), newWhitelistVal);

            if (oldWhitelistVal == newWhitelistVal) {
                commandEvent.reply(
                        String.format(
                                Locale.WHITELIST_COMMAND_ALREADY_SET,
                                serverId,
                                newWhitelistVal));
                commandEvent.reactError();
                return;
            }

            DatabaseDriver.getInstance().updateWhitelist(server.getId(), newWhitelistVal);
            commandEvent.reply(
                    String.format(
                            Locale.WHITELIST_COMMAND_UPDATED,
                            serverId,
                            newWhitelistVal));
            commandEvent.reactSuccess();
        } else {
            DatabaseDriver.getInstance().addServer(serverId);
            final Server server = new Gson().fromJson(
                    DatabaseDriver.getInstance()
                            .selectOneServer(serverId).next().toString(), Server.class);

            DatabaseDriver.getInstance().updateWhitelist(server.getId(), newWhitelistVal);
            commandEvent.reply(String.format(
                    Locale.WHITELIST_COMMAND_ADDED,
                    serverId));
            commandEvent.reactSuccess();
        }
    }
}
