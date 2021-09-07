package hinata.bot.Commands.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandDescription(
        name = "pp",
        description = "how long is his pp",
        triggers = {"pp", "penis", "howbig", "pickle"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "[command | alias] <user mention>"),
        }
)

public class CmdPp implements Command {

    private final Hinata bot;

    public CmdPp(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, Object... args) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("PP")
                .setColor(Colors.NORMAL.getCode())
                .setDescription("Looking");
        Member member;
        Guild guild = msg.getGuild();
        MessageChannel mc = msg.getChannel();
        String[] arguments = bot.getArguments(msg);

        if (arguments.length > 0) {
            if (!msg.getMentionedMembers().isEmpty()){
                member = msg.getMentionedMembers(guild).get(0);
            } else {
                member = guild.getMemberById(arguments[0]);
            }
        } else {
            member = msg.getMember();
        }

        mc.sendMessageEmbeds(embed.build()).queue(message -> {
            StringBuilder desc = new StringBuilder();
            assert member != null;
            desc.append("**").append(member.getUser().getAsTag()).append("**'s pp:\n").append("**").append(getPP()).append("**");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                bot.getLogger().error(String.valueOf(e));
            }
            embed.setDescription(desc);

            message.editMessageEmbeds(embed.build()).queue();
        });
    }

    private String getPP() {
        Random rand = new Random();

        return "8" + "=".repeat(rand.nextInt(1000) % 20) + "D";
    }
}
