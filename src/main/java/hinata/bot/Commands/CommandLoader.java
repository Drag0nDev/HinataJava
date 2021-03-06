package hinata.bot.Commands;

import hinata.bot.Commands.commands.channels.*;
import hinata.bot.Commands.commands.currency.CmdBalance;
import hinata.bot.Commands.commands.currency.CmdCoinflip;
import hinata.bot.Commands.commands.currency.CmdDaily;
import hinata.bot.Commands.commands.emojis.CmdEmoji;
import hinata.bot.Commands.commands.emojis.CmdEmojiCreate;
import hinata.bot.Commands.commands.emojis.CmdEmojiDelete;
import hinata.bot.Commands.commands.experience.CmdLevel;
import hinata.bot.Commands.commands.owner.CmdShutdown;
import hinata.bot.Commands.commands.owner.CmdTest;
import hinata.bot.Commands.commands.roles.CmdRole;
import hinata.bot.Commands.commands.roles.CmdRoleCreate;
import hinata.bot.Commands.commands.roles.CmdRoleDelete;
import hinata.bot.Hinata;
import hinata.bot.events.Listener;
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

                //currency commands
                new CmdBalance(bot),
                new CmdCoinflip(bot),
                new CmdDaily(bot),

                //emojis
                new CmdEmoji(bot),
                new CmdEmojiCreate(bot),
                new CmdEmojiDelete(bot),

                //experience
                new CmdLevel(bot),

                //fun commands
                new CmdBonk(bot),
                new CmdGetIp(bot),
                new CmdHowGay(bot),
                new CmdPp(bot),
                new CmdSimprate(bot),

                //info commands
                new CmdAccountAge(bot),
                new CmdAvatar(bot),
                new CmdHelp(bot),
                new CmdInvite(bot),
                new CmdPing(bot),
                new CmdServerIcon(bot),
                new CmdServerInfo(bot),
                new CmdStats(bot),

                //owner commands
                new CmdShutdown(bot),
                new CmdTest(bot),

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
                new CmdSpank(bot),

                //roles commands
                new CmdRole(bot),
                new CmdRoleCreate(bot),
                new CmdRoleDelete(bot)
        );

        LOGGER.info("Loaded {} commands!", COMMANDS.size());
    }

    public Set<Command> getCommands(){
        return COMMANDS;
    }

    private void loadCommands(Command... commands){
        COMMANDS.addAll(Arrays.asList(commands));
    }

    public void loadSlashCommands() {
        CommandListUpdateAction cmds = Hinata.getBot().updateCommands();

        COMMANDS.forEach(cmd -> {
            if (cmd.getSlashInfo() != null)
                cmds.addCommands(cmd.getSlashInfo());
        });

        cmds.queue();

        LOGGER.info("Loaded {} slash commands!", cmds.complete().size());
    }
}
