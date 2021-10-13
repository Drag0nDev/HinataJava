package hinata.bot.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hinata.bot.constants.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.ZonedDateTime;

public class Utils {
    public static @NotNull Message jsonToMessage(JsonObject json) {
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        JsonPrimitive contentObj = json.getAsJsonPrimitive("content");
        if (contentObj != null)
            messageBuilder.setContent(contentObj.getAsString());

        messageBuilder.setEmbeds(jsonToEmbed(json));

        return messageBuilder.build();
    }

    public static @NotNull MessageEmbed jsonToEmbed(JsonObject json) {
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
            Color color = new Color(Integer.parseInt(colorStr));
            embedBuilder.setColor(color);
        }

        JsonArray fieldsArray = json.getAsJsonArray("fields");
        if (fieldsArray != null) {
            // Loop over the fields array and add each one by order to the embed.
            fieldsArray.forEach(ele -> {
                String name = ele.getAsJsonObject().get("name").getAsString();
                String content = ele.getAsJsonObject().get("value").getAsString();
                boolean inline = ele.getAsJsonObject().get("inline").getAsBoolean();
                embedBuilder.addField(name, content, inline);
            });
        }

        JsonPrimitive thumbnailObj = json.getAsJsonPrimitive("thumbnail");
        if (thumbnailObj != null)
            embedBuilder.setThumbnail(thumbnailObj.getAsString());

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

    public static void sendNSFWWarning(@NotNull TextChannel tc, @NotNull InteractionHook hook){
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
}