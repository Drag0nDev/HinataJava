package hinata.bot.Commands;

import com.github.rainestormee.jdacommand.AbstractCommand;
import hinata.bot.constants.Colors;
import hinata.bot.util.exceptions.HinataException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import static hinata.bot.Hinata.getBot;
import static hinata.bot.util.utils.Utils.getSupportInvite;

public interface Command extends AbstractCommand<Message> {

    default void executeSlash(SlashCommandEvent event) throws Exception {
        if (event.getMember() == null)
            return;

        run(event.getGuild(), event.getTextChannel(), event.getMember(), event, event.getHook());
    }

    @Override
    default void execute(Message object, Object... args){} //This code is useless for Custom error messages

    default CommandData slashInfo() {
        return null;
    }

    default String[] getOptionNames() {
        return null;
    }

    default ArrayList<Permission> getNeededPermissions() {
        return null;
    }

    default int getCooldown() {
        return 0;
    }

    void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws HinataException;

    default void run(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws Exception {
        //empty for when the command has no slash equivalent
    }

    ;

    private void sendError(TextChannel tc) {
        String inviteLink = getSupportInvite(getBot());

        EmbedBuilder embed = new EmbedBuilder().setColor(Colors.ERROR.getCode())
                .setTitle("An error occurred")
                .setDescription("An error occurred and the command stopped executing.\n" +
                        "Please report this to the bot developer in the **[support server](" + inviteLink + ")**")
                .setTimestamp(ZonedDateTime.now());

        tc.sendMessageEmbeds(embed.build()).queue();
    }
}
