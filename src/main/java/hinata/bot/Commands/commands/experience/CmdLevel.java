package hinata.bot.Commands.commands.experience;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.constants.Colors;
import hinata.database.tables.ServerUser;
import hinata.database.tables.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;

import static hinata.util.utils.Utils.getFile;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@CommandDescription(
        name = "level",
        description = "Show the level card of yourself or a server member.",
        triggers = {"level", "lvl", "xp", "rank"},
        attributes = {
                @CommandAttribute(key = "category", value = "experience"),
                @CommandAttribute(key = "usage", value = "[command | alias] <user mention>"),
                @CommandAttribute(key = "examples", value = "`h!experience 418037700751261708`\n" +
                        "`h!bonk @Drag0n#6666`")
        }
)

public class CmdLevel implements Command {
    private final Hinata bot;
    protected final String optionName = "user";

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(USER, optionName, "User u want to see the profile card of")
                    .setRequired(false));

    public CmdLevel(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws Exception {
        net.dv8tion.jda.api.entities.User user;

        if (event.getOption(optionName) == null)
            user = member.getUser();
        else
            user = Objects.requireNonNull(event.getOption(optionName)).getAsUser();

        User dbUser = bot.getDbUtils().getUser(user.getId());
        ServerUser serverUser = bot.getDbUtils().getServerUser(guild.getId(), user.getId());

        File file = new File("xpCard_" + dbUser.userId + ".png");

        makeCard(user, dbUser, serverUser, file);
        hook.sendFile(file, "card.png").queue();

        if (!file.delete())
            Hinata.getLogger().error("File " + file.getName() + " failed to delete.\n" +
                    "Path: " + file.getAbsolutePath());
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws SQLException, URISyntaxException, IOException {
        String[] arguments = bot.getArguments(msg);
        net.dv8tion.jda.api.entities.User user;

        if (arguments.length > 0) {
            if (!msg.getMentionedMembers().isEmpty()) {
                user = msg.getMentionedMembers().get(0).getUser();
            } else {
                user = guild.getMemberById(arguments[0]).getUser();
            }
        } else {
            user = msg.getMember().getUser();
        }

        User dbUser = bot.getDbUtils().getUser(user.getId());
        ServerUser serverUser = bot.getDbUtils().getServerUser(guild.getId(), user.getId());

        File file = new File("xpCard_" + dbUser.userId + ".png");

        makeCard(user, dbUser, serverUser, file);
        msg.getTextChannel().sendFile(file, "card.png").queue();

        if (!file.delete())
            Hinata.getLogger().error("File " + file.getName() + " failed to delete.\n" +
                    "Path: " + file.getAbsolutePath());
    }

    @Override
    public CommandData getSlashInfo() {
        return slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{optionName};
    }

    private void makeCard(net.dv8tion.jda.api.entities.User member, User user, ServerUser serverUser, File file) throws URISyntaxException, IOException, SQLException {
        BufferedImage out = new BufferedImage(2048, 1024, BufferedImage.TYPE_INT_RGB);
        BufferedImage bg;
        URL link = new URL(member.getEffectiveAvatarUrl());

        int globalRank = this.bot.getDbUtils().getGlobalRank(user.userId);
        int serverRank = this.bot.getDbUtils().getServerRank(user.userId, serverUser.serverId);

        // used fonts
        Font nameFont = new Font("Dosis", Font.PLAIN, 100);
        Font otherFont = new Font("Dosis", Font.PLAIN, 50);

        // calculate lenghts of the xp fillers
        int globalWidth = ((out.getWidth() - 200) * user.level.getPercentage()) / 100;
        int serverWidth = ((out.getWidth() - 200) * serverUser.level.getPercentage()) / 100;

        // get the background if they have a custom one
        try {
            if (user.background == null)
                bg = ImageIO.read(getFile("images/inventory/default_xp.jpg"));
            else if (user.background.equals("custom"))
                bg = ImageIO.read(getFile("images/custom/" + user.userId + ".png"));
            else
                bg = ImageIO.read(getFile("images/inventory/" + user.background));
        } catch (Exception e) {
            bg = ImageIO.read(getFile("images/inventory/default_xp.jpg"));
        }

        BufferedImage av = ImageIO.read(link);

        // get the types
        int bgType = bg.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bg.getType();
        int avType = av.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : av.getType();

        // resizing the images
        BufferedImage background = resizeImage(bg, bgType, 2048, 1024);
        BufferedImage avatar = resizeImage(av, avType, 250, 250);
        BufferedImage circleAvatar = cropCicle(avatar);

        // starting the drawing
        Graphics g = out.createGraphics();

        // input background and profile picture
        g.drawImage(background, 0, 0, null);
        g.drawImage(circleAvatar, 100, 100, null);

        // draw all text
        // draw username
        g.setColor(Color.white);
        g.setFont(nameFont);
        g.drawString(user.userTag, 375, 250);

        // draw other strings
        g.setFont(otherFont);

        // draw global bar strings
        int globalLvlPlace = (out.getWidth() / 2) - (g.getFontMetrics().stringWidth("Level " + user.level.getLevel()) / 2);
        int globalRankPlace = out.getWidth() - (100 + g.getFontMetrics().stringWidth("#" + globalRank));

        g.drawString("Global", 100, 475);
        g.drawString("Level " + user.level.getLevel(), globalLvlPlace, 475);
        g.drawString("#" + globalRank, globalRankPlace, 475);

        // draw current xp
        g.drawString("" + user.level.getRemainingXp(), 100, 600);

        // draw needed xp
        g.drawString("" + user.level.getNeededXp(),
                out.getWidth() - (100 + g.getFontMetrics().stringWidth("" + user.level.getNeededXp())), 600);

        // draw global bar strings
        int serverLvlPlace = (out.getWidth() / 2) - (g.getFontMetrics().stringWidth("Level " + serverUser.level.getLevel()) / 2);
        int serverRankPlace = out.getWidth() - (100 + g.getFontMetrics().stringWidth("#" + globalRank));

        g.drawString("Server", 100, 775);
        g.drawString("Level " + serverUser.level.getLevel(), serverLvlPlace, 775);
        g.drawString("#" + serverRank, serverRankPlace, 775);

        // draw current xp
        g.drawString("" + serverUser.level.getRemainingXp(), 100, 900);

        // draw needed xp
        g.drawString("" + serverUser.level.getNeededXp(),
                out.getWidth() - (100 + g.getFontMetrics().stringWidth("" + user.level.getNeededXp())), 900);

        // draw xp bars
        // draw background of the bars
        g.fillRect(100, 500, out.getWidth() - 200, 50);
        g.fillRect(100, 800, out.getWidth() - 200, 50);

        // input the color if the user has a custom color
        if (user.color == null)
            g.setColor(new Color(Colors.NORMAL.getCode()));
        else
            g.setColor(user.color);

        // draw the xp meter
        g.fillRect(100, 500, globalWidth, 50);
        g.fillRect(100, 800, serverWidth, 50);

        ImageIO.write(out, "png", file);
        g.dispose();
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type, Integer img_width,
                                             Integer img_height) {
        BufferedImage resizedImage = new BufferedImage(img_width, img_height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, img_width, img_height, null);
        g.dispose();

        return resizedImage;
    }

    private static BufferedImage cropCicle(BufferedImage avatar) {
        int width = avatar.getWidth();
        BufferedImage circleBuffer = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleBuffer.createGraphics();
        g2.setClip(new Ellipse2D.Float(0, 0, width, width));
        g2.drawImage(avatar, 0, 0, width, width, null);

        return circleBuffer;
    }
}