package hinata.bot.Commands.commands.emojis;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import hinata.bot.Commands.Command;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import static net.dv8tion.jda.api.entities.Icon.from;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandDescription(
        name = "emojicreate",
        description = "Create a new emoji or add one that already exists.",
        triggers = {"emojicreate", "ec", "steal"},
        attributes = {
                @CommandAttribute(key = "category", value = "emojis"),
                @CommandAttribute(key = "usage", value = "[command | alias] [emoji] "),
                @CommandAttribute(key = "examples", value = "h!ec Laughing <image>")
        }
)

public class CmdEmojiCreate implements Command {
    private final Hinata bot;
    protected final String optionName1 = "name";
    protected final String optionName2 = "url";
    ArrayList<Permission> permissions = new ArrayList<>();

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description())
            .addOptions(new OptionData(STRING, optionName1, "name of the emoji")
                            .setRequired(true),
                    new OptionData(STRING, optionName2, "gif or image for the emoji")
                            .setRequired(true));

    public CmdEmojiCreate(Hinata bot) {
        this.bot = bot;
        permissions.add(Permission.MANAGE_EMOTES);
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) {
        String name = event.getOption(this.optionName1).getAsString();
        String link = event.getOption(this.optionName2).getAsString();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Emoji create")
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());

        try {
            int index = link.lastIndexOf('.');
            if (index < 0)
                guild.createEmote(link, from(new URL(link).openStream(), Icon.IconType.JPEG)).complete();

            String ext = link.substring(index + 1);
            Icon.IconType type = Icon.IconType.fromExtension(ext);

            guild.createEmote(name, from(new URL(link).openStream(), type)).complete();

            embed.setDescription("Emoji with name **" + name + "** is added to the server")
                    .setThumbnail(link);
        } catch (ErrorResponseException e) {
            embed.setColor(Colors.ERROR.getCode())
                    .setDescription("File cannot be larger than 256.0 kb.");
        } catch (MalformedURLException e) {
            embed.setDescription("Please provide a valid url");
        } catch (Exception e) {
            embed.setColor(Colors.ERROR.getCode())
                    .setDescription(
                            "Something went wrong.\n" +
                                    "Please try again or report it to the support server!"
                    );

            bot.getLogger().error("Error in emojiCreate!", e);
        }

        hook.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Emoji Create")
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());
        File emojiFile = null;

        try {
            if (msg.getEmotes().isEmpty()) {
                String[] arguments = bot.getArguments(msg);

                if (arguments.length == 0) {
                    embed.setColor(Colors.ERROR.getCode())
                            .setDescription("Please provide valid arguments.\n" +
                                    "The following methods are allowed:\n" +
                                    "**Emoji**: provide the emoji you want to add from another server.\n" +
                                    "**Name + picture**: give the name for the emoji and picture for the emoji.\n" +
                                    "**Name + link**: give the name for the emoji and the link to the picture.");

                    tc.sendMessageEmbeds(embed.build()).queue();
                    return;
                }

                String name = arguments[0];

                if (arguments.length == 1 && !msg.getAttachments().isEmpty()) {
                    Attachment attachment = msg.getAttachments().stream().findFirst().get();
                    emojiFile = attachment.downloadToFile().get();

                    guild.createEmote(name, from(emojiFile)).complete();

                    embed.setThumbnail(attachment.getUrl());
                    if (!emojiFile.delete())
                        bot.getLogger().error("File " + emojiFile.getName() + " failed to delete.\n" +
                                "Path: " + emojiFile.getAbsolutePath());
                } else if (arguments.length == 2) {
                    String emojiLink = arguments[1];

                    int index = emojiLink.lastIndexOf('.');
                    if (index < 0)
                        guild.createEmote(name, from(new URL(emojiLink).openStream(), Icon.IconType.JPEG)).complete();

                    String ext = emojiLink.substring(index + 1);
                    Icon.IconType type = Icon.IconType.fromExtension(ext);

                    guild.createEmote(name, from(new URL(emojiLink).openStream(), type)).complete();

                    embed.setThumbnail(emojiLink);
                }

                embed.setDescription("Emoji with name **" + name + "** is added to the server");
            } else {
                Emote emoji = msg.getEmotes().stream().findFirst().get();

                if (emoji.isAnimated()) {
                    guild.createEmote(emoji.getName(), from(new URL(emoji.getImageUrl()).openStream(), Icon.IconType.GIF)).queue();
                } else {
                    guild.createEmote(emoji.getName(), from(new URL(emoji.getImageUrl()).openStream(), Icon.IconType.PNG)).queue();
                }

                embed.setDescription("Emoji with name **" + emoji.getName() + "** is added to the server")
                        .setThumbnail(emoji.getImageUrl());
            }
        } catch (ErrorResponseException e) {
            if (emojiFile != null)
                if (!emojiFile.delete())
                    bot.getLogger().error("File " + emojiFile.getName() + " failed to delete.\n" +
                            "Path: " + emojiFile.getAbsolutePath());

            embed.setColor(Colors.ERROR.getCode())
                    .setDescription("File cannot be larger than 256.0 kb.");
        } catch (MalformedURLException e) {
            embed.setColor(Colors.ERROR.getCode())
                    .setDescription("Please provide a valid link or attachment!");
        } catch (Exception e) {
            embed.setColor(Colors.ERROR.getCode())
                    .setDescription(
                            "Something went wrong.\n" +
                                    "Please try again or report it to the support server!"
                    );

            bot.getLogger().error("Error in emojiCreate!", e);
        }

        tc.sendMessageEmbeds(embed.build()).queue();
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
}
