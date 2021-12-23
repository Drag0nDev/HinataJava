package hinata.bot.Commands.commands.roles;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.util.exceptions.HinataException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

@CommandDescription(
        name = "rolecreate",
        description = "Create a new role in the server.",
        triggers = {"rolecreate", "rc"},
        attributes = {
                @CommandAttribute(key = "category", value = "roles"),
                @CommandAttribute(key = "usage", value = "[command | alias] [name] [colorCode] <hoisted>"),
                @CommandAttribute(key = "examples", value = "h!rc member #F0A468 yes")
        }
)

public class CmdRoleCreate implements Command {

    private final Hinata bot;

    protected final String optionName1 = "name";
    protected final String optionName2 = "color";
    protected final String optionName3 = "hoisted";
    ArrayList<Permission> permissions = new ArrayList<>();

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(STRING, optionName1, "name for the role")
                            .setRequired(true),
                    new OptionData(STRING, optionName2, "color for the role (must be hex)")
                            .setRequired(true),
                    new OptionData(BOOLEAN, optionName3, "is the role displayed? true/false")
                            .setRequired(false));

    public CmdRoleCreate(Hinata bot) {
        this.bot = bot;
        permissions.add(Permission.MANAGE_ROLES);
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws Exception {
        OptionMapping option1 = event.getOption(optionName1);
        OptionMapping option2 = event.getOption(optionName2);
        OptionMapping option3 = event.getOption(optionName3);

        if (option1 == null || option2 == null)
            throw new Exception("Something went wrong with the slash command");

        boolean hoisted;
        String name = option1.getAsString();
        String colorStr = option2.getAsString();
        if (option3 == null)
            hoisted = false;
        else
            hoisted = option3.getAsBoolean();

        if (!colorStr.startsWith("#"))
            throw new HinataException("Please provide a valid hex code!\n" +
                    "**Example:** #fa0384");

        colorStr = colorStr.replace("#", "0x");

        hook.sendMessageEmbeds(roleCreate(guild, name, new Color(Integer.decode(colorStr)), hoisted)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] args = bot.getArguments(msg);
        Pattern pattern = Pattern.compile("(yes|y)", Pattern.CASE_INSENSITIVE);

        if (args.length < 2)
            throw new HinataException("Please provide a name and color!");
        if (!args[1].startsWith("#"))
            throw new HinataException("Please provide a valid hex code!\n" +
                    "**Example:** #fa0384");

        String name = args[0];
        String colorStr = args[1].replace("#", "0x");
        boolean hoisted = args.length == 3 && pattern.matcher(args[2]).find();

        tc.sendMessageEmbeds(roleCreate(guild, name, new Color(Integer.decode(colorStr)), hoisted)).queue();
    }

    @Override
    public CommandData getSlashInfo() {
        return this.slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{this.optionName1, this.optionName2, this.optionName3};
    }

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        return this.permissions;
    }

    private MessageEmbed roleCreate(Guild guild, String name, Color color, boolean hoisted) {
        Role newRole = guild.createRole().setName(name).setColor(color).setHoisted(hoisted).complete();

        return new EmbedBuilder().setTitle("Role create")
                .setColor(color)
                .setDescription("New role with name **" + newRole.getAsMention() + "** was created successfully!")
                .setTimestamp(ZonedDateTime.now())
                .build();
    }
}
