package hinata.bot.Commands.commands.emojis;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandDescription(
        name = "emojidelete",
        description = "Create a new emoji or add one that already exists.",
        triggers = {"emojidelete", "ed"},
        attributes = {
                @CommandAttribute(key = "category", value = "emojis"),
                @CommandAttribute(key = "usage", value = "[command | alias] [emojinamee]"),
                @CommandAttribute(key = "examples", value = "h!ed Laughing")
        }
)

public class CmdEmojiDelete implements Command {
    private final Hinata bot;
    protected final String optionName = "name";
    ArrayList<Permission> permissions = new ArrayList<>();

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(STRING, optionName, "name of the emoji")
                            .setRequired(true));

    public CmdEmojiDelete(Hinata bot) {
        this.bot = bot;
        permissions.add(Permission.MANAGE_EMOTES);
    }

    @Override
    public void run(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        String name = event.getOption(optionName).getAsString();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Emoji delete")
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());

        try {
            Emote emoji = guild.getEmotesByName(name, true).stream().findFirst().get();

            emoji.delete().complete();

            embed.setDescription("The emoji **" + name + "** was successfully deleted!");
        }
        catch (NoSuchElementException e) {
            embed.setColor(Colors.ERROR.getCode())
                    .setDescription("no emoji with **" + name + "** found!");
        }

        hook.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void execute(Message msg, Object... args) {
        TextChannel tc = msg.getTextChannel();
        Guild guild = msg.getGuild();
        String[] arguments = bot.getArguments(msg);
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Emoji delete")
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());

        if (arguments.length == 0){
            embed.setColor(Colors.ERROR.getCode())
                    .setDescription("Please provide an emoji name!");

            tc.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        String name = arguments[0];

        try {
            Emote emoji = guild.getEmotesByName(name, true).stream().findFirst().get();

            emoji.delete().complete();

            embed.setDescription("The emoji **" + name + "** was successfully deleted!");
        }
        catch (NoSuchElementException e) {
            embed.setColor(Colors.ERROR.getCode())
                    .setDescription("no emoji with **" + name + "** found!");
        }

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

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        return this.permissions;
    }
}
