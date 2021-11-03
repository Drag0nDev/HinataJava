package hinata.bot.util.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;

import static hinata.bot.util.utils.Utils.getFile;

public class Config {
    private final String token;
    private final String owner;
    private final String currencyEmoji;
    private final List<String> prefix;
    private final int levelXp;

    //database variables
    private final String url;
    private final String user;
    private final String password;

    public Config() throws IOException, ParseException, URISyntaxException {
        JSONParser parser = new JSONParser();
        Object obj;
        JSONObject config;

        obj = parser.parse(new FileReader(getFile("config.json")));
        config = (JSONObject) obj;

        this.token = (String) config.get("token");
        this.owner = (String) config.get("owner");
        this.levelXp = (int) (long) config.get("levelXp");
        this.currencyEmoji = (String) config.get("currencyEmoji");

        JSONObject db = (JSONObject) config.get("db");
        this.url = (String) db.get("url");
        this.user = (String) db.get("user");
        this.password = (String) db.get("password");

        JSONArray prefix = (JSONArray) config.get("prefix");
        this.prefix = (List<String>) prefix;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
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

    public int getLevelXp() {
        return levelXp;
    }

    public String getCurrencyEmoji() {
        return currencyEmoji;
    }
}
