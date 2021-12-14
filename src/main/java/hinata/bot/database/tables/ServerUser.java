package hinata.bot.database.tables;

import hinata.bot.util.utils.Level;

import java.sql.Timestamp;

public class ServerUser {
    public int id;
    public int xp;

    public String serverId;
    public String userId;

    public Timestamp lastMessageDate;

    public User user;

    public Level level;

    public void setLevel(Level level) {
        this.level = level;
    }
}
