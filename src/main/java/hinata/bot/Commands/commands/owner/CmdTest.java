package hinata.bot.Commands.commands.owner;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import net.dv8tion.jda.api.entities.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static hinata.bot.util.utils.Utils.jsonToEmbed;

@CommandDescription(
        name = "test",
        description = "test cmd",
        triggers = {"test"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "h!test")
        }
)

public class CmdTest implements Command {
        private final Hinata bot;

        public CmdTest(Hinata bot) {
                this.bot = bot;
        }

        @Override
        public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
                String str ="{" +
                        "\"color\": \"#ff0000\"," +
                        "\"title\": \"Shutting down\"," +
                        "\"description\": \"I am shutting down myself\"" +
                        "}";

                MessageEmbed embed = jsonToEmbed(str);

                File out = new File("src/main/resources","test.png");
                BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
                Graphics g = image.createGraphics();
                g.setColor(new Color(Colors.NORMAL.getCode()));
                g.fillRect(0, 0, 100, 100);

                try {
                        ImageIO.write(image, "png", out);
                        msg.getTextChannel().sendFile(out, "test.png").setEmbeds(embed).queue();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                if (!out.delete())
                        bot.getLogger().error("File " + out.getName() + " failed to delete.\n" +
                                "Path: " + out.getAbsolutePath());
        }
}
