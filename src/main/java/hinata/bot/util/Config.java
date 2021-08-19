package hinata.bot.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Config {
    private final String token;
    private final String owner;
    private final List<String> prefix;
    private final String currencyEmoji;
    private final long levelXp;

    public Config() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj;
        JSONObject config;

        obj = parser.parse(new FileReader("src/main/resources/config.json"));
        config = (JSONObject) obj;

        this.token = (String) config.get("token");
        this.owner = (String) config.get("owner");
        this.currencyEmoji = (String) config.get("currencyEmoji");
        this.levelXp = (long) config.get("levelXp");

        JSONArray prefix = (JSONArray) config.get("prefix");
        this.prefix = (List<String>) prefix;
    }

    public String getToken() {
        return token;
    }

    public String getOwner() {
        return owner;
    }

    public List<String> getPrefix() {
        return prefix;
    }

    public String getCurrencyEmoji() {
        return currencyEmoji;
    }

    public long getLevelXp() {
        return levelXp;
    }
}
