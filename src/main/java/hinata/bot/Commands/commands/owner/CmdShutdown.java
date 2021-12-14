package hinata.bot.Commands.commands.owner;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import static hinata.bot.util.utils.Utils.jsonToEmbed;

@CommandDescription(
        name = "shutdown",
        description = "Stop the bot.",
        triggers = {"shutdown"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "h!shutdown")
        }
)

public class CmdShutdown implements Command {
    private final Hinata bot;

    public CmdShutdown(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        String test = "{" +
                "\"color\": \"#ff0000\"," +
                "\"title\": \"Shutting down\"," +
                "\"description\": \"I am shutting down myself\"" +
                "}";

        msg.getTextChannel().sendMessageEmbeds(jsonToEmbed(test)).complete();
        Hinata.getBot().shutdownNow();
        System.exit(0);
    }
}
