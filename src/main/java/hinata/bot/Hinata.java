package hinata.bot;

import com.github.rainestormee.jdacommand.CommandHandler;
import hinata.bot.Commands.CommandListener;
import hinata.bot.Commands.CommandLoader;
import hinata.bot.constants.Colors;
import hinata.bot.constants.CustomReactions;
import hinata.bot.util.Config;
import hinata.bot.util.Listener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class Hinata {
    private final Config config = new Config();
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    private JDA bot = null;

    private final CommandLoader commandLoader = new CommandLoader(this);
    private final CommandHandler<Message> CMD_HANDLER = new CommandHandler<>();

    public Hinata() throws IOException, ParseException {
    }

    public static void main(String[] arguments) {
        try {
            new Hinata().setup();
        } catch (LoginException | IOException | ParseException ex) {
            LOGGER.error("Couldn't login to Discord!", ex);
        }
    }

    private void setup() throws LoginException, IOException, ParseException {
        CMD_HANDLER.registerCommands(new HashSet<>(commandLoader.getCommands()));

        bot = JDABuilder
                .createDefault(config.getToken())
                .disableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_BANS
                )
                .disableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(
                        new CommandListener(this, CMD_HANDLER),
                        new Listener(this)
                )
                .setActivity(Activity.playing("on a mission with Naruto"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        commandLoader.loadSlashCommands(this);
    }

    public JDA getBot() {
        return bot;
    }

    public Config getConfig() {
        return config;
    }

    public Logger getLogger() {
        return LOGGER;
    }

    public CommandHandler<Message> getCmdHandler() {
        return CMD_HANDLER;
    }

    public String[] getArguments(@NotNull Message msg) {
        //find the used prefix
        String raw = msg.getContentRaw();
        List<String> prefix = this.getConfig().getPrefix();
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
        String[] split = raw.split("\\s+");

        return Arrays.copyOfRange(split, 1, split.length);
    }

    public void sendNSFWWarning(@NotNull TextChannel tc, @NotNull InteractionHook hook){
        EmbedBuilder embed = new EmbedBuilder()
                .setDescription("This command can't be executed because this channel " + tc.getAsMention() + " is not marked NSFW")
                .setColor(Colors.ERROR.getCode())
                .setTimestamp(ZonedDateTime.now());

        hook.sendMessageEmbeds(embed.build()).queue();
    }

    public void sendNSFWWarning(@NotNull TextChannel tc) {
        EmbedBuilder embed = new EmbedBuilder()
                .setDescription("This command can't be executed because this channel " + tc.getAsMention() + " is not marked NSFW")
                .setColor(Colors.ERROR.getCode())
                .setTimestamp(ZonedDateTime.now());

        tc.sendMessageEmbeds(embed.build()).queue();
    }
}