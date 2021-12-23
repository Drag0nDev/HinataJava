package hinata.bot.Commands.commands.info;


import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.constants.Colors;
import hinata.util.exceptions.HinataException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.util.Objects;

import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@CommandDescription(
        name = "accountage",
        description = "Get someones account age",
        triggers = {"accountage", "aa", "age"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias] <mention/id>"),
                @CommandAttribute(key = "examples", value = "`h!aa`\n" +
                        "`h!aa 418037700751261708`\n" +
                        "`h!aa @Drag0n#6666`\n"),
        }
)

public class CmdAccountAge implements Command {
    private final Hinata bot;

    protected final String optionName = "user";

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(USER, optionName, "the person you want to see it of")
                    .setRequired(false));

    public CmdAccountAge(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.NORMAL.getCode())
                .setDescription("Calculating!")
                .setTimestamp(ZonedDateTime.now());
        User user;

        if (event.getOption(optionName) == null)
            user = member.getUser();
        else
            user = Objects.requireNonNull(event.getOption(optionName)).getAsUser();

        embed.setTitle("Account age of: " + user.getAsTag())
                .setDescription("**" + user.getAsTag() + "**'s account is **" + calculateAge(user.getTimeCreated()) + "** old.");
        hook.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws HinataException {
        String[] arguments = bot.getArguments(msg);
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());
        User user;

        if (arguments.length > 0) {
            if (!msg.getMentionedMembers().isEmpty()) {
                member = msg.getMentionedMembers(guild).get(0);
            } else {
                member = guild.getMemberById(arguments[0]);
            }
        } else {
            member = msg.getMember();
        }

        if (member == null)
            throw new HinataException("No member was found!");

        user = member.getUser();

        embed.setTitle("Account age of: " + user.getAsTag())
                .setDescription("**" + user.getAsTag() + "**'s account is **" + calculateAge(user.getTimeCreated()) + "** old.");

        tc.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public CommandData getSlashInfo() {
        return this.slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{this.optionName};
    }

    private @NotNull String calculateAge(@NotNull OffsetDateTime creationDate) {
        ZonedDateTime now = ZonedDateTime.now();

        long diff = Math.abs(creationDate.toEpochSecond() - now.toEpochSecond());

        long second = diff % 60;
        long minute = (diff / 60) % 60;
        long hour = (diff / (60 * 60)) % 24;
        long day = (diff / (60 * 60 * 24)) % 365;
        long year = diff / (60 * 60 * 24 * 30 * 12);


        return year + " years "
                + day + " days "
                + hour + " hours "
                + minute + " minutes "
                + second +" seconds";
    }
}
