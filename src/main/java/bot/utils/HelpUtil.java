package bot.utils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class HelpUtil {

    private static HelpUtil helpUtil;

    private HelpUtil() {
    }

    public static HelpUtil getInstance() {
        if (helpUtil == null) {
            helpUtil = new HelpUtil();
        }

        return helpUtil;
    }

    public boolean sendCommandHelp(Command command, CommandEvent commandEvent) {
        if (commandEvent.getArgs().equals("--help")) {
            EmbedBuilder embedBuilder = new MixerEmbedBuilder()
                    .setTitle(command.getName() + " command")
                    .addField("Name", command.getName(), false)
                    .addField("Category", command.getCategory().getName(), false);

            if (command.getArguments() != null)
                embedBuilder.addField("Parameters", command.getArguments(), false);

            if (command.getAliases().length != 0) {
                StringBuilder builder = new StringBuilder();
                for (String alias : command.getAliases())
                    builder.append(alias).append(", ");

                String aliases = StringUtil.replaceLastComma(builder.toString());
                embedBuilder.addField("Aliases", aliases, false);
            }

            if (!command.getHelp().equals("no help available"))
                embedBuilder.addField("What it does", command.getHelp(), false);

            if (command.getBotPermissions().length != 0) {
                Permission[] botPermissions = command.getBotPermissions();
                StringBuilder builder = new StringBuilder();
                for (Permission permission : botPermissions) {
                    builder.append(permission.getName()).append(", ");
                }
                String botPermissionString = StringUtil.replaceLastComma(builder.toString());
                embedBuilder.addField("Required bot permissions", botPermissionString, false);
            }

            if (command.getUserPermissions().length != 0) {
                Permission[] userPermissions = command.getUserPermissions();
                StringBuilder builder = new StringBuilder();
                for (Permission permission : userPermissions) {
                    builder.append(permission.getName()).append(", ");
                }

                String userPermissionString = StringUtil.replaceLastComma(builder.toString());
                embedBuilder.addField("Required user permissions", userPermissionString, false);
            }


            commandEvent.reply(embedBuilder.build());
            return true;
        }

        return false;
    }
}
