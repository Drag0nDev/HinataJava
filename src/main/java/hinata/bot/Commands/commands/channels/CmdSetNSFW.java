package hinata.bot.Commands.commands.channels;

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
                    .setRequired(true));

    public CmdSetNSFW(Hinata bot) {
        this.bot = bot;
        permissions.add(Permission.MANAGE_CHANNEL);
    }

    @Override
    public void run(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        MessageChannel mc = Objects.requireNonNull(event.getOption(this.optionName)).getAsMessageChannel();
        EmbedBuilder embed = new EmbedBuilder();
        TextChannel channel = Objects.requireNonNull(guild.getTextChannelById(mc.getId()));

        if (channel.isNSFW()) {
            channel.getManager().setNSFW(false).queue();
            embed.setColor(Colors.NORMAL.getCode())
                    .setTitle(this.getDescription().name())
                    .setDescription("**" + channel.getName() + "** is now marked as **SFW**")
                    .setTimestamp(ZonedDateTime.now());
        } else {
            channel.getManager().setNSFW(true).queue();
            embed.setColor(Colors.NORMAL.getCode())
                    .setTitle(this.getDescription().name())
                    .setDescription("**" + channel.getName() + "** is now marked as **NSFW**")
                    .setTimestamp(ZonedDateTime.now());
        }

        hook.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void execute(Message msg, Object... args) {
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        String[] arguments = bot.getArguments(msg);
        EmbedBuilder embed = new EmbedBuilder();
        TextChannel channel = Objects.requireNonNull(guild.getTextChannelById(arguments[0]));

        if (channel.isNSFW()) {
            channel.getManager().setNSFW(false).queue();
            embed.setColor(Colors.NORMAL.getCode())
                    .setTitle(this.getDescription().name())
                    .setDescription("**" + channel.getName() + "** is now marked as **SFW**")
                    .setTimestamp(ZonedDateTime.now());
        } else {
            channel.getManager().setNSFW(true).queue();
            embed.setColor(Colors.NORMAL.getCode())
                    .setTitle(this.getDescription().name())
                    .setDescription("**" + channel.getName() + "** is now marked as **NSFW**")
                    .setTimestamp(ZonedDateTime.now());
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
