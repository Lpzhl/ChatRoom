package controller;

import client.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class GroupInformation {

    public Button editProfileButton;
    public Button cancel;
    @FXML
    private Label number;

    @FXML
    private Button HomePage;

    @FXML
    private Label GroupInformation;

    @FXML
    private Label nickname;

    @FXML
    private Label CreationTime;

    @FXML
    private Button Member;

    @FXML
    public ListView<User> people;

    @FXML
    void initialize() {
        people.setCellFactory(param -> new UserCell());
    }

    @FXML
    void Member1(ActionEvent event) {
        number.setVisible(false);
        nickname.setVisible(false);
        GroupInformation.setVisible(false);
        CreationTime.setVisible(false);
        people.setVisible(true);
        System.out.println("成员按钮被点击");
    }

    @FXML
    void HomePage1(ActionEvent event) {
        number.setVisible(true);
        nickname.setVisible(true);
        GroupInformation.setVisible(true);
        CreationTime.setVisible(true);
        people.setVisible(false);
        System.out.println("首页被点击");
    }

    public void setNumber(String num) {
        number.setText(num);
    }

    public void setNickname(String name) {
        nickname.setText(name);
    }

    public void setGroupInformation(String info) {
        GroupInformation.setText(info);
    }

    public void setCreationTime(String time) {
        CreationTime.setText(time);
    }

    public void setPeople(List<User> users) {
        ObservableList<User> userList = FXCollections.observableArrayList(users);
        people.setItems(userList);
    }

    public void editProfileButton1(ActionEvent actionEvent) {
    }

    public void cancel1(ActionEvent actionEvent) {
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
