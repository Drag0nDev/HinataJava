package hinata.bot.Commands.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Date;

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
    public void execute(Message msg, Object... args) {
        Date now = new Date();
        Date sent = new Date(msg.getTimeCreated().toInstant().toEpochMilli());

        long ping = now.getTime() - sent.getTime();

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Ping")
                .setDescription(ping + "ms")
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(now.toInstant());

        msg.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
