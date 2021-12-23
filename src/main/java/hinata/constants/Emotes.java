package hinata.constants;

import javax.xml.transform.OutputKeys;

public enum Emotes {
    //Animated emotes
    BONK    ("bonk", "735549944814895115", true),

    //Static emotes
    CURRENCY("fireheart",   "777675712789217346",   false),
    YES     ("yes",         "\u2705",               false),
    NO      ("no",          "\u274C",               false)
    ;

    private final String emoteName;
    private final String id;
    private final boolean animated;

    Emotes(String emoteName, String id, boolean animated) {
        this.emoteName = emoteName;
        this.id = id;
        this.animated = animated;
    }

    public String getEmote() {
        return String.format(
                "<%s:%s:%s>",
                this.animated ? "a": "",
                this.emoteName,
                this.id
        );
    }

    public String getNameAndId() {
        return String.format(
                "%s:%s",
                this.emoteName,
                this.id
        );
    }

    public String getId() {
        return id;
    }
}
