package hinata.bot.constants;

import hinata.bot.util.APOD;
import hinata.bot.util.JsonBodyHandler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public enum ApiCalls {
    //SFW api calls
    //gifs
    HUG     ("https://nekos.life/api/v2/img/hug",       false),
    KISS    ("https://nekos.life/api/v2/img/kiss",      false),
    SLAP    ("https://nekos.life/api/v2/img/slap",      false),
    CUDDLE  ("https://nekos.life/api/v2/img/cuddle",    false),
    PAT     ("https://nekos.life/api/v2/img/pat",       false),
    BAKA    ("https://nekos.life/api/v2/img/baka",      false),
    POKE    ("https://nekos.life/api/v2/img/poke",      false),

    //images
    NEKO        ("https://nekos.life/api/v2/img/neko",      false),
    KITSUNE     ("https://nekos.life/api/v2/img/fox_girl",  false),

    //NSFW api calls
    //gifs
    SPANK   ("https://nekos.life/api/v2/img/spank",     true),

    //images
    LEWD    ("https://nekos.life/api/v2/img/lewd",      true),
    ERO     ("https://nekos.life/api/v2/img/eron",      true),
    TRAP    ("https://nekos.life/api/v2/img/trap",      true),
    FUTA    ("https://nekos.life/api/v2/img/futanari",  true),
    HENTAI  ("https://nekos.life/api/v2/img/hentai",    true)
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