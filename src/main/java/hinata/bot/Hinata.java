package hinata.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Hinata {
    public static void main(String[] arguments) throws Exception {
        JSONObject config = readConfig();

        JDA hinata = JDABuilder
                .createDefault((String) config.get("token"))
                .setActivity(Activity.playing("on a mission with naruto"))
                .build();

        hinata.addEventListener(new Listener(config));
    }

    private static JSONObject readConfig() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj;

        obj = parser.parse(new FileReader("config.json"));

        return (JSONObject) obj;
    }
}