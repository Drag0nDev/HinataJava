package hinata.bot.Commands.commands.reactions;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.ZonedDateTime;
import java.util.Objects;

import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@CommandDescription(
        name = "hug",
        description = "Hug someone",
        triggers = {"hug"},
        attributes = {
                @CommandAttribute(key = "category", value = "reactions"),
                @CommandAttribute(key = "usage", value = "[command | alias] <mention/id>"),
                @CommandAttribute(key = "examples", value = "`h!hug 418037700751261708`\n" +
                        "`h!hug @Drag0n#6666`\n"),
        }
)

public class CmdHug implements Command {

    private final Hinata bot;
    protected final String optionName = "user";

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(USER, optionName, "the person you want to hug")
                    .setRequired(false));

    public CmdHug(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        String text;
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.NORMAL.getCode())
                .setImage(ApiCalls.HUG.get())
                .setFooter("Powered by nekos.life")
                .setTimestamp(ZonedDateTime.now());

        if (event.getOption(this.optionName) != null) {
            String executor = member.getNickname() != null ?
                    member.getNickname() :
                    member.getUser().getName();
            Member target = Objects.requireNonNull(event.getOption(this.optionName)).getAsMember();

            if (member != target) {
                text = Objects.requireNonNull(target).getAsMention() + " you have been hugged by **" + executor + "**!";
            } else {
                text = "*Hugs* " + member.getAsMention();
            }
        } else {
            text = "*Hugs* " + member.getAsMention();
        }

        MessageBuilder message = new MessageBuilder().setContent(text)
                .setEmbeds(embed.build());

        hook.sendMessage(message.build()).queue();
    }

    @Override
    public void execute(Message msg, Object... args) {
        String[] arguments = bot.getArguments(msg);
        String text;
        TextChannel tc = msg.getTextChannel();
        Member executor = msg.getMember();
        Member member;
        Guild guild = msg.getGuild();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.NORMAL.getCode())
                .setImage(ApiCalls.HUG.get())
                .setFooter("Powered by nekos.life")
                .setTimestamp(ZonedDateTime.now());

        if (arguments.length > 0) {
            if (!msg.getMentionedMembers().isEmpty()) {
                member = msg.getMentionedMembers(guild).get(0);
            } else {
                member = guild.retrieveMemberById(arguments[0]).complete();
            }

            assert executor != null;
            assert member != null;

            String executorName = executor.getNickname() != null ?
                    executor.getNickname() :
                    executor.getUser().getName();

            if (member != executor) {
                text = Objects.requireNonNull(member).getAsMention() + " you have been hugged by **" + executorName + "**!";
            } else {
                text = "*Hugs* " + member.getAsMention();
            }
        } else {
            member = executor;

            assert member != null;

            text = "*Hugs* " + member.getAsMention();
        }

        MessageBuilder message = new MessageBuilder().setContent(text)
                .setEmbeds(embed.build())
                .denyMentions(
                        Message.MentionType.ROLE,
                        Message.MentionType.EVERYONE,
                        Message.MentionType.HERE
                );

        tc.sendMessage(message.build()).queue();
    }

    @Override
    public CommandData slashInfo() {
        return this.slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{this.optionName};
    }
}
