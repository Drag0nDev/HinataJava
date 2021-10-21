package hinata.bot.Commands.commands.roles;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import hinata.bot.util.exceptions.HinataException;
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

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

@CommandDescription(
        name = "role",
        description = "Add or remove a role from a member.",
        triggers = {"role", "r"},
        attributes = {
                @CommandAttribute(key = "category", value = "roles"),
                @CommandAttribute(key = "usage", value = "[command | alias] [member] [role]"),
                @CommandAttribute(key = "examples", value = "h!role @Drag0n#6669 @Member")
        }
)

public class CmdRole implements Command {
    private final Hinata bot;

    protected final String optionName1 = "user";
    protected final String optionName2 = "role";
    ArrayList<Permission> permissions = new ArrayList<>();

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(USER, optionName1, "user to give or take the role from")
                            .setRequired(true),
                    new OptionData(ROLE, optionName2, "role that will be used")
                            .setRequired(true));

    public CmdRole(Hinata bot) {
        this.bot = bot;
        permissions.add(Permission.MANAGE_ROLES);
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws Exception {
        OptionMapping option1 = event.getOption(optionName1);
        OptionMapping option2 = event.getOption(optionName2);
        Member bot = guild.retrieveMemberById(Hinata.getBot().getSelfUser().getId()).complete();

        if (option1 == null || option2 == null)
            throw new Exception("Something went wrong with the slash command");

        Member target = option1.getAsMember();
        Role role = option2.getAsRole();

        checks(member, target, bot, role);

        hook.sendMessageEmbeds(addRemove(guild, target, role)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        Member bot = guild.retrieveMemberById(Hinata.getBot().getSelfUser().getId()).complete();
        Member target;
        Role role;

        if (!msg.getMentionedMembers().isEmpty() && !msg.getMentionedRoles().isEmpty()) {
            target = msg.getMentionedMembers().get(0);
            role = msg.getMentionedRoles().get(0);
        } else {
            String[] args = this.bot.getArguments(msg);
            target = guild.retrieveMemberById(args[0].replaceAll("[^0-9]", "")).complete();
            role = guild.getRoleById(args[1].replaceAll("[^0-9]", ""));
        }

        checks(member, target, bot, role);

        tc.sendMessageEmbeds(addRemove(guild, target, role)).queue();
    }

    @Override
    public CommandData getSlashInfo() {
        return this.slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{this.optionName1, this.optionName2};
    }

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        return this.permissions;
    }

    private void checks(Member executor, Member target, Member bot, Role role) throws Exception {
        if (bot == null)
            throw new Exception("Member object for bot not found");
        if (role == null)
            throw new HinataException("The role you are looking for does not exist!");
        if (target == null)
            throw new HinataException("There was no member found with this id!");
        if (role.isManaged())
            throw new HinataException("This role is managed by an integration and can't be assigned");
        if (canGive(executor, role))
            throw new HinataException("You can't assign this role due to role hierarchy!");
        if (canGive(bot, role))
            throw new HinataException("I can't assign this role due to role hierarchy!");
    }

    private MessageEmbed addRemove(Guild guild, Member target, Role role) {
        EmbedBuilder embed = new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());

        if (target.getRoles().contains(role)){
            guild.removeRoleFromMember(target, role).complete();
            embed.setTitle("Role removed")
                    .setDescription("Role **" + role.getName() + "** was successfully removed from **" + target.getUser().getAsTag() + "**!");
        } else {
            guild.addRoleToMember(target, role).complete();
            embed.setTitle("Role added")
                    .setDescription("Role **" + role.getName() + "** was successfully added to **" + target.getUser().getAsTag() + "**!");
        }

        return embed.build();
    }

    private boolean canGive(Member member, Role role) {
        if (member.getRoles().isEmpty())
            return true;

        return !member.isOwner() && member.getRoles().get(0).getPosition() <= role.getPosition();
    }
}
