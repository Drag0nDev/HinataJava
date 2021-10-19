package hinata.bot.Commands.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import hinata.bot.util.exceptions.HinataException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static hinata.bot.util.utils.Utils.*;

@CommandDescription(
        name = "stats",
        description = "Get the bot statistics.",
        triggers = {"stats"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "`h!stats`\n"),
        }
)

public class CmdStats implements Command {
    private final Hinata bot;

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description());

    public CmdStats(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws HinataException {
        hook.sendMessageEmbeds(createEmbed(guild)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws HinataException {
        tc.sendMessageEmbeds(createEmbed(guild)).queue();
    }

    @Override
    public CommandData slashInfo() {
        return this.slashInfo;
    }

    public MessageEmbed createEmbed(Guild guild) {
        User owner = Hinata.getBot().retrieveUserById(this.bot.getConfig().getOwner()).complete();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.NORMAL.getCode())
                .setAuthor(Hinata.getBot().getSelfUser().getAsTag(), generateSupportInvite(Hinata.getBot()))
                .setThumbnail(Hinata.getBot().getSelfUser().getAvatarUrl())
                .setFooter("©2020-" + Calendar.getInstance().get(Calendar.YEAR) + "Copyright: " + owner.getAsTag() + "\n" +
                        "Version: " + this.bot.getVersion())
                .addField("Developer", owner.getAsTag(), true)
                .addField("Developer ID", owner.getId(), true)
                .addField("Bot ID", Hinata.getBot().getSelfUser().getId(), true)
                .addField("Bot creation day", Hinata.getBot().getSelfUser().getTimeCreated().format(format()), true)
                .addField("In server", guild.getName(), true)
                .addField("Uptime", getUptime(), true)
                .addField("Other", "**Server count**: " + Hinata.getBot().getGuilds().size(), true)
                .addField("Links", "`• [**Join my support sever!** - Join if you need extra help!](" + generateSupportInvite(Hinata.getBot()) + ")\n" +
                        "• [**My invite link** - It's always fun with me in the server!](" + generateInvite() + ")", false);

        return embed.build();
    }

    private String getUptime() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        String uptimeStr = "";
        long seconds = getSeconds(uptime);
        long minutes = getMinutes(uptime);
        long hours = getHours(uptime);
        long days = getDays(uptime);

        if (days > 0)
            uptimeStr += days + " days ";
        if (hours > 0)
            uptimeStr += hours + " hours ";
        if (minutes > 0)
            uptimeStr += minutes + " minutes ";

        uptimeStr += seconds + " seconds.";

        return uptimeStr;
    }

    private long getDays(long uptime){
        return TimeUnit.MILLISECONDS.toDays(uptime);
    }

    private long getHours(long uptime){
        return TimeUnit.MILLISECONDS.toHours(uptime) - getDays(uptime) * 24;
    }

    private long getMinutes(long uptime){
        return TimeUnit.MILLISECONDS.toMinutes(uptime) - getHours(uptime) * 60 - getDays(uptime) * 1440;
    }

    private long getSeconds(long uptime){
        return TimeUnit.MILLISECONDS.toSeconds(uptime) - getMinutes(uptime) * 60 - getHours(uptime) * 3600 -
                getDays(uptime) * 86400;
    }
}
