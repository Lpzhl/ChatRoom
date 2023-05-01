package serverTest;

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
                    System.out.println("添加失败，无该用户！！！");
                    // 至少一个用户名不存在
                    return false;
                }
            }
            rs.close();
            pstmt.close();

            // 检查是否已经是好友
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
                    System.out.println("添加失败你们已经是好友了！！！");
                    // 已经是好友
                    return false;
                }
            }
            rs.close();
            pstmt.close();
            // 以上检查都通过，添加好友
            String insertSql = "INSERT INTO friends (user_id, friend_id, created_at, updated_at) " +
                    "SELECT u1.id, u2.id, NOW(), NOW() FROM users u1, users u2 " +
                    "WHERE u1.username = ? AND u2.username = ?";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, username1);
            pstmt.setString(2, username2);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
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
    public void registerUser(String username, String password, String email, String nickname) {
        String query = "INSERT INTO users (username, password, email, nickname) VALUES (?, ?, ?, ?)";//问号的顺序是：(1)username, (2)password, (3)email, (4)nickname。
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // 创建一个用于执行预编译 SQL 查询的 PreparedStatement 对象。预编译的查询可以防止 SQL 注入攻击，并提高查询性能。
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);// 将第一个问号替换为 username，
            statement.setString(2, md5(password)); //MD5加密    将第二个问号替换为加密后的 password，
            statement.setString(3, email);// 将第三个问好替换成 email
            statement.setString(4, nickname);  //将第四个问号 替换成 nickname
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
            throw new RuntimeException("MD5 algorithm not found.", e);//如果在获取 MD5 算法实例过程中出现任何异常（如未找到 MD5 算法），捕获异常并抛出一个运行时异常
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

}

/*
如果 updateUserPassword 方法没有处理这个异常，那么这个异常将继续向上抛给调用 updateUserPassword 方法的代码。
如果异常没有被任何方法捕获和处理，最终它将抛给 JVM，导致程序终止运行并输出异常信息。
 */
