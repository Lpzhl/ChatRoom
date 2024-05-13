package controller;


import client.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class PopupController {

    @FXML
    private Button addFriendButton;
    private User currentUser; // 当前用户的用户名
    @FXML
    private Button joinGroupChatButton;
    public void setCurrentUser(User user) {
        currentUser = user;
        System.out.println("添加好友的用户名："+user);
    }

    @FXML
    void addFriend(ActionEvent event) throws IOException {
        System.out.println("添加好友/群被点击");
        // 在这里处理添加好友的逻辑
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Search.fxml"));
        Parent root = fxmlLoader.load();
        SearchController searchController = fxmlLoader.getController();
        searchController.setCurrentUser(currentUser);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("添加好友");
        stage.show();
    }

    @FXML
    void joinGroupChat(ActionEvent event) throws IOException {
        System.out.println("创建群聊被点击");
        // 在这里处理参加群聊的逻辑
        System.out.println("添加好友/群被点击");
        // 在这里处理添加好友的逻辑
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CreateGroupChat.fxml"));
        Parent root = fxmlLoader.load();
        CreateGroupChat CreateGroupChatController = fxmlLoader.getController();
        CreateGroupChatController.setCurrentUser(currentUser);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("添加好友");
        stage.show();
    }

}

