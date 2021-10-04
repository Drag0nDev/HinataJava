package hinata.bot.constants;

import java.util.Random;

public class CustomReactions {
    private static final String[] BLUSH = {"https://i.imgur.com/hJWIeMv.gif",
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

    private static final String[] CRY = {"https://i.imgur.com/gD8wSJt.gif",
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

    private static final String[] GROPE = {"https://i.imgur.com/lMNVEeS.gif",
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
