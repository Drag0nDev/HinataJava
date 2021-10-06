package hinata.bot.Commands;

import hinata.bot.Commands.commands.channels.*;
import hinata.bot.Hinata;
import hinata.bot.util.Listener;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//import commands
import hinata.bot.Commands.commands.fun.*;
import hinata.bot.Commands.commands.info.*;
import hinata.bot.Commands.commands.reactions.*;

public class CommandLoader {
    private final Set<Command> COMMANDS = new HashSet<>();
    private final Set<Command> SLASH = new HashSet<>();
    private final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    public CommandLoader(Hinata bot) {
        loadCommands(
                //channel commands
                new CmdCreateCategory(bot),
                new CmdCreateChannel(bot),
                new CmdCreateVoice(bot),
                new CmdDeleteChannel(bot),
                new CmdSetNSFW(bot),

                //fun commands
                new CmdBonk(bot),
                new CmdGetIp(bot),
                new CmdHowGay(bot),
                new CmdPp(bot),

                //info commands
                new CmdAvatar(bot),
                new CmdHelp(bot),
                new CmdPing(bot),

                //reaction commands
                new CmdBaka(bot),
                new CmdBlush(bot),
                new CmdCry(bot),
                new CmdCuddle(bot),
                new CmdGrope(bot),
                new CmdHug(bot),
                new CmdKiss(bot),
                new CmdPat(bot),
                new CmdPoke(bot),
                new CmdSlap(bot),
                new CmdSpank(bot)
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
            if (cmd.slashInfo() != null)
                cmds.addCommands(cmd.slashInfo());
        });

        cmds.queue();

        LOGGER.info("Loaded {} slash commands!", cmds.complete().size());
    }
}
