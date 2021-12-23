package hinata.bot.Commands.commands.fun;

import com.github.rainestormee.jdacommand.*;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@CommandDescription(
        name = "pp",
        description = "how long is his pp",
        triggers = {"pp", "penis", "howbig", "pickle"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "[command | alias] <user mention>"),
                @CommandAttribute(key = "examples", value = "`h!pp`\n" +
                        "`h!pp 418037700751261708`\n" +
                        "`h!pp @Drag0n#6666`")
        }
)

public class CmdPp implements Command {

    private final Hinata bot;
    protected final String optionName = "user";
    private final CommandData slashInfo =  new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(USER, optionName, "the person you want to know it of")
                    .setRequired(false));

    public CmdPp(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("PP")
                .setColor(Colors.NORMAL.getCode())
                .setDescription("Looking");
        MessageChannel mc = msg.getChannel();
        String[] arguments = bot.getArguments(msg);

        if (arguments.length > 0) {
            if (!msg.getMentionedMembers().isEmpty()) {
                member = msg.getMentionedMembers(guild).get(0);
            } else {
                member = guild.getMemberById(arguments[0]);
            }
        } else {
            member = msg.getMember();
        }

        if (member == null) {
            try {
                throw new Exception("member value is null");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        Member finalMember = member;
        mc.sendMessageEmbeds(embed.build()).queue(message -> {
            StringBuilder desc = new StringBuilder();
            desc.append("**").append(finalMember.getUser().getAsTag()).append("**'s pp:\n").append("**").append(getPP()).append("**");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                bot.getLogger().error(String.valueOf(e));
            }
            embed.setDescription(desc);

            message.editMessageEmbeds(embed.build()).queue();
        });
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("PP")
                .setColor(Colors.NORMAL.getCode())
                .setDescription("Looking");
        User user;

        if (event.getOption(optionName) == null)
            user = member.getUser();
        else
            user = Objects.requireNonNull(event.getOption(optionName)).getAsUser();

        hook.editOriginalEmbeds(embed.build()).queue(message -> {
            StringBuilder desc = new StringBuilder();
            desc.append("**").append(user.getAsTag()).append("**'s pp:\n").append("**").append(getPP()).append("**");

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

    @Override
    public CommandData getSlashInfo() {
        return slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{this.optionName};
    }
}
