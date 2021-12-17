package hinata.bot.Commands.commands.currency;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import hinata.bot.constants.Emotes;
import hinata.bot.database.tables.User;
import hinata.bot.util.exceptions.HinataException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Random;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

@CommandDescription(
        name = "coinflip",
        description = "Heads or tails? Guess right and your bet will be doubled",
        triggers = {"coinflip", "cf", "flip"},
        attributes = {
                @CommandAttribute(key = "category", value = "currency"),
                @CommandAttribute(key = "usage", value = "[command | alias] [heads(h)/tails(t)] [amount]"),
                @CommandAttribute(key = "examples", value = "h!cf t 100`\n"),
        }
)

public class CmdCoinflip implements Command {

    private final Hinata bot;

    protected final String optionName1 = "side";
    protected final String optionName2 = "bet";

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(STRING, optionName1, "Head or tails")
                            .setRequired(true),
                    new OptionData(INTEGER, optionName2, "Amount to bet")
                            .setRequired(true));

    private final String[] results = {"heads", "tails"};

    public CmdCoinflip(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws HinataException, SQLException {
        OptionMapping option1 = event.getOption(optionName1);
        OptionMapping option2 = event.getOption(optionName2);

        if (option1 == null)
            throw new HinataException("Please chose the side of the coin.\nHeads(h) or tails(s).");
        if (option2 == null)
            throw new HinataException("Please put in an amount to gamble with!");

        hook.sendMessageEmbeds(coinflip(member, option1.getAsString(), (int) option2.getAsLong())).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] arguments = bot.getArguments(msg);

        //check if they picked heads or tails
        if (arguments.length < 1)
            throw new HinataException("Please chose your side of the coin and place a bet!");

        String bet = arguments[0];
        if (checkBet(bet))
            throw new HinataException("Please pick a valid side to bet on.\nHeads or tails.");

        if (arguments.length < 2)
            throw new HinataException("Please place the amount you want to bet with.");

        try {
            int amount = Integer.parseInt(arguments[1]);
            tc.sendMessageEmbeds(coinflip(member, bet, amount)).queue();
        } catch (NumberFormatException e) {
            throw new HinataException("Please place a valid amount to bet!");
        }
    }

    @Override
    public CommandData getSlashInfo() {
        return slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{optionName1, optionName2};
    }

    private MessageEmbed coinflip(Member member, String msgBet, int amount) throws HinataException, SQLException {
        String bet;
        Random rand = new Random();
        EmbedBuilder embed = new EmbedBuilder().setTimestamp(ZonedDateTime.now())
                .setTitle(this.getDescription().name())
                .setDescription("Result");

        User user = bot.getDbUtils().getUser(member.getId());

        if (msgBet.equalsIgnoreCase("head") || msgBet.equalsIgnoreCase("h") || msgBet.equalsIgnoreCase("heads"))
            bet = "heads";
        else if (msgBet.equalsIgnoreCase("tail") || msgBet.equalsIgnoreCase("t") || msgBet.equalsIgnoreCase("tails"))
            bet = "tails";
        else
            throw new HinataException("Please chose the side of the coin.\nHeads(h) or tails(s).");

        if (user.balance < amount || amount < 1)
            throw new HinataException("You don't have enough " + Emotes.CURRENCY.getEmote() + " to do this command.\n" +
                    "Your balance is **" + user.balance + Emotes.CURRENCY.getEmote() + "**.");

        String result = results[rand.nextInt(results.length)];

        if (result.equals(bet)) {
            user.balance += amount;
            embed.setColor(Colors.LOGADD.getCode());
        } else {
            user.balance -= amount;
            embed.setColor(Colors.LOGREMOVE.getCode());
        }

        bot.getDbUtils().updateUser(user);

        embed.addField("Bet", amount + " " + Emotes.CURRENCY.getEmote(), true)
                .addField("Side", bet, true)
                .addField("New total", user.balance + " " + Emotes.CURRENCY.getEmote(), true);

        return embed.build();
    }

    private boolean checkBet(String bet) {
        String[] validBets = {"heads", "head", "h", "tails", "tail", "t"};
        for (String possible : validBets) {
            if (possible.equalsIgnoreCase(bet))
                return false;
        }
        return true;
    }
}
