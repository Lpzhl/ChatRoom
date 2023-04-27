package controller;

import client.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

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
    private ToggleButton HeadPicture;

    @FXML
    private HBox friendsAndGroupChatsButtonsBox;

    @FXML
    private ListView<?> friendsListView;

    private User currentUser;

    public void setCurrentUser(User user) {
        currentUser = user;
        user.setMainController(this);
    }
    @FXML
    void searchField1(ActionEvent event) {

    }

    @FXML
    void addButton1(ActionEvent event) {

    }

    @FXML
    void FriendsTab(ActionEvent event) {

    }

    //点击头像
    @FXML
    void HeadPicture1(ActionEvent event) throws IOException {
        System.out.println("头像被点击");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Personalinformation.fxml"));
        Parent root = fxmlLoader.load();
        UserInfoController userInfoController = fxmlLoader.getController();
        System.out.println(" 2 "+currentUser);
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
    void message1(ActionEvent event) {

    }

    @FXML
    void friendsButton1(ActionEvent event) {

    }

    @FXML
    void GoHome(ActionEvent event) {
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
}
