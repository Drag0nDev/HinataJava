package hinata.bot.Commands;

import com.github.rainestormee.jdacommand.AbstractCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public interface Command extends AbstractCommand<Message> {
    default void execute(Message message, String s, String trigger){
        String[] args = s.isEmpty() ? new String[0] : s.split("\\s+", 3);

        if(message.getMember() == null)
            return;

        // We trigger the below method to run the commands.
        run(message.getGuild(), message.getTextChannel(), message, message.getMember(), args);
    }

    /*
     * This is the method we use in the commands to provide the information for easier handling.
     */
    void run(Guild guild, TextChannel tc, Message msg, Member member, String... args);
}
