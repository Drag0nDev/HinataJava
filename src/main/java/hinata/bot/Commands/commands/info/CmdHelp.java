package hinata.bot.Commands.commands.info;

import com.github.rainestormee.jdacommand.AbstractCommand;
import com.github.rainestormee.jdacommand.*;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import hinata.bot.util.utils.AsciiTable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.ZonedDateTime;
import java.util.*;

import static hinata.bot.util.utils.Utils.generateSupportInvite;
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
        }
)

public class CmdHelp implements Command {
    private final Hinata bot;
    private final HashSet<String> categories = new LinkedHashSet<>();
    protected final String optionName = "command";

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(STRING, optionName, "Command/category to get help with")
                    .setRequired(false));

    public CmdHelp(Hinata bot) {
        this.bot = bot;

        categories.add("channel");
        categories.add("emojis");
        categories.add("fun");
        categories.add("info");
        categories.add("owner");
        categories.add("reactions");
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        String[] arguments = bot.getArguments(msg);
        Map<String, ArrayList<String>> cmdMap = new HashMap<>();

        //map the categories and their commands
        for (String cat : categories) {
            cmdMap.put(cat, new ArrayList<>());
            for (AbstractCommand<Message> cmd : bot.getCmdHandler().getCommands()) {
                if (cmd.getAttribute("category").contains(cat)) {
                    cmdMap.get(cat).add(cmd.getDescription().name());
                }
            }
        }

        assert member != null;

        if (arguments.length > 0) {
            Command cmd = (Command) bot.getCmdHandler().findCommand(arguments[0]);

            //check if command exists
            if (cmd == null || !isCommand(cmd)) {
                tc.sendMessageEmbeds(showHelpMenu(cmdMap, arguments[0], member.getUser())).queue();
                return;
            }

            //check if it is an owner only command
            if (cmd.getAttribute("category").contains("owner") && member.getUser().getId().equals(this.bot.getConfig().getOwner())) {
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
            tc.sendMessageEmbeds(showHelpMenu(cmdMap, null, member.getUser())).queue();
        }
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {

        Map<String, ArrayList<String>> cmdMap = new HashMap<>();
        String input = event.getOption(optionName) == null ? "" : Objects.requireNonNull(event.getOption(optionName)).getAsString();

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
                hook.sendMessageEmbeds(showHelpMenu(cmdMap, input, event.getUser())).queue();
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
            hook.sendMessageEmbeds(showHelpMenu(cmdMap, null, event.getUser())).queue();
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

        if (cmd.getNeededPermissions() != null)
            embed.addField("Permissions:", checkPermissions(member, self, cmd), false);

        return embed.build();
    }

    private MessageEmbed showHelpMenu(Map<String, ArrayList<String>> cmdMap, String category, User user) {
        if (category == null) {
            //show all commands
            EmbedBuilder embed = new EmbedBuilder().setTitle("help", generateSupportInvite(Hinata.getBot()))
                    .setColor(Colors.NORMAL.getCode())
                    .setTimestamp(ZonedDateTime.now());

            if (!user.getId().equals(this.bot.getConfig().getOwner()))
                cmdMap.remove("owner");

            //sort the categories that act as keys
            ArrayList<String> sortedKeys = new ArrayList<>(cmdMap.keySet());
            Collections.sort(sortedKeys);

            for (String cat : sortedKeys) {
                StringBuilder cmdStr = new StringBuilder();
                ArrayList<String> cmdArray = cmdMap.get(cat);

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
                if (!category.equals("owner") && !user.getId().equals(this.bot.getConfig().getOwner())) {
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
                            .setTitle("Bot owner only command")
                            .setDescription("This command is not available for your use.\n" +
                                    "This command can only be used by the bot owner.")
                            .setTimestamp(ZonedDateTime.now())
                            .build();
                }
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

    private String checkPermissions(Member member, Member self, Command cmd) {
        StringBuilder permissionsCheck = new StringBuilder();
        AsciiTable table = new AsciiTable();
        table.setMaxColumnWidth(50);
        table.getColumns().add(new AsciiTable.Column("Permission"));
        table.getColumns().add(new AsciiTable.Column("Bot"));
        table.getColumns().add(new AsciiTable.Column("User"));

        for (Permission permission : cmd.getNeededPermissions()) {
            String botCheck;
            String memberCheck;

            //check bot
            if (self.hasPermission(permission)) {
                botCheck = "✅";
            } else {
                botCheck = "❌";
            }

            //check member
            if (member.hasPermission(permission)) {
                memberCheck = "✅";
            } else {
                memberCheck = "❌";
            }

            AsciiTable.Row row = new AsciiTable.Row();
            table.getData().add(row);
            row.getValues().add(permission.getName());
            row.getValues().add(botCheck);
            row.getValues().add(memberCheck);
        }

        table.calculateColumnWidth();
        System.out.println(table.render());

        return permissionsCheck.append("`").append(table.render()).append("`").toString();
    }

    @Override
    public CommandData slashInfo() {
        return slashInfo;
    }

    @Override
    public String[] getOptionNames() {
        return new String[]{this.optionName};
    }
}