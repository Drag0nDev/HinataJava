package hinata.bot.Commands;

import hinata.bot.Hinata;
import hinata.bot.util.Listener;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hinata.bot.Commands.commands.fun.*;
import hinata.bot.Commands.commands.info.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandLoader {
    private final Set<Command> COMMANDS = new HashSet<>();
    private final Set<Command> SLASH = new HashSet<>();
    private final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    public CommandLoader(Hinata bot) {
        loadCommands(
                //fun commands
                new CmdBonk(bot),
                new CmdGetIp(bot),
                new CmdPp(bot),
                //info commands
                new CmdAvatar(bot),
                new CmdHelp(bot),
                new CmdPing(bot)
        );

        LOGGER.info("Loaded {} commands!", COMMANDS.size());
    }

    public Set<Command> getCommands(){
        return COMMANDS;
    }

    private void loadCommands(Command... commands){
        COMMANDS.addAll(Arrays.asList(commands));
    }

    public void loadSlashCommands(Hinata bot) {
        CommandListUpdateAction cmds = bot.getBot().updateCommands();

        COMMANDS.forEach(cmd -> {
            cmds.addCommands(cmd.slashInfo());
        });

        cmds.queue();

        LOGGER.info("Loaded {} slash commands!", cmds.complete().size());
    }
}
