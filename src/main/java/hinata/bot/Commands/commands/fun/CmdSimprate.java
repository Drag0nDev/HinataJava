package hinata.bot.Commands.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@CommandDescription(
        name = "simprate",
        description = "Get the simprate of a person.",
        triggers = {"simprate", "howsimp"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "[command | alias] <user mention>"),
                @CommandAttribute(key = "examples", value = "`h!simprate`\n" +
                        "`h!simprate 418037700751261708`\n" +
                        "`h!simprate @Drag0n#6666`")
        }
)

public class CmdSimprate implements Command {
    private final Hinata bot;

    protected final String optionName = "user";

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(USER, optionName, "person to calculate it on")
                            .setRequired(false));

    public CmdSimprate(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        EmbedBuilder embed = new EmbedBuilder().setTitle("howgay")
                .setColor(Colors.NORMAL.getCode())
                .setDescription("Calculating!")
                .setTimestamp(ZonedDateTime.now());

        User user;

        if (event.getOption(optionName) == null)
            user = member.getUser();
        else
            user = Objects.requireNonNull(event.getOption(optionName)).getAsUser();

        hook.sendMessageEmbeds(embed.build()).queue(message -> {
            StringBuilder desc = new StringBuilder();
            int gayrate = getSimpRate();

            desc.append("**").append(user.getAsTag()).append("** is: ").append("**").append(gayrate).append("%** simp!");
            embed.setDescription(desc);

            if (gayrate > 50)
                embed.setImage("https://media1.tenor.com/images/b5cfc5d13e8640543a528c5da6412e8e/tenor.gif");

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
                .setDescription("Calculating")
                .setTimestamp(ZonedDateTime.now());
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
            int gayrate = getSimpRate();

            desc.append("**").append(finalMember.getUser().getAsTag()).append("** is: ").append("**").append(gayrate).append("%** simp!");
            embed.setDescription(desc);

            if (gayrate > 50)
                embed.setImage("https://media1.tenor.com/images/b5cfc5d13e8640543a528c5da6412e8e/tenor.gif");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                bot.getLogger().error(String.valueOf(e));
            }

            message.editMessageEmbeds(embed.build()).queue();
        });
    }

    private int getSimpRate() {
        return new Random().nextInt(101);
    }

    @Override
    public CommandData getSlashInfo() {
        return this.slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{this.optionName};
    }

}
