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

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandDescription(
        name = "createchannel",
        description = "Create a new channel",
        triggers = {"createchannel", "nc", "newchannel"},
        attributes = {
                @CommandAttribute(key = "category", value = "channels"),
                @CommandAttribute(key = "usage", value = "[command | alias] [channel name]"),
                @CommandAttribute(key = "examples", value = "`h!nc info`\n"),
        }
)
public class CmdCreateChannel implements Command {

    private final Hinata bot;
    protected final String optionName = "name";
    ArrayList<Permission> permissions = new ArrayList<>();

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(STRING, optionName, "the name of the new channel")
                    .setRequired(true));

    public CmdCreateChannel(Hinata bot) {
        this.bot = bot;
        permissions.add(Permission.MANAGE_CHANNEL);
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        String name = Objects.requireNonNull(event.getOption(this.optionName)).getAsString();
        EmbedBuilder embed = new EmbedBuilder();

        guild.createTextChannel(name)
                //add executor permission overrides
                .addMemberPermissionOverride(
                        member.getIdLong(),
                        Permission.VIEW_CHANNEL.getRawValue() + Permission.MANAGE_CHANNEL.getRawValue(),
                        0
                )
                //add bot permission overrides
                .addMemberPermissionOverride(
                        guild.getSelfMember().getIdLong(),
                        Permission.VIEW_CHANNEL.getRawValue() + Permission.MANAGE_CHANNEL.getRawValue(),
                        0
                )
                .queue();

        embed.setColor(Colors.NORMAL.getCode())
                .setTitle(this.getDescription().name())
                .setDescription("New channel created with name **" + name + "**!")
                .setTimestamp(ZonedDateTime.now());

        hook.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        String[] arguments = bot.getArguments(msg);
        String name = arguments[0];
        EmbedBuilder embed = new EmbedBuilder();

        guild.createTextChannel(name)
                //add executor permission overrides
                .addMemberPermissionOverride(
                        member.getIdLong(),
                        Permission.VIEW_CHANNEL.getRawValue() + Permission.MANAGE_CHANNEL.getRawValue(),
                        0
                )
                //add bot permission overrides
                .addMemberPermissionOverride(
                        guild.getSelfMember().getIdLong(),
                        Permission.VIEW_CHANNEL.getRawValue() + Permission.MANAGE_CHANNEL.getRawValue(),
                        0
                )
                .queue();

        embed.setColor(Colors.NORMAL.getCode())
                .setTitle(this.getDescription().name())
                .setDescription("New channel created with name **" + name + "**!")
                .setTimestamp(ZonedDateTime.now());

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
