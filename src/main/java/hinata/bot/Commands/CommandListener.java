package hinata.bot.Commands;

import com.github.rainestormee.jdacommand.CommandHandler;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import hinata.bot.util.Listener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getGuild() == null)
            return;

        CMD_EXECUTOR.execute(() -> {
            Guild guild = event.getGuild();
            User user = event.getUser();

            MessageChannel tc = event.getChannel();
            Member self = guild.getSelfMember();

            Command command = (Command) HANDLER.findCommand(event.getName());

            if (command == null)
                return;

            if (!self.hasPermission((GuildChannel) tc, Permission.MESSAGE_WRITE))
                return;

            if (command.getAttribute("category").equals("owner") && !user.getId().equals(bot.getConfig().getOwner()))
                return;

            String args;

            if (command.getOptionName() == null) {
                args = "";
            } else {
                args = event.getOption(command.getOptionName()) == null ? "" : Objects.requireNonNull(event.getOption(command.getOptionName())).getAsString();
            }

            try {
                LOGGER.info("------------------------------\n" +
                                "Command: '{}'\n" +
                                "Arguments: '{}'\n" +
                                "User: '{}'\n" +
                                "User ID: '{}'\n" +
                                "Server: '{}'\n" +
                                "Server ID: '{}'\n" +
                                "Channel: '{}'",
                        event.getName(),
                        args,
                        user.getAsTag(),
                        user.getId(),
                        guild.getName(),
                        guild.getId(),
                        tc.getName()
                );
                command.executeSlash(bot, event);
            } catch (Exception e) {
                sendError(null, event);
                LOGGER.error("Couldn't preform command {}!", event.getName(), e);
            }
        });
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
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
            String[] args = Arrays.copyOfRange(raw.trim().split("\\s+"), 0, 10);

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
                for (i = 1; i < 10; i++) {
                    if (args[i] == null)
                        args[i] = "";
                }
                String[] arguments = Arrays.copyOfRange(args, 1, 10);
                StringBuilder argumentsLog = new StringBuilder();
                for (String arg : arguments) {
                    if (!arg.equals(""))
                        argumentsLog.append(arg).append(" ");
                }
                LOGGER.info("------------------------------\n" +
                                "Command: '{}'\n" +
                                "Arguments: '{}'\n" +
                                "User: '{}'\n" +
                                "User ID: '{}'\n" +
                                "Server: '{}'\n" +
                                "Server ID: '{}'\n" +
                                "Channel: '{}'",
                        args[0],
                        argumentsLog,
                        user.getAsTag(),
                        user.getId(),
                        guild.getName(),
                        guild.getId(),
                        tc.getName()
                );
                HANDLER.execute(command, msg, (Object[]) arguments);
            } catch (Exception e) {
                sendError(tc, null);
                LOGGER.info("Couldn't preform command {}!", command.getDescription().name(), e);
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

    private void sendError(TextChannel tc, SlashCommandEvent event) {
        String inviteLink;
        Optional<Invite> invite;
        Guild support = bot.getBot().getGuildById("645047329141030936");
        List<Invite> invites = Objects.requireNonNull(support).retrieveInvites().complete();


        invite = invites.stream().findFirst();
        if (invite.isPresent())
            inviteLink = invite.get().getUrl();
        else {
            Invite base = Objects.requireNonNull(support.getDefaultChannel())
                    .createInvite()
                    .complete();
            inviteLink = base.getUrl();
        }

        EmbedBuilder embed = new EmbedBuilder().setColor(Colors.ERROR.getCode())
                .setTitle("An error occurred")
                .setDescription("An error occurred and the command stopped executing.\n" +
                        "Please report this to the bot developer in the **[support server](" + inviteLink + ")**")
                .setTimestamp(ZonedDateTime.now());

        if (tc != null) {
            tc.sendMessageEmbeds(embed.build()).queue();
        } else {
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }
}