package controller;

import client.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.time.LocalDate;

public class UserInfoController {

    @FXML
    private Label birthday;

    @FXML
    private Label idLabel11;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Label nicknameLabel;

    @FXML
    private Label personalInfoLabel;

    @FXML
    private Button editProfileButton;

    @FXML
    private Label idLabel;

    @FXML
    private Label sex;

    @FXML
    private Label idLabel1;

    @FXML
    private Label onlineStatusLabel;

    @FXML
    private ImageView avatar;

    @FXML
    private Label creationDateLabel;

    private User currentUser;

    public void setCurrentUser(User user) {
        currentUser = user;
        currentUser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("avatar")) {
                    avatar.setImage(new Image((String) evt.getNewValue()));
                } else if (evt.getPropertyName().equals("nickname")) {
                    nicknameLabel.setText((String) evt.getNewValue());
                } else if (evt.getPropertyName().equals("signature")) {
                    personalInfoLabel.setText((String) evt.getNewValue());
                } else if (evt.getPropertyName().equals("gender")) {
                    sex.setText("M".equals(evt.getNewValue()) ? "男" : "女");
                } else if (evt.getPropertyName().equals("birthday")) {
                    birthday.setText(String.valueOf(evt.getNewValue()));
                }
            }
        });
        // Update other info
        setUserInfo(user);
    }

    @FXML
    void changePasswordButton1(ActionEvent event) throws IOException {
            String password = currentUser.getPassword();
           // System.out.println("用户："+currentUser);
            //System.out.println("用户密码： "+password);
            Stage stage = (Stage)changePasswordButton.getScene().getWindow();
            stage.close();
            Stage stage1 = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/PersonalChangePassword.fxml"));
            stage1.setTitle("修改密码");
            stage1.setScene(new Scene(root));
            stage1.show();
    }

    public void setUserInfo(User user) {
        if (user != null) {
            idLabel.setText(user.getUsername());
            nicknameLabel.setText(user.getNickname());
            personalInfoLabel.setText(user.getSignature());
            idLabel11.setText(user.getEmail());
            // 根据 user 的性别设置 sex Label 的文本
            //sex.setText(user.getGender() == "M" ? "男" : "女");
            if(user.getGender().equals("F")){
                sex.setText("女");
            }else if(user.getGender().equals("M")){
                sex.setText("男");
            }else{
                sex.setText("未知");
            }
            // 设置生日标签的文本
            LocalDate birthdayObj = user.getBirthday();
            //String birthdayText = String.format("%d-%02d-%02d", birthdayObj.getYear(), birthdayObj.getMonth(), birthdayObj.getDay());
            birthday.setText(String.valueOf(birthdayObj));
            // 根据用户在线状态设置 onlineStatusLabel 的文本
            System.out.println("用户现在的状态为："+user.getStatus());
            onlineStatusLabel.setText(user.getStatus().equals("online")?"在线" : "离线");
            // 设置用户头像 (确保 user 对象中有头像的URL)
            avatar.setImage(new Image(user.getAvatar()));
            // 设置创建时间标签文本
            LocalDate yearObj = user.getCreatedAt();
            //String yearText = String.format("%d-%02d-%02d", yearObj.getYear(), yearObj.getMonth(), yearObj.getDay());
            creationDateLabel.setText(String.valueOf(yearObj));
        }
    }
    public void editProfileButton1(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Personalinformation2.fxml"));
        Parent root = fxmlLoader.load();
        EditUserInfoController editUserInfoController = fxmlLoader.getController();
        System.out.println("1 "+currentUser);
        editUserInfoController.setCurrentUser(currentUser); // 将登录成功的用户信息传递给 EditUserInfoController
        editUserInfoController.updateScreenAvatar(currentUser.getAvatar());
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("编辑个人信息");
        stage.setScene(scene);
        stage.show();
    }
}
