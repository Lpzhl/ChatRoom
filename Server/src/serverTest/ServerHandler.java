package serverTest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import server.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

// ServerHandler类实现了Runnable接口，允许在新线程中运行
public class ServerHandler implements Runnable {
    private Socket socket; // 与客户端通信的套接字
    private DatabaseConnection dbConnection;// 数据库连接对象
    private final Gson gson = new Gson(); // 用于将 JSON 字符串转换为 Java 对象

    public  ServerHandler(Socket socket) {
        this.socket = socket;// 保存客户端Socket到类成员变量中
        dbConnection = new DatabaseConnection();// 创建一个新的DatabaseConnection对象
    }
    // run方法是Runnable接口的实现，它在新线程中执行
    @Override
    public void run() {


        if (socket.isClosed()) {
            System.out.println("Socket is closed before entering ServerHandler");
        } else {
            System.out.println("Socket is open before entering ServerHandler");
        }
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            // 创建一个PrintWriter对象，用于向客户端发送响应
            out = new PrintWriter(socket.getOutputStream(), true);
            // 创建一个BufferedReader对象，用于接收客户端发送的请求
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 读取客户端发送的请求
            String request = in.readLine();//readLine 用于读取一行字符串（遇到换行符 \n 或 \r\n 结束），并返回该字符串。
            System.out.println("客户端请求信息："+request);

            if (request == null) {
                System.out.println("Client sent an empty request or disconnected");
                return;
            }
            // 将请求分割为请求的各个部分
            String[] requestParts1=request.split(":",2);
            String[] requestParts = request.split(":");

            //System.out.println(requestParts[0]+"你好"+requestParts[1]);
            System.out.println(requestParts[0]);
            //if(requestParts[0])

            // 根据请求的第一个部分，判断是哪种请求并执行相应操作
            switch (requestParts[0]) {
                case "register"://处理注册账号的的请求
                    handleRegister(out, requestParts);
                    break;
                case "login"://处理登入的请求
                    handleLogin(out, requestParts);
                    break;
                case "getUserPhrases":
                    handleGetUserPhrases(out, Integer.parseInt(requestParts[1]));
                    break;
                case "findDup": // 处理注册生成的ID是否重复
                    handlefinDup(out,requestParts[1]);
                    break;
                case "findDup1": // 处理注册生成的ID是否重复
                    handlefinDup1(out,requestParts[1]);
                    break;
                case "logout":// 处理用户退出登入请求
                    System.out.println("退出用户："+requestParts[1]);
                    handleLogout(requestParts[1]);
                    break;
                case "email_verification"://发送验证码请求
                    handleEmailVerification(out, requestParts);
                    break;
                case "addPhrase":
                    handleAddPhrase(out, gson.fromJson(requestParts1[1], UserCommonPhrase1.class));
                    break;
                case "editPhrase":
                    handleEditPhrase(out, gson.fromJson(requestParts1[1], EditPhraseRequest1.class));
                    break;
                case "deletePhrase":
                    handleDeletePhrase(out, gson.fromJson(requestParts1[1], UserCommonPhrase1.class));
                    break;
                case "emailLogin": // 处理邮箱登录的请求
                    handleEmailLogin(out, requestParts);
                    break;
                case"resetPassword"://处理找回密码的请求
                    handleResetPassword(out,requestParts);
                    break;
                case "update": // 处理更新用户信息请求
                    handleUpdate(out, requestParts1);
                    break;
                case "getUserInfo": // 处理查询用户信息请求
                    handleGetUserInfo(out, requestParts);
                    break;
                case "FindPassword":  // 处理找回密码请求
                    handleChangePassword(out,requestParts);
                    break;
                case  "sendFriendRequest": // 处理获取好友列表请求
                    handleSendFriendRequest(out,requestParts1);
                    break;
                case "getRequestList": // 处理获取请求列表请求
                    handleGetRequestList(out, requestParts[1]);
                    break;
                case "acceptFriendRequest":// 处理接收好友申请请求
                    handleAcceptFriendRequest(out, requestParts1[1]);
                    break;
                case "deleteFriend":// 处理删除好友请求
                    handleDeleteFriendRequest(out, requestParts1[1]);
                    break;
                case "rejectFriendRequest":// 处理拒绝好友请求
                    handleRejectFriendRequest(out, requestParts1[1]);
                    break;
                case "acceptGroupRequest":
                    handleAcceptGroupRequest(out, requestParts1[1]);
                    break;
                case "rejectGroupRequest":
                    handleRejectGroupRequest(out, requestParts1[1]);
                    break;
                case "sendMessage":
                    handleSendMessage(out, requestParts);
                    break;
                /*case "addFriend":
                    handleAddFriend(out, requestParts1);
                    break;*/
                case "findUser":// 处理查看好友列表请求
                    handleFindUser(out, requestParts);
                    break;
                case "findGroup"://
                    handleFindGroup(out, requestParts);
                    break;
                case "getFriends":// 处理查找好友请求
                    handleGetFriendsList(out, requestParts);
                    break;
                case "createGroup":// 处理创建群聊的请求
                    handleCreateGroup(out,requestParts1[1]);
                    break;
                case "getGroups":// 处理查看群聊列表请求
                    System.out.println("查看群聊："+requestParts1[1]);
                    handleGetGroupsRequest(out, requestParts1[1]);
                    break;
                case "getGroupInfo":
                    handleGetGroupInfo(out, requestParts);
                    break;
                /*case "checkForNewMessages": // 处理检查新消息请求
                    handleCheckForNewMessages(out, requestParts);
                    break;*/
                case "getUserById":
                    handleGetUserById(out,requestParts);
                    break;
                case "sendGroupRequest":
                    // 加入群聊请求
                    handleJoinGroupRequest(out,requestParts1);
                    break;
                case "deleteRequest":
                    handleDeleteRequest(out, requestParts);
                    break;
                case "update1":
                    handleUpdateGroupInfo(out, requestParts1[1]);
                    break;
                case "setAdministrator":
                    handleSetAdministrator(out,requestParts);
                    break;
                case "KickOutGroup":
                    handleKickOutGroup(out,requestParts);
                    break;
                case "setMember":
                    handleSetMember(out,requestParts);
                    break;
                case "getGroupAdmins":
                    handleGetGroupAdmins(out,requestParts);
                    break;
                default:
                    out.println("error");
                    break;
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭输出流
            if (out != null) {
                System.out.println(socket+"断开连接");
                out.close();
            }
            // 关闭输入流
            if (in != null) {
                try {
                    System.out.println(socket+"断开连接");
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                System.out.println(socket+"断开连接");
                // 关闭套接字
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleGetUserPhrases(PrintWriter out, int userId) {
        List<String> phrases = dbConnection.getUserPhrases(userId);

        // Serialize the list to a JSON string and send it to the client
        String response = gson.toJson(phrases);
        out.println(response);
    }
    private void handleAddPhrase(PrintWriter out, UserCommonPhrase1 phrase) {
        boolean success = dbConnection.addPhrase(phrase);
        if (success) {
            out.println("success");
        } else {
            out.println("error");
        }
    }

    private void handleEditPhrase(PrintWriter out, EditPhraseRequest1 request) {
        boolean success = dbConnection.editPhrase(request.getOldPhrase(), request.getNewPhrase());
        if (success) {
            out.println("success");
        } else {
            out.println("error");
        }
    }

    private void handleDeletePhrase(PrintWriter out, UserCommonPhrase1 phrase) {
        boolean success = dbConnection.deletePhrase(phrase);
        if (success) {
            out.println("success");
        } else {
            out.println("error");
        }
    }
    private void handleGetGroupAdmins(PrintWriter out, String[] requestParts) {
        int groupId = Integer.parseInt(requestParts[1]);

        List<Integer> adminIds = dbConnection.getGroupAdmins(groupId);

        // 向客户端发送管理员的 ID，每个 ID 用逗号分隔
        out.println(String.join(",", adminIds.stream().map(String::valueOf).collect(Collectors.toList())));
    }


    private void handleSetMember(PrintWriter out, String[] requestParts) {
        int userId = Integer.parseInt(requestParts[2]);//被执行人
        int groupId = Integer.parseInt(requestParts[1]);//群号
        int currentUserId = Integer.parseInt(requestParts[3]);//发起者

        System.out.println("userId："+userId+"    currentUserId："+currentUserId);
        System.out.println(userId == currentUserId );
        if(userId == currentUserId ){
            out.println("6");
            return;
        }
        //可以先判断一下currentUserId是否是群主，如果是群主则可以设置任何人为普通成员，如果是管理员的话，则不可以设置任何人
        int rowsAffected = dbConnection.SetMember(groupId, userId,currentUserId);

        if (rowsAffected > 0) {
            out.println("success");
        } else {
            out.println("failure");
        }
    }

    private void handleKickOutGroup(PrintWriter out, String[] requestParts) {
        int userId = Integer.parseInt(requestParts[2]);//被执行人
        int groupId = Integer.parseInt(requestParts[1]);//群号
        int currentUserId = Integer.parseInt(requestParts[3]);//发起者

        //可以先判断一下currentUserId是否是群主，如果是群主则可以踢出任何人
        System.out.println("userId："+userId+"    currentUserId："+currentUserId);
        System.out.println(userId == currentUserId );
        if(userId == currentUserId ){
            out.println("6");
            return;
        }
        int rowsAffected = dbConnection.kickOutGroup(groupId, userId,currentUserId);

        if (rowsAffected > 0) {
            out.println("success");
        } else {
            out.println("failure");
        }
    }

    private void handleSetAdministrator(PrintWriter out, String[] requestParts) {
        int userId = Integer.parseInt(requestParts[2]);
        int groupId = Integer.parseInt(requestParts[1]);
        int currentUserId = Integer.parseInt(requestParts[3]);//发起者

        //可以先判断一下currentUserId是否是群主，如果是群主则可以踢出任何人
        System.out.println("userId："+userId+"    currentUserId："+currentUserId);
        System.out.println(userId == currentUserId );
        if(userId == currentUserId ){
            out.println("6");
            return;
        }
        boolean success = dbConnection.setGroupAdmin(groupId, userId);

        if (success) {
            out.println("success");
        } else {
            out.println("failure");
        }
    }

    private void handleUpdateGroupInfo(PrintWriter out, String requestPart) {
        // 将请求的第二部分解析为 Group 对象
        Group1 group = new Gson().fromJson(requestPart, Group1.class);

        // 更新数据库中的群聊资料
        boolean success = dbConnection.updateGroupInfo(group);
        // 向客户端发送结果
        if (success) {
            out.println("success");
        } else {
            out.println("failure");
        }
    }

    private void handleGetGroupInfo(PrintWriter out, String[] requestParts) {
        // 提取群ID
        int groupId = Integer.parseInt(requestParts[1]);

        System.out.println("群ID"+groupId);
        // 查询群信息
        Group1 group = dbConnection.getGroupInfo(groupId);

        // 将群信息转换为JSON字符串
        String groupInfo = new Gson().toJson(group);
        System.out.println("群资料是："+groupInfo);

        // 将群信息发送给客户端
        out.println(groupInfo);
    }

    private void handleAcceptGroupRequest(PrintWriter out, String requestPart) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        HashMap<String, String> map = gson.fromJson(requestPart, type);
        long userId = Long.parseLong(map.get("userId"));
        int groupId = Integer.parseInt(map.get("groupId"));

        // 检查用户是否已经是群组的成员
        if (dbConnection.isUserInGroup(userId, groupId)) {
            out.println("failure: user is already in the group");
        } else {
            boolean result = dbConnection.acceptGroupRequest(userId, groupId);
            out.println(result ? "success" : "failure");
        }
    }


    private void handleRejectGroupRequest(PrintWriter out, String requestPart) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        HashMap<String, String> map = gson.fromJson(requestPart, type);
        long userId = Long.parseLong(map.get("userId"));
        int groupId = Integer.parseInt(map.get("groupId"));
        boolean result = dbConnection.rejectGroupRequest(userId, groupId);
        out.println(result ? "success" : "failure");
    }

    private void handleDeleteRequest(PrintWriter out, String[] respond) {
        System.out.println("删除请求：" + respond[1] + " -> " + respond[2]);
        boolean result =false;
        System.out.println("长度："+respond.length);
        if(respond.length==5) {
            result = dbConnection.deleteRequest(respond[1], respond[2], respond[3], Integer.valueOf(respond[4]));
        }else{
            result = dbConnection.deleteRequest(respond[1], respond[2], respond[3], null);
        }
        out.println(result ? "success" : "failure");
    }

    private void handleJoinGroupRequest(PrintWriter out,String[] requestJson ) throws SQLException {
        System.out.println("进来了吗："+requestJson[1]);
        // 将JSON字符串解析成map对象
        Gson gson = new Gson();
        Map<String, String> requestMap = gson.fromJson(requestJson[1], new TypeToken<Map<String, String>>(){}.getType());
        // 获取请求参数
        System.out.println("requestMap:"+requestMap);
        String username = requestMap.get("username");
        System.out.println("username："+username);
        String name_id = requestMap.get("groupname");
        System.out.println("name_id："+name_id);
        // 查询是否存在该用户和该群聊
            // 判断用户是否已经加入了该群聊
        if(username==null||name_id==null){
            out.println("error");
            System.out.println("1");
            return;

        }
        if(dbConnection.isMemberOfGroup(username, name_id)) {
            // 用户已经加入了该群聊
            System.out.println("2");
            out.println("failure1");
            return;
        }
        boolean isSuccess = dbConnection.sendGroupRequest(username,name_id);
        if(isSuccess){
            System.out.println("3");
            out.println("success");
        }else {
            System.out.println("4");
            out.println("error");
        }
    }
    private void handleCheckForNewMessages(PrintWriter out, String[] requestParts) {
        if (requestParts.length == 3) {

            int userId = Integer.parseInt(requestParts[1]);
            int activeReceiverId = Integer.parseInt(requestParts[2]);
            System.out.println("发送者："+userId+ " 接收者:"+activeReceiverId);
            DatabaseConnection dbConnection = new DatabaseConnection();

            List<ChatMessage1> unreadMessages = dbConnection.getUnreadMessages(userId,activeReceiverId);//目的是获取当前登录用户从活动接收者那里收到的所有未读消息。

            System.out.println("有对象吗1");
            // 将未读消息发送回客户端
            for (ChatMessage1 message : unreadMessages) {
                //System.out.println("发送ID："+message.getSender().getId()+" 接收者："+message.getReceiver().getId()+" 发送的内容："+message.getContent());
                //out.println(message.getSender().getId() + ":" + message.getReceiver().getId() + ":" + message.getContent() + ":" + message.getCreatedAt());
                out.println(message.getSender().getId() + ":" + message.getReceiver().getId() + ":" + message.getContent());
                System.out.println("有对象吗");
            }
        } else {
            out.println("error");
        }
    }

    private void handleGetUserById(PrintWriter out, String[] requestParts) {
        if (requestParts.length == 2) {
            int userId = Integer.parseInt(requestParts[1]);

            DatabaseConnection dbConnection = new DatabaseConnection();
            User1 user = dbConnection.getUserById1(userId);

            if (user != null) {
                // 将用户信息发送回客户端，格式为：id:username
                out.println(user.getId() + ":" + user.getUsername());
            } else {
                out.println("error");
            }
        } else {
            out.println("error");
        }
    }

    private void handleSendMessage(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 4) {
            out.println("error:invalid request format");
            return;
        }

        int senderId = Integer.parseInt(requestParts[1]);
        int receiverId = Integer.parseInt(requestParts[2]);
        String messageContent = requestParts[3];

        // 插入消息到messages表
        int messageId = dbConnection.insertMessage(senderId, receiverId, messageContent, "text");

        // 插入聊天记录到chat_records表
        if (messageId != -1) {
            dbConnection.insertChatRecord(senderId, messageId, "read");
            dbConnection.insertChatRecord(receiverId, messageId, "unread");
            out.println("success");
        } else {
            out.println("error");
        }
    }
    // 处理雪花算法群账号的唯一性
    private void handlefinDup1(PrintWriter out, String requestPart) {
        String username = requestPart;
        boolean register = dbConnection.GroupUserExists(username);
        if(register){
            out.println("false");
        }else {
            out.println("success");
        }
    }

    //处理雪花算法账号的唯一性
    private void handlefinDup(PrintWriter out, String requestPart) {
        String username = requestPart;
        boolean register = dbConnection.userExists(username);
        if(register){
            out.println("false");
        }else {
            out.println("success");
        }
    }

    private void handleGetGroupsRequest(PrintWriter out, String request) {
        // 从请求中获取用户ID
        System.out.println("这是啥:"+request);

        // 将请求字符串转换为整数
        int userId;
        try {
            userId = Integer.parseInt(request);
        } catch (NumberFormatException e) {
            System.err.println("请求的格式不正确，应该是一个整数的用户ID，但收到的是: " + request);
            return;
        }

        System.out.println("查看群聊列表用户id："+userId);

        // 从数据库获取用户的群聊列表
        List<Group1> groupList = dbConnection.getGroupsByUserId(userId);

        // 创建一个Gson对象，用于将Group对象转换为Json字符串
        Gson gson = new Gson();

        // 遍历群聊列表，将每个Group对象转换为Json字符串，并发送给客户端
        for (Group1 group : groupList) {
            String groupJson = gson.toJson(group);
            System.out.println("序列化后："+group);
            out.println(groupJson);
        }

        // 发送一个空行，表示群聊列表已经发送完毕
        out.println();
    }


    private void handleCreateGroup(PrintWriter out, String request) {
        System.out.println("服务端接收的群聊信息："+request);
        Gson gson = new Gson();
        Group1 group = gson.fromJson(request, Group1.class);
        System.out.println("反序列化后："+group);
        boolean result = dbConnection.createGroup(group,group.getCreatedBy());
        if (result) {
            out.println("success");
        } else {
            out.println("failure");
        }
    }

    //处理删除好友的请求
    private void handleDeleteFriendRequest(PrintWriter out, String request) {
        Gson gson = new Gson();
        Map<String, String> requestMap = gson.fromJson(request, new TypeToken<Map<String, String>>() {}.getType());
        String username1 = requestMap.get("username1");
        String username2 = requestMap.get("username2");
        if (username1 == null || username2 == null) {
            out.println("error:missing username");
            return;
        }
        boolean success = dbConnection.deleteFriend(username1, username2);
        if (success) {
            out.println("success");
        } else {
            out.println("failure");
        }
    }
    private synchronized void handleLogin(PrintWriter out, String[] requestParts) {
        // 如果请求参数的数量不等于3，返回错误信息
        if (requestParts.length != 3) {
            out.println("error");
            return;
        }

        // 获取用户名和密码
        String username = requestParts[1];
        String password = requestParts[2];

        // 检查用户名和密码是否正确
        boolean loginSuccess = dbConnection.checkLogin(username, password);

        if (loginSuccess) {

            // 判断该用户是否已经在线
            String status = dbConnection.getUserStatus(username);
            if (status.equals("online")) {
                out.println("error:该用户已经登录");
                System.out.println("该用户状态："+status);
                return;
            }
            // 设置用户状态为在线
            dbConnection.setUserStatus(username, "online");
            // 如果用户名和密码正确，从数据库中获取用户信息
            User1 user1 = dbConnection.getUserByUsername(username);

            // 使用Gson将用户信息转换为JSON格式
            Gson gson = new Gson();
            String userJson = gson.toJson(user1);
           System.out.println("用户在线信息："+userJson);
            // 将成功的响应和用户信息发送给客户端
            out.println("success:" + userJson);
        } else {
            // 如果用户名和密码不正确，发送失败的响应给客户端
            out.println("error:用户名或密码错误");
        }
    }


    private synchronized void handleLogout(String username) {
        // 设置用户状态为离线
        dbConnection.setUserStatus(username, "offline");
        // 从 userConnectionsMap 中移除用户连接
        ServerHandlerLongConnection.userConnectionsMap.remove(username);
        // 从 socketUserIdMap 中移除对应的 Socket
        ServerHandlerLongConnection.socketUserIdMap.remove(socket);
        System.out.println("用户：" + username + "退出登录");
    }



    private void handleGetRequestList(PrintWriter out, String username) {
        System.out.println("用户的信息列表：" + username);
        List<Request1> requestList = dbConnection.getRequestList(username);
        Gson gson = new Gson();
        String response = gson.toJson(requestList);
        System.out.println("信息列表序列化后：" + response);
        out.println(response);
    }
    private void handleAcceptFriendRequest(PrintWriter out, String request) {
        Gson gson = new Gson();
        Map<String, String> requestMap = gson.fromJson(request, new TypeToken<Map<String, String>>() {}.getType());
        String username1 = requestMap.get("username1");
        String username2 = requestMap.get("username2");
        if (username1 == null || username2 == null) {
            out.println("error:missing username");
            return;
        }
        Boolean success1 = dbConnection.updateFriendRequestStatus(username1,username2, "accepted");
        System.out.println("success1:"+success1);
        boolean success = dbConnection.addFriend(username1, username2);
        if(success&&success1){
            out.println("success");
        }else{
            out.println("failure");
        }
    }

    private void handleRejectFriendRequest(PrintWriter out, String request) {
        Gson gson = new Gson();
        Map<String, String> requestMap = gson.fromJson(request, new TypeToken<Map<String, String>>() {}.getType());
        boolean success = dbConnection.updateFriendRequestStatus(requestMap.get("username1"), requestMap.get("username2"), "rejected");
        out.println(success ? "success" : "failure");
    }
    // 处理发送好友请求
    private void handleSendFriendRequest(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 2) {
            out.println("error:invalid request format");
            return;
        }
        Gson gson = new Gson();
        Map<String, String> userInfo;
        try {
            userInfo = gson.fromJson(requestParts[1], new TypeToken<Map<String, String>>(){}.getType());
            System.out.println("反序列化后: "+userInfo);
        } catch (JsonSyntaxException e) {
            out.println("error:invalid json");
            return;
        }
        String senderUsername = userInfo.get("username1");
        String receiverUsername = userInfo.get("username2");

        if (senderUsername == null || receiverUsername == null) {
            out.println("error:missing username");
            return;
        }
        if (!dbConnection.canAddFriend(senderUsername, receiverUsername)) {
            out.println("error:cannot send friend request");
            return;
        }
        boolean isSuccess = dbConnection.sendFriendRequest(senderUsername, receiverUsername);
        if (isSuccess) {
            out.println("success");
        } else {
            out.println("error:failed to send friend request");
        }
    }
        // 该方法用于处理获取好友列表的请求
    private void handleGetFriendsList(PrintWriter out, String[] requestParts) {
        // 如果请求的格式不正确（即请求的部分不等于2），返回错误信息
        if (requestParts.length != 2) {
            out.println("error:invalid request format");
            return;
        }
        // 将请求的第二部分转化为用户ID
        int userId = Integer.parseInt(requestParts[1]);
        System.out.println("用户ID："+userId);
        // 通过用户ID从数据库中获取好友列表
        List<User1> friends = dbConnection.getFriendsByUserId(userId);
        // 遍历好友列表
        for (User1 friend : friends) {
            // 创建一个Gson对象，用于将User对象转化为JSON字符串
            Gson gson = new Gson();
            // 将好友对象序列化为JSON字符串
            String friendJson = gson.toJson(friend);
            System.out.println("序列化后好友信息："+friendJson);
            // 将好友的JSON字符串发送给客户端
            out.println(friendJson);
        }
    }
    //处理查找好友
    private void handleFindUser(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 2) {
            out.println("error:invalid request format");
            return;
        }
        // 从请求中获取用户名
        String username = requestParts[1];
        // 查找用户
        User1 user = dbConnection.findUser(username);
        if (user != null) {
            // 将用户信息转换为JSON对象
            Gson gson = new Gson();
            String userJson = gson.toJson(user);
            out.println("success:" + userJson);
        } else {
            out.println("error:user not found");
        }
    }

    //处理添加群聊
    private void handleFindGroup(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 2) {
            out.println("error:invalid request format");
            return;
        }
        // 从请求中获取用户名
        String name_id = requestParts[1];
        // 查找用户
        Group1 group1 = dbConnection.findGroup1(name_id);
        if (group1 != null) {
            // 将用户信息转换为JSON对象
            Gson gson = new Gson();
            String userJson = gson.toJson(group1);
            out.println("success:" + userJson);
        } else {
            out.println("error:user not found");
        }
    }


    //处理修改密码
    private void handleChangePassword(PrintWriter out, String[] requestParts) {
        if(requestParts.length != 3) {
            out.println("false");
            return;
        }
        String username = requestParts[1];
        String password = requestParts[2];
        boolean loginSuccess = dbConnection.checkLogin(username, password);
        if (loginSuccess) {
            out.println("success:");
        } else {
            out.println("false:");
        }
    }

    private void handleGetUserInfo(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 2) {
            out.println("error");
            return;
        }
        String username = requestParts[1];
        User1 user = dbConnection.getUserInfo(username);
        System.out.println("请求用户名看信息："+user);
        if (user == null) {
            out.println("error");
        } else {
            Gson gson = new Gson();
            String response = gson.toJson(user);
            out.println(response);
            System.out.println("给你啦："+response);
        }
    }
    //处理编辑资料
    private void handleUpdate(PrintWriter out, String[] requestParts) {
        String userJson = requestParts[1];
        System.out.println(userJson);
        User1 user;
        try {
            Gson gson = new Gson();
            user = gson.fromJson(userJson, User1.class);
            System.out.println(user);
        } catch (JsonSyntaxException e) {
            out.println("error:invalid user json");
            return;
        }

        String username = user.getUsername();
        System.out.println("请求者： "+username);
        String newAvatarPath = user.getAvatar();
        /*if(newAvatarPath==null) {
            newAvatarPath = "file:/image/默认头像.png";
        }*/
        System.out.print("  头像："+newAvatarPath);
        String newNickname = user.getNickname();
        System.out.print("  昵称: "+newNickname);
        String newGender = user.getGender();
        if("未知".equals(newGender)){
            newGender = String.valueOf('O');
        }
        System.out.print(" 性别: "+newGender);
        LocalDate newBirthday = user.getBirthday();
        System.out.print("  生日: "+newBirthday);
        String newSignature = user.getSignature();
        System.out.print("  个性签名："+newSignature);

        if (newBirthday != null) {
            try {
                newBirthday = LocalDate.parse(newBirthday.toString());
            } catch (DateTimeParseException e) {
                out.println("error:invalid date");
                return;
            }
        }
        dbConnection.updateUser(username, newAvatarPath, newNickname, newGender, newBirthday, newSignature);
        out.println("success");
        System.out.println("更新数据成功");
    }


    // 处理注册请求
    private void handleRegister(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 5) {
            out.println("error");
            return;
        }
        String username= requestParts[1];
        String password= requestParts[2];
        String email= requestParts[3];
        String nickname= requestParts[4];
        System.out.println("注册信息："+"邮箱号："+email+" "+"用户名："+nickname+" "+"账号："+username+" 密码："+password);
        String avatar = "/image/默认头像.png";
        String signature = "这个人很懒什么都没有了留下~";


        // 如果用户名已存在，返回"duplicate"，否则注册用户并返回"success"
        if (dbConnection.emailExists(email)) {
            out.println("duplicate");
        } else {
            dbConnection.registerUser(username, password,email,nickname,avatar,signature);
            out.println("success");
        }
    }

    //处理发送验证码请求
    private void handleEmailVerification(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 2) {
            out.println("error");
            return;
        }

        String email = requestParts[1];
        String generatedCode = EmailVca.generateRandomCode();//得到 6 位数验证码
        EmailVca emailVca = new EmailVca(email, generatedCode);//发送验证码

        //创建一个 FutureTask 对象 emailTask，并将 emailVca 对象作为参数传入。
        // FutureTask 是 Java 中的一个类，用于封装一个 callable 对象，并支持获取其执行结果、取消任务等操作。
        FutureTask<Void> emailTask = new FutureTask<>(emailVca);
        /*
        创建一个新的线程 emailThread来执行emailTask，并将 emailTask 作为构造方法的参数传入。
        调用 setDaemon(true) 方法将该线程设置为守护线程，以便在主线程结束后自动销毁。
         */
        Thread emailThread = new Thread(emailTask);
        emailThread.setDaemon(true);
        emailThread.start();

        // 等待线程执行完成，以便在发生异常时处理
        try {
            emailThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 检查是否发生异常
        try {
            /*
            首先调用 emailTask.get() 方法获取 emailVca.call() 方法的执行结果。
            如果邮件发送成功，则将返回值 generatedCode 输出；否则，打印错误信息并输出 "fail"。
             */
            emailTask.get();
            out.println(generatedCode);
        } catch (ExecutionException e) {
            /*
            e.getCause().printStackTrace() 表示获取 e 异常的原因（也就是导致该异常产生的异常，可能是一个嵌套异常）并打印其堆栈信息。
            getCause() 是 Java 中 Throwable 类中的一个方法，用于获取当前 Throwable 对象的原因，即导致当前异常发生的原因、上级异常等。如果当前异常没有原因，则返回 null 值。
             */
            e.getCause().printStackTrace();
            out.println("fail");
        } catch (InterruptedException e) {
            e.printStackTrace();
            out.println("fail");
        }
    }


    // 处理邮箱登录请求的方法
    private void handleEmailLogin(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 2) {
            out.println("error");
            System.out.println("请求格式错误");
            return;
        }
        String email = requestParts[1];
        // 如果邮箱和验证码匹配，返回"success"，否则返回"fail"
        if (dbConnection.emailExists(email)) {
            User1 user1 = dbConnection.getUserByEmail(email);
            System.out.println("电子邮箱："+email);
            String username = user1.getUsername();
            System.out.println("账号："+username);
            String status = dbConnection.getUserStatus(username);
            System.out.println("用户状态："+status);
            if (status.equals("online")) {
                out.println("error:该用户已经登录");
                System.out.println("该用户状态："+status);
                return;
            }
            dbConnection.setUserStatus(username, "online");
            User1 user2 = dbConnection.getUserByEmail(email);
            System.out.println("邮箱登录："+user2);
            //更新在线状态
            //序列化
            Gson gson = new Gson();
            String userJson = gson.toJson(user2);
            System.out.println("序列化后: "+userJson);
            out.println("success:"+userJson);
            System.out.println("登录成功");
        } else {
            out.println("fail");
            System.out.println("电子邮件不存在,没有绑定账号");
        }
    }

    private void handleResetPassword(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 4) {
            out.println("error");
            System.out.println("请求格式错误!");
            return;
        }

        String username = requestParts[1];
        String newPassword = requestParts[2];
        String email = requestParts[3];

        if (dbConnection.userExists(username) && dbConnection.isUsernameAndEmailMatched(username, email)) {
            if (dbConnection.updateUserPassword(username, newPassword)) {
                out.println("success");
                System.out.println("用户" + username + "修改密码成功!");
            } else {
                out.println("failure");
                System.out.println("用户" + username + "修改密码失败!");
            }
        } else {
            out.println("user_not_found");
            System.out.println("该" + username + "用户未注册或邮箱不匹配！");
        }
    }


}




