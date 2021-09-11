package hinata.bot.Commands.commands.info;

import com.github.rainestormee.jdacommand.*;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

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
    protected final String optionName = "user";

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(USER, optionName, "the person you want to see it of")
                    .setRequired(false));

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

        assert member != null;
        tc.sendMessageEmbeds(new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                .setTitle("Avatar of: " + member.getUser().getAsTag())
                .setImage(member.getUser().getEffectiveAvatarUrl() + "?size=4096")
                .build()).queue();
    }

    @Override
    public void run(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        hook.sendMessageEmbeds(new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                .setTitle("Avatar of: " + member.getUser().getAsTag())
                .setImage(member.getUser().getEffectiveAvatarUrl() + "?size=4096")
                .build()).queue();
    }

    @Override
    public CommandData slashInfo() {
        return slashInfo;
    }

    @Override
    public String getOptionName() {
        return optionName;
    }
}
