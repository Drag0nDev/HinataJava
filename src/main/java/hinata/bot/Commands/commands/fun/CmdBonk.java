package hinata.bot.Commands.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import hinata.bot.constants.Emotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;

import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@CommandDescription(
        name = "bonk",
        description = "Bonk a user",
        triggers = {"bonk"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "[command | alias] <user mention>"),
                @CommandAttribute(key = "examples", value = "`h!bonk 418037700751261708`\n" +
                        "`h!bonk @Drag0n#6666`")
        }
)

public class CmdBonk implements Command {

    private final Hinata bot;
    protected final String optionName = "user";
    private final CommandData slashInfo =  new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(USER, optionName, "the person you want to bonk")
                    .setRequired(false));

    public CmdBonk(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        StringBuilder desc = new StringBuilder();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Bonk")
                .setColor(Colors.NORMAL.getCode())
                .setDescription("Looking");
        User user;

        if (event.getOption(optionName) == null)
            user = member.getUser();
        else
            user = Objects.requireNonNull(event.getOption(optionName)).getAsUser();

        desc.append(user.getAsMention()).append(" ").append(Emotes.BONK.getEmote());

        hook.sendMessageEmbeds(embed.setDescription(desc.toString()).build()).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Bonk")
                .setColor(Colors.NORMAL.getCode());
        MessageChannel mc = msg.getChannel();
        String[] arguments = bot.getArguments(msg);
        StringBuilder desc = new StringBuilder();

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
        desc.append(member.getAsMention()).append(" ").append(Emotes.BONK.getEmote());

        mc.sendMessageEmbeds(embed.setDescription(desc.toString()).build()).queue();
    }

    @Override
    public CommandData slashInfo() {
        return slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{this.optionName};
    }
}
