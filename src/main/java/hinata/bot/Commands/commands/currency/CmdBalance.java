package hinata.bot.Commands.commands.currency;

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
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@CommandDescription(
        name = "balance",
        description = "See the balance of you or someone else",
        triggers = {"balance", "bal", "$"},
        attributes = {
                @CommandAttribute(key = "category", value = "currency"),
                @CommandAttribute(key = "usage", value = "[command | alias] <mention/id>"),
                @CommandAttribute(key = "examples", value = "`h!bal`\n" +
                        "h!bal @Drag0n#6669"),
        }
)

public class CmdBalance implements Command {

    private final Hinata bot;

    protected final String optionName = "user";

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(USER, optionName, "the name of the new category")
                    .setRequired(false));

    public CmdBalance(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws Exception {
        OptionMapping option = event.getOption(optionName);

        if (option == null)
            throw new Exception("Something went wrong with the slash command");

        User user = event.getOption(this.optionName) != null ? option.getAsUser() : member.getUser();

        hook.sendMessageEmbeds(getBalance(user)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        Member target;
        if (!msg.getMentionedMembers().isEmpty()) {
            target = msg.getMentionedMembers().get(0);
        } else {
            String[] args = this.bot.getArguments(msg);
            if (args.length == 0)
                target = member;
            else
                target = guild.retrieveMemberById(args[0].replaceAll("[^0-9]", "")).complete();
        }

        if (target == null)
            throw new HinataException("There was no member found with this id!");

        tc.sendMessageEmbeds(getBalance(target.getUser())).queue();
    }

    @Override
    public CommandData getSlashInfo() {
        return slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{this.optionName};
    }

    private MessageEmbed getBalance(User user) throws Exception {
        hinata.bot.database.tables.User userDb = bot.getDbUtils().getUser(user.getId());

        return new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                .setDescription("You have **" + userDb.balance + "** " + bot.getConfig().getCurrencyEmoji())
                .build();
    }
}
