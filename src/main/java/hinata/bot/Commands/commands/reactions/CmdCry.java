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

import java.time.ZonedDateTime;

@CommandDescription(
        name = "cry",
        description = "Show a crying gif",
        triggers = {"cry"},
        attributes = {
                @CommandAttribute(key = "category", value = "reactions"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "`h!cry"),
        }
)

public class CmdCry implements Command {
    private final Hinata bot;

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description());

    public CmdCry(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        String text = "Aww don't cry " + member.getAsMention() + ". I will hug you!";
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.NORMAL.getCode())
                .setImage(CustomReactions.cry())
                .setFooter("Powered by lost hopes and dreams")
                .setTimestamp(ZonedDateTime.now());

        MessageBuilder message = new MessageBuilder().setContent(text)
                .setEmbeds(embed.build());

        hook.sendMessage(message.build()).queue();
    }

    @Override
    public void execute(Message msg, Object... args) {
        TextChannel tc = msg.getTextChannel();
        Member executor = msg.getMember();

        assert executor != null;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.NORMAL.getCode())
                .setImage(CustomReactions.cry())
                .setFooter("Powered by lost hopes and dreams")
                .setTimestamp(ZonedDateTime.now());

        String text = "Aww don't cry " + executor.getAsMention() + ". I will hug you!";

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
}