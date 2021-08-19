package hinata.bot;

import ch.qos.logback.core.db.dialect.DBUtil;
import com.github.rainestormee.jdacommand.AbstractCommand;
import com.github.rainestormee.jdacommand.CommandHandler;
import hinata.bot.Commands.CommandListener;
import hinata.bot.Commands.CommandLoader;
import hinata.bot.util.Config;
import hinata.bot.util.Listener;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.*;

public class Hinata {
    private final Config config = new Config();
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    private ShardManager shardManager = null;

    private final CommandLoader commandLoader = new CommandLoader(this);
    private final CommandHandler<Message> CMD_HANDLER = new CommandHandler<>();

    public Hinata() throws IOException, ParseException {
    }

    public static void main(String[] arguments){
        try {
            new Hinata().setup();
        } catch (LoginException | IOException | ParseException ex) {
            LOGGER.error("Couldn't login to Discord!", ex);
        }
    }

    private void setup() throws LoginException, IOException, ParseException {
        CMD_HANDLER.registerCommands(new HashSet<>(commandLoader.getCommands()));

        shardManager = DefaultShardManagerBuilder
                .createDefault(config.getToken())
                .disableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .disableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(
                        new CommandListener(this, CMD_HANDLER),
                        new Listener(this)
                )
                .setShardsTotal(-1)
                .setActivity(Activity.playing("on a mission with Naruto"))
                .build();
    }

    public Config getConfig() {
        return config;
    }
}