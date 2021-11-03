package hinata.bot.util.utils;

import com.google.gson.*;
import hinata.bot.Hinata;
import hinata.bot.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static @NotNull String getArgs(String raw, List<String> prefix) {
        String usedPrefix = "";
        int i = 0;

        while (usedPrefix.equals("")) {
            String toFind = "^" + prefix.get(i);
            Pattern pattern = Pattern.compile(toFind, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(raw);
            boolean matchFound = matcher.find();
            if (matchFound) usedPrefix = prefix.get(i);
            i++;
        }

        raw = raw.replaceFirst(Pattern.quote(usedPrefix), "");
        return raw;
    }

    public static @NotNull File getFile(@NotNull String fileName) throws URISyntaxException {
        ClassLoader classLoader = Utils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        }

        return new File(resource.toURI());
    }

    public static @NotNull Message jsonToMessage(@NotNull JsonObject json) {
        MessageBuilder messageBuilder = new MessageBuilder();

        JsonPrimitive contentObj = json.getAsJsonPrimitive("content");
        if (contentObj != null)
            messageBuilder.setContent(contentObj.getAsString());

        messageBuilder.setEmbeds(jsonToEmbed(json));

        return messageBuilder.build();
    }

    public static @NotNull Message jsonToMessage(String str) {
        JsonObject json = stringToJsonEmbed(str);

        return jsonToMessage(json);
    }

    public static @NotNull MessageEmbed jsonToEmbed(@NotNull JsonObject json) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        JsonPrimitive titleObj = json.getAsJsonPrimitive("title");
        if (titleObj != null) // Make sure the object is not null before adding it onto the embed.
            embedBuilder.setTitle(titleObj.getAsString());


        JsonObject authorObj = json.getAsJsonObject("author");
        if (authorObj != null) {
            String authorName = authorObj.get("name").getAsString();
            String authorIconUrl = authorObj.get("icon_url").getAsString();
            if (authorIconUrl != null) // Make sure the icon_url is not null before adding it onto the embed. If its null then add just the author's name.
                embedBuilder.setAuthor(authorName, authorIconUrl);
            else
                embedBuilder.setAuthor(authorName);
        }

        JsonPrimitive descObj = json.getAsJsonPrimitive("description");
        if (descObj != null)
            embedBuilder.setDescription(descObj.getAsString());


        JsonPrimitive colorObj = json.getAsJsonPrimitive("color");
        if (colorObj != null) {
            String colorStr = colorObj.getAsString().replace("#", "0x");
            Color color = new Color(Integer.decode(colorStr));
            embedBuilder.setColor(color);
        }

        JsonArray fieldsArray = json.getAsJsonArray("fields");
        if (fieldsArray != null) {
            // Loop over the fields array and add each one by order to the embed.
            fieldsArray.forEach(ele -> {
                boolean inline;
                String name = ele.getAsJsonObject().get("name").getAsString();
                String content = ele.getAsJsonObject().get("value").getAsString();
                if (ele.getAsJsonObject().get("inline") != null)
                    inline = ele.getAsJsonObject().get("inline").getAsBoolean();
                else
                    inline = false;
                embedBuilder.addField(name, content, inline);
            });
        }

        JsonPrimitive thumbnailObj = json.getAsJsonPrimitive("thumbnail");
        if (thumbnailObj != null) {
            embedBuilder.setThumbnail(thumbnailObj.getAsString());
        }

        JsonObject footerObj = json.getAsJsonObject("footer");
        if (footerObj != null) {
            String content = footerObj.get("text").getAsString();
            String footerIconUrl = footerObj.get("icon_url").getAsString();

            if (footerIconUrl != null)
                embedBuilder.setFooter(content, footerIconUrl);
            else
                embedBuilder.setFooter(content);
        }

        return embedBuilder.build();
    }

    public static @NotNull MessageEmbed jsonToEmbed(String str) {
        JsonObject json = stringToJsonEmbed(str);

        return jsonToEmbed(json);
    }

    public static void sendNSFWWarning(@NotNull TextChannel tc, @NotNull InteractionHook hook) {
        EmbedBuilder embed = new EmbedBuilder()
                .setDescription("This command can't be executed because this channel " + tc.getAsMention() + " is not marked NSFW")
                .setColor(Colors.ERROR.getCode())
                .setTimestamp(ZonedDateTime.now());

        hook.sendMessageEmbeds(embed.build()).queue();
    }

    public static void sendNSFWWarning(@NotNull TextChannel tc) {
        EmbedBuilder embed = new EmbedBuilder()
                .setDescription("This command can't be executed because this channel " + tc.getAsMention() + " is not marked NSFW")
                .setColor(Colors.ERROR.getCode())
                .setTimestamp(ZonedDateTime.now());

        tc.sendMessageEmbeds(embed.build()).queue();
    }

    public static @NotNull String generateSupportInvite(@NotNull JDA bot) {
        Optional<Invite> invite;
        Guild support = bot.getGuildById("645047329141030936");
        List<Invite> invites = Objects.requireNonNull(support).retrieveInvites().complete();


        invite = invites.stream().findFirst();
        if (invite.isPresent())
            return invite.get().getUrl();
        else {
            Invite base = Objects.requireNonNull(support.getDefaultChannel())
                    .createInvite()
                    .complete();
            return base.getUrl();
        }
    }

    public static @NotNull String generateInvite() {
        return Hinata.getBot().getInviteUrl(
                //for everything and smooth operations
                Permission.ADMINISTRATOR,
                //when the person can't give the admin permission, or they don't want the bot to have it
                Permission.KICK_MEMBERS, Permission.BAN_MEMBERS, Permission.VOICE_MUTE_OTHERS,
                Permission.MANAGE_CHANNEL, Permission.MANAGE_SERVER,
                Permission.MESSAGE_ADD_REACTION,
                Permission.VIEW_AUDIT_LOGS, Permission.VIEW_CHANNEL,
                Permission.MESSAGE_WRITE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_HISTORY, Permission.MESSAGE_EXT_EMOJI,
                Permission.VOICE_USE_VAD,
                Permission.NICKNAME_CHANGE,
                Permission.NICKNAME_MANAGE, Permission.MANAGE_ROLES, Permission.MANAGE_WEBHOOKS, Permission.MANAGE_EMOTES
        );
    }

    public static int getBots(@NotNull Guild guild) {
        AtomicInteger amount = new AtomicInteger();
        List<Member> members = guild.loadMembers().get();

        members.forEach(member -> {
            if (member.getUser().isBot())
                amount.getAndIncrement();
        });

        return amount.get();
    }

    public static @NotNull DateTimeFormatter format() {
        return new DateTimeFormatterBuilder().appendPattern("dd-M-yyyy hh:mm:ss a").toFormatter();
    }

    public static int getChannelAmount(@NotNull List<GuildChannel> channels, ChannelType sort) {
        AtomicInteger amount = new AtomicInteger();

        channels.forEach(guildChannel -> {
            guildChannel.getType();
            if (guildChannel.getType() == sort)
                amount.getAndIncrement();
        });

        return amount.get();
    }

    public static @NotNull String getSystemChannel(@NotNull Guild guild) {
        if (guild.getSystemChannel() == null)
            return "-";

        return guild.getSystemChannel().getName();
    }

    public static @NotNull String getAfkChannel(@NotNull Guild guild) {
        if (guild.getAfkChannel() == null)
            return "-";

        return guild.getAfkChannel().getName();
    }

    public static @NotNull String getRegion(@NotNull Guild guild) {
        if (guild.getVoiceChannels().stream().findFirst().isEmpty())
            return "Could not get the region";

        return guild.getVoiceChannels().stream().findFirst().get().getRegion().getName();
    }

    private static JsonObject stringToJsonEmbed(String str) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = gson.fromJson(str, JsonElement.class);
        return jsonElement.getAsJsonObject();
    }
}