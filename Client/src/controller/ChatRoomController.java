package controller;

import Util.ConnectionManager;
import Util.EmoticonPicker;
import Util.ZoomingPane;
import client.ChatMessage;
import client.Group;
import client.Request;
import client.User;

import java.awt.*;
import java.io.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;

import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class ChatRoomController implements Util.ConnectionManager.MessageListener ,controller.CommonPhrasesController.PhraseSelectionListener{

    @FXML
    public Label PeopleName;

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
    private ImageView File;  //  发送文件图片
    @FXML
    private Button SendEmoticons; // 用于聊天发送表情包
    @FXML
    private ImageView Emoticons; // 发送表情包图片
    @FXML
    private Button SendFile;// 用于聊天发送文件
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

    @FXML
    private ListView<ChatMessage> ChatRecord1;

    @FXML
    private HBox searchAndAddBox;
    @FXML
    private ToggleButton RequestisLt;

    @FXML
    public User activeReceiver;
    @FXML
    private ToggleButton HeadPicture;

    @FXML
    private HBox friendsAndGroupChatsButtonsBox;

    @FXML
    private ToggleButton pinToggleButton;
    @FXML
    private Stage primaryStage;
    @FXML
    private ImageView Home;
    @FXML
    public  Circle RequestPrompt;
    @FXML
    private ListView<User> friendsListView;

    @FXML
    private Button SendCommon;
    @FXML
    private ImageView common;

    public List<Stage> childStages = new ArrayList<>();
    private User activeChatFriend;
    public List<Stage> getChildStages() {
        return childStages;
    }
    private ObservableList<User> friendList = FXCollections.observableArrayList();
    public User currentUser;
    private User currentUser1;
    public void setCurrentUser(User user) {
        currentUser = user;
        currentUser1 = user;
        user.setMainController(this);
    }

    @Override
    public void onPhraseSelected(String phrase) {
        // 将选择的常用语插入到消息输入框中
        messageInput.setText(phrase);
    }

    private ObservableList<User> observableFriendsList = FXCollections.observableArrayList();

    public void sendChatMessage(ActionEvent actionEvent) {

    }
    private ConnectionManager connectionManager;
    @Override
    public void onMessageReceived(ChatMessage message) {
        System.out.println("onMessageReceived: " + message.getContent());
        Platform.runLater(() -> {
            if (message.getGroupId()!=0) {
                if (message.getGroupId() == currentGroupId) {
                    addGroupMessageToChatRecord(message.getSender(), message.getContent(), true,message.getUpdatedAt(), message.getContentType(), message.getGroupId());
                }
            } else {
                // handle private message
                if (message.getSender().getId() == activeReceiver.getId()) {
                    addMessageToChatRecord(message.getSender(), message.getContent(), true,message.getUpdatedAt(), message.getContentType());
                }
            }
        });
    }
    private int currentGroupId = 0;
  /*  public void onGroupMessageReceived(ChatMessage message) {
        Platform.runLater(() -> {
            if (message.getGroupId() == currentGroupId) {
                addGroupMessageToChatRecord(message.getSender(), message.getContent(), true,message.getUpdatedAt(), message.getContentType(), message.getGroupId());
            }
        });
    }*/

    private void addGroupMessageToChatRecord(User sender, String content, boolean isSentByCurrentUser, LocalDateTime dateTimeFormatter, String contentType, int groupId) {
        ChatMessage chatMessage = new ChatMessage(sender, content, dateTimeFormatter, groupId);
        chatMessage.setContentType(contentType);
        if (isSentByCurrentUser) {
            chatMessage.setSentByCurrentUser(true);
        }
        chatRecordList.add(chatMessage);
    }
    private void addMessageToChatRecord(User sender, String content, boolean isSentByCurrentUser,LocalDateTime dateTimeFormatter,String contentType) {
        System.out.println("实际上："+dateTimeFormatter);

        ChatMessage chatMessage = new ChatMessage(sender, content, dateTimeFormatter);
        chatMessage.setContentType(contentType);
        if (isSentByCurrentUser) {
            chatMessage.setSentByCurrentUser(true);
        }
        System.out.println("消息1："+chatMessage);
        chatRecordList.add(chatMessage);
    }
    @Override
    public void onChatHistoryReceived(List<ChatMessage> chatHistory) {
        System.out.println("onChatHistoryReceived: " + chatHistory.size() + " messages");
        Platform.runLater(() -> {
            // 清空当前的聊天记录
            messagesListView.getItems().clear();
            // 遍历聊天记录，将每条记录添加到聊天窗口中
            for (ChatMessage message : chatHistory) {
                boolean isSender = message.getSender().getId() == currentUser.getId();
                System.out.println("就觉得说出口: "+message.getCreatedAt());
                System.out.println("消息："+message);
                System.out.println("用户："+message.getSender());
                if (message.getGroupId() != 0) {
                    addGroupMessageToChatRecord(message.getSender(), message.getContent(), isSender,message.getCreatedAt(), message.getContentType(), message.getGroupId());
                } else {
                    addMessageToChatRecord(message.getSender(), message.getContent(), isSender,message.getCreatedAt(), message.getContentType());
                }
            }
        });
    }
    // 用于存储群聊列表的ObservableList
    private ObservableList<Group> groupList = FXCollections.observableArrayList();
    private ObservableList<ChatMessage> chatRecordList = FXCollections.observableArrayList();

    public void updtaed (){
        System.out.println("修改了成功");
        System.out.println("按钮以为红色");
        RequestPrompt.setFill(Color.RED);
        RequestPrompt.setOpacity(1.0);
    }
    public void updatad(){
        // 如果没有新请求，将RequestPrompt设置为原始颜色（例如，白色）并使其透明
        RequestPrompt.setFill(Color.WHITE);
        RequestPrompt.setOpacity(0.0);
    }
    private static GroupInformation groupInformationController;
    private static Transfer groupInformationController1;

    @FXML
    public void togglePin(ActionEvent event) {
        primaryStage.setAlwaysOnTop(pinToggleButton.isSelected());
    }

    //登入的时候把界面传进来
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    // 在这个方法中初始化控制器
        @FXML
        private void initialize() {

            // 在initialize方法中

            ConnectionManager connectionManager = ConnectionManager.getInstance();
            connectionManager.startMessageListening();
            connectionManager.setMessageListener(this);
            // 将朋友列表数据设置到ListView中
            PeopleName.setVisible(false);
            friendsListView.setItems(friendList);
            friendsListView.setVisible(false);
            groupChatsListView.setVisible(false);
            messagesListView.setVisible(false);
            messagesListView1.setVisible(false);
            sendButton.setVisible(false);
            messageInput.setVisible(false);
            ChatRecord1.setVisible(false);
            FriendChat.setTextFill(Color.GRAY);// 好友按钮变色
            GroupChat.setTextFill(Color.GRAY);// 群聊按钮变回去
            RequestPrompt.setFill(Color.WHITE);
            RequestPrompt.setOpacity(1.0);
            ChatRecord1.setItems(chatRecordList);
            File.setVisible(false);// 文件图片消失
            SendFile.setVisible(false);  //发送文件按钮消失
            Emoticons.setVisible(false); // 表情图片消失
            SendEmoticons.setVisible(false);// 发送表情包按钮消失
            common.setVisible(false);
            SendCommon.setVisible(false);

            messageInput.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    // 判断是否按下了 shift 键，如果是，则插入换行符
                    if (event.isShiftDown()) {
                        messageInput.appendText("\n");
                    } else {
                        // 否则，发送消息
                        event.consume(); // 防止事件进一步传播
                        sendButton.fire(); // 触发 sendButton 的 ActionEvent
                    }
                }
            });


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
                        private VBox contentBox = new VBox();// 这里移除了 content
                        private VBox messageBox = new VBox(header, contentBox);

                        {
                            messageBox.setSpacing(5);
                            setPadding(new Insets(10));
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                            // 设置背景颜色和圆角
                            BackgroundFill backgroundFill = new BackgroundFill(Color.web("#CCCCCC"), new CornerRadii(5), Insets.EMPTY);
                            Background background = new Background(backgroundFill);
                            contentBox.setBackground(background);
                            //contentBox.setPadding(new Insets(5));
                            contentBox.setPadding(new Insets(10)); // 10 是一个示例值，可以根据需要调整


                            // 设置自动换行
                            content.setWrapText(true);
                            content.setMaxWidth(500);
                        }

                        @Override
                        protected void updateItem(ChatMessage chatMessage, boolean empty) {
                            super.updateItem(chatMessage, empty);
                            //System.out.println("时间家家户户："+chatMessage);
                           // contentBox.getChildren().clear();  // 防止滑动界面导致多个按钮出现。。清空 contentBox
                           /* if (chatMessage != null) {
                                content.setText(chatMessage.getContent());
                                contentBox.getChildren().add(content); // 先添加content到contentB
                                if (chatMessage.getContentType().equals("file")) {
                                    // 如果这个消息包含一个文件，创建一个链接或按钮来打开这个文件
                                    Button openFileButton = new Button("Open File");
                                    openFileButton.setOnAction(event -> {
                                        try {
                                            Desktop.getDesktop().open(new File(chatMessage.getContent()));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    // 添加这个按钮到contentBox
                                    contentBox.getChildren().add(openFileButton);
                                }*/
                            contentBox.getChildren().clear();  // 防止滑动界面导致多个按钮出现。。清空 contentBox
                            if (chatMessage != null) {
                                if (chatMessage.getContentType().equals("file")) {
                                    content.setText(chatMessage.getContent());
                                    contentBox.getChildren().add(content);
                                    // 如果这个消息包含一个文件，创建一个链接或按钮来打开这个文件
                                    Button openFileButton = new Button("Open File");
                                    openFileButton.setOnAction(event -> {
                                        try {
                                            Desktop.getDesktop().open(new File(chatMessage.getContent()));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    // 添加这个按钮到contentBox
                                    contentBox.getChildren().add(openFileButton);
                                    // 如果这个消息是一个图片，显示这个图片
                                } else if (chatMessage.getContentType().equals("image")) {
                                    byte[] decodedBytes = Base64.getDecoder().decode(chatMessage.getContent());
                                    InputStream inputStream = new ByteArrayInputStream(decodedBytes);
                                    Image image = new Image(inputStream);
                                    ImageView imageView = new ImageView(image);
                                    imageView.setFitHeight(150); // 设置图片的最大高度
                                    imageView.setPreserveRatio(true); // 保持图片的宽高比
                                    contentBox.getChildren().add(imageView);

                                    // 添加点击事件
                                    imageView.setOnMouseClicked(event -> {
                                        if (event.getClickCount() == 2) { // 双击打开新窗口
                                            Image fullImage = new Image(new ByteArrayInputStream(decodedBytes));
                                            ImageView fullImageView = new ImageView(fullImage);
                                            ZoomingPane zoomingPane = new ZoomingPane(fullImageView);
                                            ScrollPane scrollPane = new ScrollPane(zoomingPane);
                                            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                                            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                                            scrollPane.setPannable(true);

                                            Scene scene = new Scene(scrollPane, 800, 600);
                                            scene.setOnScroll(scrollEvent -> {
                                                if (scrollEvent.getDeltaY() > 0) {
                                                    zoomingPane.zoomIn();
                                                } else {
                                                    zoomingPane.zoomOut();
                                                }
                                            });

                                            Stage stage = new Stage();
                                            stage.setScene(scene);
                                            stage.show();
                                        }
                                    });
                                } else {
                                    content.setText(chatMessage.getContent());
                                    if (!contentBox.getChildren().contains(content)) {
                                        contentBox.getChildren().add(content); // 先添加content到contentB
                                    }
                                }
                                imageView.setImage(new Image(chatMessage.getSender().getAvatar()));
                                imageView.setFitHeight(50);
                                imageView.setFitWidth(50);
                                // senderName.setText(chatMessage.getSender().getNickname());
                                System.out.println(chatMessage);
                                //senderName.setStyle("-fx-font-size: 18px;"); // 18px 是一个示例值，可以根据需要调整
                                System.out.println("发送者："+chatMessage.getSender());
                                System.out.println("时间："+chatMessage.getUpdatedAt());
                                System.out.println("聊天信息："+chatMessage.getContent());
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                timestamp.setText(chatMessage.getUpdatedAt().format(formatter));
                                // 创建一个新的HBox来容纳时间戳
                                // HBox timestampBox = new HBox();
                                // timestampBox.setAlignment(Pos.CENTER); // 让时间戳在HBox中居中

                                // 将时间戳添加到新的HBox
                                //timestampBox.getChildren().add(timestamp);
                                content.setText(chatMessage.getContent());
                                content.setStyle("-fx-font-size: 16px;"); // 16px 是一个示例值，可以根据需要调整

                                if (chatMessage.getSender().getId() == currentUser.getId()) {
                                    // 如果是当前用户发送的消息，则将气泡的对齐方式设置为右对齐
                                    header.setAlignment(Pos.CENTER_RIGHT);
                                    contentBox.setAlignment(Pos.CENTER_RIGHT);
                                    content.setAlignment(Pos.CENTER_RIGHT);
                                    HBox.setHgrow(spacer, Priority.ALWAYS);
                                    header.getChildren().setAll(spacer,timestamp, senderName, imageView );
                                } else {
                                    // 如果是其他用户发送的消息，则将气泡的对齐方式设置为左对齐
                                    header.setAlignment(Pos.CENTER_LEFT);
                                    contentBox.setAlignment(Pos.CENTER_LEFT);
                                    content.setAlignment(Pos.CENTER_LEFT);
                                    HBox.setHgrow(spacer, Priority.ALWAYS);
                                    header.getChildren().setAll(imageView, senderName, timestamp, spacer);
                                }

                                double contentWidth = content.prefWidth(-1);
                                content.setPrefWidth(contentWidth);
                                contentBox.setPrefWidth(contentWidth + 10); // +10 用于添加气泡的边距

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
                            // 在这里添加发送消息的代码
                            sendMessageItem.setOnAction(event -> {
                                User user = getItem();
                                sendButton.setVisible(true);
                                messageInput.setVisible(true);
                                ChatRecord1.setVisible(true);
                                PeopleName.setVisible(true);
                                File.setVisible(true);// 文件图片出现
                                SendFile.setVisible(true);  //发送文件按钮出现
                                Emoticons.setVisible(true); // 表情图片出现
                                SendEmoticons.setVisible(true);// 发送表情包按钮出现
                                common.setVisible(true);
                                SendCommon.setVisible(true);
                                currentGroupId=0;
                                messageInput.clear();

                                System.out.println("发送消息按钮被点击：" + user.getUsername());
                                System.out.println("发送消息按钮被点击：" + user.getNickname());
                                // 在右侧显示聊天界面和被选中的好友的用户名
                                PeopleName.setText(user.getNickname());

                                System.out.println("发送消息按钮被点击：");
                                // 设置接收者的ID
                                receiverId = user.getId();
                                System.out.println("receiverId:"+(int)receiverId);

                                // 清除之前的聊天记录并加载当前接收者的聊天记录
                                if (activeReceiver != user) {
                                    chatRecordList.clear();
                                    activeReceiver = user;
                                    // 在这里添加加载当前
                                    // 设置消息监听器和当前聊天的好友 ID
                                    System.out.println("receiverId:"+(int)receiverId);
                                    System.out.println("receiverId:"+activeReceiver.getId());

                                    // 在发送 getChatHistory 请求之前，确保已经连接到服务端
                                    connectionManager.getOut().println("getChatHistory:" + currentUser.getId() + ":" + activeReceiver.getId());

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
                                imageView.setFitHeight(70);
                                imageView.setFitWidth(70);
                                setText(user.getNickname());//设置单元格的文本内容
                                setGraphic(imageView);//设置单元格的图形内容。
                                setStyle("-fx-font-size: 20px;");
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
                                setStyle("-fx-font-size: 20px;");
                                setGraphic(imageView);

                                List<Integer> admins = new ArrayList<>();  // 将 admins 的声明移动到 try 代码块之前
                                // 向服务端发送 "getGroupAdmins" 请求，获取群主和管理员的 ID
                                try(Socket socket = new Socket("127.0.0.1", 6000);
                                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
                                    out.println("getGroupAdmins:" + group.getId());

                                    String response = in.readLine();
                                    String[] adminIds = response.split(",");
                                    admins = Arrays.stream(adminIds)
                                            .map(Integer::parseInt)
                                            .collect(Collectors.toList());

                                } catch (IOException e){
                                    e.printStackTrace();
                                }
                                ContextMenu contextMenu = new ContextMenu();
                                MenuItem sendMessageItem = new MenuItem("发信息");
                                MenuItem quitGroupItem = new MenuItem("退出群");
                                MenuItem disbandGroupItem = new MenuItem("解散群");
                                MenuItem manageGroupItem = new MenuItem("管理群");
                                MenuItem viewGroupItem = new MenuItem("查看群");

                                System.out.println("管理员群主："+admins);
                                System.out.println("当前登入者："+currentUser.getId());
                                System.out.println(admins.contains(currentUser.getId()));
                                boolean containsCurrentUser = admins.stream().map(Object::toString)
                                        .anyMatch(adminId -> adminId.equals(String.valueOf(currentUser.getId())));
                                System.out.println(containsCurrentUser);
                                //如果是群主则可以进行一下按钮
                                if (currentUser.getId() == group.getCreatedBy()){
                                    disbandGroupItem.setVisible(true);
                                    manageGroupItem.setVisible(true);
                                }else {
                                    disbandGroupItem.setVisible(false);
                                    manageGroupItem.setVisible(false);
                                }

                                //如果是管理员则可以进行以下按钮 管理群
                                if (containsCurrentUser) {
                                    manageGroupItem.setVisible(true);
                                } else {
                                    manageGroupItem.setVisible(false);
                                }

                                contextMenu.getItems().addAll(sendMessageItem, quitGroupItem, disbandGroupItem, manageGroupItem,viewGroupItem);

                                sendMessageItem.setOnAction(event -> {
                                    // Handle sending message
                                    System.out.println("发送信息按钮被点击");
                                    sendButton.setVisible(true);
                                    messageInput.setVisible(true);
                                    ChatRecord1.setVisible(true);
                                    PeopleName.setVisible(true);
                                    File.setVisible(true);// 文件图片出现
                                    SendFile.setVisible(true);  //发送文件按钮出现
                                    Emoticons.setVisible(true); // 表情图片出现
                                    SendEmoticons.setVisible(true);// 发送表情包按钮出现
                                    PeopleName.setText(group.getName());
                                    currentGroupId = group.getId();  // 设置当前的群聊id
                                    common.setVisible(true);
                                    SendCommon.setVisible(true);
                                    chatRecordList.clear();
                                    receiverId=0;
                                    messageInput.clear();
                                    // 发送获取群聊记录的请求
                                    connectionManager.getOut().println("getGroupChatHistory:" + group.getId());
                                });

                                quitGroupItem.setOnAction(event -> {
                                    // Handle quitting the group
                                    System.out.println("退出群按钮被点击");
                                    //首先判断退出者是不是群主，如果是群主则，在退出前给群主一个选择
                                    //将群转交给群中其他人
                                    System.out.println("currentUser.getId() "+currentUser.getId()+"  "+"group.getCreatedBy())"+group.getCreatedBy());
                                    if (currentUser.getId() == group.getCreatedBy()) {
                                        // 提示用户他是群主，是否要转让群主
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("退出群聊");
                                        alert.setHeaderText("您是群主，是否要在退出前转让群主？");
                                        alert.setContentText("选择您的选项.");

                                        ButtonType buttonTypeOne = new ButtonType("转让群主");
                                        ButtonType buttonTypeTwo = new ButtonType("直接退出并解散群聊");
                                        ButtonType buttonTypeThree = new ButtonType("取消");

                                        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo,buttonTypeThree);

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == buttonTypeOne){
                                            // 用户选择转让群主，发送获取群成员请求，然后在接收到响应后显示选择新群主的界面
                                            String request = "getGroupMembers:" + group.getId()+ ":" + currentUser.getId();
                                            connectionManager.getOut().println(request);
                                            try {
                                                // 加载新的窗口
                                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Transfer.fxml"));
                                                Parent root = loader.load();

                                                // 获取控制器
                                                groupInformationController1 = loader.getController();
                                                Transfer transfer = loader.getController();
                                                transfer.setcurrentGroupId(group.getId());
                                                // 在界面上删除群聊
                                                Platform.runLater(() -> {
                                                    groupList.remove(group);
                                                });

                                                // 创建新的场景并显示
                                                Stage stage = new Stage();
                                                stage.setScene(new Scene(root));
                                                stage.show();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (result.get() == buttonTypeTwo) {
                                            // 用户选择直接退出，发送解散群聊请求
                                            System.out.println("解散群聊");
                                            String request = "disbandGroup:" + currentUser.getId() + ":" + group.getId();
                                            connectionManager.getOut().println(request);

                                            // 在界面上删除群聊
                                            Platform.runLater(() -> {
                                                groupList.remove(group);
                                            });
                                        }
                                    } else {
                                        // 发送退出群聊的请求到服务器
                                       String request = "quitGroup:" + currentUser.getId() + ":" + group.getId();
                                       connectionManager.getOut().println(request);
                                        // 在界面上删除群聊
                                      Platform.runLater(() -> {
                                          groupList.remove(group);
                                       });
                                    }
                                });
                                disbandGroupItem.setOnAction(event -> {
                                    //这个按钮只有群主才会显示出来
                                    System.out.println("解散群按钮被点击");
                                    // 提示用户他是群主，是否要转让群主
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("解散群聊");
                                    alert.setHeaderText("您是群主，是否确定解散群聊？");
                                    alert.setContentText("选择您的选项.");

                                    ButtonType buttonTypeOne = new ButtonType("确定");
                                    ButtonType buttonTypeTwo = new ButtonType("取消");

                                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

                                    Optional<ButtonType> result = alert.showAndWait();
                                    if(result.get()==buttonTypeOne) {
                                        //解散群聊
                                        String request = "disbandGroup:" + currentUser.getId() + ":" + group.getId();
                                        connectionManager.getOut().println(request);
                                        // 在界面上删除群聊
                                        Platform.runLater(() -> {
                                            groupList.remove(group);
                                        });

                                    } else if (result.get()==buttonTypeTwo) {
                                    }
                                });
                                manageGroupItem.setOnAction(event -> {
                                    // 这个按钮群主和管理员都能显示出来
                                    System.out.println("管理群按钮被点击");
                                    // 向服务器发送查看群聊信息的请求
                                    try(Socket socket = new Socket("127.0.0.1", 6000);//IP:127.0.0.1   端口6000
                                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){

                                        String request ="getGroupInfo:" + group.getId();
                                        out.println(request);

                                        String response = in.readLine();
                                        System.out.println("群管理："+response);

                                        // 将 JSON 字符串转换回 Group 对象
                                        Group group1 = new Gson().fromJson(response, Group.class);
                                        try {
                                            // 这部分代码替换你现有的加载新窗口的代码
                                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GroupInformation2.fxml"));
                                            Parent root = loader.load();

                                            GroupInformation2 controller = loader.getController();
                                            controller.setGroup(group1);
                                            controller.setcurrentGroupId(group.getId(),currentUser);

                                            Stage stage = new Stage();
                                            stage.setScene(new Scene(root));
                                            stage.show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (UnknownHostException e) {
                                        throw new RuntimeException(e);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                                viewGroupItem.setOnAction(event -> {
                                    System.out.println("查看群聊资料被点击");
                                    // 向服务器发送查看群聊信息的请求
                                    connectionManager.getOut().println("getGroupInfo:" + group.getId());

                                    try {
                                        // 加载新的窗口
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GroupInformation.fxml"));
                                        Parent root = loader.load();

                                        // 获取控制器
                                        groupInformationController = loader.getController();

                                        // 创建新的场景并显示
                                        Stage stage = new Stage();
                                        stage.setScene(new Scene(root));
                                        stage.show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
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

            //用来处理发送消息
            AtomicReference<String> chosenEmoticonRef = new AtomicReference<>();
            SendEmoticons.setOnAction(event -> {
                // 创建一个表情选择器
                EmoticonPicker emoticonPicker = new EmoticonPicker();
                emoticonPicker.setTitle("选择表情");
                // 显示表情选择对话框并获取选择的表情
                Optional<String> chosenEmoticon = emoticonPicker.showAndWait();
                chosenEmoticon.ifPresent(s -> {
                    // 将选择的表情插入到消息输入框中
                    String currentText = messageInput.getText();
                    messageInput.setText(currentText + " " + s);
                    // 保存chosenEmoticon到AtomicReference
                    chosenEmoticonRef.set(s);
                });
            });

            AtomicReference<File> chosenFileRef = new AtomicReference<>();
            SendFile.setOnAction(event -> {
                // 创建一个文件选择器
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("选择文件");
                // 显示文件选择对话框并获取选择的文件
                File chosenFile = fileChooser.showOpenDialog(messageInput.getScene().getWindow());
                if (chosenFile != null) {
                    // 将选择的文件的路径显示在消息输入框中
                    messageInput.setText(chosenFile.getAbsolutePath());
                    // 保存chosenFile到AtomicReference
                    chosenFileRef.set(chosenFile);
                }
            });
            sendButton.setOnAction(event -> {
                if (receiverId == 0&&currentGroupId == 0) {
                    // 如果没有选择聊天对象，不发送消息
                    return;
                } else if (receiverId!=0) {
                    String messageText = messageInput.getText().trim();
                    if (!messageText.isEmpty()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime currentTime = LocalDateTime.now();
                        String currentTimeStr = currentTime.format(formatter);
                        System.out.println("时间是："+currentTime);
                        JsonObject messageObject = new JsonObject();
                        messageObject.addProperty("senderId", currentUser.getId());
                        messageObject.addProperty("receiverId", receiverId);
                        messageObject.addProperty("timestamp", currentTimeStr);

                        // 从AtomicReference获取chosenFile
                        File chosenFile = chosenFileRef.get();

                        // 如果选择了文件，则将文件转换为Base64编码的字符串
                        if (chosenFile != null) {
                            try {
                                byte[] fileContent = Files.readAllBytes(chosenFile.toPath());
                                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                                messageObject.addProperty("content", encodedString);
                                // 检查文件是否为图片
                                String fileName = chosenFile.getName();
                                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                                if (fileExtension.equals("jpg") || fileExtension.equals("png") || fileExtension.equals("gif")||fileExtension.equals("jpeg")) {
                                    messageObject.addProperty("content_type", "image");
                                    messageText = messageObject.get("content").getAsString();
                                } else {
                                    messageObject.addProperty("content_type", "file");
                                }
                                messageObject.addProperty("file_name", fileName);
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            messageObject.addProperty("content", messageText);
                            messageObject.addProperty("content_type", "text");
                        }

                        String messageString = messageObject.toString();
                        connectionManager.getOut().println("sendMessageLongConnection:" + messageString);

                        String messageType = messageObject.get("content_type").getAsString();
                        //String message = messageObject.get("content").getAsString();
                        System.out.println("文件类型是："+messageType);
                        System.out.println("发送的文本"+messageObject);
                        addMessageToChatRecord(currentUser, messageText, true, currentTime, messageType);
                        messageInput.clear();
                        // 清空AtomicReference
                        chosenFileRef.set(null);
                    }

                } else if (currentGroupId!=0) {
                    String messageText = messageInput.getText().trim();
                    if (!messageText.isEmpty()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime currentTime = LocalDateTime.now();
                        String currentTimeStr = currentTime.format(formatter);
                        System.out.println("时间是："+currentTime);
                        JsonObject messageObject = new JsonObject();
                        messageObject.addProperty("senderId", currentUser.getId());
                        messageObject.addProperty("groupId", currentGroupId);
                        messageObject.addProperty("timestamp", currentTimeStr);

                        // 从AtomicReference获取chosenFile
                        File chosenFile = chosenFileRef.get();

                        // 如果选择了文件，则将文件转换为Base64编码的字符串
                        if (chosenFile != null) {
                            try {
                                byte[] fileContent = Files.readAllBytes(chosenFile.toPath());
                                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                                messageObject.addProperty("content", encodedString);
                                // 检查文件是否为图片
                                String fileName = chosenFile.getName();
                                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                                if (fileExtension.equals("jpg") || fileExtension.equals("png") || fileExtension.equals("gif")||fileExtension.equals("jpeg")) {
                                    messageObject.addProperty("content_type", "image");
                                    messageText = messageObject.get("content").getAsString();
                                } else {
                                    messageObject.addProperty("content_type", "file");
                                }
                                messageObject.addProperty("file_name", fileName);
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            messageObject.addProperty("content", messageText);
                            messageObject.addProperty("content_type", "text");
                        }

                        String messageString = messageObject.toString();
                        connectionManager.getOut().println("sendMessageGroupChat:" + messageString);//发送群聊消息

                        String messageType = messageObject.get("content_type").getAsString();
                        //String message = messageObject.get("content").getAsString();
                        System.out.println("文件类型是："+messageType);
                        System.out.println("发送的文本"+message);
                        addGroupMessageToChatRecord(currentUser, messageText, true, currentTime, messageType,currentGroupId);

                        messageInput.clear();
                        // 清空AtomicReference
                        chosenFileRef.set(null);
                    }
                }
            });
            SendCommon.setOnAction(event -> openCommonPhrasesWindow());
        }

    public void openCommonPhrasesWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/phrases_management.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("常用语管理");
            stage.show();

            CommonPhrasesController controller = loader.getController();
            System.out.println("用户你好1："+currentUser);
            controller.setCurrentUser(currentUser);
            controller.setPhraseSelectionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void searchField1(ActionEvent event) {

    }
  /*  public void receiveMessage(int senderId, String messageContent) {
        System.out.println("查找到的Id:"+senderId);
        // 根据senderId从好友列表中查找User对象
        User sender = findUserById(senderId);
        System.out.println("从好友表里面查找到的好友："+sender);
        if (sender != null) {
            // 如果找到了发送者，将消息添加到聊天记录中，并刷新聊天界面
            System.out.println("发送者："+sender+"发送的消息:"+messageContent);
            addMessageToChatRecord(sender, messageContent, false,);
        } else {
            System.out.println("无法找到发送者：" + senderId);
        }
    }*/

    private User findUserById(long userId) {
        for (User user : observableFriendsList) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    @FXML
   void FriendsTab(ActionEvent event) {
        PeopleName.setVisible(false);
        sendButton.setVisible(false);
        messageInput.setVisible(false);
        ChatRecord1.setVisible(false);
        File.setVisible(false);// 文件图片消失
        SendFile.setVisible(false);  //发送文件按钮消失
        Emoticons.setVisible(false); // 表情图片消失
        SendEmoticons.setVisible(false);// 发送表情包按钮消失
        common.setVisible(false);
        SendCommon.setVisible(false);

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

    // 更新好友列表的方法
    public void updateFriendsList(List<User> newFriends) {
        // 清空旧的好友列表
        friendList.clear();
        // 将新的好友列表添加到ObservableList中
        friendList.addAll(newFriends);
        // 更新ListView，这会将新的好友列表显示出来
        //friendListView.refresh();
        // 清空旧的好友列表
        observableFriendsList.clear();
        // 将新的好友列表添加到ObservableList中
        observableFriendsList.addAll(newFriends);
        // 更新ListView，这会将新的好友列表显示出来
    }

    @FXML
    public void GroupsTab(ActionEvent actionEvent) {
        PeopleName.setVisible(false);
        System.out.println("查看群聊列表被点击");
        sendButton.setVisible(false);
        messageInput.setVisible(false);
        ChatRecord1.setVisible(false);
        groupChatsListView.setVisible(true);
        friendsListView.setVisible(false);
        messagesListView.setVisible(false);
        messagesListView1.setVisible(false);
        FriendChat.setTextFill(Color.GRAY);// 好友按钮变色
        GroupChat.setTextFill(Color.BLACK);// 群聊按钮变回去
        File.setVisible(false);// 文件图片消失
        SendFile.setVisible(false);  //发送文件按钮消失
        Emoticons.setVisible(false); // 表情图片消失
        SendEmoticons.setVisible(false);// 发送表情包按钮消失
        common.setVisible(false);
        SendCommon.setVisible(false);


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
        PeopleName.setVisible(false);
        sendButton.setVisible(false);
        messageInput.setVisible(false);
        ChatRecord1.setVisible(false);
        FriendChat.setTextFill(Color.GRAY);// 好友按钮变色
        GroupChat.setTextFill(Color.GRAY);// 群聊按钮变回去
        File.setVisible(false);// 文件图片消失
        SendFile.setVisible(false);  //发送文件按钮消失
        Emoticons.setVisible(false); // 表情图片消失
        SendEmoticons.setVisible(false);// 发送表情包按钮消失
        common.setVisible(false);
        SendCommon.setVisible(false);

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
        PeopleName.setVisible(false);
        friendsAndGroupChatsButtonsBox.setVisible(false);
        File.setVisible(false);// 文件图片消失
        SendFile.setVisible(false);  //发送文件按钮消失
        Emoticons.setVisible(false); // 表情图片消失
        SendEmoticons.setVisible(false);// 发送表情包按钮消失
        common.setVisible(false);
        SendCommon.setVisible(false);

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
        messagesListView1.setVisible(true);
        friendsListView.setVisible(false);
        groupChatsListView.setVisible(false);
        messagesListView.setVisible(false);
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
            System.out.println("发送退出登入请求："+currentUser.getUsername());
            /*ConnectionManager connectionManager = ConnectionManager.getInstance();
            connectionManager.stopMessageListening();
            connectionManager.shutdown(); // 添加这行*/
            stopPeriodicTask();
            // 关闭 JavaFX 应用程序
            Platform.exit();
        }
        // 在 JavaFX 应用程序线程中执行关闭操作
        Platform.runLater(() -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
            Platform.exit();
        });
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
        PeopleName.setVisible(false);
        messagesListView1.setVisible(false);
        ChatRecord1.setVisible(false);
        friendsAndGroupChatsButtonsBox.setVisible(false);
        messageInput.setVisible(false);
        File.setVisible(false);// 文件图片消失
        SendFile.setVisible(false);  //发送文件按钮消失
        Emoticons.setVisible(false); // 表情图片消失
        SendEmoticons.setVisible(false);// 发送表情包按钮消失
        common.setVisible(false);
        SendCommon.setVisible(false);

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
            List<Request> requestList = gson.fromJson(response, new TypeToken<List<Request>>() {}.getType());

            // 清空ListView中的所有元素
            messagesListView.getItems().clear();

            // 在ListView中显示请求
            ObservableList<HBox> items = FXCollections.observableArrayList();
            for(Request request1 : requestList) {
                final HBox requestBox = new HBox();
                requestBox.setSpacing(10);
                Label requesterLabel = new Label(request1.getUsername());
                requesterLabel.setStyle("-fx-font-size: 16px");
                User user1 = null;
                Socket socket1 = new Socket("127.0.0.1", 6000);
                PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
                BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
                try {
                    User user = null;
                    System.out.println("这个是账号吗："+request1.getUsername());
                    out1.println("getUserInfo:" + request1.getUsername());
                    String response1 = in1.readLine();
                    //System.out.println("服务器响应：" + response1);
                    Gson gson1 = new Gson();
                    user = gson1.fromJson(response1, User.class);
                    user1 = user;
                   /// System.out.println("序列化后: " + user1);
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    try {
                        if(out1 != null) out1.close();
                        if(in1 != null) in1.close();
                        if(socket1 != null) socket1.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                ImageView requesterAvatar = new ImageView(new Image(user1.getAvatar()));
                requesterAvatar.setFitWidth(100);
                requesterAvatar.setFitHeight(100);
                requesterAvatar.setPreserveRatio(false);
                Label requesterNickname = new Label(user1.getNickname());
                requesterNickname.setStyle("-fx-font-size: 16px");
                Button acceptButton = new Button("同意");
                //acceptButton.getStyleClass().add("accepted-button");//??????
                acceptButton.setStyle("-fx-background-color: purple; -fx-text-fill: white; -fx-font-size:12px;");
                Button rejectButton = new Button("拒绝");
                rejectButton.setStyle("-fx-background-color: purple; -fx-text-fill: white; -fx-font-size:12px;");
                Label statusLabel = new Label();
                Label requestTypeLabel = new Label("请求类型: " + (request1.getRequestType().equals("friend") ? "好友请求" : "群聊请求"));
                requestTypeLabel.setStyle("-fx-font-size: 16px");

                switch (request1.getStatus()) {
                    case "pending":
                        statusLabel.setText("待处理");
                        statusLabel.setStyle("-fx-font-size: 16px");
                        break;
                    case "accepted":
                        statusLabel.setText("已同意");
                        statusLabel.setStyle("-fx-font-size: 16px");
                        acceptButton.setVisible(false);
                        rejectButton.setVisible(false);
                        break;
                    case "rejected":
                        statusLabel.setText("已拒绝");
                        statusLabel.setStyle("-fx-font-size: 16px");
                        acceptButton.setVisible(false);
                        rejectButton.setVisible(false);
                        break;
                }
                User finalUser = user1;
                acceptButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if(request1.getRequestType().equals("friend")) {
                            acceptRequest(request1.getUsername());
                        }else if(request1.getRequestType().equals("group")){
                            acceptRequest(finalUser.getId(),request1.getGroupId());
                        }
                        //acceptRequest(request1.getUsername());
                        items.remove(requestBox);  // 删除这个HBox
                    }
                });


                rejectButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if(request1.getRequestType().equals("friend")) {
                            rejectRequest(request1.getUsername());
                        }else if(request1.getRequestType().equals("group")){
                            rejectRequest(finalUser.getId(),request1.getGroupId());
                        }
                        // rejectRequest(request1.getUsername());
                        items.remove(requestBox);  // 删除这个HBox
                    }
                });
                Button deleteButton = new Button("删除");
                deleteButton.getStyleClass().add("delete-button");  // 设置样式类
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");  // 设置样式
                // Add the deleteButton to the userBox
                deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        deleteRequest(request1.getUsername(), request1.getRequestType(), request1.getGroupId());

                        items.remove(requestBox);  // 删除这个HBox
                    }
                });
                // 将接受和拒绝按钮放到一个HBox中
                HBox acceptRejectBox = new HBox();
                acceptRejectBox.setAlignment(Pos.CENTER_RIGHT);
                acceptRejectBox.getChildren().addAll(acceptButton, rejectButton);

                // 将删除按钮放到另一个HBox中
                HBox deleteButtonBox = new HBox();
                deleteButtonBox.setAlignment(Pos.CENTER_RIGHT);
                deleteButtonBox.getChildren().add(deleteButton);

                // 将接受和拒绝按钮的HBox以及删除按钮的HBox放到一个VBox中
                VBox buttonBox = new VBox();
                buttonBox.setAlignment(Pos.CENTER_RIGHT);
                buttonBox.getChildren().addAll(acceptRejectBox, deleteButtonBox);

                // 将包含接受、拒绝和删除按钮的buttonBox添加到userBox中
                VBox userBox = new VBox(requesterAvatar, requesterLabel, requesterNickname, requestTypeLabel, statusLabel, buttonBox);

                requestBox.getChildren().addAll(requesterAvatar, userBox);
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

    //删除请求
    private void deleteRequest(String username, String requestType, Integer groupId) {
        String request = "deleteRequest:" + username+ ":" + currentUser.getUsername()  + ":" + requestType;
        if (groupId != null) {
            request += ":" + groupId;
        }

        try {
            Socket socket = new Socket("127.0.0.1", 6000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(request);

            String response = in.readLine();
            System.out.println("服务器响应：" + response);

            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
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
        System.out.println(requestMap.get("username1")+"   "+requestMap.get("username2"));
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
    // 处理接受群组请求
    void acceptRequest(long id, int groupId) {
        // 向服务器发送接受请求
        Map<Object, Object> requestMap = new HashMap<>();
        requestMap.put("userId", id);
        requestMap.put("groupId", groupId);
        Gson gson = new Gson();
        String requestJson = gson.toJson(requestMap);
        String request = "acceptGroupRequest:" + requestJson;
        try {
            Socket socket = new Socket("127.0.0.1", 6000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(request);
            System.out.println("接受群组请求：" + request);
            // 关闭连接
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 处理拒绝群组请求
    void rejectRequest(long id, int groupId) {
        // 向服务器发送拒绝请求
        Map<Object, Object> requestMap = new HashMap<>();
        requestMap.put("userId", id);
        requestMap.put("groupId", groupId);
        Gson gson = new Gson();
        String requestJson = gson.toJson(requestMap);
        String request = "rejectGroupRequest:" + requestJson;
        try {
            Socket socket = new Socket("127.0.0.1", 6000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(request);
            System.out.println("拒绝群组请求：" + request);
            // 关闭连接
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static ChatRoomController instance;
    private Timeline timeline;

    public ChatRoomController() {
        instance = this;
    }

   public static ChatRoomController getInstance() {
        return instance;
    }

    public void startPeriodicTask() {
         timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            // 获取当前登录用户的用户名
            String currentUsername = currentUser.getUsername();
            connectionManager.getOut().println("checkRequest:" + currentUsername);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        System.out.println("一样的吗："+timeline);
    }
    public void stopPeriodicTask() {
        if (timeline != null) {
            System.out.println("一样的吗1："+timeline);
            timeline.stop();
        }
    }

    public User getUserById(String id) throws IOException {
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
        //startPeriodicTask(); // 在设置 ConnectionManager 对象后启动周期检测
    }

    public static GroupInformation getGroupInformationController() {
        return groupInformationController;
    }
    public static Transfer getGroupInformationController1() {
        return groupInformationController1;
    }

    public void chooseCommonPhrase(ActionEvent actionEvent) {

    }
}


