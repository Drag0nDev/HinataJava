package hinata.bot.Commands.commands.emojis;

import com.coder4.emoji.EmojiUtils;
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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandDescription(
        name = "emoji",
        description = "Show a bigger version of a server emoji.",
        triggers = {"emoji", "e"},
        attributes = {
                @CommandAttribute(key = "category", value = "emojis"),
                @CommandAttribute(key = "usage", value = "[command | alias] [emoji]"),
        }
)

public class CmdEmoji implements Command {
    private final Hinata bot;
    protected final String optionName = "emoji";

    //regex patterns used
    protected final Pattern emoji       = Pattern.compile("<a:.+?:\\d+>|<:.+?:\\d+>", Pattern.CASE_INSENSITIVE);
    protected final Pattern antimated   = Pattern.compile("<a:.+?:\\d+>", Pattern.CASE_INSENSITIVE);
    protected final Pattern id          = Pattern.compile("\\d+");
    protected final Pattern name        = Pattern.compile(":.*?:");

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(STRING, optionName, "Emoji u want to see bigger")
                    .setRequired(true));

    public CmdEmoji(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        ArrayList<String> idMatches = new ArrayList<>();
        String input = event.getOption(this.optionName).getAsString();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Emoji")
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());

        //matchers declarations
        Matcher idMatcher = id.matcher(input);
        Matcher nameMatcher = name.matcher(input);

        if (!emoji.matcher(input).matches()) {
            embed = new EmbedBuilder().setColor(Colors.ERROR.getCode())
                    .setDescription("Please provide a valid emoji!");

            hook.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        while (idMatcher.find()){
            idMatches.add(idMatcher.group());
        }

        if (nameMatcher.find()) {
            embed.setDescription("Emoji: **" + nameMatcher.group().replace(":", "") + "**");
        }

        if (antimated.matcher(input).matches()) {
            embed.setImage("https://cdn.discordapp.com/emojis/" + idMatches.get(idMatches.size() - 1) + ".gif");
        } else {
            embed.setImage("https://cdn.discordapp.com/emojis/" + idMatches.get(idMatches.size() - 1) + ".png");
        }

        hook.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void execute(Message msg, Object... args) {
        TextChannel tc = msg.getTextChannel();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Emoji")
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());

        if (msg.getEmotes().isEmpty()) {
            embed = new EmbedBuilder().setColor(Colors.ERROR.getCode())
                    .setDescription("Please provide a valid emoji!");

            tc.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        Emote emoji = msg.getEmotes().stream().findFirst().get();

        embed.setDescription("Emoji: **" + emoji.getName() + "**")
                .setImage(emoji.getImageUrl());

        tc.sendMessageEmbeds(embed.build()).queue();
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
