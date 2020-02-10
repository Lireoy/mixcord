package bot.commands.owner;

import bot.Mixcord;
import bot.structure.CommandCategory;
import bot.structure.Server;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.rethinkdb.net.Cursor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

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
        User commandAuthor = commandEvent.getAuthor();
        log.info("Command ran by {}", commandAuthor);

        String[] args = commandEvent.getArgs().trim().split(",", 2);
        ArrayList<String> argList = new ArrayList<>(Arrays.asList(args));


        Gson gson = new Gson();
        if (argList.get(0).trim().equalsIgnoreCase("all")) {
            StringBuilder serversDetails = new StringBuilder();
            Cursor cursor = Mixcord.getDatabase().selectAllGuilds();
            for (Object o : cursor) {
                Server server = gson.fromJson(new JSONObject(o.toString()).toString(), Server.class);

                Guild guild = Mixcord.getJda().getGuildById(server.getServerId());
                serversDetails.append("· <@")
                        .append(guild != null ? guild.getOwnerId() : "(Could not retrieve owner ID)")
                        .append("> - `")
                        .append(guild != null ? guild.getName() : "(Could not retrieve name)")
                        .append("` - `")
                        .append(server.getServerId())
                        .append("` - `")
                        .append(guild != null ? guild.getMembers().size() : -1)
                        .append(" members`\n");
            }

            commandEvent.replyFormatted(serversDetails.toString());
            return;
        }

        String serverId = argList.get(0).trim();
        boolean newWhitelistVal = false;
        if (argList.get(1).trim().equalsIgnoreCase("true")) {
            newWhitelistVal = true;
        }


        Guild guild = Mixcord.getJda().getGuildById(serverId);

        if (!Mixcord.getJda().getGuilds().contains(guild)) {
            commandEvent.reply("The bot is not in this server.");
            commandEvent.reactError();
            return;
        }

        Cursor whitelistCursor = Mixcord.getDatabase().selectOneServer(serverId);
        if (whitelistCursor.hasNext()) {
            Server server = gson.fromJson(new JSONObject(whitelistCursor.next().toString()).toString(), Server.class);
            boolean oldWhitelistVal = server.isWhitelisted();

            Mixcord.getDatabase().updateWhitelist(server.getId(), newWhitelistVal);

            if (oldWhitelistVal == newWhitelistVal) {
                commandEvent.reply("`" + serverId + "` is already set to `" + newWhitelistVal + "`");
                commandEvent.reactError();
                return;
            }

            Mixcord.getDatabase().updateWhitelist(server.getId(), newWhitelistVal);
            commandEvent.reply("Successfully updated `" + serverId + "` to `" + newWhitelistVal + "`.");
            commandEvent.reactSuccess();
        } else {
            Mixcord.getDatabase().addServer(serverId);
            Server server = gson.fromJson(new JSONObject(
                    Mixcord.getDatabase().selectOneServer(serverId).next().toString())
                    .toString(), Server.class);

            Mixcord.getDatabase().updateWhitelist(server.getId(), newWhitelistVal);
            commandEvent.reply("Successfully whitelisted `" + serverId + "`.");
            commandEvent.reactSuccess();
        }
    }
}
