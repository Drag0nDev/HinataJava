package hinata.bot.Commands;

import com.github.rainestormee.jdacommand.AbstractCommand;
import hinata.bot.util.exceptions.HinataException;
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

    void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws HinataException;

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
}
