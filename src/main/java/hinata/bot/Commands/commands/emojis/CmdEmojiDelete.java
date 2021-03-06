package hinata.bot.Commands.commands.emojis;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.constants.Colors;
import hinata.util.exceptions.HinataException;
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
        permissions.add(Permission.MANAGE_EMOTES_AND_STICKERS);
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        String name = event.getOption(optionName).getAsString();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Emoji delete")
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());

        try {
            if (guild.getEmotesByName(name, true).stream().findFirst().isEmpty())
                throw new HinataException("no emoji with **" + name + "** found!");

            Emote emoji = guild.getEmotesByName(name, true).stream().findFirst().get();

            emoji.delete().complete();

            embed.setDescription("The emoji **" + name + "** was successfully deleted!");
        }
        catch (HinataException e) {
            embed.setColor(Colors.ERROR.getCode())
                    .setDescription(e.getMessage());
        }

        hook.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
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
            if (guild.getEmotesByName(name, true).stream().findFirst().isEmpty())
                throw new HinataException("no emoji with **" + name + "** found!");

            Emote emoji = guild.getEmotesByName(name, true).stream().findFirst().get();

            emoji.delete().complete();

            embed.setDescription("The emoji **" + name + "** was successfully deleted!");
        }
        catch (HinataException e) {
            embed.setColor(Colors.ERROR.getCode())
                    .setDescription(e.getMessage());
        }

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

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        return this.permissions;
    }
}
