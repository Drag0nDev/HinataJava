package hinata.bot.Commands;

import hinata.bot.Hinata;
import hinata.bot.util.Listener;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hinata.bot.Commands.commands.fun.*;
import hinata.bot.Commands.commands.info.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandLoader {
    private final Set<Command> COMMANDS = new HashSet<>();
    private final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    public CommandLoader(Hinata bot) throws IOException, ParseException {
        loadCommands(
                //info commands
                new CmdAvatar(bot),
                new CmdHelp(bot),
                new CmdPing(bot),

                //fun commands
                new CmdPp(bot)
        );

        LOGGER.info("Loaded {} commands!", COMMANDS.size());
    }

    public Set<Command> getCommands(){
        return COMMANDS;
    }

    private void loadCommands(Command... commands){
        COMMANDS.addAll(Arrays.asList(commands));
    }
}
