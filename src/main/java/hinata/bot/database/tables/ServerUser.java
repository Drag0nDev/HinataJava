package hinata.bot.database.tables;

import java.sql.Timestamp;

public class ServerUser {
    public int id;
    public int xp;

    public String serverId;
    public String userId;

    public Timestamp lastMessageDate;

    public User user;
}
