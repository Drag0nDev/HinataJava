package hinata.database;

import hinata.bot.Hinata;
import hinata.database.tables.*;
import hinata.util.utils.Level;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static hinata.bot.Hinata.getLogger;

public class DbUtils {
    private final Hinata bot;
    private final String url;
    private final Properties props;

    Connection conn = null;

    public DbUtils(@NotNull Hinata bot) {

        String url = this.url = bot.getConfig().getUrl();
        String user = bot.getConfig().getUser();
        String password = bot.getConfig().getPassword();

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);

        this.props = props;

        try {
            this.conn = DriverManager.getConnection(url, props);
            getLogger().info("Successfully connected to the database!");
        } catch (SQLException e) {
            getLogger().error("Failed to connect to the database", e);
        }

        this.bot = bot;
    }

    //reconnect to the database if the connection has been closed unexpectedly
    public void reconnect() {
        try {
            conn = DriverManager.getConnection(url, props);
            getLogger().info("Successfully reconnected to the database!");
        } catch (SQLException e) {
            getLogger().error("Failed to reconnect to the database", e);
        }
    }

    // database check
    public void checkDb(Guild guild, @NotNull Member member) throws SQLException {
        if (member.getUser().isBot())
            return;

        User user = memberToUser(member);
        Server server = guildToServer(guild);
        ServerUser serverUser = memberToServerUser(member);

        if (checkUserDb(user))
            addUser(user);

        if (checkServerDb(server))
            addServer(server);

        if (checkServerUserDb(serverUser))
            addServerUser(serverUser);
    }

    public boolean checkUserDb(@NotNull User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users where userId = ?;");
        stmt.setString(1, user.userId);

        ResultSet rs = stmt.executeQuery();

        return !rs.next();
    }

    public boolean checkServerDb(@NotNull Server server) throws SQLException {
        PreparedStatement serverSql = conn.prepareStatement("SELECT * FROM servers where serverId = ?;");
        PreparedStatement settings = conn.prepareStatement("SELECT * FROM serversettings where serverId = ?;");

        serverSql.setString(1, server.serverId);
        settings.setString(1, server.serverId);

        ResultSet serverRs = serverSql.executeQuery();
        ResultSet settingsRs = settings.executeQuery();

        return !(serverRs.next() && settingsRs.next());
    }

    public boolean checkServerUserDb(@NotNull ServerUser user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM serverusers where serverId = ? AND userId = ?;",
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        stmt.setString(1, user.serverId);
        stmt.setString(2, user.userId);

        ResultSet rs = stmt.executeQuery();

        return !rs.next();
    }

    // add to database
    /*
     * TODO: make all functions to add new instances
     */
    public void addCategory(@NotNull Category category) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into categories" +
                " (name)" +
                " values" +
                " (?);",
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);

        stmt.setString(1, category.name);

        stmt.executeUpdate();
    }

    public void addInventory(@NotNull Inventory inventory) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into inventories" +
                " (invid, userid, shopid, categoryid)" +
                " values" +
                " (?, ?, ?, ?);");

        stmt.setInt(1, inventory.invId);
        stmt.setString(2, inventory.userId);
        stmt.setInt(3, inventory.shopId);
        stmt.setInt(4, inventory.categoryId);

        stmt.executeUpdate();
    }

    public void addServer(@NotNull Server server) throws SQLException {
        PreparedStatement insertServer = conn.prepareStatement("insert into servers(serverid, servername) " +
                "values (?, ?);");
        PreparedStatement insertSettings = conn.prepareStatement("insert into serversettings(serverId) " +
                "values (?);");

        insertServer.setString(1, server.serverId);
        insertSettings.setString(1, server.serverId);
        insertServer.setString(2, server.serverName);

        insertServer.executeUpdate();
        insertSettings.executeUpdate();
    }

    public void addServerUser(@NotNull ServerUser user) throws SQLException {
        PreparedStatement insertUser = conn.prepareStatement("insert into serverusers(serverid, userid) " +
                "values (?, ?);");

        insertUser.setString(1, user.serverId);
        insertUser.setString(2, user.userId);

        insertUser.executeUpdate();
    }

    public void addUser(@NotNull User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into users" +
                " (userid, usertag, balance, xp, isBanned, dailystreak, color, background, badge1, badge2, badge3, badge4, badge5, badge6)"
                +
                " values" +
                " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

        stmt.setString(1, user.userId);
        stmt.setString(2, user.userTag);
        stmt.setInt(3, user.balance);
        stmt.setInt(4, user.xp);
        stmt.setBoolean(5, user.isBanned);
        stmt.setInt(6, user.dailyStreak);
        stmt.setString(7, null);
        stmt.setString(8, user.background);
        stmt.setInt(9, user.badge1);
        stmt.setInt(10, user.badge2);
        stmt.setInt(11, user.badge3);
        stmt.setInt(12, user.badge4);
        stmt.setInt(13, user.badge5);
        stmt.setInt(14, user.badge6);

        stmt.executeUpdate();
    }

    public void addWarning(@NotNull Warning warning) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into warnings" +
                " (casenr, serverid, userid, moderatorid, reason)" +
                " values" +
                " (?, ?, ?, ?, ?);");

        stmt.setInt(1, warning.caseNr);
        stmt.setString(2, warning.serverId);
        stmt.setString(3, warning.userId);
        stmt.setString(4, warning.moderatorId);
        stmt.setString(5, warning.reason);

        stmt.executeUpdate();
    }

    // getters
    /*
     * TODO: make all getters that are needed
     */
    public User getUser(String id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users where userId = ?;");
        stmt.setString(1, id);

        ResultSet rs = stmt.executeQuery();

        return getUserObject(rs);
    }

    public Server getServer(String id) throws SQLException {
        PreparedStatement getServer = conn.prepareStatement("SELECT * FROM servers " +
                "where serverId = ?");

        getServer.setString(1, id);

        ResultSet resultServer = getServer.executeQuery();

        return getServerObject(resultServer);
    }

    public Server getFullServer(String id) throws SQLException {
        Server server = getServer(id);

        PreparedStatement getUsers = conn.prepareStatement("SELECT * FROM serverusers " +
                "where serverId = ?");

        getUsers.setString(1, server.serverId);
        ResultSet resultUsers = getUsers.executeQuery();

        while (resultUsers.next()) {
            server.users.add(getServerUserObject(resultUsers));
        }

        return server;
    }

    public ServerUser getServerUser(String serverId, String userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select * from serverusers where serverid = ? AND userid = ?;");

        stmt.setString(1, serverId);
        stmt.setString(2, userId);

        ResultSet rs = stmt.executeQuery();

        return getServerUserObject(rs);
    }

    public List<User> getUsersWithXp() throws Exception {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users " +
                "where xp > 0 " +
                "order by xp DESC");

        ResultSet rs = stmt.executeQuery();

        List<User> users = new ArrayList<>();

        while (rs.next()) {
            users.add(getUserObject(rs));
        }

        return users;
    }

    public int getGlobalRank(String userId) throws SQLException {
        boolean found = false;
        int place = 0;
        PreparedStatement stmt = conn.prepareStatement("select * from users order by xp DESC;");
        ResultSet rs = stmt.executeQuery();

        while (rs.next() && !found){
            String dbId = rs.getString("userid");
            if(dbId.equals(userId)){
                found = true;
                place = rs.getRow();
            }
        }
        
        return place;
    }

    public int getServerRank(String userId, String serverId) throws SQLException {
        boolean found = false;
        int place = 0;
        PreparedStatement stmt = conn.prepareStatement("select * from serverusers where serverid = ? order by xp DESC;");
        stmt.setString(1, serverId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next() && !found){
            String dbId = rs.getString("userid");
            if(dbId.equals(userId)){
                found = true;
                place = rs.getRow();
            }
        }
        
        return place;
    }

    // updates
    /*
     * TODO: make all instance updates (including list updates if needed)
     */
    public void updateUser(@NotNull User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE users" +
                " set usertag       = ?," +
                "    balance        = ?," +
                "    xp             = ?," +
                "    lastmessagedate= ?," +
                "    isBanned       = ?," +
                "    dailytaken     = ?," +
                "    dailystreak    = ?," +
                "    color          = ?," +
                "    background     = ?," +
                "    badge1         = ?," +
                "    badge2         = ?," +
                "    badge3         = ?," +
                "    badge4         = ?," +
                "    badge5         = ?," +
                "    badge6         = ?" +
                " where userid = ?;");

        stmt.setString(1, user.userTag);
        stmt.setInt(2, user.balance);
        stmt.setInt(3, user.xp);
        stmt.setTimestamp(4, user.lastMessageDate);
        stmt.setBoolean(5, user.isBanned);
        stmt.setTimestamp(6, user.dailyTaken);
        stmt.setInt(7, user.dailyStreak);
        stmt.setString(9, user.background);
        stmt.setInt(10, user.badge1);
        stmt.setInt(11, user.badge2);
        stmt.setInt(12, user.badge3);
        stmt.setInt(13, user.badge4);
        stmt.setInt(14, user.badge5);
        stmt.setInt(15, user.badge6);

        if (user.color == null)
            stmt.setString(8, null);
        else
            stmt.setString(8, "#" + Integer.toHexString(user.color.getRGB()).substring(2));

        stmt.setString(16, user.userId);

        stmt.executeUpdate();
    }

    public void updateUsers(@NotNull List<User> users) throws SQLException {
        for (User user : users)
            updateUser(user);
    }

    public void updateServerUser(@NotNull ServerUser user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("update serverusers " +
                "set xp = ?," +
                "    lastmessagedate=? " +
                "where id=?;");

        stmt.setInt(1, user.xp);
        stmt.setTimestamp(2, user.lastMessageDate);
        stmt.setInt(3, user.id);

        stmt.executeUpdate();
    }

    public void updateWarningReason(@NotNull Warning warning) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("update warnings " +
                "set reason = ? " +
                "where id=?;");

        stmt.setString(1, warning.reason);
        stmt.setInt(2, warning.id);

        stmt.executeUpdate();
    }

    public void updateTimerReason(@NotNull Timer timer) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("update timers " +
                "set reason = ? " +
                "where id=?;");

        stmt.setString(1, timer.reason);
        stmt.setInt(2, timer.id);

        stmt.executeUpdate();
    }

    public void addXp(@NotNull Guild guild, @NotNull Member member) throws SQLException {
        User user = getUser(member.getId());
        ServerUser serverUser = getServerUser(guild.getId(), member.getId());

        if (isEligible(user.lastMessageDate) && !user.isBanned) {
            user.xp += 5;
            user.lastMessageDate = new Timestamp(new java.util.Date().getTime());
            updateUser(user);
        }

        if (isEligible(serverUser.lastMessageDate)) {
            serverUser.xp += 5;
            serverUser.lastMessageDate = new Timestamp(new java.util.Date().getTime());
            updateServerUser(serverUser);
        }
    }

    // delete functions
    public void deleteServerUser(@NotNull ServerUser user) throws SQLException {
        // delete the serveruser
        PreparedStatement delUser = conn.prepareStatement("delete from serverusers" +
                " where id=?;");
        delUser.setInt(1, user.id);

        // delete the timers from the server
        PreparedStatement delTimers = conn.prepareStatement("delete from timers" +
                " where serverId=? AND userId=?;");
        delTimers.setString(1, user.serverId);
        delTimers.setString(2, user.userId);

        // delete the warnings from the server
        PreparedStatement delWarnings = conn.prepareStatement("delete from warnings" +
                " where serverId=? AND userId=?;");
        delWarnings.setString(1, user.serverId);
        delWarnings.setString(2, user.userId);

        delUser.executeUpdate();
        delTimers.executeUpdate();
        delWarnings.executeUpdate();
    }

    public void deleteServer(@NotNull Server server) throws SQLException {
        // delete the server
        PreparedStatement delServer = conn.prepareStatement("delete from servers" +
                " where serverId=?;");
        delServer.setString(1, server.serverId);

        // delete the associated rows in ServerUsers
        PreparedStatement delSU = conn.prepareStatement("delete from serversusers" +
                " where serverId=?;");
        delSU.setString(1, server.serverId);

        // delete the associated settings
        PreparedStatement delSettings = conn.prepareStatement("delete from serversettings" +
                " where serverId=?;");
        delSettings.setString(1, server.serverId);

        // delete the timers from the server
        PreparedStatement delTimers = conn.prepareStatement("delete from timers" +
                " where serverId=?;");
        delTimers.setString(1, server.serverId);

        // delete the warnings from the server
        PreparedStatement delWarnings = conn.prepareStatement("delete from warnings" +
                " where serverId=?;");
        delWarnings.setString(1, server.serverId);

        // execute the scripts
        delSU.executeUpdate();
        delSettings.executeUpdate();
        delTimers.executeUpdate();
        delWarnings.executeUpdate();
        delServer.executeUpdate();
    }

    // conversions JDA to Database class
    public @NotNull User memberToUser(@NotNull Member member) {
        User user = new User();

        net.dv8tion.jda.api.entities.User userJDA = member.getUser();

        user.userId = userJDA.getId();
        user.userTag = userJDA.getAsTag();

        return user;
    }

    public @NotNull Server guildToServer(@NotNull Guild guild) {
        Server server = new Server();

        server.serverId = guild.getId();
        server.serverName = guild.getName();

        return server;
    }

    public @NotNull ServerUser memberToServerUser(@NotNull Member member) {
        ServerUser user = new ServerUser();

        user.userId = member.getId();
        user.serverId = member.getGuild().getId();

        return user;
    }

    // private setter functions
    protected @NotNull Category getCategoryObject(@NotNull ResultSet rs) throws SQLException {
        Category category = new Category();

        if (rs.next()) {
            category.id = rs.getInt("id");

            category.name = rs.getString("name");
        }

        return category;
    }

    private @NotNull Inventory getInventoryItem(@NotNull ResultSet rs) throws SQLException {
        Inventory inventory = new Inventory();

        if (rs.next()) {
            inventory.id = rs.getInt("id");
            inventory.invId = rs.getInt("invId");
            inventory.shopId = rs.getInt("shopId");
            inventory.categoryId = rs.getInt("categoryId");

            inventory.userId = rs.getString("userId");
        }

        return inventory;
    }

    private @NotNull Reward getRewardObject(@NotNull ResultSet rs) throws SQLException {
        Reward reward = new Reward();

        if (rs.next()) {
            reward.id = rs.getInt("id");
            reward.xp = rs.getInt("xp");

            reward.serverId = rs.getString("serverId");
            reward.roleId = rs.getString("roleId");
        }

        return reward;
    }

    private @NotNull Server getServerObject(@NotNull ResultSet rs) throws SQLException {
        Server server = new Server();

        if (rs.next()) {
            server.serverId = rs.getString("serverId");
            server.serverName = rs.getString("serverName");
        }

        return server;
    }

    private @NotNull ServerSettings getServerSettingsObject(@NotNull ResultSet rs) throws SQLException {
        ServerSettings settings = new ServerSettings();

        if (rs.next()) {
            settings.serverId = rs.getString("serverId");
            settings.prefix = rs.getString("prefix");
            settings.muteRoleId = rs.getString("muteRoleId");
            settings.modlogChannel = rs.getString("modlogChannel");
            settings.joinLeaveLogChannel = rs.getString("joinLeaveLogChannel");
            settings.memberLogChannel = rs.getString("memberLogChannel");
            settings.serverLogChannel = rs.getString("serverLogChannel");
            settings.messageLogChannel = rs.getString("messageLogChannel");
            settings.voiceLogChannel = rs.getString("voiceLogChannel");
            settings.levelUpMessage = rs.getString("levelUpMessage");
            settings.levelUpRoleMessage = rs.getString("levelUpRoleMessage");
            settings.noXpRole = rs.getString("noXpRole");
            settings.joinMessage = rs.getString("joinMessage");
            settings.joinMessageChannel = rs.getString("joinMessageChannel");
            settings.leaveMessage = rs.getString("leaveMessage");
            settings.leaveMessageChannel = rs.getString("leaveMessageChannel");
        }

        return settings;
    }

    private @NotNull ServerUser getServerUserObject(@NotNull ResultSet rs) throws SQLException {
        ServerUser user = new ServerUser();

        if (rs.next()) {
            user.id = rs.getInt("id");
            user.xp = rs.getInt("xp");

            user.serverId = rs.getString("serverId");
            user.userId = rs.getString("userId");

            user.lastMessageDate = rs.getTimestamp("lastMessageDate");

            user.user = getUser(user.userId);

            user.setLevel(new Level(user.xp, bot.getConfig().getLevelXp()));
        }

        return user;
    }

    private @NotNull Shop getShopItem(@NotNull ResultSet rs) throws SQLException {
        Shop item = new Shop();

        if (rs.next()) {
            item.id = rs.getInt("id");
            item.price = rs.getInt("price");
            item.category = rs.getInt("category");

            item.name = rs.getString("name");
            item.image = rs.getString("image");
        }

        return item;
    }

    private @NotNull Timer getTimerObject(@NotNull ResultSet rs) throws SQLException {
        Timer timer = new Timer();

        if (rs.next()) {
            timer.id = rs.getInt("id");

            timer.serverId = rs.getString("serverId");
            timer.userId = rs.getString("userId");
            timer.moderatorId = rs.getString("moderatorId");
            timer.type = rs.getString("type");
            timer.reason = rs.getString("reason");

            timer.expiration = rs.getTimestamp("expiration");
        }

        return timer;
    }

    private @NotNull User getUserObject(@NotNull ResultSet rs) throws SQLException {
        User user = new User();

        if (rs.next()) {
            user.userId = rs.getString("userId");
            user.userTag = rs.getString("userTag");
            user.background = rs.getString("background");

            user.balance = rs.getInt("balance");
            user.xp = rs.getInt("xp");
            user.dailyStreak = rs.getInt("dailyStreak");
            user.badge1 = rs.getInt("badge1");
            user.badge2 = rs.getInt("badge2");
            user.badge3 = rs.getInt("badge3");
            user.badge4 = rs.getInt("badge4");
            user.badge5 = rs.getInt("badge5");
            user.badge6 = rs.getInt("badge6");

            user.isBanned = rs.getBoolean("isBanned");

            user.lastMessageDate = rs.getTimestamp("lastMessageDate");
            user.dailyTaken = rs.getTimestamp("dailyTaken");

            if (rs.getString("color") == null)
                user.color = null;
            else
                user.color = new Color(
                        Integer.decode(
                                rs.getString("color")
                                        .replace("#", "0x")));

            user.setLevel(new Level(user.xp, bot.getConfig().getLevelXp()));
        }

        return user;
    }

    private @NotNull Warning getWarningObject(@NotNull ResultSet rs) throws SQLException {
        Warning warning = new Warning();

        if (rs.next()) {
            warning.id = rs.getInt("id");
            warning.caseNr = rs.getInt("caseNr");

            warning.serverId = rs.getString("serverId");
            warning.userId = rs.getString("userId");
            warning.moderatorId = rs.getString("moderatorId");
            warning.reason = rs.getString("reason");
        }

        return warning;
    }

    private @NotNull List<Member> getBotlessList(@NotNull List<Member> members) {
        List<Member> botless = new ArrayList<>();

        members.forEach(member -> {
            if (!member.getUser().isBot()) {
                botless.add(member);
            }
        });

        return botless;
    }

    private java.util.@NotNull Date convertDate(@NotNull Date date) {
        return new java.util.Date(date.getTime());
    }

    private boolean isEligible(Timestamp lastXpMessage) {
        if (lastXpMessage == null)
            return true;

        java.util.Date now = new java.util.Date();
        long diff = Math.abs(lastXpMessage.getTime() - now.getTime());

        return ((diff / 60) % 60) > 1;
    }
}
