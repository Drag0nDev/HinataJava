package hinata.database.tables;

public class ServerSettings {
    public String serverId;
    public String prefix;

    //special roles in server
    public String muteRoleId;
    public String noXpRole;

    //assignable channels
    public String modlogChannel;
    public String joinLeaveLogChannel;
    public String memberLogChannel;
    public String serverLogChannel;
    public String messageLogChannel;
    public String voiceLogChannel;
    public String joinMessageChannel;
    public String leaveMessageChannel;

    //custom messages in server
    public String levelUpMessage;
    public String levelUpRoleMessage;
    public String joinMessage;
    public String leaveMessage;
}
