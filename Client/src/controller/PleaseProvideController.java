package controller;

import client.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PleaseProvideController {

    @FXML
    private Circle onlineStatusIndicator;

    @FXML
    private TextField searchField;

    @FXML
    private ToggleButton friendsTabButton;

    @FXML
    private ListView<?> groupChatsListView;

    @FXML
    private Button addButton;

    @FXML
    private ListView<?> messagesListView;

    @FXML
    private ToggleButton message;

    @FXML
    private ToggleButton friendsButton;

    @FXML
    private Button sendButton;

    @FXML
    private ToggleButton groupChatsTabButton;

    @FXML
    private Button exitButton;


    @FXML
    private ImageView Goodfriend;


    @FXML
    private ImageView HomeScreenAvatar;

    @FXML
    private ListView<?> ChatRecord;

    @FXML
    private TextField messageInput;

    @FXML
    private HBox searchAndAddBox;
    @FXML
    private ToggleButton RequestisLt;


    @FXML
    private ToggleButton HeadPicture;

    @FXML
    private HBox friendsAndGroupChatsButtonsBox;

    /*
    这行代码声明了一个ListView<User>类型的私有变量friendsListView。
    ListView是JavaFX中用于显示列表数据的一种控件。在尖括号中的User是泛型参数，表示这个ListView中的每一项都是一个User对象。
     */
    @FXML
    private ListView<User> friendsListView;

    /*
    这行代码创建了一个ObservableList<User>类型的私有变量friendList，并初始化为一个空的ObservableArrayList。
    ObservableList是JavaFX中用于表示可观察列表的接口。这种列表可以被监听，当列表中的数据发生变化时，所有的监听器都会收到通知。
    在这个例子中，friendList是用来存储User对象的。
    当你向friendList中添加或删除User对象时，任何监听这个列表的控件（例如，你的friendsListView）都会自动更新，以反映列表中的新内容。
     */
    private ObservableList<User> friendList = FXCollections.observableArrayList();
    private User currentUser;

    /*
    ，首先通过 setItems 方法将 friendsListView 中的数据设置为 friendList，然后通过 setCellFactory 方法设置单元格工厂。
    当ListView需要一个新的单元格来显示一个User对象时，这个工厂会创建一个新的ListCell对象。
    这个ListCell对象会显示User对象的头像和昵称。如果ListCell没有User对象，就不显示任何东西。
     */
    // 在这个方法中初始化你的控制器
    public void initialize() {
        // 将朋友列表数据设置到ListView中
        friendsListView.setItems(friendList);

        // 设置列表的单元格工厂
        friendsListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            // 当ListView需要一个新的ListCell来显示User对象时，会调用这个方法
            @Override
            public ListCell<User> call(ListView<User> userListView) {
                // 返回一个自定义的ListCell对象
                return new ListCell<User>() {
                    // 创建一个ImageView对象，用来显示用户的头像
                    private ImageView imageView = new ImageView();

                    // 当ListCell的item属性发生改变时（例如，从一个User对象变为另一个User对象），或者ListCell的empty属性发生改变时（从非空变为空，或者从空变为非空），会调用这个方法
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        // 调用父类的updateItem方法，这是一个好的实践
                        super.updateItem(user, empty);

                        // 检查这个ListCell是否有User对象，如果有，显示User对象的头像和昵称
                        if (user != null) {
                            // 将ImageView的图片设置为用户的头像
                            imageView.setImage(new Image(user.getAvatar()));
                            // 设置图片的大小
                            imageView.setFitHeight(50);
                            imageView.setFitWidth(50);
                            // 将ListCell的文字设置为用户的昵称
                            setText(user.getNickname());
                            // 将ListCell的图形设置为ImageView
                            setGraphic(imageView);
                        } else {
                            // 如果ListCell没有User对象，就不显示任何东西
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };
            }
        });
    }

    public void setCurrentUser(User user) {
        currentUser = user;
        user.setMainController(this);
    }

    @FXML
    void searchField1(ActionEvent event) {

    }

    @FXML
    void FriendsTab(ActionEvent event) {
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
            while (friendJson != null) {
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
        // friendListView.refresh();
    }

    @FXML
    public void GroupsTab(ActionEvent actionEvent) {
        System.out.println("查看群聊列表被点击");
        groupChatsListView.setVisible(true);
        friendsListView.setVisible(false);
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
        stage.setTitle("个人信息");
        stage.setScene(scene);
        stage.show();
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

    }

    @FXML
    void friendsButton1(ActionEvent event) {
        // 当点击好友1按钮时，显示好友和群聊的按钮
        friendsAndGroupChatsButtonsBox.setVisible(true);
    }

    @FXML
    void GoHome(ActionEvent event) {
        System.out.println("返回");
        try {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
            Stage stage1 = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            stage1.setTitle("聊天室");
            stage1.setScene(new Scene(root));
            stage1.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setUserAvatar(Image avatar) {
        HomeScreenAvatar.setImage(avatar);
    }

    public void updateHomeScreenAvatar(String avatarUrl) {
        Image avatarImage = new Image(avatarUrl);
        setUserAvatar(avatarImage);
    }

    public void RequestisLt1(ActionEvent actionEvent) {

    }
}


