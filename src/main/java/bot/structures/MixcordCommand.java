package bot.structures;

import com.jagrosh.jdautilities.command.Command;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MixcordCommand extends Command {

    protected String[] commandExamples = new String[0];

    public String[] getCommandExamples() {
        return commandExamples;
    }
}
