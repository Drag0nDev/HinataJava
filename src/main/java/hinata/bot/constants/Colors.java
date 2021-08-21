package hinata.bot.constants;

public enum Colors {
    //Embed colors
    //normal use
    NORMAL      (0xBE4F70),
    READY       (0x00FF00),
    ERROR       (0xFF0000),
    //modlog
    WARN        (0xD69636),
    KICK        (0xE4E868),
    BAN         (0xDC143C),
    UNBAN       (0x16F36D),
    SOFTBAN     (0xD05F33),
    MUTE        (0xDBAF40),
    //events
    JOIN        (0x83C14D),
    LEAVE       (0xAC4C4C),
    //logs
    LOGCHANGE   (0x0096D0),
    LOGADD      (0x0E860B),
    LOGREMOVE   (0xC65339)
    ;

    private final int code;

    Colors(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
