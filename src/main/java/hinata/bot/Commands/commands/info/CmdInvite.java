package hinata.bot.Commands.commands.info;

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

import java.time.ZonedDateTime;

import static hinata.bot.util.utils.Utils.generateInvite;

@CommandDescription(
        name = "invite",
        description = "Invitelink for the bot.",
        triggers = {"invite", "i"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "`h!invite`\n"),
        }
)

public class CmdInvite implements Command {
    private final Hinata bot;

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description());

    public CmdInvite(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        hook.sendMessageEmbeds(
                        new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                                .setTitle("Invite")
                                .setDescription("Invite me to your server: [link](" + generateInvite() + ").")
                                .setTimestamp(ZonedDateTime.now())
                                .build())
                .queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        tc.sendMessageEmbeds(
                        new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                                .setTitle("Invite")
                                .setDescription("Invite me to your server: [link](" + generateInvite() + ").")
                                .setTimestamp(ZonedDateTime.now())
                                .build())
                .queue();
    }

    @Override
    public CommandData slashInfo() {
        return this.slashInfo;
    }
}
