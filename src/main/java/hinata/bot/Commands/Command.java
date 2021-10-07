package hinata.bot.Commands;

import com.github.rainestormee.jdacommand.AbstractCommand;
import hinata.bot.Hinata;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;

public interface Command extends AbstractCommand<Message> {

    default void executeSlash(SlashCommandEvent event) {
        if (event.getMember() == null)
            return;

        run(event.getGuild(), event.getTextChannel(), event.getMember(), event, event.getHook());
    }

    CommandData slashInfo();

    String getOptionName();

    default ArrayList<Permission> getNeededPermissions(){
        return null;
    }

    void run(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook);
}
