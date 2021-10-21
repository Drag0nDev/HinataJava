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
        name = "getip",
        description = "Get a users ip (not really)",
        triggers = {"getip", "gi"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "[command | alias] <user mention>"),
                @CommandAttribute(key = "examples", value = "`h!gi`\n" +
                        "`h!bonk 418037700751261708`\n" +
                        "`h!bonk @Drag0n#6666`")
        }
)

public class CmdGetIp implements Command {

    private final Hinata bot;
    protected final String optionName = "user";
    private final CommandData slashInfo =  new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(USER, optionName, "the person you want to bonk")
                    .setRequired(false));

    public CmdGetIp(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("getip")
                .setImage("https://media1.tenor.com/images/b7cd57136bb82a1784bedc5408149eb1/tenor.gif?itemid=13247943")
                .setDescription("Getting ip ...")
                .setColor(Colors.NORMAL.getCode());
        User user;

        if (event.getOption(optionName) == null)
            user = member.getUser();
        else
            user = Objects.requireNonNull(event.getOption(optionName)).getAsUser();

        hook.editOriginalEmbeds(embed.build()).queue(message -> {
            StringBuilder desc = new StringBuilder();
            desc.append("**").append(user.getAsTag()).append("**'s IP: ").append("**").append(getIp()).append("**");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                bot.getLogger().error(String.valueOf(e));
            }
            embed.setDescription(desc)
                    .setImage(null);

            message.editMessageEmbeds(embed.build()).queue();
        });
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("getip")
                .setImage("https://media1.tenor.com/images/b7cd57136bb82a1784bedc5408149eb1/tenor.gif?itemid=13247943")
                .setDescription("Getting ip ...")
                .setColor(Colors.NORMAL.getCode());
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

        assert member != null;

        Member finalMember = member;
        mc.sendMessageEmbeds(embed.build()).queue(message -> {
            StringBuilder desc = new StringBuilder();
            desc.append("**").append(finalMember.getUser().getAsTag()).append("**'s IP: ").append("**").append(getIp()).append("**");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                bot.getLogger().error(String.valueOf(e));
            }
            embed.setDescription(desc)
                    .setImage(null);

            message.editMessageEmbeds(embed.build()).queue();
        });
    }

    @Override
    public CommandData getSlashInfo() {
        return slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{this.optionName};
    }

    private String getIp() {
        Random random = new Random();

        return random.nextInt(256) + "." +
                random.nextInt(256) + "." +
                random.nextInt(256) + "." +
                random.nextInt(256);
    }
}
