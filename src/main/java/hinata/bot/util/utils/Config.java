package hinata.bot.util.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Config {
    private final String token;
    private final String owner;
    private final List<String> prefix;
    private final long levelXp;

    public Config() throws IOException, ParseException, URISyntaxException {
        JSONParser parser = new JSONParser();
        Object obj;
        JSONObject config;

        obj = parser.parse(new FileReader(getConfigFile("config.json")));
        config = (JSONObject) obj;

        this.token = (String) config.get("token");
        this.owner = (String) config.get("owner");
        this.levelXp = (long) config.get("levelXp");

        JSONArray prefix = (JSONArray) config.get("prefix");
        this.prefix = (List<String>) prefix;
    }

    private File getConfigFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }
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

    public long getLevelXp() {
        return levelXp;
    }
}
