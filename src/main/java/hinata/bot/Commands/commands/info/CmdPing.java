package hinata.bot.Commands.commands.info;

import com.github.rainestormee.jdacommand.*;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Date;

@CommandDescription(
        name = "ping",
        description = "Response time of the bot",
        triggers = {"ping"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "h!ping")
        }
)

public class CmdPing implements Command {
    private final Hinata bot;

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description());

    public CmdPing(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        Date now = new Date();
        Date sent = new Date(msg.getTimeCreated().toInstant().toEpochMilli());

        long ping = now.getTime() - sent.getTime();

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Ping")
                .setDescription(ping + "ms")
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(now.toInstant());

        msg.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        Date now = new Date();
        Date sent = new Date(event.getTimeCreated().toInstant().toEpochMilli());

        long ping = now.getTime() - sent.getTime();

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Ping")
                .setDescription(ping + "ms")
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(now.toInstant());

        hook.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public CommandData getSlashInfo() {
        return slashInfo;
    }
}
