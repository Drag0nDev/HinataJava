package hinata.bot.Commands.commands;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

@CommandDescription(
        name = "Ping",
        description = "Response time of the bot",
        triggers = {"ping"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "help", value = "[command | alias]")
        }
)

public class CmdPing implements Command {
    private final Hinata bot;

    public CmdPing(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args) {

    }

    @Override
    public void execute(Message object, Object... args) {

    }
}
