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

    public boolean sendCommandHelp(final Command cmd, final CommandEvent cmdEvent, final String[] cmdExamples) {
        if (cmdEvent.getArgs().equals("--help")) {
            EmbedBuilder embedBuilder = new MixerEmbedBuilder()
                    .setTitle(cmd.getName() + " command")
                    .addField("Category", cmd.getCategory().getName(), false)
                    .addField("Name", cmd.getName(), false);


            if (cmd.getArguments() != null)
                embedBuilder.addField("Parameters", cmd.getArguments(), false);

            StringBuilder example = new StringBuilder();
            for (String commandExample : cmdExamples) {
                example.append("`").append(commandExample).append("`\n");
            }

            embedBuilder.addField("Command usage example", example.toString(), false);

            if (cmd.getAliases().length != 0) {
                StringBuilder builder = new StringBuilder();
                for (String alias : cmd.getAliases())
                    builder.append(alias).append(", ");

                final String aliases = StringUtil.replaceLastComma(builder.toString());
                embedBuilder.addField("Aliases", aliases, false);
            }

            if (!cmd.getHelp().equals("no help available"))
                embedBuilder.addField("What it does", cmd.getHelp(), false);

            if (cmd.getBotPermissions().length != 0) {
                Permission[] botPermissions = cmd.getBotPermissions();
                StringBuilder builder = new StringBuilder();
                for (Permission permission : botPermissions) {
                    builder.append(permission.getName()).append(", ");
                }
                final String botPermissionString = StringUtil.replaceLastComma(builder.toString());
                embedBuilder.addField("Required bot permissions", botPermissionString, false);
            }

            if (cmd.getUserPermissions().length != 0) {
                Permission[] userPermissions = cmd.getUserPermissions();
                StringBuilder builder = new StringBuilder();
                for (Permission permission : userPermissions) {
                    builder.append(permission.getName()).append(", ");
                }

                final String userPermissionString = StringUtil.replaceLastComma(builder.toString());
                embedBuilder.addField("Required user permissions", userPermissionString, false);
            }


            cmdEvent.reply(embedBuilder.build());
            return true;
        }

        return false;
    }
}
