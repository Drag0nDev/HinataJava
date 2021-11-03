package hinata.bot.Commands.commands.channels;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import hinata.bot.util.exceptions.HinataException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Objects;

import static net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL;

@CommandDescription(
        name = "setnsfw",
        description = "Toggle the nsfw mark on a channel",
        triggers = {"setnsfw", "snsfw"},
        attributes = {
                @CommandAttribute(key = "category", value = "channels"),
                @CommandAttribute(key = "usage", value = "[command | alias] [channel id]"),
                @CommandAttribute(key = "examples", value = "`h!setnsfw 762241328599269396`\n"),
        }
)
public class CmdSetNSFW implements Command {

    private final Hinata bot;
    protected final String optionName = "channel";
    ArrayList<Permission> permissions = new ArrayList<>();

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(CHANNEL, optionName, "channel")
                    .setRequired(false));

    public CmdSetNSFW(Hinata bot) {
        this.bot = bot;
        permissions.add(Permission.MANAGE_CHANNEL);
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws Exception {
        var option = event.getOption(this.optionName);

        MessageChannel mc = option != null ? option.getAsMessageChannel() : null;
        TextChannel channel = mc == null ? tc : guild.getTextChannelById(mc.getId());

        if (channel == null)
            throw new Exception("Channel is null");

        hook.sendMessageEmbeds(toggleNSFW(channel)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws HinataException {
        String[] arguments = bot.getArguments(msg);
        TextChannel channel = arguments.length == 0 ? tc : Objects.requireNonNull(guild.getTextChannelById(arguments[0]));

        if (channel == null)
            throw new HinataException("No valid channel provided");

        tc.sendMessageEmbeds(toggleNSFW(channel)).queue();
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

    private @NotNull MessageEmbed toggleNSFW(TextChannel channel) {
        if (channel.isNSFW()) {
            channel.getManager().setNSFW(false).queue();
            return new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                    .setTitle(this.getDescription().name())
                    .setDescription("**" + channel.getAsMention() + "** is now marked as **SFW**")
                    .setTimestamp(ZonedDateTime.now())
                    .build();
        } else {
            channel.getManager().setNSFW(true).queue();
            return new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                    .setTitle(this.getDescription().name())
                    .setDescription("**" + channel.getAsMention() + "** is now marked as **NSFW**")
                    .setTimestamp(ZonedDateTime.now())
                    .build();
        }
    }
}
