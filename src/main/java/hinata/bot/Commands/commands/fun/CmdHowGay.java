package hinata.bot.Commands.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
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
        name = "howgay",
        description = "Calculates how gay someone is",
        triggers = {"howgay", "gayrate"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "[command | alias] <user mention>"),
                @CommandAttribute(key = "examples", value = "`h!howgay`\n" +
                        "`h!howgay 418037700751261708`\n" +
                        "`h!howgay @Drag0n#6666`")
        }
)

public class CmdHowGay implements Command {

    private final Hinata bot;
    protected final String optionName = "user";
    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(USER, optionName, "person to calculate it on")
                    .setRequired(false));

    public CmdHowGay(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        EmbedBuilder embed = new EmbedBuilder().setTitle("howgay")
                .setColor(Colors.NORMAL.getCode())
                .setDescription("Calculating!");

        User user;

        if (event.getOption(optionName) == null)
            user = member.getUser();
        else
            user = Objects.requireNonNull(event.getOption(optionName)).getAsUser();

        hook.sendMessageEmbeds(embed.build()).queue(message -> {
            StringBuilder desc = new StringBuilder();
            int gayrate = getGayrate();

            desc.append("**").append(user.getAsTag()).append("** is: ").append("**").append(gayrate).append("%** gay!");
            embed.setDescription(desc);

            if (gayrate > 50)
                embed.setImage("https://media1.tenor.com/images/07ca40330ec6b96b50de2f7539ca718d/tenor.gif");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                bot.getLogger().error(String.valueOf(e));
            }

            message.editMessageEmbeds(embed.build()).queue();
        });
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        EmbedBuilder embed = new EmbedBuilder().setTitle("howgay")
                .setColor(Colors.NORMAL.getCode())
                .setDescription("Calculating");
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
                bot.getLogger().error(String.valueOf(e));
            }
            return;
        }

        Member finalMember = member;
        mc.sendMessageEmbeds(embed.build()).queue(message -> {
            StringBuilder desc = new StringBuilder();
            int gayrate = getGayrate();

            desc.append("**").append(finalMember.getUser().getAsTag()).append("** is: ").append("**").append(gayrate).append("%** gay!");
            embed.setDescription(desc);

            if (gayrate > 50)
                embed.setImage("https://media1.tenor.com/images/07ca40330ec6b96b50de2f7539ca718d/tenor.gif");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                bot.getLogger().error(String.valueOf(e));
            }

            message.editMessageEmbeds(embed.build()).queue();
        });
    }

    private int getGayrate() {
        return new Random().nextInt(101);
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
