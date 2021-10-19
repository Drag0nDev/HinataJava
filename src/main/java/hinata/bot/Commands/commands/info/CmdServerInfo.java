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

import java.time.ZonedDateTime;

import static hinata.bot.util.utils.Utils.*;

@CommandDescription(
        name = "serverinfo",
        description = "See the info about the server.",
        triggers = {"serverinfo", "sinfo"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "`h!sinfo`\n"),
        }
)

public class CmdServerInfo implements Command {
    private final Hinata bot;

    private final CommandData slashInfo = new CommandData(this.getDescription().name(), this.getDescription().description());

    public CmdServerInfo(Hinata bot) {
        this.bot = bot;
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandEvent event, InteractionHook hook) throws HinataException {
        EmbedBuilder embed = new EmbedBuilder().setTitle(guild.getName())
                .setThumbnail(guild.getIconUrl())
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());

        //checks
        if (guild.getOwner() == null)
            throw new HinataException("No owner found!");
        if (guild.getVoiceChannels().stream().findFirst().isEmpty())
            throw new HinataException("Error finding the region!");

        //owner field
        embed.addField("Owner", guild.getOwner().getUser().getAsTag(), true)
                //users field
                .addField("Users", String.valueOf(guild.getMemberCount()), true)
                //find bot count
                .addField("Bots", String.valueOf(getBots(guild)), true)
                //get the creation date
                .addField("Creation date", guild.getTimeCreated().format(format()), true)
                //get amount of each channel sort
                .addField(
                        "Channels",
                        "**Categories: " + getChannelAmount(guild.getChannels(), ChannelType.CATEGORY) + "\n" +
                                "**Text channels:** " + getChannelAmount(guild.getChannels(), ChannelType.TEXT) + "\n" +
                                "**Voice channels:** " + getChannelAmount(guild.getChannels(), ChannelType.VOICE),
                        true
                )
                //loko for the system channel
                .addField(
                        "System channel",
                        getSystemChannel(guild),
                        true
                )
                //get afk channel
                .addField(
                        "AFK channel",
                        getAfkChannel(guild),
                        true
                )
                //other minor fields
                .addField("Region", guild.getVoiceChannels().stream().findFirst().get().getRegionRaw(), true)
                .addField("Verification level", guild.getVerificationLevel().name(), true)
                .addField("Boost tier", guild.getBoostTier().name(), true)
                .addField("Boosts", String.valueOf(guild.getBoostCount()), true);
        
        if (guild.getBannerUrl() != null)
            embed.setImage(guild.getBannerUrl());
        
        hook.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws HinataException {
        EmbedBuilder embed = new EmbedBuilder().setTitle(guild.getName())
                .setThumbnail(guild.getIconUrl())
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());
        //checks
        if (guild.getOwner() == null)
            throw new HinataException("No owner found!");
        if (guild.getVoiceChannels().stream().findFirst().isEmpty())
            throw new HinataException("Error finding the region!");

        //owner field
        embed.addField("Owner", guild.getOwner().getUser().getAsTag(), true)
                //users field
                .addField("Users", String.valueOf(guild.getMemberCount()), true)
                //find bot count
                .addField("Bots", String.valueOf(getBots(guild)), true)
                //get the creation date
                .addField("Creation date", guild.getTimeCreated().format(format()), true)
                //get amount of each channel sort
                .addField(
                        "Channels",
                        "**Categories: " + getChannelAmount(guild.getChannels(), ChannelType.CATEGORY) + "\n" +
                                "**Text channels:** " + getChannelAmount(guild.getChannels(), ChannelType.TEXT) + "\n" +
                                "**Voice channels:** " + getChannelAmount(guild.getChannels(), ChannelType.VOICE),
                        true
                )
                //loko for the system channel
                .addField(
                        "System channel",
                        getSystemChannel(guild),
                        true
                )
                //get afk channel
                .addField(
                        "AFK channel",
                        getAfkChannel(guild),
                        true
                )
                //other minor fields
                .addField("Region", guild.getVoiceChannels().stream().findFirst().get().getRegionRaw(), true)
                .addField("Verification level", guild.getVerificationLevel().name(), true)
                .addField("Boost tier", guild.getBoostTier().name(), true)
                .addField("Boosts", String.valueOf(guild.getBoostCount()), true);

        if (guild.getBannerUrl() != null)
            embed.setImage(guild.getBannerUrl());

        tc.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public CommandData slashInfo() {
        return slashInfo;
    }
}
