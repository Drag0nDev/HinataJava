package hinata.database.tables;

import java.sql.Timestamp;

public class Timer {
    public int id;

    public String serverId;
    public String userId;
    public String moderatorId;
    public String type;
    public String reason;

    public Timestamp expiration;
}
