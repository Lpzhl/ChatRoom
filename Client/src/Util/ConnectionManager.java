package Util;

import client.ChatMessage;
import client.Group;
import client.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import controller.ChatRoomController;
import controller.GroupInformation;
import controller.Transfer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ConnectionManager {
    public interface MessageListener {
        void onMessageReceived(ChatMessage message);
        void onChatHistoryReceived(List<ChatMessage> chatHistory);
    }

    private Thread messageListeningThread;

    private static ConnectionManager instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageListener messageListener;
    private int currentChatFriendId; // 新增变量
    private ConnectionManager() {
        try {
            socket = new Socket("127.0.0.1", 6001);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 修改此方法以接收 currentChatFriendId 参数
    public void setMessageListener(MessageListener messageListener, int currentChatFriendId) {
        this.messageListener = messageListener;
        this.currentChatFriendId = currentChatFriendId;
        System.out.println("In setMessageListener, currentChatFriendId: " + this.currentChatFriendId);
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void connect(String username) {
        new Thread(() -> {
            try {
                out.println("connect:" + username);
                String response = in.readLine();
                System.out.println("服务器响应：" + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void shutdown() {
        // 停止消息监听线程
        stopMessageListening();

        // 关闭 I/O 资源
        closeConnection();
    }
    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void closeConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopMessageListening() {
        if (messageListeningThread != null) {
            messageListeningThread.interrupt();
        }
    }
    public void startMessageListening() {
        messageListeningThread = new Thread(() -> {
            while (true) {
                try {
                    String message = in.readLine();
                    System.out.println("接收到的信息是：" + message);
                    if (message != null) {
                        String[] parts = message.split(":", 2);
                        String responseType = parts[0];
                        //String responseData = parts[1];

                        switch (responseType) {
                            case "getFriends":
                                handleGetFriendsResponse(parts);
                                break;
                            case "messageFrom":
                                handleMessageFromResponse(parts);
                                break;
                            case "getGroupInfo1":
                                handleGetGroupInfoResponse(parts);
                                break;
                            case "messageFromGroup": // 处理来自群聊的消息
                                handleMessageFromGroupResponse(parts);
                                break;
                            case "newRequest":
                                handleCheckRequestResponse(parts);
                                break;
                            case "chatHistory":
                                handleChatHistoryResponse(parts);
                                break;
                            case "quitGroup":
                                handleQuitGroupResponse(parts);
                                break;
                            case "disbandGroup":
                                handleDisbandGroupResponse(parts);
                                break;
                            case "getGroupMembers":
                                handleGetGroupMembersResponse(parts);
                                break;
                            case "setGroupOwner":
                                handleSetGroupOwner(parts);
                                break;
                            case "groupChatHistory":
                                handleGroupChatHistoryResponse(parts);
                                break;
                            default:
                                System.out.println("未知响应类型: " + responseType);
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    disconnect();
                    break; // 结束循环
                }
            }
        });
        messageListeningThread.start();
    }
    private void handleGroupChatHistoryResponse(String[] responseData) {
        // 检查 groupMessageListener 是否为 null
        if (messageListener != null) {
            // 使用 Gson 将 JSON 字符串转换为 ChatMessage1 列表
            Gson gson = new Gson();
            Type groupChatHistoryType = new TypeToken<List<ChatMessage>>() {}.getType();
            List<ChatMessage> groupChatHistory = gson.fromJson(responseData[1], groupChatHistoryType);

            // 在 JavaFX 应用程序线程中调用 onGroupChatHistoryReceived 方法
            Platform.runLater(() -> messageListener.onChatHistoryReceived(groupChatHistory));
        }
    }




    private void handleChatHistoryResponse(String[] responseData) {
        // 检查 messageListener 是否为 null
        if (messageListener != null) {
            // 使用 Gson 将 JSON 字符串转换为 ChatMessage 列表
            Gson gson = new Gson();
            Type chatHistoryType = new TypeToken<List<ChatMessage>>() {}.getType();
            List<ChatMessage> chatHistory = gson.fromJson(responseData[1], chatHistoryType);

            // 在 JavaFX 应用程序线程中调用 onChatHistoryReceived 方法
            Platform.runLater(() -> messageListener.onChatHistoryReceived(chatHistory));
        }
    }

    private void handleSetGroupOwner(String[] parts) {
        String result = parts[1];
        if ("success".equals(result)) {
            //设置成功

        } else {

        }
    }

    private void handleDisbandGroupResponse(String[] parts) {
        String result = parts[1];
        if ("success".equals(result)) {
            // 解散群聊成功，执行相应的操作
        } else {
            // 解散群聊失败，显示错误消息
        }
    }
    private void handleGetGroupMembersResponse(String[] parts) {
        // 创建Gson对象
        Gson gson = new Gson();

        try {
            // 解析接收到的JSON字符串
            List<User> groupMembers = gson.fromJson(parts[1], new TypeToken<List<User>>(){}.getType());
            Transfer groupInformationController = ChatRoomController.getGroupInformationController1();
            groupInformationController.setPeople1(groupMembers);
            // 使用groupMembers更新选择新群主的界面
        } catch (JsonSyntaxException e) {
            // 打印错误信息或者通知用户解析失败
            e.printStackTrace();
        }
        // 使用groupMembers更新选择新群主的界面
    }
    private void handleQuitGroupResponse(String[] parts) {
        String status = parts[1];

        if (status.equals("success")) {
            /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("成功");
            alert.setHeaderText(null);
            alert.setContentText("成功退出群聊");
            alert.showAndWait();*/
        } else {
            // 显示错误消息
            System.out.println("退出群聊失败");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("失败");
            alert.setHeaderText(null);
            alert.setContentText("退出群聊失败");
            alert.showAndWait();
        }
    }

    private void handleGetGroupInfoResponse(String[] responseData) {
        // responseData[1] 应该包含服务器返回的群组信息的 JSON 字符串
        String groupInfoJson = responseData[1];

        // 将 JSON 字符串转换回 Group 对象
        Group group = new Gson().fromJson(groupInfoJson, Group.class);
        // 获取对控制器的引用
        GroupInformation groupInformationController = ChatRoomController.getGroupInformationController();
        // 在UI线程中更新界面
        Platform.runLater(() -> {
            // 让我们假设你有一个对GroupInformation控制器的引用，它被称为groupInformationController
            groupInformationController.setNumber(group.getUsername());
            groupInformationController.setNickname(group.getName());
            groupInformationController.setGroupInformation(group.getDescription());
            groupInformationController.setCreationTime(String.valueOf(group.getCreatedAt()));
            // 添加用户到 ListView 中
            groupInformationController.setPeople(group.getMembers());
        });
    }


    private void handleMessageFromGroupResponse(String[] responseData) {
        // 处理收到的群聊消息响应的逻辑
        if (messageListener != null) {
            Gson gson = new Gson();
            ChatMessage chatMessage = gson.fromJson(responseData[1], ChatMessage.class);
            System.out.println("发送的是群聊消息");
            // 获取发送者和内容
            User sender = chatMessage.getSender();
            String content = chatMessage.getContent();

            System.out.println("发送者ID：" + sender.getId());
            System.out.println("发送者用户名：" + sender.getUsername());
            System.out.println("消息内容：" + content);
            System.out.println("发送时间："+chatMessage.getUpdatedAt());

            Platform.runLater(() -> messageListener.onMessageReceived(chatMessage));
        }
    }

    private void handleMessageFromResponse(String[] responseData) {
        // 处理收到的消息响应的逻辑
        if (messageListener != null) {
            Gson gson = new Gson();
            ChatMessage chatMessage = gson.fromJson(responseData[1], ChatMessage.class);
            //System.out.println("时间1：" + chatMessage.getUpdatedAt());
            System.out.println("发送的是私聊消息");
            // 获取发送者和内容
            User sender = chatMessage.getSender();
            String content = chatMessage.getContent();

            System.out.println("发送者ID：" + sender.getId());
            System.out.println("发送者用户名：" + sender.getUsername());
            System.out.println("消息内容：" + content);
            System.out.println("发送时间："+chatMessage.getUpdatedAt());

            Platform.runLater(() -> messageListener.onMessageReceived(chatMessage));
        }
    }

    private void handleGetFriendsResponse(String[] responseData) {
        // 处理获取好友列表响应的逻辑
    }
    ChatRoomController controller = new ChatRoomController();
    private void handleCheckRequestResponse(String[] responseData) {
        Platform.runLater(() -> {
            if ("newRequest".equals(responseData[0])) {
                System.out.println("改了吗");
                /// controller.updtaed();
            } else {
                // controller.updatad();
                System.out.println("不知道");
            }
        });
    }

    public void disconnect() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}