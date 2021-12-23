package hinata.bot.Commands.commands.currency;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.constants.Colors;
import hinata.constants.Emotes;
import hinata.database.tables.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

@CommandDescription(
        name = "daily",
        description = "Create a new category",
        triggers = {"daily"},
        attributes = {
                @CommandAttribute(key = "category", value = "currency"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "`h!daily`\n"),
        }
)

public class CmdDaily implements Command {
    private final Hinata bot;

    protected final String optionName = "user";

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description());

    public CmdDaily(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws Exception {
        hook.sendMessageEmbeds(daily(member)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        tc.sendMessageEmbeds(daily(member)).queue();
    }

    @Override
    public CommandData getSlashInfo() {
        return slashInfo;
    }

    private @NotNull MessageEmbed daily(@NotNull Member member) throws Exception {
        User user = bot.getDbUtils().getUser(member.getId());
        int daily;

        if (user.dailyTaken != null) {
            Calendar nextDaily = Calendar.getInstance();
            nextDaily.setTimeInMillis(user.dailyTaken.getTime());
            nextDaily.add(Calendar.DATE, 1);

            if (user.isBanned)
                return new EmbedBuilder().setTitle("You have a botban")
                        .setColor(Colors.ERROR.getCode())
                        .setDescription("You can't claim dailies due to being banned from using the daily command.\n" +
                                "You will also not earn any xp globally with the botban.")
                        .setTimestamp(ZonedDateTime.now())
                        .build();

            if (nextDaily.getTime().getTime() > Calendar.getInstance().getTime().getTime())
                return new EmbedBuilder().setColor(Colors.ERROR.getCode())
                        .setDescription("You can claim your next daily in: **" + getTimeDiff(user.dailyTaken) + "**")
                        .build();

            if (user.dailyTaken != null) {
                if ((getDiff(user.dailyTaken) / (60 * 60 * 24) % 365) > 1)
                    user.dailyStreak = 0;
            }

        }

        int dailyReward = 100;
        daily = dailyReward + ((dailyReward / 10) * user.dailyStreak);
        user.balance += daily;
        user.dailyTaken = new Timestamp(new Date().getTime());
        user.dailyStreak++;

        bot.getDbUtils().updateUser(user);
        return new EmbedBuilder()
                .setColor(Colors.NORMAL.getCode())
                .setDescription("You have claimed your daily of **" + daily + Emotes.CURRENCY.getEmote() + "**.\n" +
                        "Your total balance now is at **" + user.balance + Emotes.CURRENCY.getEmote() + "**.\n" +
                        "Your daily streak is at **" + (user.dailyStreak - 1) + " day(s)**.")
                .setTimestamp(ZonedDateTime.now())
                .build();
    }

    private @NotNull String getTimeDiff(@NotNull Timestamp time) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime sdt = time.toInstant().atZone(now.getZone());

        long diff = Math.abs(sdt.toEpochSecond() - now.toEpochSecond());

        long second = 59 - (diff % 60);
        long minute = 59 - ((diff / 60) % 60);
        long hour = 23 - ((diff / (60 * 60)) % 24);

        if (hour > 0)
            return hour + " hours "
                    + minute + " minutes "
                    + second + " seconds";
        else if (minute > 0)
            return minute + " minutes "
                    + second + " seconds";
        else
            return second + " seconds";
    }

    private long getDiff(@NotNull Timestamp time) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime sdt = time.toInstant().atZone(now.getZone());

        return Math.abs(sdt.toEpochSecond() - now.toEpochSecond());
    }
}
