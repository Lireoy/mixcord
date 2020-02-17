package bot.commands.owner;

import bot.factories.DatabaseFactory;
import bot.services.ShardService;
import bot.structure.Server;
import bot.structure.enums.CommandCategory;
import bot.utils.StringUtil;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class Whitelist extends Command {

    public Whitelist() {
        this.name = "Whitelist";
        this.help = "Add / remove a server from the whitelist, or list all whitelisted servers.";
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

        final String[] args = StringUtil.separateArgs(commandEvent.getArgs());
        final ArrayList<String> argList = new ArrayList<>(Arrays.asList(args));

        if (argList.get(0).trim().equalsIgnoreCase("all")) {
            final Cursor cursor = DatabaseFactory.getDatabase().selectAllGuilds();
            StringBuilder serversDetails = new StringBuilder();
            for (Object o : cursor) {
                final Server server = new Gson().fromJson(o.toString(), Server.class);
                final Guild guild = ShardService.manager().getGuildById(server.getServerId());

                String guildOwnerId = guild != null ? guild.getOwnerId() : "(Could not retrieve owner ID)";
                String guildName = guild != null ? guild.getName() : "(Could not retrieve name)";
                String guildMemberCount = guild != null ? String.valueOf(guild.getMembers().size()) : "-1";

                String line = "· <@%s> - `%s` - `%s` - `%s members`\n";
                serversDetails.append(String.format(line, guildOwnerId, guildName, server.getServerId(), guildMemberCount));
            }

            cursor.close();
            commandEvent.replyFormatted(serversDetails.toString());
            return;
        }

        final String serverId = argList.get(0).trim();
        boolean newWhitelistVal = false;
        if (argList.get(1).trim().equalsIgnoreCase("true")) {
            newWhitelistVal = true;
        }

        final Guild guild = ShardService.manager().getGuildById(serverId);

        if (!ShardService.manager().getGuilds().contains(guild)) {
            commandEvent.reply("The bot is not in that server.");
            commandEvent.reactError();

            String docId = DatabaseFactory.getDatabase().getGuildDocId(serverId);
            DatabaseFactory.getDatabase().deleteGuild(docId);

            log.info("Guild is not reachable. G:{}. Deleted from database.", serverId);
            commandEvent.reply("Deleted G:`" + serverId + "` from database.");
            return;
        }

        final Cursor whitelistCursor = DatabaseFactory.getDatabase().selectOneServer(serverId);
        if (whitelistCursor.hasNext()) {
            final Server server = new Gson().fromJson(whitelistCursor.next().toString(), Server.class);
            boolean oldWhitelistVal = server.isWhitelisted();

            DatabaseFactory.getDatabase().updateWhitelist(server.getId(), newWhitelistVal);

            if (oldWhitelistVal == newWhitelistVal) {
                commandEvent.reply("`" + serverId + "` is already set to `" + newWhitelistVal + "`");
                commandEvent.reactError();
                return;
            }

            DatabaseFactory.getDatabase().updateWhitelist(server.getId(), newWhitelistVal);
            commandEvent.reply("Successfully updated `" + serverId + "` to `" + newWhitelistVal + "`.");
            commandEvent.reactSuccess();
        } else {
            DatabaseFactory.getDatabase().addServer(serverId);
            final Server server = new Gson().fromJson(DatabaseFactory.getDatabase().selectOneServer(serverId).next().toString(), Server.class);

            DatabaseFactory.getDatabase().updateWhitelist(server.getId(), newWhitelistVal);
            commandEvent.reply("Successfully whitelisted `" + serverId + "`.");
            commandEvent.reactSuccess();
        }
    }
}
