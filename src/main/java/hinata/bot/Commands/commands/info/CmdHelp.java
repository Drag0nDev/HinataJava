package hinata.bot.Commands.commands.info;

import com.github.rainestormee.jdacommand.AbstractCommand;
import com.github.rainestormee.jdacommand.*;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.ZonedDateTime;
import java.util.*;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandDescription(
        name = "help",
        description = "A command to show all commands or help for a single command/category!",
        triggers = {"help", "h"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias] <categoryname/commandname>"),
                @CommandAttribute(key = "examples", value = "`h!help`\n" +
                        "`h!help info`\n" +
                        "`h!help slum`"),
                @CommandAttribute(key = "permissions", value = "ban_members"),
        }
)

public class CmdHelp implements Command {
    private final Hinata bot;
    private final HashSet<String> categories = new LinkedHashSet<>();
    protected final String optionName = "command";

    private final CommandData slashInfo = new CommandData("help", "A command to show all commands or help for a single command/category!")
            .addOptions(new OptionData(STRING, optionName, "Comamnd/category to get help with")
                    .setRequired(false));

    public CmdHelp(Hinata bot) {
        this.bot = bot;

        categories.add("fun");
        categories.add("info");
    }

    @Override
    public void execute(Message msg, Object... args) {
        String[] arguments = bot.getArguments(msg);
        Map<String, ArrayList<String>> cmdMap = new HashMap<>();
        TextChannel tc = msg.getTextChannel();
        Member member = msg.getMember();
        Guild guild = msg.getGuild();

        //map the categories and their commands
        for (String cat : categories) {
            cmdMap.put(cat, new ArrayList<>());
            for (AbstractCommand<Message> cmd : bot.getCmdHandler().getCommands()) {
                if (cmd.getAttribute("category").contains(cat)) {
                    cmdMap.get(cat).add(cmd.getDescription().name());
                }
            }
        }

        if (arguments.length > 0) {
            Command cmd = (Command) bot.getCmdHandler().findCommand(arguments[0]);

            //check if command exists
            if (cmd == null || !isCommand(cmd)) {
                tc.sendMessageEmbeds(showHelpMenu(cmdMap, arguments[0])).queue();
                return;
            }

            //check if it is an owner only command
            if (cmd.getAttribute("category").contains("owner")) {
                EmbedBuilder embed = new EmbedBuilder().setColor(Colors.ERROR.getCode())
                        .setTitle("Bot owner only command")
                        .setDescription("This command is not available for your use.\n" +
                                "This command can only be used by the bot owner.")
                        .setTimestamp(ZonedDateTime.now());

                tc.sendMessageEmbeds(embed.build()).queue();
                return;
            }

            tc.sendMessageEmbeds(commandHelp(member, guild, cmd)).queue();
        } else {
            tc.sendMessageEmbeds(showHelpMenu(cmdMap, null)).queue();
        }
    }

