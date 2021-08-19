package hinata.bot.Commands.commands;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.util.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

@CommandDescription(
        name = "Help",
        description = "General command to view all commands or get help with commands",
        triggers = {"help", "h"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias] <categoryname/commandname>"),
                @CommandAttribute(key = "help", value = "[command | alias] <categoryname/commandname>")
        }
)
public class CmdHelp implements Command {
    private final Hinata bot;
    private final Config config = new Config();

    public CmdHelp(Hinata bot) throws IOException, ParseException {
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args) {

    }

    @Override
    public void execute(Message object, Object... args) {

    }
}
