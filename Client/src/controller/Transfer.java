package controller;

import Util.ConnectionManager;
import client.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class Transfer {
    private int currentGroupId;  // 新添加的成员变量
    public void  setcurrentGroupId(int currentGroupId){
        System.out.println("群号是1："+currentGroupId);
        this.currentGroupId = currentGroupId;
    }
    @FXML
    private ListView<User> people1;

    public void setPeople1(List<User> users) {
        System.out.println("成员："+users);
        ObservableList<User> userList = FXCollections.observableArrayList(users);
        people1.setItems(userList);
    }
    @FXML
    void initialize() {
        System.out.println("群号是2："+currentGroupId);
        people1.setCellFactory(param -> new UserCell(currentGroupId));
    }

    ConnectionManager connectionManager = ConnectionManager.getInstance();
    class UserCell extends ListCell<User> {
        private ImageView avatarImageView = new ImageView();
        private Text usernameText = new Text();
        private Text nicknameText = new Text();
        private HBox hBox = new HBox(avatarImageView, usernameText, nicknameText);
        private ContextMenu contextMenu = new ContextMenu();  // 添加上下文菜单
        private User user;  // 新添加的成员变量
        private int currentGroupId;  // 新添加的成员变量
        public UserCell(int currentGroupId) {
            super();
            this.currentGroupId = currentGroupId;  // 将传入的值赋给成员变量
            avatarImageView.setFitHeight(100);  // 设置头像大小
            avatarImageView.setFitWidth(100);

            hBox.setSpacing(10);  // 设置头像、用户名和昵称之间的间隔

            MenuItem setAsGroupOwnerItem = new MenuItem("设置为群主");
            contextMenu.getItems().add(setAsGroupOwnerItem);

            setAsGroupOwnerItem.setOnAction(event -> {
                // 这里处理设置为群主的逻辑
                System.out.println("设置为群主按钮被点击");
                System.out.println("66666666user:"+user.getId()+"   " +currentGroupId);
                String request = "setGroupOwner:" + user.getId() + ":" + currentGroupId;
                connectionManager.getOut().println(request);
            });
        }

        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            this.user = user;  // 为新添加的成员变量赋值
            if (empty || user == null) {
                setGraphic(null);
                setContextMenu(null);  // 清除上下文菜单
            } else {
                avatarImageView.setImage(new Image(user.getAvatar()));  // getAvatar 方法返回头像的 URL
                usernameText.setText(user.getUsername());
                nicknameText.setText(user.getNickname());
                setStyle("-fx-font-size: 15px;");
                setGraphic(hBox);
                setContextMenu(contextMenu);  // 设置上下文菜单
            }
        }
    }
}