    @Override
    public void run(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {

        Map<String, ArrayList<String>> cmdMap = new HashMap<>();
        String input = event.getOption(optionName) == null ? "" : event.getOption(optionName).getAsString();

        //map the categories and their commands
        for (String cat : categories) {
            cmdMap.put(cat, new ArrayList<>());
            for (AbstractCommand<Message> cmd : bot.getCmdHandler().getCommands()) {
                if (cmd.getAttribute("category").contains(cat)) {
                    cmdMap.get(cat).add(cmd.getDescription().name());
                }
            }
        }

        if (!input.equals("")) {
            Command cmd = (Command) bot.getCmdHandler().findCommand(input);

            //check if command exists
            if (cmd == null || !isCommand(cmd)) {
                hook.sendMessageEmbeds(showHelpMenu(cmdMap, input)).queue();
                return;
            }

            //check if it is an owner only command
            if (cmd.getAttribute("category").contains("owner")) {
                EmbedBuilder embed = new EmbedBuilder().setColor(Colors.ERROR.getCode())
                        .setTitle("Bot owner only command")
                        .setDescription("This command is not available for your use.\n" +
                                "This command can only be used by the bot owner.")
                        .setTimestamp(ZonedDateTime.now());

                hook.sendMessageEmbeds(embed.build()).queue();
                return;
            }

            hook.sendMessageEmbeds(commandHelp(member, guild, cmd)).queue();
        } else
            hook.sendMessageEmbeds(showHelpMenu(cmdMap, null)).queue();
    }

    private MessageEmbed commandHelp(Member member, Guild guild, Command cmd) {
        EmbedBuilder embed = new EmbedBuilder();
        StringBuilder triggers = new StringBuilder();
        Member self = guild.getSelfMember();

        for (int i = 0; i < cmd.getDescription().triggers().length; i++) {
            triggers.append("`").append(cmd.getDescription().triggers()[i]).append("`").append("\n");
        }

        embed.setTitle(cmd.getDescription().name())
                .setColor(Colors.NORMAL.getCode())
                .addField("Command name:", cmd.getDescription().name(), false)
                .addField("Category", cmd.getAttribute("category"), true)
                .addField("Triggers", triggers.toString(), true)
                .addField("Description", cmd.getDescription().description(), false)
                .addField("Usage", cmd.getAttribute("usage"), true)
                .addField("Examples", cmd.getAttribute("examples"), true)
                .setFooter("Syntax: [] = required, <> = optional")
                .setTimestamp(ZonedDateTime.now());

        if (cmd.getAttribute("permissions") != null) {
            embed.addField("User permissions:", checkPermissions(member, cmd), false)
                    .addField("Bot permissions:", checkPermissions(self, cmd), true);
        }

        return embed.build();
    }

    private MessageEmbed showHelpMenu(Map<String, ArrayList<String>> cmdMap, String category) {
        if (category == null) {
            //show all commands
            EmbedBuilder embed = new EmbedBuilder().setTitle("help", "https://discord.gg/ReBJ4AB")
                    .setColor(Colors.NORMAL.getCode())
                    .setTimestamp(ZonedDateTime.now());

            for (Map.Entry catMap : cmdMap.entrySet()) {
                StringBuilder cmdStr = new StringBuilder();
                String cat = (String) catMap.getKey();
                ArrayList<String> cmdArray = (ArrayList<String>) catMap.getValue();

                for (String cmd : cmdArray) {
                    cmdStr.append(cmd).append("\n");
                }

                embed.addField(cat, cmdStr.toString(), true);

                if (embed.getFields().size() >= 10) {
                    break;
                }
            }

            return embed.build();
        } else {
            if (cmdMap.containsKey(category)) {
                ArrayList<String> cmds = cmdMap.get(category);

                EmbedBuilder embed = new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                        .setTitle("Category: " + category)
                        .setTimestamp(ZonedDateTime.now());

                for (String cmdStr : cmds) {
                    Command cmd = (Command) bot.getCmdHandler().findCommand(cmdStr);

                    embed.addField(cmd.getDescription().name(), cmd.getDescription().description(), false);
                }

                return embed.build();
            } else {
                return new EmbedBuilder().setColor(Colors.ERROR.getCode())
                        .setTitle("No command found")
                        .setDescription("No information is found for command/category **" + category.toLowerCase() + "**")
                        .setTimestamp(ZonedDateTime.now())
                        .setFooter("Maybe you typed it wrong?").build();
            }
        }
    }

    private boolean isCommand(Command cmd) {
        return cmd.getDescription() != null || cmd.hasAttribute("description");
    }

    private String checkPermissions(Member member, Command cmd) {
        String[] permissions = cmd.getAttribute("permissions").trim().split(",\\s");
        StringBuilder permissionsCheck = new StringBuilder();

        for (String permission : permissions) {
            if (member.hasPermission(Permission.valueOf(permission.toUpperCase()))) {
                permissionsCheck.append("**").append(permission.toUpperCase()).append(":** ").append("\u2705").append("\n");
            } else {
                permissionsCheck.append("**").append(permission.toUpperCase()).append(":** ").append("\u274C").append("\n");
            }
        }

        return permissionsCheck.toString();
    }

    @Override
    public CommandData slashInfo() {
        return slashInfo;
    }

    @Override
    public String getOptionName() {
        return optionName;
    }
}