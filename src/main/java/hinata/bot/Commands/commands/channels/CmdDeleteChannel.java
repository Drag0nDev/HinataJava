package hinata.bot.Commands.commands.channels;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.constants.Colors;
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
        name = "deletechannel",
        description = "Delete any channel in the server",
        triggers = {"deletechannel", "dc", "delchan"},
        attributes = {
                @CommandAttribute(key = "category", value = "channels"),
                @CommandAttribute(key = "usage", value = "[command | alias] [channel id]"),
                @CommandAttribute(key = "examples", value = "`h!dc 762241328599269396`\n"),
        }
)
public class CmdDeleteChannel implements Command {

    private final Hinata bot;
    protected final String optionName = "channel_id";
    ArrayList<Permission> permissions = new ArrayList<>();

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(STRING, optionName, "channel id")
                    .setRequired(true));

    public CmdDeleteChannel(Hinata bot) {
        this.bot = bot;
        permissions.add(Permission.MANAGE_CHANNEL);
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        String channelIdString = Objects.requireNonNull(event.getOption(this.optionName)).getAsString();
        EmbedBuilder embed = new EmbedBuilder();

        //check if it is an actual numbers only string
        if(!channelIdString.matches("\\d*")){
            embed.setColor(Colors.ERROR.getCode())
                    .setTitle(this.getDescription().name())
                    .setDescription("Please provide a valid channel ID")
                    .setTimestamp(ZonedDateTime.now());

            hook.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        long channelId = Long.parseLong(Objects.requireNonNull(event.getOption(this.optionName)).getAsString());
        if (guild.getGuildChannelById(channelId) == null){
            embed.setColor(Colors.ERROR.getCode())
                    .setTitle(this.getDescription().name())
                    .setDescription("Please provide a valid channel ID")
                    .setTimestamp(ZonedDateTime.now());

            hook.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        GuildChannel channel = guild.getGuildChannelById(channelId);

        channel.delete().queue();

        embed.setColor(Colors.NORMAL.getCode())
                .setTitle(this.getDescription().name())
                .setDescription(channel.getType().name().toLowerCase() + "channel with name **" + channel.getName() + "** has been deleted!")
                .setTimestamp(ZonedDateTime.now());

        hook.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        String[] arguments = bot.getArguments(msg);
        EmbedBuilder embed = new EmbedBuilder();

        //check if it is an actual numbers only string
        if(!arguments[0].matches("\\d*")){
            embed.setColor(Colors.ERROR.getCode())
                    .setTitle(this.getDescription().name())
                    .setDescription("Please provide a valid channel ID")
                    .setTimestamp(ZonedDateTime.now());

            tc.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        long channelId = Long.parseLong(arguments[0]);

        if (guild.getGuildChannelById(channelId) == null){
            embed.setColor(Colors.ERROR.getCode())
                    .setTitle(this.getDescription().name())
                    .setDescription("Please provide a valid channel ID")
                    .setTimestamp(ZonedDateTime.now());

            tc.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        GuildChannel channel = guild.getGuildChannelById(channelId);

        channel.delete().queue();

        embed.setColor(Colors.NORMAL.getCode())
                .setTitle(this.getDescription().name())
                .setDescription(channel.getType().name().toLowerCase() + "channel with name **" + channel.getName() + "** has been deleted!")
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
