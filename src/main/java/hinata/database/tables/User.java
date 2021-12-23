package hinata.database.tables;

import hinata.util.utils.Level;

import java.awt.*;
import java.sql.Timestamp;

public class User {
    public String userId;
    public String userTag;
    public String background;

    public int balance;
    public int xp;
    public int dailyStreak;
    public int badge1;
    public int badge2;
    public int badge3;
    public int badge4;
    public int badge5;
    public int badge6;

    public boolean isBanned;

    public Timestamp lastMessageDate;
    public Timestamp dailyTaken;

    public Color color;

    public Level level;

    public void setLevel(Level level) {
        this.level = level;
    }
}
