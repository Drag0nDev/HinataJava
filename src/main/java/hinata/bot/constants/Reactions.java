package hinata.bot.constants;

import hinata.bot.util.APOD;
import hinata.bot.util.JsonBodyHandler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public enum Reactions {
    //sfw
    //gif
    BAKA    (ApiCalls.BAKA.get(),   ApiCalls.BAKA.isNsfw()),
    BLUSH   (Custom.blush(),        false),
    CUDDLE  (ApiCalls.CUDDLE.get(), ApiCalls.CUDDLE.isNsfw()),
    CRY     (Custom.cry(),          false),
    HUG     (ApiCalls.HUG.get(),    ApiCalls.HUG.isNsfw()),
    KISS    (ApiCalls.KISS.get(),   ApiCalls.KISS.isNsfw()),
    PAT     (ApiCalls.PAT.get(),    ApiCalls.PAT.isNsfw()),
    POKE    (ApiCalls.POKE.get(),   ApiCalls.POKE.isNsfw()),
    SLAP    (ApiCalls.SLAP.get(),   ApiCalls.SLAP.isNsfw()),

    //image
    KITSUNE (ApiCalls.KITSUNE.get(),    ApiCalls.KITSUNE.isNsfw()),
    NEKO    (ApiCalls.NEKO.get(),       ApiCalls.NEKO.isNsfw()),

    //nsfw
    //gif
    GROPE   (Custom.grope(),            true),
    SPANK   (ApiCalls.SPANK.get(),      ApiCalls.SPANK.isNsfw()),

    //image
    ERO     (ApiCalls.ERO.get(),        ApiCalls.ERO.isNsfw()),
    FUTA    (ApiCalls.FUTA.get(),       ApiCalls.FUTA.isNsfw()),
    HENTAI  (ApiCalls.HENTAI.get(),     ApiCalls.HENTAI.isNsfw()),
    LEWD    (ApiCalls.LEWD.get(),       ApiCalls.LEWD.isNsfw()),
    TRAP    (ApiCalls.TRAP.get(),       ApiCalls.TRAP.isNsfw())
    ;

    private final String url;
    private final Boolean nsfw;

    Reactions(String url, Boolean nsfw) {
        this.url = url;
        this.nsfw = nsfw;
    }

    public String get() {
        return url;
    }

    public boolean isNSFW(){
        return this.nsfw;
    }
}

enum ApiCalls {
    //SFW api calls
    //gifs
    BAKA    ("https://nekos.life/api/v2/img/baka",      false),
    CUDDLE  ("https://nekos.life/api/v2/img/cuddle",    false),
    HUG     ("https://nekos.life/api/v2/img/hug",       false),
    KISS    ("https://nekos.life/api/v2/img/kiss",      false),
    PAT     ("https://nekos.life/api/v2/img/pat",       false),
    POKE    ("https://nekos.life/api/v2/img/poke",      false),
    SLAP    ("https://nekos.life/api/v2/img/slap",      false),

    //images
    KITSUNE     ("https://nekos.life/api/v2/img/fox_girl",  false),
    NEKO        ("https://nekos.life/api/v2/img/neko",      false),

    //NSFW api calls
    //gifs
    SPANK   ("https://nekos.life/api/v2/img/spank",     true),

    //images
    ERO     ("https://nekos.life/api/v2/img/eron",      true),
    FUTA    ("https://nekos.life/api/v2/img/futanari",  true),
    HENTAI  ("https://nekos.life/api/v2/img/hentai",    true),
    LEWD    ("https://nekos.life/api/v2/img/lewd",      true),
    TRAP    ("https://nekos.life/api/v2/img/trap",      true)
    ;

    private final String apiCall;
    private final boolean nsfw;

    ApiCalls(String apiCall, boolean nsfw) {
        this.apiCall = apiCall;
        this.nsfw = nsfw;
    }

    public boolean isNsfw(){
        return this.nsfw;
    }

    public String get(){
        var client = HttpClient.newHttpClient();

        var request = HttpRequest.newBuilder(
                        URI.create(this.apiCall))
                .header("accept", "application/json")
                .build();

        var responseFuture = client.sendAsync(request, new JsonBodyHandler<>(APOD.class));

        HttpResponse<Supplier<APOD>> response = null;
        try {
            response = responseFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        assert response != null;
        return response.body().get().url;
    }
}

class Custom{
    private static final String[] BLUSH    = {"https://i.imgur.com/hJWIeMv.gif",
            "https://i.imgur.com/RHHL6aK.gif",
            "https://i.imgur.com/dXS8TDc.gif",
            "https://i.imgur.com/QkLsr6q.gif",
            "https://i.imgur.com/dH6yGFD.gif",
            "https://i.imgur.com/l5A8UJx.gif",
            "https://i.imgur.com/hlaBHi4.gif",
            "https://i.imgur.com/yxnYG9n.gif",
            "https://i.imgur.com/AQpwG9m.gif",
            "https://i.imgur.com/0b0optH.gif",
            "https://i.imgur.com/Vln95nw.gif",
            "https://i.imgur.com/mb0POnv.gif",
            "https://i.imgur.com/p6NkRgx.gif",
            "https://i.imgur.com/sUb0jK6.gif",
            "https://i.imgur.com/iN3g1iO.gif",
            "https://i.imgur.com/K9KHnbX.gif",
            "https://i.imgur.com/oi6bTZh.gif"};

    private static final String[] CRY      = {"https://i.imgur.com/gD8wSJt.gif",
            "https://i.imgur.com/MSxWi8a.gif",
            "https://i.imgur.com/W4ulgbm.gif",
            "https://i.imgur.com/iEkGy0K.gif",
            "https://i.imgur.com/L7uGp75.gif",
            "https://i.imgur.com/itviV3b.gif",
            "https://i.imgur.com/lB8spOZ.gif",
            "https://i.imgur.com/GMy2eUD.gif",
            "https://i.imgur.com/pJuUyPC.gif",
            "https://i.imgur.com/sjrUvG6.gif",
            "https://i.imgur.com/KOUSm61.gif",
            "https://i.imgur.com/MOOn649.gif",
            "https://i.imgur.com/7tQDnEJ.gif",
            "https://i.imgur.com/4vwuGzK.gif",
            "https://i.imgur.com/zyKcpwu.gif",
            "https://i.imgur.com/lZOVeYQ.gif",
            "https://i.imgur.com/edbPydA.gif",
            "https://i.imgur.com/2JRSf41.gif"};

    private static final String[] GROPE    = {"https://i.imgur.com/lMNVEeS.gif",
            "https://i.imgur.com/2T4UY8Z.gif",
            "https://i.imgur.com/SICwu0O.gif",
            "https://i.imgur.com/z3zluLK.gif",
            "https://i.imgur.com/98jc66h.gif",
            "https://i.imgur.com/9m7msF5.gif",
            "https://i.imgur.com/nWeIr5x.gif",
            "https://i.imgur.com/1NJcHbo.gif",
            "https://i.imgur.com/Tf2rG7l.gif",
            "https://i.imgur.com/Tf2rG7l.gif",
            "https://i.imgur.com/vQ471NF.gif",
            "https://i.imgur.com/lGpvcIN.gif",
            "https://i.imgur.com/EbLmDx6.gif"};

    public static String blush() {
        Random rand = new Random();
        return BLUSH[rand.nextInt(BLUSH.length)];
    }

    public static String cry() {
        Random rand = new Random();
        return CRY[rand.nextInt(CRY.length)];
    }

    public static String grope() {
        Random rand = new Random();
        return GROPE[rand.nextInt(GROPE.length)];
    }
}