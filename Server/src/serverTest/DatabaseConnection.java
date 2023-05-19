package serverTest;

import server.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
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


    public boolean removeGroupCreator(int groupId) {
        String query = "DELETE FROM group_members WHERE group_id = ? AND role = 'creator'";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean updateGroupMemberRoleToMember(int groupId) {
        String query = "UPDATE group_members SET role = 'member' WHERE group_id = ? AND role = 'creator'";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateGroupMemberRoleToCreator(int groupId, int userId) {
        String query = "UPDATE group_members SET role = 'creator' WHERE group_id = ? AND user_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, userId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean handleQuitGroup(int userId, int groupId) {
        String query = "DELETE FROM group_members WHERE user_id = ? AND group_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, groupId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Group1 getGroupInfo(int groupId) {
        String query = "SELECT * FROM `groups` WHERE id = ?";
        Group1 group = new Group1();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // 如果resultSet有数据，填充群组信息
            if (resultSet.next()) {
                group.setId(resultSet.getInt("id"));
                group.setName(resultSet.getString("name"));
                group.setName_id(resultSet.getString("name_id"));
                group.setDescription(resultSet.getString("description"));
                group.setCreatedBy(resultSet.getInt("created_by"));
                ZoneId zoneId = ZoneId.of("UTC");
                group.setCreatedAt(Timestamp.valueOf(resultSet.getTimestamp("created_at").toInstant().atZone(zoneId).toLocalDateTime()));
                group.setAvatar(resultSet.getString("avatar_path"));

                // 获取群组成员信息
                group.setMembers(getGroupMembers(groupId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return group;
    }


    // userid有问题
  /*  public List<User1> getGroupMembers(int groupId) {
        String query = "SELECT * FROM group_members JOIN users ON group_members.user_id = users.id WHERE group_id = ?";
        List<User1> members = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // 如果resultSet有数据，添加到成员列表
            while (resultSet.next()) {
                User1 user = new User1();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setAvatar(resultSet.getString("avatar"));
                user.setNickname(resultSet.getString("nickname"));
                user.setGender(resultSet.getString("gender"));
                user.setBirthday(resultSet.getDate("birthday").toLocalDate());
                user.setSignature(resultSet.getString("signature"));
                user.setStatus(resultSet.getString("status"));
                user.setRole(resultSet.getString("role"));
                user.setJoinTime(resultSet.getTimestamp("join_time").toLocalDateTime());

                members.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }*/

    public List<User1> getGroupMembers(int groupId) {
        String query = "SELECT users.id as user_id, group_members.*, users.* FROM group_members JOIN users ON group_members.user_id = users.id WHERE group_id = ?";
        List<User1> members = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, groupId);
            ResultSet resultSet = preparedStatement.executeQuery();

            // 如果resultSet有数据，添加到成员列表
            while (resultSet.next()) {
                User1 user = new User1();
                user.setId(resultSet.getInt("user_id"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setAvatar(resultSet.getString("avatar"));
                user.setNickname(resultSet.getString("nickname"));
                user.setGender(resultSet.getString("gender"));
                user.setBirthday(resultSet.getDate("birthday").toLocalDate());
                user.setSignature(resultSet.getString("signature"));
                user.setStatus(resultSet.getString("status"));
                user.setRole(resultSet.getString("role"));
                ZoneId zoneId = ZoneId.of("UTC");
                user.setJoinTime(resultSet.getTimestamp("join_time").toInstant().atZone(zoneId).toLocalDateTime());

                members.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }


    public boolean isUserInGroup(long userId, int groupId) {
        String query = "SELECT * FROM group_members WHERE user_id = ? AND group_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, userId);
            preparedStatement.setInt(2, groupId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // 如果resultSet有数据，表示用户已经是群组的成员
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 如果没有查询到数据或查询过程中出现异常，表示用户不是群组的成员
        return false;
    }

    public boolean acceptGroupRequest(long userId, int groupId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateRequestSql = "UPDATE requests SET status = 'accepted' WHERE sender_id = ? AND group_id = ?";
            PreparedStatement updateRequestStmt = conn.prepareStatement(updateRequestSql);
            updateRequestStmt.setLong(1, userId);
            updateRequestStmt.setInt(2, groupId);
            int updatedRows = updateRequestStmt.executeUpdate();

            if (updatedRows > 0) {
                String insertGroupMemberSql = "INSERT INTO group_members (group_id, user_id, role, status) VALUES (?, ?, 'member', 'active')";
                PreparedStatement insertGroupMemberStmt = conn.prepareStatement(insertGroupMemberSql);
                insertGroupMemberStmt.setInt(1, groupId);
                insertGroupMemberStmt.setLong(2, userId);
                int insertedRows = insertGroupMemberStmt.executeUpdate();
                return insertedRows > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rejectGroupRequest(long userId, int groupId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateRequestSql = "UPDATE requests SET status = 'rejected' WHERE sender_id = ? AND group_id = ?";
            PreparedStatement updateRequestStmt = conn.prepareStatement(updateRequestSql);
            updateRequestStmt.setLong(1, userId);
            updateRequestStmt.setInt(2, groupId);
            return updateRequestStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 定义一个方法，接收两个用户 ID 作为参数，返回两个用户之间的聊天历史记录
    public List<ChatMessage1> getChatHistory(int userId, int friendId) {
        // 创建一个列表，用于存储聊天历史记录
        List<ChatMessage1> chatHistory = new ArrayList<>();

        String sql = "SELECT m.id, m.sender_id, m.receiver_id, m.group_id, m.content, m.content_type, m.file_name, m.created_at, u1.username AS sender_username, u2.username AS receiver_username, u1.avatar AS sender_avatar, u2.avatar AS receiver_avatar, u1.nickname AS sender_nickname, u2.nickname AS receiver_nickname " +
                "FROM messages m " +
                "JOIN users u1 ON m.sender_id = u1.id " +
                "JOIN users u2 ON m.receiver_id = u2.id " +
                "WHERE (m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?) " +
                "ORDER BY m.created_at";

        // 创建数据库连接
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             // 使用 PreparedStatement 对象设置 SQL 语句的参数，并执行查询
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, friendId);
            statement.setInt(3, friendId);
            statement.setInt(4, userId);

            // 获取查询结果
            try (ResultSet resultSet = statement.executeQuery()) {
                // 遍历查询结果
                while (resultSet.next()) {
                    // 从结果集中获取各个字段的值
                    int id = resultSet.getInt("id");
                    int senderId = resultSet.getInt("sender_id");
                    int receiverId = resultSet.getInt("receiver_id");
                    int groupId = resultSet.getInt("group_id");
                    String content = resultSet.getString("content");
                    String contentType = resultSet.getString("content_type");
                    String filename = resultSet.getString("file_name");
                    ZoneId zoneId = ZoneId.of("UTC");
                    LocalDateTime createdAt = resultSet.getTimestamp("created_at").toInstant().atZone(zoneId).toLocalDateTime();

                    String senderUsername = resultSet.getString("sender_username");
                    String receiverUsername = resultSet.getString("receiver_username");
                    String senderAvatar = resultSet.getString("sender_avatar");
                    String receiverAvatar = resultSet.getString("receiver_avatar");
                    String senderNickname = resultSet.getString("sender_nickname");
                    String receiverNickname = resultSet.getString("receiver_nickname");

                    // 根据查询结果创建用户和聊天消息对象
                    User1 sender = new User1(senderId, senderUsername, senderAvatar, senderNickname);
                    User1 receiver = new User1(receiverId, receiverUsername, receiverAvatar, receiverNickname);
                    boolean isCurrentUser = senderId == userId;

                    // 创建 ChatMessage1 对象，并设置相关属性
                    ChatMessage1 chatMessage = new ChatMessage1(id, sender, content, isCurrentUser, createdAt, filename);
                    chatMessage.setGroupId(groupId);
                    chatMessage.setContentType(contentType);

                    setChatRecordAsRead(id, userId);
                    // 将聊天消息对象添加到聊天历史记录列表中
                    chatHistory.add(chatMessage);
                }
            }
        } catch (SQLException e) {
            // 处理可能发生的异常
            e.printStackTrace();
        }

        // 返回聊天历史记录列表
        return chatHistory;
    }

    public void updateMessageStatus(int messageId, boolean isRead) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("UPDATE chat_records SET read_status = ? WHERE id = ?");
            statement.setString(1, "read");
            statement.setInt(2, messageId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int insertMessage(int senderId, int receiverId, String messageContent, String contentType, String fileName) {
        String insertMessageQuery = "INSERT INTO messages (sender_id, receiver_id, content, content_type, file_name) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertMessageQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, senderId);
            preparedStatement.setInt(2, receiverId);
            preparedStatement.setString(3, messageContent);
            preparedStatement.setString(4, contentType);
            preparedStatement.setString(5, fileName);
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
                group.setCreatedBy(resultSet.getInt("created_by"));
                System.out.println("群众："+group);
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
    public List<Request1> getRequestList(String username) {
        int userId = getUserIdByUsername(username);
        String select = "SELECT u.username, r.status, r.request_type, r.group_id FROM users u INNER JOIN requests r ON u.id = r.sender_id WHERE r.receiver_id = ? AND r.status IN ('pending', 'accepted', 'rejected')";
        List<Request1> requestList = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = connection.prepareStatement(select)
        ) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username1 = resultSet.getString("username");
                String status = resultSet.getString("status");
                String requestType = resultSet.getString("request_type");
                Integer groupId = resultSet.getObject("group_id", Integer.class);  // 获取群组ID，如果没有则为null
                Request1 request = new Request1(username1, status, requestType, groupId);
                requestList.add(request);
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
    public Group1 findGroup1(String groupName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Group1 group = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String checkSql = "SELECT * FROM  `groups` WHERE name_id = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, groupName);
            rs = pstmt.executeQuery();
            if(rs.next()){
                group = new Group1();
                group.setName(rs.getString("name"));
                group.setDescription(rs.getString("description"));
                group.setAvatar(rs.getString("avatar_path"));
                // 更多字段...
                System.out.println("查找到的群聊信息："+group);
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
        return group;
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

    public boolean isMemberOfGroup(String username, String name_id) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(
     "SELECT COUNT(*) FROM group_members WHERE user_id = (SELECT id FROM users WHERE username = ?) AND group_id = (SELECT id FROM `groups` WHERE name_id = ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, name_id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("这是啥");
                    int count = rs.getInt(1);
                    return  count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    // 处理发送群请求
    public boolean sendGroupRequest(String username, String groupName) {
        try {
            // 获取用户 ID 和群聊 ID
            int userId = getUserIdByUsername(username);
            int groupId = getGroupIdByName(groupName);
            if (userId == -1 || groupId == -1) {
                System.out.println("User or group not found");
                return false;
            }

            // 查询所有的管理员和群主的 ID
            System.out.println("查询所有的管理员和群主的 ID"+groupId);
            List<Integer> adminIds = getAdminIdsByGroupId(groupId);
            if (adminIds.isEmpty()) {
                System.out.println("No admins or creators found for the group");
                return false;
            }

            // 为每一个管理员和群主创建新的请求记录
            String query = "INSERT INTO requests (sender_id, receiver_id, group_id, request_type, status) VALUES (?, ?, ?, 'group', 'pending')";
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(query);

            for (Integer adminId : adminIds) {
                statement.setInt(1, userId);
                statement.setInt(2, adminId);
                statement.setInt(3, groupId);
                int rowsInserted = statement.executeUpdate();

                // 检查是否成功插入新的请求记录
                if (rowsInserted <= 0) {
                    System.out.println("Failed to send group request to admin " + adminId);
                    return false;
                }
            }

            System.out.println("Group request sent successfully");
            return true;
        } catch (SQLException e) {
            System.out.println("Error sending group request: " + e.getMessage());
            return false;
        }
    }

    //查找出每个群聊的管理员和群主id
    public List<Integer> getAdminIdsByGroupId(int groupId) {
        List<Integer> adminIds = new ArrayList<>();
        try {
            String query = "SELECT user_id FROM group_members WHERE group_id = ? AND role IN ('creator', 'admin')";
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, groupId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                adminIds.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting admin IDs: " + e.getMessage());
        }
        return adminIds;
    }


    public int getGroupOwnerId(int groupId) {
        try {
            // 查询群聊创建者 ID
            String query = "SELECT user_id FROM group_members WHERE group_id = ? AND role = 'creator'";
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, groupId);
            ResultSet resultSet = statement.executeQuery();

            // 检查结果集是否非空
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            } else {
                System.out.println("Group owner not found");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Error getting group owner ID: " + e.getMessage());
            return -1;
        }
    }


    public int getGroupIdByName(String groupName) {
        try {
            // 查询群聊 ID
            String query = "SELECT id FROM `groups` WHERE name_id = ?";
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, groupName);
            ResultSet resultSet = statement.executeQuery();

            // 检查结果集是否非空
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                System.out.println("Group not found");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Error getting group ID: " + e.getMessage());
            return -1;
        }
    }


    public boolean deleteRequest(String senderUsername, String receiverUsername, String requestType, Integer groupId) {
        int senderId = getUserIdByUsername(senderUsername);
        int receiverId = getUserIdByUsername(receiverUsername);

        String delete = "DELETE FROM requests WHERE sender_id = ? AND receiver_id = ? AND request_type = ?";
        if (groupId != null) {
            delete += " AND group_id = ?";
        }

        try (
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = connection.prepareStatement(delete)
        ) {
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            statement.setString(3, requestType);
            if (groupId != null) {
                statement.setInt(4, groupId);
            }

            int rowCount = statement.executeUpdate();
            return rowCount > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public int insertGroupMessage(int senderId, int groupId, String messageContent, String contentType, String fileName) {
        String insertMessageQuery = "INSERT INTO messages (sender_id, group_id, content, content_type, file_name) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertMessageQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, senderId);
            preparedStatement.setInt(2, groupId);
            preparedStatement.setString(3, messageContent);
            preparedStatement.setString(4, contentType);
            preparedStatement.setString(5, fileName);
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

    public List<User1> getGroupMembers(int groupId, int senderId) {
        List<User1> groupMembers = new ArrayList<>();
        try {
            String query = "SELECT u.* FROM users u " +
                    "JOIN group_members gm ON u.id = gm.user_id " +
                    "WHERE gm.group_id = ? AND gm.status = 'active' AND u.id <> ?";
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, groupId);
            ps.setInt(2, senderId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String avatar = rs.getString("avatar");
                String nickname = rs.getString("nickname");
                String gender = rs.getString("gender");
                LocalDate birthday = rs.getDate("birthday").toLocalDate();
                String signature = rs.getString("signature");
                String status = rs.getString("status");
                LocalDate createdAt = rs.getTimestamp("created_at").toLocalDateTime().toLocalDate();
                LocalDate updatedAt = rs.getTimestamp("updated_at").toLocalDateTime().toLocalDate();

                User1 user = new User1(id, username, email, avatar, nickname, gender, birthday, signature, status, createdAt, updatedAt);
                groupMembers.add(user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return groupMembers;
    }

    public boolean handleRemoveGroupMembers(int groupId) {
        String query = "DELETE FROM group_members WHERE group_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean handleDisbandGroup(int groupId) {
        String query = "DELETE FROM `groups` WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<User1> getGroupMembers1(int groupId, int excludeUserId) {
        String query = "SELECT user_id FROM group_members WHERE group_id = ? AND user_id != ?";
        List<User1> groupMembers = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, excludeUserId);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                // 假设你有一个方法可以通过用户ID获取用户对象
                User1 user = getUserById(userId);
                groupMembers.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupMembers;
    }

    public boolean setGroupOwner(int userId, int groupId) {
        String query = "UPDATE `groups` SET created_by = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, groupId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteChatRecordsByGroupId(int groupId) {
        String query = "DELETE chat_records FROM chat_records INNER JOIN messages ON chat_records.message_id = messages.id WHERE messages.group_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteMessagesByGroupId(int groupId) {
        String query = "DELETE FROM messages WHERE group_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

        public boolean updateGroupInfo(Group1 group) {
            String sql = "UPDATE  `groups` SET name = ?, description = ?, avatar_path = ?, updated_at = NOW() WHERE id = ?";
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, group.getName());
                pstmt.setString(2, group.getDescription());
                pstmt.setString(3, group.getAvatar());
                pstmt.setInt(4, group.getId());
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

    public boolean setGroupAdmin(int groupId, int userId) {
        String query = "UPDATE group_members SET role = 'admin' WHERE group_id = ? AND user_id = ? AND status = 'active'";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, userId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int kickOutGroup(int groupId, int userId, int currentUserId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            String query = "SELECT role FROM group_members WHERE group_id = ? AND user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Check the role of current user
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, currentUserId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String role = resultSet.getString("role");
                if ("creator".equals(role)) {
                    // If the current user is the group creator, he can kick out any member
                    query = "DELETE FROM group_members WHERE group_id = ? AND user_id = ?";
                } else {
                    // If the current user is not the group creator, he cannot kick out creator or admin
                    query = "DELETE FROM group_members WHERE group_id = ? AND user_id = ? AND role != 'creator' AND role != 'admin'";
                }
            }

            // Perform the delete operation
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, userId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int SetMember(int groupId, int userId, int currentUserId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT role FROM group_members WHERE group_id = ? AND user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, currentUserId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");
                if ("creator".equals(role)) {
                    query = "UPDATE group_members SET role = 'member' WHERE group_id = ? AND user_id = ? AND role <> 'creator'";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, groupId);
                    preparedStatement.setInt(2, userId);
                    return preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Integer> getGroupAdmins(int groupId) {
        String query = "SELECT user_id FROM group_members WHERE group_id = ? AND role IN ('creator', 'admin')";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Integer> adminIds = new ArrayList<>();
            while (resultSet.next()) {
                adminIds.add(resultSet.getInt("user_id"));
            }

            return adminIds;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
    public List<ChatMessage1> getGroupChatHistory(int groupId) {
        List<ChatMessage1> chatHistory = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT m.id, m.sender_id, m.group_id, m.content, m.content_type, m.created_at, " +
                    "u.username, u.nickname, u.avatar " +
                    "FROM messages m " +
                    "JOIN users u ON m.sender_id = u.id " +
                    "WHERE m.group_id = ? " +
                    "ORDER BY m.created_at ASC";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, groupId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int senderId = resultSet.getInt("sender_id");
                String content = resultSet.getString("content");
                String contentType = resultSet.getString("content_type");
                ZoneId zoneId = ZoneId.of("UTC");
                LocalDateTime createdAt = resultSet.getTimestamp("created_at").toInstant().atZone(zoneId).toLocalDateTime();

                String senderUsername = resultSet.getString("username");
                String senderAvatar = resultSet.getString("avatar");
                String senderNickname = resultSet.getString("nickname");

                User1 sender = new User1(senderId, senderUsername, senderAvatar, senderNickname);

                ChatMessage1 chatMessage = new ChatMessage1(id, sender, content, false, createdAt);
                chatMessage.setGroupId(groupId);
                chatMessage.setContentType(contentType);

                chatHistory.add(chatMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatHistory;
    }

    public boolean deleteRequestsByGroupId(int groupId) {
        // 使用try-with-resources语句确保PreparedStatement和Connection的正确关闭
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement("DELETE FROM requests WHERE group_id = ?")) {

            // 设置groupId
            pstmt.setInt(1, groupId);

            // 执行删除操作
            int affectedRows = pstmt.executeUpdate();

            // 如果affectedRows大于0，则说明至少有一行被成功更新了
            return affectedRows > 0;

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public boolean addPhrase(UserCommonPhrase1 phrase) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "INSERT INTO user_common_phrases (user_id, phrase) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, phrase.getUserId());
            statement.setString(2, phrase.getPhrase());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editPhrase(UserCommonPhrase1 oldPhrase, UserCommonPhrase1 newPhrase) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "UPDATE user_common_phrases SET phrase = ? WHERE user_id = ? AND phrase = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newPhrase.getPhrase());
            statement.setInt(2, oldPhrase.getUserId());
            statement.setString(3, oldPhrase.getPhrase());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePhrase(UserCommonPhrase1 phrase) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "DELETE FROM user_common_phrases WHERE user_id = ? AND phrase = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, phrase.getUserId());
            statement.setString(2, phrase.getPhrase());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getUserPhrases(int userId) {
        List<String> phrases = new ArrayList<>();

        try (
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = connection.prepareStatement("SELECT phrase FROM user_common_phrases WHERE user_id = ?")) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    phrases.add(rs.getString("phrase"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return phrases;
    }





    /*public int kickOutGroup(int groupId, int userId) {
        String query = "UPDATE group_members SET status = 'removed' WHERE group_id = ? AND user_id = ? AND role != 'creator' AND role != 'admin'";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, userId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }*/

}


