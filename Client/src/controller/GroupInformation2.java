package controller;

import client.Group;
import client.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GroupInformation2 {

    @FXML
    private Button cancel;

    @FXML
    private Label number;

    @FXML
    private Button HomePage;

    @FXML
    private Label GroupInformation;

    @FXML
    private Button editProfileButton;

    @FXML
    private Label nickname;

    @FXML
    private Label CreationTime;

    @FXML
    private Button Member;

    @FXML
    private ListView<User> people;

    private Group group;
    private User currentUser;
    private int currentGroupId;  // 新添加的成员变量
    public void  setcurrentGroupId(int currentGroupId,User currentUser){
        System.out.println("群号是1："+currentGroupId);
        System.out.println("用户是："+currentUser.getId());
        this.currentGroupId = currentGroupId;
        this.currentUser = currentUser;
    }

    public void setGroup(Group group) {
        this.group = group;
        // 设置群组信息到对应的 Label 中
        number.setText(group.getUsername());
        nickname.setText(group.getName());
        GroupInformation.setText(group.getDescription());
        CreationTime.setText(group.getCreatedAt().toString());

        // 设置群组成员列表
        System.out.println("群成员1："+group.getMembers());
        ObservableList<User> memberList = FXCollections.observableArrayList(group.getMembers());
        people.setItems(memberList);
    }
    @FXML
    void editProfileButton1(ActionEvent event) {
        try {
            // 加载新的窗口
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GroupInformation1.fxml"));
            Parent root = loader.load();
            GroupInformation1 controller = loader.getController();
            controller.setGroup(group,currentUser);
            controller.setcurrentGroupId(currentGroupId);
            controller.updatedGroupProperty.addListener((observable, oldValue, newValue) -> refreshGroup(newValue));

            // 创建新的场景并显示
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void cancel1(ActionEvent event) {
        number.getScene().getWindow().hide();
    }
    @FXML
    void initialize() {
        people.setCellFactory(param -> new GroupInformation.UserCell());
    }

    @FXML
    void Member1(ActionEvent event) {
        number.setVisible(false);
        nickname.setVisible(false);
        GroupInformation.setVisible(false);
        CreationTime.setVisible(false);
        people.setVisible(true);
        editProfileButton.setVisible(false);
        cancel.setVisible(false);
        System.out.println("成员按钮被点击");
    }

    @FXML
    void HomePage1(ActionEvent event) {
        number.setVisible(true);
        nickname.setVisible(true);
        GroupInformation.setVisible(true);
        CreationTime.setVisible(true);
        people.setVisible(false);
        editProfileButton.setVisible(true);
        cancel.setVisible(true);
        System.out.println("首页被点击");
    }
    public void refreshGroup(Group group) {
        this.group = group;
        number.setText(group.getUsername());
        nickname.setText(group.getName());
        GroupInformation.setText(group.getDescription());
        CreationTime.setText(group.getCreatedAt().toString());
        ObservableList<User> memberList = FXCollections.observableArrayList(group.getMembers());
        people.setItems(memberList);
    }


    static class UserCell extends ListCell<User> {
        private ImageView avatarImageView = new ImageView();
        private Text usernameText = new Text();
        private Text nicknameText = new Text();
        private Text roleText = new Text();
        private Text joinText = new Text();
        private HBox hBox = new HBox(avatarImageView, usernameText, nicknameText,roleText,joinText);

        public UserCell() {
            super();
            avatarImageView.setFitHeight(100);  // 设置头像大小
            avatarImageView.setFitWidth(100);

            hBox.setSpacing(10);  // 设置头像、用户名和昵称之间的间隔
        }

        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            if (empty || user == null) {
                setGraphic(null);
            } else {
                avatarImageView.setImage(new Image(user.getAvatar()));  // getAvatar 方法返回头像的 URL
                usernameText.setText(user.getUsername());
                nicknameText.setText(user.getNickname());
                roleText.setText(user.getRole());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                joinText.setText(user.getJoinTime().format(formatter));
                setStyle("-fx-font-size: 15px;");
                setGraphic(hBox);
            }
        }
    }

}
