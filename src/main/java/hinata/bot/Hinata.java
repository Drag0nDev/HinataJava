package hinata.bot;

import com.github.rainestormee.jdacommand.CommandHandler;
import hinata.bot.Commands.CommandListener;
import hinata.bot.Commands.CommandLoader;
import hinata.bot.util.utils.Config;
import hinata.bot.events.Listener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hinata.bot.util.utils.Utils.getArgs;

public class Hinata {
    private final Config config = new Config();
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    private static JDA bot = null;

    private final CommandLoader commandLoader = new CommandLoader(this);
    private final CommandHandler<Message> CMD_HANDLER = new CommandHandler<>();

    public Hinata() throws IOException, ParseException, URISyntaxException {
    }

    public static void main(String[] arguments) {
        try {
            new Hinata().setup();
        } catch (LoginException | IOException | ParseException | URISyntaxException ex) {
            logger.error("Couldn't login to Discord!", ex);
        }
    }

    private void setup() throws LoginException, IOException, ParseException, URISyntaxException {
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

    public static JDA getBot() {
        return bot;
    }

    public Config getConfig() {
        return config;
    }

    public static Logger getLogger() {
        return logger;
    }

    public CommandHandler<Message> getCmdHandler() {
        return CMD_HANDLER;
    }

    public String[] getArguments(@NotNull Message msg) {
        //find the used prefix
        String raw = msg.getContentRaw();
        List<String> prefix = this.getConfig().getPrefix();
        raw = getArgs(raw, prefix);
        String[] split = raw.split("\\s+");

        return Arrays.copyOfRange(split, 1, split.length);
    }

    public Hinata getHinata() {
        return this;
    }
}