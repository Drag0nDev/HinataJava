package hinata.bot.Commands;

import com.github.rainestormee.jdacommand.CommandHandler;
import hinata.bot.Hinata;
import hinata.bot.util.Listener;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    private final ThreadGroup CMD_THREAD = new ThreadGroup("CommandThread");
    private final Executor CMD_EXECUTOR = Executors.newCachedThreadPool(
            r -> new Thread(CMD_THREAD, r, "CommandPool")
    );

    private final Hinata bot;
    private final CommandHandler<Message> HANDLER;

    public CommandListener(Hinata bot, CommandHandler<Message> HANDLER) {
        this.bot = bot;
        this.HANDLER = HANDLER;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
        CMD_EXECUTOR.execute(() -> {
            if (!event.getChannel().getType().equals(ChannelType.TEXT))
                return;

            Message msg = event.getMessage();
            Guild guild = event.getGuild();
            User user = event.getAuthor();

            if (user.isBot() || event.isWebhookMessage())
                return;

            if (event.getChannel().isNews())
                return;

            if (!checkPrefix(msg.getContentRaw()))
                return;

            String raw = msg.getContentRaw();

            //find the used prefix
            List<String> prefix = bot.getConfig().getPrefix();
            String usedPrefix = "";
            int i = 0;

            while (usedPrefix.equals("")) {
                String toFind = "^" + prefix.get(i);
                Pattern pattern = Pattern.compile(toFind, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(raw);
                boolean matchFound = matcher.find();
                if (matchFound) usedPrefix = prefix.get(i);
                i++;
            }

            raw = raw.replaceFirst(Pattern.quote(usedPrefix), "");
            String[] args = Arrays.copyOf(raw.trim().split("\\s+"), 2);

            TextChannel tc = event.getChannel();
            Member self = guild.getSelfMember();

            if (args[0] == null)
                return;

            Command command = (Command) HANDLER.findCommand(args[0].toLowerCase());

            if (command == null)
                return;

            if (!self.hasPermission(tc, Permission.MESSAGE_WRITE))
                return;

            if (command.getAttribute("category").equals("owner") && !user.getId().equals(bot.getConfig().getOwner()))
                return;

            try {
                String arguments = args[1] == null ? "" : args[1];
                LOGGER.info("------------------------------\n" +
                                "Command: '{}'\n" +
                                "Arguments: '{}'\n" +
                                "User: '{}'\n" +
                                "User ID: '{}'\n" +
                                "Server: '{}'\n" +
                                "Server ID: '{}'\n" +
                                "Channel: '{}'",
                        args[0], arguments, user.getAsTag(), user.getId(), guild.getName(), guild.getId(), tc.getName()
                );
                HANDLER.execute(command, msg, arguments);
            } catch (Exception e) {
                LOGGER.error("Couldn't preform command {}!", args[0], e);
            }
        });
    }

    private boolean checkPrefix(String message) {
        List<String> prefix = bot.getConfig().getPrefix();

        for (String newPrefix : prefix) {
            String toFind = "^" + newPrefix;
            Pattern pattern = Pattern.compile(toFind, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(message);
            boolean matchFound = matcher.find();
            if (matchFound) return true;
        }

        return false;
    }
}
