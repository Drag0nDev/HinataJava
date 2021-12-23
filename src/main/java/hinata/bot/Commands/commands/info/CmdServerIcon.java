package hinata.bot.Commands.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.ZonedDateTime;

@CommandDescription(
        name = "servericon",
        description = "See the icon of the server.",
        triggers = {"servericon", "si"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "`h!si`\n"),
        }
)

public class CmdServerIcon implements Command {
    private final Hinata bot;

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description());

    public CmdServerIcon(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        hook.sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("Server icon of server: " + guild.getName())
                        .setImage(guild.getIconUrl())
                        .setColor(Colors.NORMAL.getCode())
                        .setTimestamp(ZonedDateTime.now())
                        .build())
                .queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        tc.sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("Server icon of server: " + guild.getName())
                        .setImage(guild.getIconUrl())
                        .setColor(Colors.NORMAL.getCode())
                        .setTimestamp(ZonedDateTime.now())
                        .build())
                .queue();
    }

    @Override
    public CommandData getSlashInfo() {
        return slashInfo;
    }
}
