package controller;

import client.User;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class EditUserInfoController {

    public ImageView avatar;
    @FXML
    private TextField nicknameTextField;
    @FXML
    private ComboBox<String> sexComboBox;
    @FXML
    private DatePicker birthdayDatePicker;
    @FXML
    private TextArea personalInfoTextArea;

    private String currentAvatarPath;
    private String newAvatarFilePath = currentAvatarPath;

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 6000;
    private Gson gson = new Gson();
    private User currentUser;

   public void setCurrentUser(User user) {
        currentUser = user;
       System.out.println(user);
   }


   //点击编辑信息时提前更新将要更新的界面图片
    public void setUserAvatar(Image avatar1) {
        avatar.setImage(avatar1);
    }
    public void updateScreenAvatar(String avatarUrl) {
        Image avatarImage = new Image(avatarUrl);
        setUserAvatar(avatarImage);
    }


    @FXML
    void saveButtonAction(ActionEvent event) {
        // 获取更新后的用户信息
        String username1 = currentUser.getUsername();
        String newAvatarPath = newAvatarFilePath == null || newAvatarFilePath.isEmpty() ? currentUser.getAvatar() : "file:/"+newAvatarFilePath;
        String newNickname = nicknameTextField.getText();
        if (newNickname == null || newNickname.isEmpty()) {
            newNickname = currentUser.getNickname();
        }
        String newGender = sexComboBox.getValue();
        if (newGender == null || newGender.isEmpty()) {
            newGender = currentUser.getGender();
        }
        LocalDate newBirthday = birthdayDatePicker.getValue();
        if (newBirthday == null) {
            newBirthday = currentUser.getBirthday();
        }
        String newSignature = personalInfoTextArea.getText();
        if (newSignature == null || newSignature.isEmpty()) {
            newSignature = currentUser.getSignature();
        }

        //String newNickname = nicknameTextField==null||nicknameTextField.ise();
        //String newGender = sexComboBox.getValue();
        //LocalDate newBirthday = birthdayDatePicker.getValue();
        //String newSignature = personalInfoTextArea.getText();
        // 更新数据库向服务端发送请求
        User user = new User(username1,newNickname, newAvatarPath, newGender, newSignature, newBirthday);
        System.out.println("头像 :"+newAvatarPath);
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String request = gson.toJson(user);
            System.out.println("修改资料： "+request);
            out.println("update:" + request);
            String response = in.readLine();
            System.out.println("接收："+response);
            if ("success".equals(response)) {
                // 更新成功，关闭当前窗口
                nicknameTextField.getScene().getWindow().hide();
                // 同时更新当前用户信息
                // 更新用户信息
                currentUser.setAvatar(newAvatarPath);
                currentUser.setNickname(newNickname);
                currentUser.setSignature(newSignature);
                currentUser.setGender(newGender);
                currentUser.setBirthday(newBirthday);
            } else {
                // TODO: 显示错误信息
                System.out.println("更新失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void avatarClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择头像");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.gif", "*.bmp")
        );
        File selectedFile = fileChooser.showOpenDialog(avatar.getScene().getWindow());
        if (selectedFile != null) {
            newAvatarFilePath = copyImageFile(selectedFile);
            avatar.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    private String copyImageFile(File sourceFile) {
        try {
            Path targetDirectory = Paths.get("E:\\java\\Java练习\\测试图片");
            Files.createDirectories(targetDirectory);
            String fileName = sourceFile.getName();
            Path targetPath = targetDirectory.resolve(fileName);
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    void cancelButtonAction(ActionEvent event) {
        // 取消修改个人信息，关闭当前窗口
        nicknameTextField.getScene().getWindow().hide();
    }
}

