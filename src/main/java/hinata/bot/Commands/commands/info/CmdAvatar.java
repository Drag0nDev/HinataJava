package hinata.bot.Commands.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@CommandDescription(
        name = "avatar",
        description = "Get the avatar of yourself/another person",
        triggers = {"avatar", "av", "pfp"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias] <mention/id>"),
                @CommandAttribute(key = "examples", value = "`h!av`\n" +
                        "`h!av 418037700751261708`\n" +
                        "`h!av @Drag0n#6666`\n"),
        }
)

public class CmdAvatar implements Command {

    private final Hinata bot;

    public CmdAvatar(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, Object... args) {
        String[] arguments = bot.getArguments(msg);
        TextChannel tc = msg.getTextChannel();
        Member member;
        Guild guild = msg.getGuild();

        if (arguments.length > 0) {
            if (!msg.getMentionedMembers().isEmpty()) {
                member = msg.getMentionedMembers(guild).get(0);
            } else {
                member = guild.getMemberById(arguments[0]);
            }
        } else {
            member = msg.getMember();
        }

        tc.sendMessageEmbeds(new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                .setTitle("Avatar of: " + member.getUser().getAsTag())
                .setImage(member.getUser().getEffectiveAvatarUrl() + "?size=4096")
                .build()).queue();
    }
}
