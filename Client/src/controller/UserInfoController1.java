package controller;

import client.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.LocalDate;

public class UserInfoController1 {

    @FXML
    private Label birthday;

    @FXML
    private Label creationDateLabel1;

    @FXML
    private Label idLabel11;

    @FXML
    private Label sex11;

    @FXML
    private Label nicknameLabel;

    @FXML
    private Label personalInfoLabel;

    @FXML
    private Label sex;

    @FXML
    private Label idLabel1;

    @FXML
    private ImageView avatar;

    @FXML
    private Label idLabel111;

    @FXML
    private Label idLabel;

    @FXML
    private Label onlineStatusLabel;

    @FXML
    private Label birthday1;

    @FXML
    private Label creationDateLabel;

    @FXML
    private Label idLabel1111;

    @FXML
    private Label sex1;

    public void setUserInfo(User user) {
        if (user != null) {
            idLabel.setText(user.getUsername());
            nicknameLabel.setText(user.getNickname());
            personalInfoLabel.setText(user.getSignature());
            idLabel11.setText(user.getEmail());
            // 根据 user 的性别设置 sex Label 的文本
            //sex.setText(user.getGender() == "M" ? "男" : "女");
            if(user.getGender().equals("M")){
                sex.setText("男");
            }else if(user.getGender().equals("F")){
                sex.setText("女");
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

}
