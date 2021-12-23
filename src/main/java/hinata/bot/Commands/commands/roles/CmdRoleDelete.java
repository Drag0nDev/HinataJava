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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

@CommandDescription(
        name = "roledelete",
        description = "Create a new role in the server.",
        triggers = {"roledelete", "rd"},
        attributes = {
                @CommandAttribute(key = "category", value = "roles"),
                @CommandAttribute(key = "usage", value = "[command | alias] [name/id]"),
                @CommandAttribute(key = "examples", value = "h!rd member")
        }
)

public class CmdRoleDelete implements Command {

    private final Hinata bot;

    protected final String optionName1 = "role";

    ArrayList<Permission> permissions = new ArrayList<>();

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(ROLE, optionName1, "name of the role")
                    .setRequired(true));

    public CmdRoleDelete(Hinata bot) {
        this.bot = bot;
        permissions.add(Permission.MANAGE_ROLES);
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws Exception {
        OptionMapping option1 = event.getOption(optionName1);

        if (option1 == null)
            throw new Exception("Something went wrong with the slash command");

        Role role = option1.getAsRole();

        hook.sendMessageEmbeds(roleDelete(guild, member, role)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] args = bot.getArguments(msg);

        if (args.length == 0)
            throw new HinataException("Please provide a valid id/role/name!");

        Role role;
        if (msg.getMentionedRoles().isEmpty())
            role = getRole(guild, args[0]);
        else
            role = msg.getMentionedRoles().get(0);

        tc.sendMessageEmbeds(roleDelete(guild, member, role)).queue();
    }

    @Override
    public CommandData getSlashInfo() {
        return this.slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{this.optionName1};
    }

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        return this.permissions;
    }

    private Role getRole(Guild guild, String input) throws HinataException {
        Role role;

        if (!guild.getRolesByName(input, true).isEmpty())
            role = guild.getRolesByName(input, true).get(0);
        else {
            try {
                role = guild.getRoleById(input);
            } catch (Exception e) {
                Pattern pattern = Pattern.compile("\\d");

                if (pattern.matcher(input).matches())
                    throw new HinataException("No role with id **" + input + "** was found!");
                else
                    throw new HinataException("No role with name **" + input + "** was found!");
            }
        }

        return role;
    }

    private MessageEmbed roleDelete(Guild guild, Member executor, Role role) throws HinataException {
        Member bot = guild.retrieveMemberById(Hinata.getBot().getSelfUser().getId()).complete();

        if (!executor.canInteract(role))
            throw new HinataException("You can't delete this role because of role hierarchy");
        if (!bot.canInteract(role))
            throw new HinataException("I can't delete this role because of role hierarchy");

        role.delete().complete();

        return new EmbedBuilder().setTitle("Role delete")
                .setColor(role.getColor())
                .setDescription("Role with name **" + role.getName() + "** was successfully deleted!")
                .setTimestamp(ZonedDateTime.now())
                .build();
    }
}
