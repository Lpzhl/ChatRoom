package serverTest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.ChatMessage1;
import server.Group1;
import server.User1;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ServerHandlerLongConnection implements Runnable {
    private Socket socket;
    private DatabaseConnection dbConnection;
    static Map<String, PrintWriter> userConnectionsMap = new HashMap<>();
    static Map<Socket, String> socketUserIdMap = new HashMap<>();

    public ServerHandlerLongConnection(Socket socket) {
        this.socket = socket;
        this.dbConnection = new DatabaseConnection();
    }

    @Override
    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;
        boolean exceptionOccurred = false;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String request;
            while ((request = in.readLine()) != null) {

                String[] requestParts1 = request.split(":", 2);
                String[] requestParts = request.split(":");
                System.out.println("长连接收到的：" + requestParts);

                switch (requestParts[0]) {
                    // 处理请求种类
                    case "connect":
                        handleConnect(out, requestParts);
                        break;
                    case "getFriends": // 处理查找好友请求
                        handleGetFriendsList(out, requestParts);
                        break;
                    case "checkRequest":
                        handleCheckRequest(out, requestParts);
                        break;
                    case "sendMessageGroupChat":
                        handleGroupChatMessage(out, requestParts1[1]);
                        break;
                    /*case "sendMessageLongConnection":
                        handleSendMessageLongConnection(out,requestParts);
                        break;*/
                    case "sendMessageLongConnection":
                        handleMessage(out, requestParts1[1]);
                        break;
                    case "quitGroup":
                        handleQuitGroup(out, requestParts);
                        break;
                    case "logout":
                        handleLogout(out, requestParts);
                        break;
                    case "getChatHistory":
                        handleGetChatHistory(out,requestParts);
                        break;
                    case "getGroupInfo":
                        handleGetGroupInfo(out, requestParts);
                        break;
                    case "disbandGroup":
                        handleDisbandGroup(out, requestParts);
                        break;
                    case "getGroupMembers":
                        handleGetGroupMembers(out, requestParts);
                        break;
                    case "setGroupOwner":
                        handleSetGroupOwner(out,requestParts);
                        break;
                    case "getGroupChatHistory":
                        handleGetGroupChatHistory(out,requestParts);
                    default:
                        out.println("error");
                        break;
                }
            }
        } catch (SocketException e) {
            System.out.println("客户端断开连接: " + e.getMessage());
            exceptionOccurred = true;
        } catch (IOException e) {
            e.printStackTrace();
            exceptionOccurred = true;
        } finally {
            // 只有在出现异常时，才关闭流和Socket
            if (exceptionOccurred) {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    socket.close();
                    // 删除连接
                    // 删除连接
                    String userId = getUserIdFromSocket(socket);
                    if (userId != null) {
                        userConnectionsMap.remove(userId);
                        socketUserIdMap.remove(socket);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void handleGetGroupChatHistory(PrintWriter out, String[] response) {
        // 获取群聊ID
        String groupId = response[1];

        // 查询群聊记录
        List<ChatMessage1> groupChatHistory = dbConnection.getGroupChatHistory(Integer.parseInt(groupId));

        // 将查询结果序列化为 JSON 字符串
        Gson gson = new Gson();
        String groupChatHistoryJson = gson.toJson(groupChatHistory);

        // 将查询结果发送回客户端
        out.println("groupChatHistory:" + groupChatHistoryJson);
        System.out.println("groupChatHistory:"+groupChatHistoryJson);
    }

    private void handleSetGroupOwner(PrintWriter out,String[] requestParts) {
        int userId = Integer.parseInt(requestParts[1]);
        int groupId = Integer.parseInt(requestParts[2]);

        boolean isSuccess = dbConnection.setGroupOwner(userId, groupId);
        System.out.println("userId:"+userId+"  group:"+groupId);

        if (isSuccess) {
            // 将原来的群主在group_members表中的role字段改为'member'
           // boolean isOldOwnerUpdated = dbConnection.updateGroupMemberRoleToMember(groupId);
            //直接把群主删了
            boolean isOldOwnerUpdated = dbConnection. removeGroupCreator(groupId);
            // 将新的群主的role字段改为'creator'
            boolean isNewOwnerUpdated = dbConnection.updateGroupMemberRoleToCreator(groupId, userId);

            isSuccess = isOldOwnerUpdated && isNewOwnerUpdated;
        }

        // 将结果发送给客户端
        out.println("setGroupOwner:" + (isSuccess ? "success" : "fail"));
    }


    private void handleDisbandGroup(PrintWriter out, String[] requestParts) {
        // 提取用户ID和群ID
        int userId = Integer.parseInt(requestParts[1]);
        int groupId = Integer.parseInt(requestParts[2]);
        System.out.println("解散群聊："+userId+ "    "+groupId);

        // 删除requests表中所有有关groupId的请求
        boolean areRequestsDeleted = dbConnection.deleteRequestsByGroupId(groupId);


        //在messages表中查找所有有关groupId的消息id，然后在chat_records表中将所有刚刚查找出来的message_id删除
        boolean areChatRecordsDeleted = dbConnection.deleteChatRecordsByGroupId(groupId);

        if (areChatRecordsDeleted) {
            //在chat_records表删除之后，在把messages表中所有有关groupId的消息全部删除
            boolean areMessagesDeleted = dbConnection.deleteMessagesByGroupId(groupId);
        }

        // 调用数据库方法来处理解散群聊
        boolean isMembersRemoved = dbConnection.handleRemoveGroupMembers(groupId);

        System.out.println("解散了吗："+isMembersRemoved);
        boolean isSuccess = false;
        if (isMembersRemoved) {
            //isSuccess = dbConnection.handleDisbandGroup(userId, groupId);

            isSuccess = dbConnection.handleDisbandGroup(groupId);
        }

        System.out.println("是否成功："+isSuccess);
        // 将结果发送给客户端
        out.println("disbandGroup:" + (isSuccess ? "success" : "fail"));
    }



    private void handleGetGroupMembers(PrintWriter out, String[] requestParts) {
        // 提取群ID
        int groupId = Integer.parseInt(requestParts[1]);
        int userId = Integer.parseInt(requestParts[2]); // 需要排除的用户ID

        // 调用数据库方法来获取群成员列表
        List<User1> groupMembers = dbConnection.getGroupMembers1(groupId, userId);

      // 创建Gson对象
        Gson gson = new Gson();

        // 将结果转换为JSON字符串并发送给客户端
        String groupMembersJson = gson.toJson(groupMembers);
        out.println("getGroupMembers:" + groupMembersJson);
    }
    private void handleQuitGroup(PrintWriter out, String[] requestParts) {
        // 提取用户ID和群ID
        int userId = Integer.parseInt(requestParts[1]);
        int groupId = Integer.parseInt(requestParts[2]);

        // 调用数据库方法来处理退出群聊
        boolean isSuccess = dbConnection.handleQuitGroup(userId, groupId);

        // 将结果发送给客户端
        out.println("quitGroup:" + (isSuccess ? "success" : "fail"));
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
        out.println("getGroupInfo1:"+groupInfo);
    }

    private void handleGroupChatMessage(PrintWriter out, String message) {
        // 解析消息并获取发送者、群组和消息内容
        JsonObject receivedObject = new JsonParser().parse(message).getAsJsonObject();
        System.out.println(receivedObject);
        int senderId = receivedObject.get("senderId").getAsInt();
        int groupId = receivedObject.get("groupId").getAsInt();

        // 解析 content_type 和 file_name
        String contentType = receivedObject.get("content_type").getAsString();
        String fileName = null;
        String messageContent;
        if ("image".equals(contentType)) {
            // 如果是图片，将内容从Base64编码转换回原始文件，并将文件保存在某处
            String encodedString = receivedObject.get("content").getAsString();
            byte[] fileContent = Base64.getDecoder().decode(encodedString);
            fileName = receivedObject.get("file_name").getAsString();

            // Save fileContent to a file
            File directory = new File("E:/java/Java练习");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            messageContent = encodedString;  // 保存Base64编码的图片数据，而不是文件路径
        } else if ("file".equals(contentType)) {
            // 如果是文件，将内容从Base64编码转换回原始文件，并将文件保存在某处
            String encodedString = receivedObject.get("content").getAsString();
            byte[] fileContent = Base64.getDecoder().decode(encodedString);
            fileName = receivedObject.get("file_name").getAsString();

            // Save fileContent to a file
            File directory = new File("E:/java/Java练习");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            messageContent = file.getAbsolutePath();
        } else {
            messageContent = receivedObject.get("content").getAsString();
        }

        String receivedTimestamp = receivedObject.get("timestamp").getAsString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime updatedAt = LocalDateTime.parse(receivedTimestamp, formatter);
        System.out.println("发送者：" + senderId + "群组：" + groupId + "消息：" + messageContent);

        // 将消息插入group_messages表
        int messageId = dbConnection.insertGroupMessage(senderId, groupId, messageContent, contentType, fileName);
        if (messageId == -1) {
            System.out.println("插入群组消息失败");
            return;
        }

        // 获取发送者和群组的信息
        User1 sender = dbConnection.getUserById(senderId);
        //Group1 group = dbConnection.getGroupById(groupId);
        System.out.println("发送时间：" + updatedAt);
        // 创建一个GroupChatMessage对象
        ChatMessage1 groupChatMessage;
        groupChatMessage = new ChatMessage1(sender, groupId,messageContent, fileName, contentType, false,updatedAt);

        // 将GroupChatMessage对象转换为JSON格式
        Gson gson = new Gson();
        String groupChatMessageJson = gson.toJson(groupChatMessage);
        System.out.println("发给群组的消息是：" + groupChatMessage.getContent());

        // 获取群组的所有成员,不包括发送者,就是因为这里，真的服了。导致发送群聊消息一直是两份
        List<User1> groupMembers = dbConnection.getGroupMembers(groupId,sender.getId());

        // 将消息转发给所有群组成员
        for (User1 member : groupMembers) {
            System.out.println("目标群组成员的ID：" + member.getId());
            System.out.println("目标群组成员的账号：" + member.getUsername());
            PrintWriter memberConnection = userConnectionsMap.get(member.getUsername());
            if (memberConnection != null) {
                System.out.println("发给群组成员的消息是：" + groupChatMessage.getContent());
                memberConnection.println("messageFromGroup:" + groupChatMessageJson);
                // 将聊天记录插入chat_records表，修改成已读和未读
                boolean success = dbConnection.insertChatRecords(senderId, member.getId(), messageId);
                if (!success) {
                    System.out.println("插入聊天记录失败");
                    return;
                }
                System.out.println("这是：" + groupChatMessageJson);
            } else {
                System.out.println("群组成员未连接");
            }
        }
    }



        private void handleGetChatHistory(PrintWriter out, String[] response) {
        // 创建一个DatabaseConnection对象
        String userId = response[1];
        String friendId = response[2];
        DatabaseConnection dbConnection = new DatabaseConnection();

        // 使用getChatHistory方法获取聊天记录
        List<ChatMessage1> chatHistory = dbConnection.getChatHistory(Integer.parseInt(userId), Integer.parseInt(friendId));

        // 将查询结果序列化为 JSON 字符串
        Gson gson = new Gson();
        String chatHistoryJson = gson.toJson(chatHistory);

        // 将查询结果发送回客户端
        out.println("chatHistory:" + chatHistoryJson);
        System.out.println("chatHistory:"+chatHistoryJson);
    }


    private void handleLogout(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 2) {
            out.println("error:invalid request format");
            return;
        }
        String username = requestParts[1];
        System.out.println("用户已登出：" + username);
        dbConnection.setUserStatus(username, "offline");
        // 从 userConnectionsMap 中移除用户连接
        userConnectionsMap.remove(username);
        // 从 socketUserIdMap 中移除对应的 Socket
        socketUserIdMap.remove(socket);
        out.println("success");
    }
    private void handleMessage(PrintWriter out, String parts1) {
        // 解析消息并获取发送者和消息内容
        JsonObject receivedObject = new JsonParser().parse(parts1).getAsJsonObject();
        System.out.println(receivedObject);
        int senderId = receivedObject.get("senderId").getAsInt();
        int receiverId = receivedObject.get("receiverId").getAsInt();

        // 解析 content_type 和 file_name
        String contentType = receivedObject.get("content_type").getAsString();
        String fileName = null;
        String messageContent;
        if ("image".equals(contentType)) {
            // 如果是图片，将内容从Base64编码转换回原始文件，并将文件保存在某处
            String encodedString = receivedObject.get("content").getAsString();
            byte[] fileContent = Base64.getDecoder().decode(encodedString);
            fileName = receivedObject.get("file_name").getAsString();

            // 保存文件
            File directory = new File("E:/java/Java练习");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            messageContent = encodedString;  // 保存Base64编码的图片数据，而不是文件路径
        } else if ("file".equals(contentType)) {
            // 如果是文件，将内容从Base64编码转换回原始文件，并将文件保存在某处
            String encodedString = receivedObject.get("content").getAsString();
            byte[] fileContent = Base64.getDecoder().decode(encodedString);
            fileName = receivedObject.get("file_name").getAsString();

            // 保存文件
            File directory = new File("E:/java/Java练习/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            messageContent = file.getAbsolutePath();
        } else {
            messageContent = receivedObject.get("content").getAsString();
        }

            String receivedTimestamp = receivedObject.get("timestamp").getAsString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime updatedAt = LocalDateTime.parse(receivedTimestamp, formatter);
            System.out.println("发送者：" + senderId + "接收者：" + receiverId + "消息：" + messageContent);

            // 将消息插入messages表
            int messageId = dbConnection.insertMessage(senderId, receiverId, messageContent, contentType, fileName);
            if (messageId == -1) {
                System.out.println("插入消息失败");
                return;
            }

            // 将聊天记录插入chat_records表，修改成已读和未读
            boolean success = dbConnection.insertChatRecords(senderId, receiverId, messageId);
            if (!success) {
                System.out.println("插入聊天记录失败");
                return;
            }

            // 获取发送者和接收者的用户信息
            User1 sender = dbConnection.getUserById(senderId);
            User1 receiver = dbConnection.getUserById(receiverId);
            System.out.println("发送时间：" + updatedAt);

            // 创建一个ChatMessage对象
            ChatMessage1 chatMessage;
            chatMessage = new ChatMessage1(sender, messageContent, fileName, contentType, false, updatedAt);

            // 将ChatMessage对象转换为JSON格式
            Gson gson = new Gson();
            String chatMessageJson = gson.toJson(chatMessage);
            System.out.println("发给用户的消息是：" + chatMessage.getContent());

            // 将消息转发给目标用户
            System.out.println("目标客户的ID：" + receiverId);
            System.out.println("目标客户的账号：" + receiver.getUsername());
            PrintWriter targetUserConnection = userConnectionsMap.get(receiver.getUsername());
            if (targetUserConnection != null) {
                System.out.println("发给用户的消息是1：" + chatMessage.getContent());
                targetUserConnection.println("messageFrom:" + chatMessageJson);
                System.out.println("这是：" + chatMessageJson);
            } else {
                System.out.println("目标用户未连接");
            }
        }
    private void handleCheckRequest(PrintWriter out, String[] requestParts1) {
        if (requestParts1.length != 2) {
            out.println("错误");
            return;
        }
        String username = requestParts1[1];
        System.out.println("检查请求：" + username);

        // 使用hasNewRequestForUser方法检查是否有新的请求
        DatabaseConnection dbConnection = new DatabaseConnection();
        boolean hasNewRequest = dbConnection.hasNewRequestForUser(username);
        System.out.println("数据库变化了吗：" + hasNewRequest);

        if (hasNewRequest) {
            out.println("newRequest");
        } else {
            out.println("noNewRequest666");
        }
    }

    private void handleConnect(PrintWriter out, String[] requestParts) {
        System.out.println("连接了吗");
        if (requestParts.length != 2) {
            out.println("error:invalid request format2");
            return;
        }
        String username = requestParts[1];
        System.out.println("用户已连接：" + username);
        // 添加连接到 userConnectionsMap
        userConnectionsMap.put(username, out);

        // 获取用户ID
        User1 user = dbConnection.getUserByUsername(username);
        int userId = user.getId();

        // 获取用户的好友列表
        List<User1> friends = dbConnection.getFriendsList(userId);

        // 为每个好友调用handleCheckForNewMessages
       /*for (User1 friend : friends) {
            handleCheckForNewMessages(out, new String[]{"checkForNewMessages", String.valueOf(userId), String.valueOf(friend.getId())});
        }
*/
        // 将用户 ID 与 Socket 关联
        socketUserIdMap.put(socket, username);
        out.println("success");
    }



   /* private void handleCheckForNewMessages(PrintWriter out, String[] requestParts) {
        if (requestParts.length == 3) {

            int userId = Integer.parseInt(requestParts[1]);
            int activeReceiverId = Integer.parseInt(requestParts[2]);
            System.out.println("发送者：" + userId + " 接收者:" + activeReceiverId);
            DatabaseConnection dbConnection = new DatabaseConnection();

            List<ChatMessage1> unreadMessages = dbConnection.getUnreadMessages(userId, activeReceiverId); //目的是获取当前登录用户从活动接收者那里收到的所有未读消息。

            System.out.println("有对象吗1");
            // 将未读消息发送回客户端
            for (ChatMessage1 message : unreadMessages) {
                // 创建一个 ChatMessage 对象
                // 创建一个 ChatMessage 对象
                ChatMessage1 chatMessage = new ChatMessage1(message.getId(), message.getSender(), message.getContent(), false);


                // 将 ChatMessage 对象转换为 JSON 格式
                Gson gson = new Gson();
                String chatMessageJson = gson.toJson(chatMessage);
                System.out.println("消息："+chatMessageJson);
                // 获取目标用户的用户名
               // String receiverUsername = dbConnection.getUserById(activeReceiverId).getUsername();
                // 获取发送者用户的用户名
                String senderUsername = dbConnection.getUserById(userId).getUsername();


                // 将消息发送给目标用户
                PrintWriter targetUserConnection = userConnectionsMap.get(senderUsername);

                if (targetUserConnection != null) {
                    targetUserConnection.println("messageFrom:" + chatMessageJson);
                    System.out.println("离线发送的："+chatMessageJson);
                } else {
                    System.out.println("目标用户未连接");
                    // 更新消息状态为已读
                    System.out.println("信息读取状态："+message.getId());
                    dbConnection.updateMessageStatus(message.getId(), true);
                }
            }
        } else {
            System.out.println("长度为："+requestParts.length);
            //out.println("error");
        }
    }*/




    private String getUserIdFromSocket(Socket socket) {
        String userId = socketUserIdMap.get(socket);
        System.out.println("断开连接的用户ID："+userId);
        if (userId != null) {
            return userId;
        } else {
            return null;
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
        System.out.println("用户ID：" + userId);
        // 通过用户ID从数据库中获取好友列表
        List<User1> friends = dbConnection.getFriendsByUserId(userId);
        // 创建一个Gson对象，用于将User对象转化为JSON字符串
        Gson gson = new Gson();
        // 遍历好友列表
        for (User1 friend : friends) {
            // 将好友对象序列化为JSON字符串
            String friendJson = gson.toJson(friend);
            System.out.println("序列化后好友信息：" + friendJson);
            // 将好友的JSON字符串发送给客户端
            out.println(friendJson);
        }
        // 在发送完好友列表之后，发送一个 "end_of_friends" 字符串
       out.println("end_of_friends");
    }


}


/*
    @Override
    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;
        boolean exceptionOccurred = false;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String request;
            while ((request = in.readLine()) != null) {
                String[] requestParts = request.split(":", 2);
                System.out.println("长连接收到的：" + requestParts);

                // Define a mapping between request commands and method names using reflection
                Map<String, String> commandToMethodMapping = new HashMap<>();
                commandToMethodMapping.put("connect", "handleConnect");
                commandToMethodMapping.put("getFriends", "handleGetFriendsList");
                commandToMethodMapping.put("checkRequest", "handleCheckRequest");
                commandToMethodMapping.put("sendMessageGroupChat", "handleGroupChatMessage");
                commandToMethodMapping.put("sendMessageLongConnection", "handleMessage");
                commandToMethodMapping.put("quitGroup", "handleQuitGroup");
                commandToMethodMapping.put("logout", "handleLogout");
                commandToMethodMapping.put("getChatHistory", "handleGetChatHistory");
                commandToMethodMapping.put("getGroupInfo", "handleGetGroupInfo");
                commandToMethodMapping.put("disbandGroup", "handleDisbandGroup");
                commandToMethodMapping.put("getGroupMembers", "handleGetGroupMembers");
                commandToMethodMapping.put("setGroupOwner", "handleSetGroupOwner");
                commandToMethodMapping.put("getGroupChatHistory", "handleGetGroupChatHistory");

                String command = requestParts[0];
                String methodToInvoke = commandToMethodMapping.get(command);
                if (methodToInvoke != null) {
                    // Use reflection to invoke the appropriate method
                    Method method = this.getClass().getMethod(methodToInvoke, PrintWriter.class, String.class);
                    method.invoke(this, out, requestParts[1]);
                } else {
                    out.println("error");
                }
            }
        } catch (SocketException e) {
            System.out.println("客户端断开连接: " + e.getMessage());
            exceptionOccurred = true;
        } catch (IOException e) {
            e.printStackTrace();
            exceptionOccurred = true;
        } catch (Exception e) {
            e.printStackTrace();
            exceptionOccurred = true;
        } finally {
            if (exceptionOccurred) {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    socket.close();
                    String userId = getUserIdFromSocket(socket);
                    if (userId != null) {
                        userConnectionsMap.remove(userId);
                        socketUserIdMap.remove(socket);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }*/

/* try {
         // 构造要调用的方法名称
         String methodName = "handle" + capitalizeFirstLetter(requestParts[0]);
         Method method;

         // 根据请求部分的数量决定调用哪个方法
         if (requestParts.length > 1) {
         method = this.getClass().getDeclaredMethod(methodName, PrintWriter.class, String.class);
        method.invoke(this, out, requestParts[1]);
        } else {
        method = this.getClass().getDeclaredMethod(methodName, PrintWriter.class, String[].class);
        method.invoke(this, out, new Object[]{requestParts});
        }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
        out.println("error");
        }
        }
        } catch (SocketException e) {
        System.out.println("客户端断开连接: " + e.getMessage());
        exceptionOccurred = true;
        } catch (IOException e) {
        e.printStackTrace();
        exceptionOccurred = true;
        } finally {
        if (exceptionOccurred) {
        closeResources(out, in, socket);
        }
        }
        }*/
