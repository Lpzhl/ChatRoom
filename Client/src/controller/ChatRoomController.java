package controller;

import Util.ConnectionManager;
import client.ChatMessage;
import client.ChatRecord;
import client.Group;
import client.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.bcel.internal.generic.NEW;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatRoomController {

    @FXML
    private Label PeopleName;

    @FXML
    private Circle RequestPrompt;

    @FXML
    private TextField searchField;

    @FXML
    private Circle Message2;

    @FXML
    private ToggleButton friendsTabButton;

    @FXML
    private ListView<Group> groupChatsListView;

    @FXML
    private Button addButton;

    @FXML
    private Label GroupChat;

    @FXML
    private Label FriendChat;

    @FXML
    private ListView<HBox> messagesListView;
    @FXML
    private ListView<HBox> messagesListView1;

    @FXML
    private ToggleButton message;

    @FXML
    private ToggleButton friendsButton;

    @FXML
    private Button sendButton;

    @FXML
    private ToggleButton groupChatsTabButton;
    @FXML
    private TextArea messageInput;

    @FXML
    private Button exitButton;
    @FXML
    private ImageView Message;

    @FXML
    private ImageView Goodfriend;

    private long receiverId;

    @FXML
    private ImageView HomeScreenAvatar;

    //@FXML
    //private ListView<ChatRecord> ChatRecord1;
    @FXML
    private ListView<ChatMessage> ChatRecord1;


    @FXML
    private HBox searchAndAddBox;
    @FXML
    private ToggleButton RequestisLt;

    @FXML
    private User activeReceiver;
    @FXML
    private ToggleButton HeadPicture;

    @FXML
    private HBox friendsAndGroupChatsButtonsBox;

    @FXML
    private ToggleButton pinToggleButton;

    private Stage primaryStage;

    /*
    这行代码声明了一个ListView<User>类型的私有变量friendsListView。
    ListView是JavaFX中用于显示列表数据的一种控件。在尖括号中的User是泛型参数，表示这个ListView中的每一项都是一个User对象。
     */
    @FXML
    private ListView<User> friendsListView;

    public List<Stage> childStages = new ArrayList<>();

    public List<Stage> getChildStages() {
        return childStages;
    }
    /*
    这行代码创建了一个ObservableList<User>类型的私有变量friendList，并初始化为一个空的ObservableArrayList。
    ObservableList是JavaFX中用于表示可观察列表的接口。这种列表可以被监听，当列表中的数据发生变化时，所有的监听器都会收到通知。
    在这个例子中，friendList是用来存储User对象的。
    当你向friendList中添加或删除User对象时，任何监听这个列表的控件（例如，你的friendsListView）都会自动更新，以反映列表中的新内容。
     */
    private ObservableList<User> friendList = FXCollections.observableArrayList();
    private User currentUser;
    private User currentUser1;
    public void setCurrentUser(User user) {
        currentUser = user;
        currentUser1 = user;
        user.setMainController(this);
    }

    // 用于存储群聊列表的ObservableList
    private ObservableList<Group> groupList = FXCollections.observableArrayList();
    private ObservableList<ChatMessage> chatRecordList = FXCollections.observableArrayList();
/*
    ，首先通过 setItems 方法将 friendsListView 中的数据设置为 friendList，然后通过 setCellFactory 方法设置单元格工厂。
    当ListView需要一个新的单元格来显示一个User对象时，这个工厂会创建一个新的ListCell对象。
    这个ListCell对象会显示User对象的头像和昵称。如果ListCell没有User对象，就不显示任何东西。
     */

    @FXML
    public void togglePin(ActionEvent event) {
        primaryStage.setAlwaysOnTop(pinToggleButton.isSelected());
    }

    //登入的时候把界面传进来
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    // 在这个方法中初始化你的控制器
        @FXML
        private void initialize() {
            // 将朋友列表数据设置到ListView中
            friendsListView.setItems(friendList);
            friendsListView.setVisible(false);
            groupChatsListView.setVisible(false);
            messagesListView.setVisible(false);
            FriendChat.setTextFill(Color.GRAY);// 好友按钮变色
            GroupChat.setTextFill(Color.GRAY);// 群聊按钮变回去
            RequestPrompt.setFill(Color.WHITE);
            RequestPrompt.setOpacity(1.0);
           /* ChatRecord1.setItems(chatRecordList);

            ChatRecord1.setCellFactory(new Callback<ListView<ChatMessage>, ListCell<ChatMessage>>() {
                @Override
                public ListCell<ChatMessage> call(ListView<ChatMessage> chatMessageListView) {
                    return new ListCell<ChatMessage>() {
                        private ImageView imageView = new ImageView();
                        private Label senderName = new Label();
                        private Label timestamp = new Label();
                        private Label content = new Label();

                        private HBox header = new HBox(10, senderName, imageView);
                        private VBox messageBox = new VBox(header, content);
                        private BorderPane borderPane = new BorderPane();*/

                        /*{
                            header.setAlignment(Pos.TOP_RIGHT);
                            messageBox.setAlignment(Pos.TOP_RIGHT);
                            messageBox.setSpacing(5);
                            borderPane.setPadding(new Insets(10));
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                            // 设置背景颜色
                            BackgroundFill backgroundFill = new BackgroundFill(Color.web("#CCCCCC"), new CornerRadii(5), Insets.EMPTY);
                            Background background = new Background(backgroundFill);
                            borderPane.setBackground(background);
                        }*/



                       /* @Override
                        protected void updateItem(ChatMessage chatMessage, boolean empty) {
                            super.updateItem(chatMessage, empty);

                            if (chatMessage != null) {
                                imageView.setImage(new Image(chatMessage.getSender().getAvatar()));
                                imageView.setFitHeight(30);
                                imageView.setFitWidth(30);

                                senderName.setText(chatMessage.getSender().getNickname());
                                //timestamp.setText(chatMessage.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                                content.setText(chatMessage.getContent());

                                header.getChildren().setAll(senderName, imageView);
                                messageBox.getChildren().setAll(header, content);
                                borderPane.setCenter(messageBox);

                                setGraphic(borderPane);
                            } else {
                                setText(null);
                                setGraphic(null);
                            }
                        }
                    };
                }
            });*/
            ChatRecord1.setItems(chatRecordList);

            ChatRecord1.setCellFactory(new Callback<ListView<ChatMessage>, ListCell<ChatMessage>>() {
                @Override
                public ListCell<ChatMessage> call(ListView<ChatMessage> chatMessageListView) {
                    return new ListCell<ChatMessage>() {
                        private ImageView imageView = new ImageView();
                        private Label senderName = new Label();
                        private Label timestamp = new Label();
                        private Label content = new Label();
                        private Region spacer = new Region();

                        private HBox header = new HBox(10, spacer, senderName, timestamp);
                        private VBox contentBox = new VBox(content);
                        private VBox messageBox = new VBox(header, contentBox);

                        {
                            header.setAlignment(Pos.TOP_RIGHT); //头像用户名右对齐
                            //HBox.setHgrow(spacer, Priority.ALWAYS); // 使得 spacer 可以扩展以填充空间
                            HBox.setHgrow(spacer, Priority.NEVER);
                            messageBox.setAlignment(Pos.TOP_RIGHT);
                            messageBox.setSpacing(5);
                            setPadding(new Insets(10));
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                            // 设置背景颜色和圆角
                            BackgroundFill backgroundFill = new BackgroundFill(Color.web("#CCCCCC"), new CornerRadii(5), Insets.EMPTY);
                            Background background = new Background(backgroundFill);
                            contentBox.setBackground(background);
                            contentBox.setPadding(new Insets(5));

                            // 设置自动换行
                            content.setWrapText(true);
                            content.setMaxWidth(500);
                            content.setAlignment(Pos.TOP_RIGHT);
                        }

                        @Override
                        protected void updateItem(ChatMessage chatMessage, boolean empty) {
                            super.updateItem(chatMessage, empty);

                            if (chatMessage != null) {
                                imageView.setImage(new Image(chatMessage.getSender().getAvatar()));
                                imageView.setFitHeight(30);
                                imageView.setFitWidth(30);
                                senderName.setText(chatMessage.getSender().getNickname());
                                //timestamp.setText(chatMessage.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                                content.setText(chatMessage.getContent());
                                System.out.println("发送者："+chatMessage.getSender());
                                if (chatMessage.getSender().equals(currentUser)) {
                                    // 如果是当前用户发送的消息，则将气泡的对齐方式设置为右对齐
                                    contentBox.setAlignment(Pos.TOP_LEFT);
                                    contentBox.setPadding(new Insets(5, 5, 5, 0));
                                    header.getChildren().setAll(spacer, timestamp, senderName, imageView);
                                } else {
                                    // 如果是其他用户发送的消息，则将气泡的对齐方式设置为左对齐
                                    contentBox.setAlignment(Pos.TOP_RIGHT);
                                    contentBox.setPadding(new Insets(5, 0, 5, 5));
                                    header.getChildren().setAll(imageView, senderName, timestamp, spacer);
                                }

                                double contentWidth = content.prefWidth(-1);
                                content.setPrefWidth(contentWidth);
                                contentBox.setPrefWidth(contentWidth + 10); // +10 用于添加气泡的边距


                                header.getChildren().setAll(spacer, imageView, senderName, timestamp);
                                messageBox.getChildren().setAll(header, contentBox);
                                setGraphic(messageBox);
                            } else {
                                setText(null);
                                setGraphic(null);
                            }
                        }
                    };
                }
            });
            sendButton.setOnAction(event -> {
                if (currentUser == null) {
                    // 如果没有选择聊天对象，不发送消息
                    return;
                }
                String messageText = messageInput.getText().trim();
                if (!messageText.isEmpty()) {
                    // 发送请求到服务器
                    String request = "sendMessage:" + currentUser.getId() + ":" + receiverId + ":" + messageText;
                    sendRequestToServer(request);

                    // 将消息添加到聊天记录列表中
                    addMessageToChatRecord(currentUser, messageText, true);
                    messageInput.clear();
                }
            });
            // 设置列表的单元格工厂
            friendsListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
                @Override
                public ListCell<User> call(ListView<User> userListView) {
                    return new ListCell<User>() {
                        private ImageView imageView = new ImageView();

                        // 创建ContextMenu
                        ContextMenu contextMenu = new ContextMenu();

                        // 创建MenuItem
                        MenuItem sendMessageItem = new MenuItem("发送消息");
                        MenuItem viewProfileItem = new MenuItem("查看用户资料");
                        MenuItem deleteFriendItem = new MenuItem("删除好友");

                        {
                            // 为每个MenuItem设置一个事件处理器，当点击菜单项时会被触发
                            sendMessageItem.setOnAction(event -> {
                                User user = getItem();
                                // 在这里添加发送消息的代码
                                System.out.println("发送消息按钮被点击：" + user.getUsername());
                                System.out.println("发送消息按钮被点击：" + user.getNickname());
                                // 在右侧显示聊天界面和被选中的好友的用户名
                                PeopleName.setText(user.getNickname());

                                System.out.println("发送消息按钮被点击：");
                                // 设置接收者的ID
                                receiverId = user.getId();
                                //activeReceiver = user;
                                // 清除之前的聊天记录并加载当前接收者的聊天记录
                                if (activeReceiver != user) {
                                    chatRecordList.clear();
                                    activeReceiver = user;
                                    // 在这里添加加载当前接收者聊天记录的代码
                                }
                            });

                            viewProfileItem.setOnAction(event -> {
                                User user = getItem();
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Personalinformation3.fxml"));
                                Parent root = null;
                                try {
                                    root = fxmlLoader.load();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                // 获取新创建的控制器实例
                                UserInfoController1 userInfoController = fxmlLoader.getController();
                                // 设置用户信息
                                userInfoController.setUserInfo(user);
                                Scene scene = new Scene(root);
                                Stage stage = new Stage();
                                stage.setTitle("个人信息");
                                stage.setScene(scene);
                                stage.show();
                                // 在这里添加查看用户资料的代码
                                System.out.println("查看用户资料按钮被点击：");
                            });
                            deleteFriendItem.setOnAction(event -> {
                                User user = getItem();
                                // 在这里添加删除好友的代码
                                System.out.println("删除好友钮被点击：");

                                // 创建一个Socket连接到本地的6000端口
                                try (Socket socket = new Socket("127.0.0.1", 6000);
                                     // 创建一个PrintWriter，用于向Socket发送数据
                                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                     // 创建一个BufferedReader，用于从Socket接收数据
                                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                                    // 向服务端发送一个请求，请求删除当前用户和指定用户的好友关系
                                    Map<String, String> requestMap = new HashMap<>();
                                    requestMap.put("username1", currentUser.getUsername());
                                    requestMap.put("username2", user.getUsername());
                                    Gson gson = new Gson();
                                    String requestJson = gson.toJson(requestMap);
                                    out.println("deleteFriend:" + requestJson);

                                    // 从服务端接收响应
                                    String response = in.readLine();

                                    if ("success".equals(response)) {
                                        // 如果删除成功，从好友列表中删除该用户
                                        friendList.remove(user);
                                    } else {
                                        // 如果删除失败，打印错误信息
                                        System.out.println("删除好友失败");
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            // 将MenuItem添加到ContextMenu
                            contextMenu.getItems().addAll(sendMessageItem, viewProfileItem, deleteFriendItem);
                        }

                        @Override
                        protected void updateItem(User user, boolean empty) {
                            super.updateItem(user, empty);

                            if (user != null) {
                                imageView.setImage(new Image(user.getAvatar()));
                                imageView.setFitHeight(50);
                                imageView.setFitWidth(50);
                                setText(user.getNickname());
                                setGraphic(imageView);

                                // 当单元格不为空时，设置ContextMenu
                                setContextMenu(contextMenu);
                            } else {
                                setText(null);
                                setGraphic(null);

                                // 当单元格为空时，移除ContextMenu
                                setContextMenu(null);
                            }
                        }
                    };
                }
            });
            // 在initialize方法中，将群聊列表数据设置到ListView中
            groupChatsListView.setItems(groupList);
            // 设置列表的单元格工厂
            groupChatsListView.setCellFactory(new Callback<ListView<Group>, ListCell<Group>>() {
                @Override
                public ListCell<Group> call(ListView<Group> groupListView) {
                    return new ListCell<Group>() {
                        private ImageView imageView = new ImageView();

                        @Override
                        protected void updateItem(Group group, boolean empty) {
                            super.updateItem(group, empty);

                            if (group != null) {
                                imageView.setImage(new Image(group.getAvatar()));
                                imageView.setFitHeight(50);
                                imageView.setFitWidth(50);
                                setText(group.getName());
                                setGraphic(imageView);

                                // Create the context menu with four menu items
                                ContextMenu contextMenu = new ContextMenu();
                                MenuItem sendMessageItem = new MenuItem("发信息");
                                MenuItem quitGroupItem = new MenuItem("退出群");
                                MenuItem disbandGroupItem = new MenuItem("解散群");
                                MenuItem manageGroupItem = new MenuItem("管理群");

                                contextMenu.getItems().addAll(sendMessageItem, quitGroupItem, disbandGroupItem, manageGroupItem);

                                sendMessageItem.setOnAction(event -> {
                                    // Handle sending message
                                    System.out.println("发送信息按钮被点击");
                                });

                                quitGroupItem.setOnAction(event -> {
                                    // Handle quitting the group
                                    System.out.println("退出群按钮被点击");
                                });

                                disbandGroupItem.setOnAction(event -> {
                                    // Handle disbanding the group
                                    System.out.println("解散群按钮被点击");
                                });

                                manageGroupItem.setOnAction(event -> {
                                    // Handle managing the group
                                    System.out.println("管理群按钮被点击");
                                });

                                // Set the context menu to the cell
                                setContextMenu(contextMenu);
                            } else {
                                setText(null);
                                setGraphic(null);
                                setContextMenu(null);
                            }
                        }
                    };
                }
            });
        }

    private void sendRequestToServer(String request) {
        try (Socket socket = new Socket("127.0.0.1", 6000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 向服务器发送请求
            out.println(request);

            // 从服务器接收响应
            String response = in.readLine();
            System.out.println("服务器响应：" + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addMessageToChatRecord(User sender, String content, boolean isCurrentUser) {
        // 如果消息来自当前聊天对象，将消息添加到聊天记录列表中
        ChatMessage chatMessage = new ChatMessage(sender, content, isCurrentUser);
        chatRecordList.add(chatMessage);
    }

    @FXML
    void searchField1(ActionEvent event) {

    }

    @FXML
   void FriendsTab(ActionEvent event) {
        FriendChat.setTextFill(Color.BLACK);// 好友按钮变色
        GroupChat.setTextFill(Color.GRAY);// 群聊按钮变回去
        // 设置好友列表控件为可见
        friendsListView.setVisible(true);
        // 在控制台打印一条消息，表示这个方法被调用了
        System.out.println("查看好友列表被点击");

        // 创建一个Socket连接到本地的6000端口
        try (Socket socket = new Socket("127.0.0.1", 6000);
             // 创建一个PrintWriter，用于向Socket发送数据
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             // 创建一个BufferedReader，用于从Socket接收数据
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 向服务端发送一个请求，请求获取当前用户的好友列表
            out.println("getFriends:" + currentUser.getId());

            // 从服务端接收响应
            String friendJson = in.readLine();
            // 创建一个新的列表，用于存储从服务端接收到的好友数据
            List<User> friendList = new ArrayList<>();
            // 创建一个Gson对象，用于将Json数据转换为User对象
            Gson gson = new Gson();
            // 当服务端发送的数据不为空时，将数据转换为User对象，并添加到列表中
            while (friendJson != null){
                // 打印服务端发送的数据
                System.out.println("客户端收到的："+friendJson);
                // 将Json数据转换为User对象
                User friend = gson.fromJson(friendJson, User.class);
                // 将User对象添加到列表中
                friendList.add(friend);
                // 继续读取下一行数据
                friendJson = in.readLine();
            }
            // 更新UI，将新的好友列表显示出来
            updateFriendsList(friendList);

            // 如果在处理Socket连接时发生了异常，打印异常信息
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /*void FriendsTab(ActionEvent event) {
        // 设置好友列表控件为可见
        friendsListView.setVisible(true);
        // 在控制台打印一条消息，表示这个方法被调用了
        System.out.println("查看好友列表被点击");

        // 创建一个新的 Task 对象
        Task<List<User>> getFriendsTask = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                // 将与服务器通信的代码移到 call 方法中
                List<User> friendList = new ArrayList<>();
                try (Socket socket = new Socket("127.0.0.1", 6001);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    out.println("getFriends:" + currentUser.getId());

                    String friendJson = in.readLine();
                    Gson gson = new Gson();
                    while (friendJson != null&&!friendJson.equals("end_of_friends")) {
                        System.out.println("客户端收到的：" + friendJson);
                        User friend = gson.fromJson(friendJson, User.class);
                        friendList.add(friend);
                        friendJson = in.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return friendList;
            }
        };

        // 当任务完成时，更新 UI
        getFriendsTask.setOnSucceeded(event1 -> updateFriendsList(getFriendsTask.getValue()));

        // 启动任务
        new Thread(getFriendsTask).start();
    }*/

    // 更新好友列表的方法
    public void updateFriendsList(List<User> newFriends) {
        // 清空旧的好友列表
        friendList.clear();
        // 将新的好友列表添加到ObservableList中
        friendList.addAll(newFriends);
        // 更新ListView，这会将新的好友列表显示出来
        // friendListView.refresh();
    }

    @FXML
    public void GroupsTab(ActionEvent actionEvent) {
        System.out.println("查看群聊列表被点击");
        groupChatsListView.setVisible(true);
        friendsListView.setVisible(false);
        messagesListView.setVisible(false);
        messagesListView1.setVisible(false);
        FriendChat.setTextFill(Color.GRAY);// 好友按钮变色
        GroupChat.setTextFill(Color.BLACK);// 群聊按钮变回去

        // 创建一个Socket连接到本地的6000端口
        try (Socket socket = new Socket("127.0.0.1", 6000);
             // 创建一个PrintWriter，用于向Socket发送数据
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             // 创建一个BufferedReader，用于从Socket接收数据
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("当前用户ID："+currentUser.getId());
            // 向服务端发送一个请求，请求获取当前用户的群聊列表
            out.println("getGroups:" + currentUser.getId());

            // 从服务端接收响应
            String groupJson = in.readLine();
            // 创建一个新的列表，用于存储从服务端接收到的群聊数据
            List<Group> groupList = new ArrayList<>();
            // 创建一个Gson对象，用于将Json数据转换为Group对象
            Gson gson = new Gson();
            // 当服务端发送的数据不为空时，将数据转换为Group对象，并添加到列表中
            while (groupJson != null) {
                // 打印服务端发送的数据
                System.out.println("客户端收到的："+groupJson);
                // 将Json数据转换为Group对象
                Group group = gson.fromJson(groupJson, Group.class);
                // 将Group对象添加到列表中
                groupList.add(group);
                // 继续读取下一行数据
                groupJson = in.readLine();
            }

            // 更新UI，将新的群聊列表显示出来
            updateGroupsList(groupList);

            // 如果在处理Socket连接时发生了异常，打印异常信息
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 更新群聊列表的方法
    public void updateGroupsList(List<Group> newGroups) {
        // 清空旧的群聊列表
        groupList.clear();
        // 将新的群聊列表添加到ObservableList中
        groupList.addAll(newGroups);
        // 更新ListView，这会将新的群聊列表显示出来
        groupChatsListView.refresh();
    }


    @FXML
    void friendsButton1(ActionEvent event) {
        FriendChat.setTextFill(Color.GRAY);// 好友按钮变色
        GroupChat.setTextFill(Color.GRAY);// 群聊按钮变回去
        // 获取当前场景
        Scene scene2 = ((Node) event.getSource()).getScene();
        ImageView imageView2 = (ImageView) scene2.lookup("#Requestpicture");

        if (imageView2 != null) {
            // 修改图片颜色
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(1.0);
            imageView2.setEffect(colorAdjust);

            Blend blend = new Blend();
            blend.setMode(BlendMode.SRC_ATOP);
            blend.setTopInput(new ColorInput(0, 0, imageView2.getFitWidth(), imageView2.getFitHeight(), Color.BLACK));
            blend.setBottomInput(colorAdjust);

            imageView2.setEffect(blend);
        }
        Scene scene = ((Node) event.getSource()).getScene();
        ImageView imageView = (ImageView) scene.lookup("#Goodfriend");

        if (imageView != null) {
            // 修改图片颜色
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(1.0);
            imageView.setEffect(colorAdjust);

            Blend blend = new Blend();
            blend.setMode(BlendMode.SRC_ATOP);
            blend.setTopInput(new ColorInput(0, 0, imageView.getFitWidth(), imageView.getFitHeight(), Color.GREEN));
            blend.setBottomInput(colorAdjust);

            imageView.setEffect(blend);
        }
        // 获取当前场景
        Scene scene1 = ((Node) event.getSource()).getScene();

        // 查找 ImageView
        ImageView imageView1 = (ImageView) scene1.lookup("#Message");

        if (imageView1 != null) {
            // 修改图片颜色
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(0.0); // 设置色相值，这里设置为120.0，即绿色
            imageView1.setEffect(colorAdjust);
        }

        // 当点击好友1按钮时，显示好友和群聊的按钮
        friendsAndGroupChatsButtonsBox.setVisible(true);
        groupChatsListView.setVisible(false);
        messagesListView.setVisible(false);
        messagesListView1.setVisible(false);
    }

    @FXML
    void addButton1(ActionEvent event) throws IOException {
        System.out.println("添加好友按钮被点击");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/popup.fxml"));
        Parent root = fxmlLoader.load();
        PopupController popupController = fxmlLoader.getController();
        popupController.setCurrentUser(currentUser);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        //将子类窗口添加进来
        childStages.add(stage);

        // 设置窗口样式为无装饰
        stage.initStyle(StageStyle.UNDECORATED);

        stage.setScene(scene);

        // 获取添加按钮的屏幕坐标
        Bounds addButtonBounds = addButton.localToScreen(addButton.getBoundsInLocal());
        double x = addButtonBounds.getMinX();
        double y = addButtonBounds.getMaxY();

        // 设置弹出窗口的位置
        stage.setX(x - 20);
        stage.setY(y);

        stage.show();

        // 监听窗口的焦点属性，当窗口失去焦点时关闭窗口
        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                stage.close();
            }
        });
    }

    @FXML
    void message1(ActionEvent event) {
        friendsAndGroupChatsButtonsBox.setVisible(false);
        // 获取当前场景
        Scene scene2 = ((Node) event.getSource()).getScene();
        ImageView imageView2 = (ImageView) scene2.lookup("#Requestpicture");

        if (imageView2 != null) {
            // 修改图片颜色
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(1.0);
            imageView2.setEffect(colorAdjust);

            Blend blend = new Blend();
            blend.setMode(BlendMode.SRC_ATOP);
            blend.setTopInput(new ColorInput(0, 0, imageView2.getFitWidth(), imageView2.getFitHeight(), Color.BLACK));
            blend.setBottomInput(colorAdjust);

            imageView2.setEffect(blend);
        }
        System.out.println("消息按钮被点击");
        // 设置消息列表可见
        messagesListView.setVisible(true);
        friendsListView.setVisible(false);
        groupChatsListView.setVisible(false);
        messagesListView1.setVisible(false);
        // 获取当前场景
        Scene scene = ((Node) event.getSource()).getScene();

       // 查找 ImageView
        ImageView imageView = (ImageView) scene.lookup("#Message");

        if (imageView != null) {
            // 修改图片颜色
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(1.0);
            imageView.setEffect(colorAdjust);

            Blend blend = new Blend();
            blend.setMode(BlendMode.SRC_ATOP);
            blend.setTopInput(new ColorInput(0, 0, imageView.getFitWidth(), imageView.getFitHeight(), Color.GREEN));
            blend.setBottomInput(colorAdjust);

            imageView.setEffect(blend);
        }

        // 获取当前场景
        Scene scene1 = ((Node) event.getSource()).getScene();

        // 查找 ImageView
        ImageView imageView1 = (ImageView) scene1.lookup("#Goodfriend");

        if (imageView1 != null) { // 这里修改了 imageView -> imageView1
            // 修改图片颜色
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(0.0); // 设置色相值，这里设置为0.0，即不改变颜色
            imageView1.setEffect(colorAdjust);
        }
    }


    //点击头像
    @FXML
    void HeadPicture1(ActionEvent event) throws IOException {
        System.out.println("头像被点击");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Personalinformation.fxml"));
        Parent root = fxmlLoader.load();
        UserInfoController userInfoController = fxmlLoader.getController();
        System.out.println(" 2 " + currentUser);
        userInfoController.setUserInfo(currentUser);
        UserInfoController chatController = fxmlLoader.getController();
        chatController.setCurrentUser(currentUser);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        childStages.add(stage);//将子类窗口添加进来
        stage.setTitle("个人信息");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void GoHome(ActionEvent event) throws IOException {
        // 这里是向服务器发送注销消息的代码
        try(Socket socket = new Socket("127.0.0.1",6000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            //发送退出请求
            out.println("logout:"+currentUser.getUsername());
        }
        System.out.println("用户："+currentUser.getUsername()+" 退出登录");
        /*try {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
            Stage stage1 = new Stage();
            childStages.add(stage1);//将子类窗口添加进来
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            stage1.setTitle("聊天室");
            stage1.setScene(new Scene(root));
            stage1.show();
        } catch (IOException e) {
            e.printStackTrace();
        }       */
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void setUserAvatar(Image avatar) {
        HomeScreenAvatar.setImage(avatar);
    }

    public void updateHomeScreenAvatar(String avatarUrl) {
        Image avatarImage = new Image(avatarUrl);
        setUserAvatar(avatarImage);
    }

    @FXML
    void RequestisLt1(ActionEvent event) throws IOException {
        friendsAndGroupChatsButtonsBox.setVisible(false);
        Scene scene2 = ((Node) event.getSource()).getScene();

        ImageView imageView = (ImageView) scene2.lookup("#Requestpicture");

        if (imageView != null) {
            // 修改图片颜色
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(1.0);
            imageView.setEffect(colorAdjust);

            Blend blend = new Blend();
            blend.setMode(BlendMode.SRC_ATOP);
            blend.setTopInput(new ColorInput(0, 0, imageView.getFitWidth(), imageView.getFitHeight(), Color.GREEN));
            blend.setBottomInput(colorAdjust);

            imageView.setEffect(blend);
        }
        // 获取当前场景
        Scene scene = ((Node) event.getSource()).getScene();

        // 查找 ImageView
        ImageView imageView2 = (ImageView) scene.lookup("#Goodfriend");

        if (imageView2 != null) {
            // 修改图片颜色
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(0.0); // 设置色相值，这里设置为120.0，即绿色
            imageView2.setEffect(colorAdjust);
        }
        // 获取当前场景
        Scene scene1 = ((Node) event.getSource()).getScene();

        // 查找 ImageView
        ImageView imageView1 = (ImageView) scene1.lookup("#Message");

        if (imageView1 != null) {
            // 修改图片颜色
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(0.0); // 设置色相值，这里设置为120.0，即绿色
            imageView1.setEffect(colorAdjust);
        }
        // 设置消息列表可见
        messagesListView.setVisible(true);
        friendsListView.setVisible(false);
        groupChatsListView.setVisible(false);

        // 构造一个获取请求列表的请求字符串，currentUser是当前用户
        String request = "getRequestList:" + currentUser.getUsername();

        try {
            // 创建一个连接到服务器的Socket，"127.0.0.1"是服务器地址，6000是端口
            Socket socket = new Socket("127.0.0.1",6000);

            // 创建一个PrintWriter，用来向服务器发送请求
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 创建一个BufferedReader，用来读取服务器的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 向服务器发送请求
            out.println(request);

            // 从服务器读取响应
            String response = in.readLine();
            System.out.println("服务器响应：" + response);

            // 使用Gson库将服务器的响应从JSON格式转换为Java对象
            Gson gson = new Gson();
            List<String> requestList = gson.fromJson(response, new TypeToken<List<String>>() {}.getType());

            // 清空ListView中的所有元素
            messagesListView.getItems().clear();

            // 在ListView中显示请求
            ObservableList<HBox> items = FXCollections.observableArrayList();
            for(String requester : requestList) {
                final HBox requestBox = new HBox();  // 注意这里我们把requestBox声明为final
                requestBox.setSpacing(10);
                Label requesterLabel = new Label(requester);//用户名
                User user1 =null;
                Socket socket1 = new Socket("127.0.0.1", 6000);
                PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
                BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
                try {
                    User user = null;
                    // 创建一个连接到服务器的Socket，"127.0.0.1"是服务器地址，6000是端口
                    System.out.println("这个是账号吗："+requester);
                    out1.println("getUserInfo:" + requester);
                    // 从服务器读取响应
                    String response1 = in1.readLine();
                    System.out.println("服务器响应：" + response1);
                    Gson gson1 = new Gson();
                    user = gson1.fromJson(response1, User.class);
                    user1 = user;
                    System.out.println("序列化后: " + user1);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        if(out1 != null) out1.close();
                        if(in1 != null) in1.close();
                        if(socket1 != null) socket1.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                ImageView requesterAvatar = new ImageView(new Image(user1.getAvatar()));
                requesterAvatar.setFitWidth(50);
                requesterAvatar.setFitHeight(50);
                requesterAvatar.setPreserveRatio(false);
                Button acceptButton = new Button("接受");
                Button rejectButton = new Button("拒绝");
                acceptButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        acceptRequest(requester);
                        items.remove(requestBox);  // 删除这个HBox
                    }
                });

                rejectButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        rejectRequest(requester);
                        items.remove(requestBox);  // 删除这个HBox
                    }
                });

                requestBox.getChildren().addAll(requesterAvatar, requesterLabel, acceptButton, rejectButton);
                items.add(requestBox);
            }


            // 将items列表设置为ListView的元素
            messagesListView.setItems(items);

            // 关闭连接
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            // 如果在处理过程中出现任何I/O错误，打印错误堆栈信息
            e.printStackTrace();
        }
    }


    // 处理接受请求
    void acceptRequest(String requester) {
        // 向服务器发送接受请求
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("username1", currentUser.getUsername());
        requestMap.put("username2", requester);
        Gson gson = new Gson();
        String requestJson = gson.toJson(requestMap);
        String request = "acceptFriendRequest:" + requestJson;
        try {
            Socket socket = new Socket("127.0.0.1",6000); // 将"server address"和port替换为你的服务器地址和端口
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(request);
            System.out.println("接受："+request);
            // 关闭连接
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 处理拒绝请求
    void rejectRequest(String requester) {
        // 向服务器发送拒绝请求
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("username1", currentUser.getUsername());
        requestMap.put("username2", requester);
        Gson gson = new Gson();
        String requestJson = gson.toJson(requestMap);
        String request = "rejectFriendRequest:" + requestJson;
        try {
            Socket socket = new Socket("127.0.0.1",6000); // 将"server address"和port替换为你的服务器地址和端口
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(request);
            System.out.println("拒绝："+request);
            // 关闭连接
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ConnectionManager connectionManager;

   public void startPeriodicTask() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            // 获取当前登录用户的用户名
            String currentUsername = currentUser.getUsername();
            connectionManager.getOut().println("checkRequest:" + currentUsername);
            new Thread(() -> {
                try {
                    String response = connectionManager.getIn().readLine();

                    System.out.println("长连接响应：" + response);
                    if ("newRequest".equals(response)) {
                        System.out.println("修改了成功");
                        Platform.runLater(() -> {
                            System.out.println("按钮以为红色");
                            RequestPrompt.setFill(Color.RED);
                            RequestPrompt.setOpacity(1.0);
                        });
                    } else {
                        // 如果没有新请求，将RequestPrompt设置为原始颜色（例如，白色）并使其透明
                        RequestPrompt.setFill(Color.WHITE);
                        RequestPrompt.setOpacity(0.0);
                    }

                    if (activeReceiver != null) {
                        // 发送请求到服务器以检查新消息
                        //String request = "checkForNewMessages:" + currentUser.getId() + ":" + activeReceiver.getId();
                        //sendRequestToServer1(request);
                       // System.out.println("hello");
                        ChatMessage newMessage = getNewMessageFromServer1(); //短连接
                        //ChatMessage newMessage = getNewMessageFromServer(); // 长连接吧
                        //System.out.println("哈哈哈哈哈哈哈哈哈哈："+newMessage.getSender()+" "+newMessage.getContent());
                        Platform.runLater(() -> handleMessageFromServer(newMessage));
                    }

                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }).start();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private ChatMessage getNewMessageFromServer1() throws IOException {
        try(Socket socket = new Socket("127.0.0.1",6000);
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            System.out.println("当前登入者："+currentUser.getId()+" 活跃者："+activeReceiver.getId());
            out.println("checkForNewMessages:" + currentUser.getId() + ":" + activeReceiver.getId());
            String newMessageLine =in.readLine();
            System.out.println("服务端传回来的："+newMessageLine);
            if (newMessageLine != null && !newMessageLine.isEmpty()) {
                String[] messageParts = newMessageLine.split(":", 3);
                System.out.println("长度："+messageParts.length);
                if (messageParts.length == 3) {
                    String senderId = messageParts[0];
                    System.out.println("用户："+currentUser.getId() +"收到："+ senderId+"发来的一条"+messageParts[2]);
                    String receiverId = messageParts[1];
                    System.out.println("接收者：" + receiverId);
                    String messageContent = messageParts[2];
                    System.out.println("接收的消息：" + messageContent);

                    // 一个从ID获取User对象的方法
                    User sender = getUserById(senderId);
                    User receiver = getUserById(receiverId);
                    System.out.println("发送者:" + sender + "  " + "接收者" + receiver);

                    if (sender != null && receiver != null) {
                        return new ChatMessage(sender, receiver, messageContent);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

   private ChatMessage getNewMessageFromServer() {
       try {
           String newMessageLine = connectionManager.getIn().readLine();
           System.out.println("服务端收到：" + newMessageLine);
           if (newMessageLine != null) {
               String[] messageParts = newMessageLine.split(":", 3);
               if (messageParts.length == 3) {
                   String senderId = messageParts[0];
                   System.out.println("发送者：" + senderId);
                   String receiverId = messageParts[1];
                   System.out.println("接收者：" + receiverId);
                   String messageContent = messageParts[2];
                   System.out.println("消息：" + messageContent);

                   User sender = getUserById(senderId);
                   User receiver = getUserById(receiverId);
                   System.out.println("发送者:" + sender + "  " + "接收者" + receiver);

                   if (sender != null && receiver != null) {
                       return new ChatMessage(sender, receiver, messageContent);
                   }
               }
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
       return null;
   }
    // 发送请求到服务器
    private void sendRequestToServer1(String request) {
        connectionManager.getOut().println(request);
    }
    private void handleMessageFromServer(ChatMessage message) {
        //System.out.println("发送者在这里："+message.getSender().getId());
       // System.out.println("当前获得者在这里: "+activeReceiver.getId());
       // System.out.println(message.getSender().getId()==activeReceiver.getUsername());
       // System.out.println(message.getSender().equals(activeReceiver));
        //System.out.println(message.getSender().getUsername());
        //System.out.println(activeReceiver.getUsername());
        if (message != null && message.getSender().getUsername1().equals(activeReceiver.getUsername())) {
            // 如果消息来自当前聊天对象，将消息添加到聊天记录列表中
            System.out.println("添加成功！！！");
            //addMessageToChatRecord(message.getSender(), message.getContent(), false);
            addMessageToChatRecord(activeReceiver, message.getContent(), false);
        }
    }

    private User getUserById(String id) throws IOException {
        try (Socket socket = new Socket("127.0.0.1",6000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            // 向服务器发送请求以获取用户信息
           out.println("getUserById:" + id);

            // 从服务器接收响应
           String response=  in.readLine();
            if (response != null && !response.isEmpty()) {
                // 假设服务器返回格式为：id:username
                String[] userParts = response.split(":");

                if (userParts.length == 2) {
                    String username = userParts[1];
                    String senderID = userParts[0];
                    System.out.println("username:"+username);
                    System.out.println("senderID"+senderID);
                    return new User(username, senderID);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 设置 ConnectionManager 对象
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        startPeriodicTask(); // 在设置 ConnectionManager 对象后启动周期检测
    }

    public void sendChatMessage(ActionEvent actionEvent) {

    }

}


