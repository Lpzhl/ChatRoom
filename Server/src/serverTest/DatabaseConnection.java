package serverTest;

import server.ChatMessage1;
import server.Group1;
import server.Message1;
import server.User1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    // 定义数据库连接的URL、用户名和密码
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chatroom?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    // 构造方法，加载MySQL数据库驱动
    public DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public int insertMessage(int senderId, int receiverId, String messageContent) {
        String insertMessageQuery = "INSERT INTO messages (sender_id, receiver_id, content, content_type) VALUES (?, ?, ?, 'text')";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertMessageQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, senderId);
            preparedStatement.setInt(2, receiverId);
            preparedStatement.setString(3, messageContent);
            preparedStatement.executeUpdate();

            // 获取插入消息的id
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("插入消息失败，无法获取生成的ID。");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
   /* public List<ChatMessage1> getUnreadMessages(int userId) {
        List<ChatMessage1> unreadMessages = new ArrayList<>();

        String query = "SELECT messages.* FROM messages INNER JOIN chat_records ON messages.id = chat_records.message_id WHERE chat_records.user_id = ? AND chat_records.read_status = 'unread'";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int messageId = resultSet.getInt("id");
                    int senderId = resultSet.getInt("sender_id");
                    String content = resultSet.getString("content");
                    User1 sender = getUserById(senderId);
                    ChatMessage1 message = new ChatMessage1(sender, content,false);
                    unreadMessages.add(message);
                }
            }

            // Mark messages as read
            markMessagesAsRead(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unreadMessages;
    }*/


    public void markMessagesAsRead(int userId) {
        try {
            String query = "UPDATE chat_records SET read_status = 'read' WHERE user_id = ? AND read_status = 'unread'";
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insertChatRecords(int senderId, int receiverId, int messageId) {
        String insertChatRecordQuery = "INSERT INTO chat_records (user_id, message_id, read_status) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertChatRecordQuery)) {
            preparedStatement.setInt(1, senderId);
            preparedStatement.setInt(2, messageId);
            preparedStatement.setString(3, "read");
            preparedStatement.executeUpdate();

            preparedStatement.setInt(1, receiverId);
            preparedStatement.setInt(2, messageId);
            preparedStatement.setString(3, "unread");
            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public User1 getUserById1(int userId) {
        String query = "SELECT id, username FROM users WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                return new User1(userId, username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ChatMessage1> getUnreadMessages(int userId, int activeReceiverId) {
        String query = "SELECT m.id, m.sender_id, m.receiver_id, m.content, m.content_type, m.created_at " +
                "FROM messages m " +
                "JOIN chat_records cr ON m.id = cr.message_id " +
                "WHERE cr.user_id = ? AND cr.read_status = 'unread' AND m.sender_id = ? " +
                "ORDER BY m.created_at ASC";

        List<ChatMessage1> unreadMessages = new ArrayList<>();
        //System.out.println("有东西吗666：：：");
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, activeReceiverId);
            //System.out.println("有东西吗：：：");
            ResultSet rs = pstmt.executeQuery();
            System.out.println(rs);
            while (rs.next()) {
                int id = rs.getInt("id");
                int senderId = rs.getInt("sender_id");
                int receiverId = rs.getInt("receiver_id");
                String content = rs.getString("content");
                String contentType = rs.getString("content_type");
                Timestamp createdAt = rs.getTimestamp("created_at");

                // 这里假设您有一个从ID获取User对象的方法
                User1 sender = getUserById(senderId);
                User1 receiver = getUserById(receiverId);
               // System.out.println("有东西吗655：：：");

                if (sender != null && receiver != null) {
                    Message1 message = new Message1(id, senderId, receiverId, null, content, contentType, createdAt);
                    unreadMessages.add(new ChatMessage1(message, sender, receiver));
                }

                // 将消息设置为已读
                setChatRecordAsRead(id, userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unreadMessages;
    }

    public void setChatRecordAsRead(int messageId, int userId) {
        String query = "UPDATE chat_records SET read_status = 'read', updated_at = CURRENT_TIMESTAMP WHERE message_id = ? AND user_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, messageId);
            pstmt.setInt(2, userId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public int insertMessage(int senderId, int receiverId, String content, String contentType) {
        String query = "INSERT INTO messages (sender_id, receiver_id, content, content_type) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            pstmt.setString(3, content);
            pstmt.setString(4, contentType);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void insertChatRecord(int userId, int messageId, String readStatus) {
        String query = "INSERT INTO chat_records (user_id, message_id, read_status) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, messageId);
            pstmt.setString(3, readStatus);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //检测
    public boolean hasNewRequestForUser(String username) {
        String sql = "SELECT COUNT(*) FROM requests WHERE receiver_id = (SELECT id FROM users WHERE username = ?) AND status = 'pending'";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Group1> getGroupsByUserId(int userId) {
        String sql = "SELECT g.* FROM `groups` g JOIN group_members gm ON g.id = gm.group_id WHERE gm.user_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            List<Group1> groups = new ArrayList<>();
            //System.out.println("ResultSet是否有下一行："+resultSet.next());
            while (resultSet.next()) {
                System.out.println("111");
                //System.out.println(resultSet.next());
                Group1 group = new Group1();
                group.setId(resultSet.getInt("id"));
                group.setName(resultSet.getString("name"));
                group.setDescription(resultSet.getString("description"));
                group.setAvatar(resultSet.getString("avatar_path"));
                System.out.println("群众："+group);
                // 根据你的Group类的定义，你可能需要获取更多的列
                groups.add(group);
            }
            System.out.println("返回的："+groups);
            return groups;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean createGroup(Group1 group, int creatorId) {
        String sql = "INSERT INTO `groups` (name, description, avatar_path, created_by,name_id) VALUES (?, ?, ?, ?,?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, group.getName());
            statement.setString(2, group.getDescription());
            statement.setString(3, group.getAvatar());
            statement.setInt(4, creatorId);
            System.out.println("群ID："+group.getName_id());
            statement.setString(5,group.getName_id());
            int rowCount = statement.executeUpdate();
            if (rowCount > 0) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    int groupId = rs.getInt(1);
                    String memberSql = "INSERT INTO group_members (group_id, user_id, role, join_time, status) VALUES (?, ?, ?, ?, ?)";
                    try (Connection connection1 = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                         PreparedStatement memberStatement = connection1.prepareStatement(memberSql)) {
                        memberStatement.setInt(1, groupId);
                        memberStatement.setInt(2, creatorId);
                        memberStatement.setString(3, "creator");
                        memberStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                        memberStatement.setString(5, "active");
                        memberStatement.executeUpdate();
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    // 获取用户状态
    public String getUserStatus(String username) {
        String select = "SELECT status FROM users WHERE username = ?";
        try (
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = connection.prepareStatement(select);
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 设置用户状态
    public void setUserStatus(String username, String status) {
        String update = "UPDATE users SET status = ? WHERE username = ?";
        try (
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = connection.prepareStatement(update);
        ) {
            statement.setString(1, status);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 获取请求列表
    public List<String> getRequestList(String username) {
        int userId = getUserIdByUsername(username);
        String select = "SELECT username FROM users WHERE id IN (SELECT sender_id FROM requests WHERE receiver_id = ? AND status = 'pending')";
        List<String> requestList = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = connection.prepareStatement(select)
        ) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                requestList.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requestList;
    }

    // 更新请求状态
    public boolean updateFriendRequestStatus(String username1, String username2, String status) {
        int userId1 = getUserIdByUsername(username1);
        int userId2 = getUserIdByUsername(username2);
        String update = "UPDATE requests SET status = ? WHERE sender_id = ? AND receiver_id = ?";
        try (
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = connection.prepareStatement(update)
        ) {
            statement.setString(1, status);
            statement.setInt(2, userId2); // 注意这里是userId2，因为userId2是
            statement.setInt(3, userId1); // and userId1 is the receiver_id
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // 该方法用于向数据库中添加一条新的好友请求
    public boolean sendFriendRequest(String senderUsername, String receiverUsername) {
        // 创建一个SQL插入语句，用于向requests表中添加一条新的记录
        String insert = "INSERT INTO requests(sender_id, receiver_id, request_type, status, created_at, updated_at) VALUES (?, ?, 'friend', 'pending', NOW(), NOW())";
        // 使用try-with-resources结构来自动关闭数据库连接和PreparedStatement
        try (
                // 获取数据库连接
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                // 预处理SQL插入语句
                PreparedStatement statement = connection.prepareStatement(insert)
        ) {
            // 从数据库中获取发送者和接收者的ID
            int senderId = getUserIdByUsername(senderUsername);
            int receiverId = getUserIdByUsername(receiverUsername);
            // 如果发送者或接收者不存在，则返回false
            if (senderId == -1 || receiverId == -1) {
                return false;
            }
            // 将SQL插入语句中的占位符替换为发送者和接收者的ID
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            // 执行SQL插入语句
            statement.executeUpdate();
            // 如果执行成功，则返回true
            return true;
        } catch (SQLException e) {
            // 如果执行过程中发生了SQL异常，则返回false
            e.printStackTrace();
            return false;
        }
    }
    // 该方法用于根据用户名从数据库中获取用户ID
    private int getUserIdByUsername(String username) {
        // 创建一个SQL查询语句，用于从users表中获取用户的ID
        String select = "SELECT id FROM users WHERE username = ?";
        // 使用try-with-resources结构来自动关闭数据库连接和PreparedStatement
        try (
                // 获取数据库连接
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                // 预处理SQL查询语句
                PreparedStatement statement = connection.prepareStatement(select)
        ) {
            // 将SQL查询语句中的占位符替换为用户名
            statement.setString(1, username);
            // 执行SQL查询语句，并获取查询结果
            ResultSet resultSet = statement.executeQuery();
            // 如果查询结果不为空，则返回用户的ID
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                // 如果查询结果为空，则返回-1
                return -1;
            }
        } catch (SQLException e) {
            // 如果执行过程中发生了SQL异常，则返回-1
            e.printStackTrace();
            return -1;
        }
    }

    public boolean canAddFriend(String username1, String username2) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String checkSql = "SELECT COUNT(*) FROM users WHERE username IN (?, ?)";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, username1);
            pstmt.setString(2, username2);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count < 2) {
                    return false;
                }
            }
            rs.close();
            pstmt.close();

            String checkFriendSql = "SELECT COUNT(*) FROM friends f " +
                    "JOIN users u1 ON f.user_id = u1.id " +
                    "JOIN users u2 ON f.friend_id = u2.id " +
                    "WHERE (u1.username = ? AND u2.username = ?) OR (u1.username = ? AND u2.username = ?)";
            pstmt = conn.prepareStatement(checkFriendSql);
            pstmt.setString(1, username1);
            pstmt.setString(2, username2);
            pstmt.setString(3, username2);
            pstmt.setString(4, username1);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //添加好友
    public boolean addFriend(String username1, String username2) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 检查两个用户名是否存在
            String checkSql = "SELECT COUNT(*) FROM users WHERE username IN (?, ?)";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, username1);
            pstmt.setString(2, username2);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count < 2) {
                    // 至少一个用户名不存在
                    return false;
                }
            }
            rs.close();
            pstmt.close();

            String checkFriendSql = "SELECT COUNT(*) FROM friends f " +
                    "JOIN users u1 ON f.user_id = u1.id " +
                    "JOIN users u2 ON f.friend_id = u2.id " +
                    "WHERE (u1.username = ? AND u2.username = ?) OR (u1.username = ? AND u2.username = ?)";
            pstmt = conn.prepareStatement(checkFriendSql);
            pstmt.setString(1, username1);
            pstmt.setString(2, username2);
            pstmt.setString(3, username2);
            pstmt.setString(4, username1);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    return false;
                }
            }
            rs.close();
            pstmt.close();

            // 以上检查都通过，添加好友
            // 添加第一条记录：username1 的 user_id 和 username2 的 friend_id
            String insertSql = "INSERT INTO friends (user_id, friend_id, created_at, updated_at) " +
                    "SELECT u1.id, u2.id, NOW(), NOW() FROM users u1, users u2 " +
                    "WHERE u1.username = ? AND u2.username = ?";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, username1);
            pstmt.setString(2, username2);
            int affectedRows = pstmt.executeUpdate();

            // 添加第二条记录：username2 的 user_id 和 username1 的 friend_id
            insertSql = "INSERT INTO friends (user_id, friend_id, created_at, updated_at) " +
                    "SELECT u1.id, u2.id, NOW(), NOW() FROM users u1, users u2 " +
                    "WHERE u1.username = ? AND u2.username = ?";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, username2);
            pstmt.setString(2, username1);
            affectedRows += pstmt.executeUpdate();

            return affectedRows > 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //删除好友关系
    public boolean deleteFriend(String username1, String username2) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 删除好友关系
            String deleteSql = "DELETE FROM friends " +
                    "WHERE (user_id = (SELECT id FROM users WHERE username = ?) AND " +
                    "friend_id = (SELECT id FROM users WHERE username = ?)) OR " +
                    "(user_id = (SELECT id FROM users WHERE username = ?) AND " +
                    "friend_id = (SELECT id FROM users WHERE username = ?))";
            pstmt = conn.prepareStatement(deleteSql);
            pstmt.setString(1, username1);
            pstmt.setString(2, username2);
            pstmt.setString(3, username2);
            pstmt.setString(4, username1);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 关闭资源
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 该方法通过用户ID从数据库中获取该用户的所有好友
    public List<User1> getFriendsByUserId(int userId) {
        // 创建一个空的好友列表
        List<User1> friends = new ArrayList<>();
        // 创建一个SQL查询语句，用于从friends表中获取指定用户的所有好友ID
        String query = "SELECT friend_id FROM friends WHERE user_id = ?";
        // 使用try-with-resources结构来自动关闭数据库连接和PreparedStatement
        try (
                // 获取数据库连接
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                // 预处理SQL查询语句
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            // 将SQL查询语句中的第一个占位符替换为用户ID
            statement.setInt(1, userId);
            // 执行SQL查询语句并获取结果集
            ResultSet resultSet = statement.executeQuery();
            // 遍历结果集
            while (resultSet.next()) {
                // 从当前结果中获取好友ID
                int friendId = resultSet.getInt("friend_id");
                // 通过好友ID从数据库中获取好友对象
                User1 friend = getUserById(friendId);
                // 将好友对象添加到好友列表中
                friends.add(friend);
            }
        } catch (SQLException e) {
            // 如果在获取数据库连接或执行SQL查询语句时发生错误，打印堆栈轨迹
            e.printStackTrace();
        }
        // 返回好友列表
        return friends;
    }

    public User1 getUserById(int id) {
        User1 user = null;
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // 从数据库获取所有字段的值，这里假设你的字段名和User1类的属性名相同
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String avatar = resultSet.getString("avatar");
                String nickname = resultSet.getString("nickname");
                String gender = resultSet.getString("gender");
                LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
                String signature = resultSet.getString("signature");
                String status = resultSet.getString("status");
                LocalDate createdAt = resultSet.getTimestamp("created_at").toLocalDateTime().toLocalDate();
                LocalDate updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime().toLocalDate();

                // 创建User对象
                user = new User1(id, username, email, avatar, nickname, gender, birthday, signature, status, createdAt, updatedAt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }


    //查找好友
    public User1 findUser(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User1 user = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String checkSql = "SELECT * FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if(rs.next()){
                user = new User1();
                user.setNickname(rs.getString("nickname"));
                user.setAvatar(rs.getString("avatar"));
                user.setGender(rs.getString("gender"));
                System.out.println("查找到的好友信息："+user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if(rs != null) rs.close();
                if(pstmt != null) pstmt.close();
                if(conn != null) conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return user;
    }


    public User1 getUserInfo(String username) {
        User1 user = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                String avatar = rs.getString("avatar");
                String nickname = rs.getString("nickname");
                String gender = rs.getString("gender");
                LocalDate birthday = rs.getDate("birthday").toLocalDate();
                String signature = rs.getString("signature");
                String status = rs.getString("status");
                LocalDate createdAt = rs.getDate("created_at").toLocalDate();
                LocalDate updatedAt = rs.getDate("updated_at").toLocalDate();
                user = new User1(id, username, email, avatar, nickname, gender, birthday, signature, status, createdAt, updatedAt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /*public User1 getUserByUsername(String username) {
        User1 user1 = null;
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user1 = new User1();
                user1.setId(resultSet.getInt("id"));
                user1.setUsername(resultSet.getString("username"));
                user1.setEmail(resultSet.getString("email"));
                // 获取创建时间和更新时间
                Timestamp createdAtTimestamp = resultSet.getTimestamp("created_at");
                user1.setCreatedAt(createdAtTimestamp.toLocalDateTime().toLocalDate());

                Timestamp updatedAtTimestamp = resultSet.getTimestamp("updated_at");
                user1.setUpdatedAt(updatedAtTimestamp.toLocalDateTime().toLocalDate());




                // 设置默认头像
               // 获取头像 URL
                String avatarUrl = resultSet.getString("avatar");
                user1.setAvatar(avatarUrl != null ? avatarUrl : "/image/默认头像.png");


                // 设置默认昵称
                String nickname = resultSet.getString("nickname");
                user1.setNickname(resultSet.wasNull() ? "？？？" : nickname);

                // 设置默认性别
                String gender = resultSet.getString("gender");
                user1.setGender(resultSet.wasNull() ? "未知" : gender);

                // 设置默认生日
                Date birthday = resultSet.getDate("birthday");
                user1.setBirthday(resultSet.wasNull() ? LocalDate.of(1999, 6, 7) : birthday.toLocalDate());

                // 设置默认个性签名
                String signature = resultSet.getString("signature");
                user1.setSignature(resultSet.wasNull() ? "这个人很懒什么都没有了留下~" : signature);

                // 设置默认状态
                String status = resultSet.getString("status");
                user1.setStatus(resultSet.wasNull() ? "离线" : status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user1;
    }*/
    public User1 getUserByUsername(String username) {
        User1 user1 = null;
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // 从数据库获取所有字段的值
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");

                // 获取创建时间和更新时间
                LocalDate createdAt = resultSet.getTimestamp("created_at").toLocalDateTime().toLocalDate();
                LocalDate updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime().toLocalDate();

                // 获取头像 URL
                String avatarUrl = resultSet.getString("avatar");
                if (avatarUrl == null) avatarUrl = "/image/默认头像.png";

                // 获取昵称
                String nickname = resultSet.getString("nickname");
                if (resultSet.wasNull()) nickname = "？？？";

                // 获取性别
                String gender = resultSet.getString("gender");
                if (resultSet.wasNull()) gender = "未知";

                // 获取生日
                LocalDate birthday = null;
                if (!resultSet.wasNull()) {
                    birthday = resultSet.getDate("birthday").toLocalDate();
                } else {
                    birthday = LocalDate.of(1999, 6, 7);
                }
                // 获取个性签名
                String signature = resultSet.getString("signature");
                if (resultSet.wasNull()) signature = "这个人很懒什么都没有了留下~";

                // 获取状态
                String status = resultSet.getString("status");
                if (resultSet.wasNull()) status = "离线";

                // 使用全参数构造方法创建 User1 对象
              user1 = new User1(id, username, email, avatarUrl, nickname, gender, birthday, signature, status, createdAt, updatedAt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user1;
    }

    public User1 getUserByEmail(String email) {
        User1 user1 = null;
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // 从数据库获取所有字段的值
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");

                // 获取创建时间和更新时间
                LocalDate createdAt = resultSet.getTimestamp("created_at").toLocalDateTime().toLocalDate();
                LocalDate updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime().toLocalDate();

                // 获取头像 URL
                String avatarUrl = resultSet.getString("avatar");
                if (avatarUrl == null) avatarUrl = "/image/默认头像.png";

                // 获取昵称
                String nickname = resultSet.getString("nickname");
                if (resultSet.wasNull()) nickname = "？？？";

                // 获取性别
                String gender = resultSet.getString("gender");
                if (resultSet.wasNull()) gender = "未知";

                // 获取生日
                LocalDate birthday = null;
                if (!resultSet.wasNull()) {
                    birthday = resultSet.getDate("birthday").toLocalDate();
                } else {
                    birthday = LocalDate.of(1900, 1, 1);
                }
                // 获取个性签名
                String signature = resultSet.getString("signature");
                if (resultSet.wasNull()) signature = "这个人很懒什么都没有了留下~";

                // 获取状态
                String status = resultSet.getString("status");
                if (resultSet.wasNull()) status = "离线";

                // 使用全参数构造方法创建 User1 对象
                user1 = new User1(id, username, email, avatarUrl, nickname, gender, birthday, signature, status, createdAt, updatedAt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user1;
    }



    //查找是否存在用户名
    public boolean userExists(String username) {
        // 准备查询语句
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            // 设置插入参数
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //在找回密码的时候查找id号是否和email相互匹配
    public boolean isUsernameAndEmailMatched(String username, String email) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ? AND email = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //注册账号
    public void registerUser(String username, String password, String email, String nickname,String avatar,String signature) {
        String query = "INSERT INTO users (username, password, email, nickname,avatar,signature) VALUES (?, ?, ?, ?,?,?)";//问号的顺序是：(1)username, (2)password, (3)email, (4)nickname。
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // 创建一个用于执行预编译 SQL 查询的 PreparedStatement 对象。预编译的查询可以防止 SQL 注入攻击，并提高查询性能。
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);// 将第一个问号替换为 username，
            statement.setString(2, md5(password)); //MD5加密    将第二个问号替换为加密后的 password，
            statement.setString(3, email);// 将第三个问好替换成 email
            statement.setString(4, nickname);  //将第四个问号 替换成 nickname
            statement.setString(5,avatar);
            statement.setString(6,signature);
            statement.executeUpdate();//执行预编译的 SQL 更新查询，例如插入、更新或删除记录
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //检测登入和   //处理检测原始密码是否正确
    public boolean checkLogin(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("password").equals(md5(password)); // 使用加密后比较
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    //查找QQ邮箱是否绑定账号
    public boolean emailExists(String email) {
        /*
        检查结果集是否有下一条记录。因为我们使用了 COUNT(*) 函数，所以结果集只会包含一个整数值。
        如果这个值大于 0，说明存在至少一个匹配的用户，返回 true；否则返回 false
         */
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();//执行查询并获取结果集 resultSet：
            if (resultSet.next()) {
                /*
                resultSet.getInt(1) 是从 ResultSet 对象中获取第一列的整数值。resultSet.getInt(1) 返回一个整数，表示第一列的值（在数据库中列是从 1 开始索引的）。
                此处使用 getInt() 方法是因为查询的 SQL 语句返回的是一个整数类型的结果集而不是一个布尔类型的结果集。
                 */
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //修改密码
    public boolean updateUserPassword(String username, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE username = ?";
        //这行代码创建了一个 Connection 对象，用于建立 Java 应用程序和数据库之间的连接。connection 是 Connection 对象的引用名称。
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, md5(newPassword)); // 修改后也要md5加密
            statement.setString(2, username);
            int updatedRows = statement.executeUpdate();//执行更新操作，并获取受影响的行数 updatedRows
            return updatedRows > 0;//如果受影响的行数大于 0，说明更新操作成功，返回 true；否则返回 false：
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    //MD5加密
    private String md5(String input) {
        try {
            //接受一个参数 input，表示要进行 MD5 加密的字符串。
            //使用 MessageDigest 类的 getInstance 方法获取一个 MD5 算法的实例 md：
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes(), 0, input.length());//使用 md 对 input 进行加密。这里传入 input 的字节数组、起始位置和长度：
            return new BigInteger(1, md.digest()).toString(16);//将加密后的字节数组转换为一个 BigInteger 实例，并将其转换为十六进制字符串。最后返回这个字符串：
        } catch (NoSuchAlgorithmException e) {
            //这个 RuntimeException 会抛给调用 md5 方法的代码。   在这个例子中，调用 md5 方法的代码是 updateUserPassword 方法。
            throw new RuntimeException("MD5 加密未实现", e);//如果在获取 MD5 算法实例过程中出现任何异常（如未找到 MD5 算法），捕获异常并抛出一个运行时异常
        }
    }

    public void updateUser(String username, String newAvatarPath, String newNickname, String newGender, LocalDate newBirthday, String newSignature) {
        String query = "UPDATE users SET avatar = ?, nickname = ?, gender = ?, birthday = ?, signature = ? WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newAvatarPath);
            statement.setString(2, newNickname);
            statement.setString(3, newGender);
            statement.setDate(4, java.sql.Date.valueOf(newBirthday));
            statement.setString(5, newSignature);
            statement.setString(6, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean GroupUserExists(String username) {
            // 准备查询语句
            String query = "SELECT COUNT(*) FROM `groups` WHERE name_id = ?";
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {
                // 设置插入参数
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
    }

    public List<User1> getFriendsList(int userId) {
        List<User1> friends = new ArrayList<>();
        try {
            String query = "SELECT users.* FROM friends INNER JOIN users ON friends.friend_id = users.id WHERE friends.user_id = ?";
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String avatar = rs.getString("avatar");
                String status = rs.getString("status");
                String signature = rs.getString("signature");

                User1 friend = new User1(id, username, password, email, avatar, status,signature);
                friends.add(friend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

}

/*
如果 updateUserPassword 方法没有处理这个异常，那么这个异常将继续向上抛给调用 updateUserPassword 方法的代码。
如果异常没有被任何方法捕获和处理，最终它将抛给 JVM，导致程序终止运行并输出异常信息。
 */
