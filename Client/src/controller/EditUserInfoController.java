package controller;

import client.User;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        }else if(!isValidUserName(newNickname)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("用户名允许包含字母、数字，长度为3-18");
            alert.showAndWait();
            return;
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
        // 更新数据库向服务端发送请求
        User user = new User(username1,newNickname, newAvatarPath, newGender, newSignature, newBirthday);
        System.out.println("头像 :"+newAvatarPath);
        try (Socket socket = new Socket("127.0.0.1", 6000);
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


    //1. `avatarClicked(MouseEvent event)`: 这是一个事件处理器，当用户点击头像（avatar）时会被触发。
    @FXML
    void avatarClicked(MouseEvent event) {
       //2. `FileChooser fileChooser = new FileChooser();`: 创建一个 `FileChooser` 对象。这个对象能让用户在图形用户界面 (GUI) 中选择文件。
        FileChooser fileChooser = new FileChooser();
        //3. `fileChooser.setTitle("选择头像");`: 设置 `FileChooser` 的标题为 "选择头像"。
        fileChooser.setTitle("选择头像");
        //4. `fileChooser.getExtensionFilters().addAll(...)`: 设置文件过滤器，这样 `FileChooser` 只会显示用户可以选择的文件类型。在这个例子中，用户只能选择扩展名为 `.png`、`.jpg`、`.gif` 或 `.bmp`等。
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.gif", "*.bmp","*.jpeg")
        );
        /*
        5. `File selectedFile = fileChooser.showOpenDialog(avatar.getScene().getWindow());`: 显示 `FileChooser` 对话框，让用户选择文件。用户选择的文件会被保存在 `selectedFile` 变量中。
         */
        File selectedFile = fileChooser.showOpenDialog(avatar.getScene().getWindow());
        if (selectedFile != null) {
            //7. `newAvatarFilePath = copyImageFile(selectedFile);`: 调用 `copyImageFile` 方法，将用户选择的文件复制到指定的目录，并返回新文件的路径。这个路径会被保存在 `newAvatarFilePath` 变量中。
            newAvatarFilePath = copyImageFile(selectedFile);
//    8. `avatar.setImage(new Image(selectedFile.toURI().toString()));`: 这行代码将用户选择的图片设为头像。这里，`new Image(selectedFile.toURI().toString())` 将用户选择的文件路径转换为 `Image` 对象，然后用 `setImage()` 方法将头像设置为这个新的 `Image` 对象。
            avatar.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    // 用于将源文件（`sourceFile`）复制到指定的目录。
    private String copyImageFile(File sourceFile) {
        try {
            //1. `Path targetDirectory = Paths.get("E:\\java\\Java练习\\测试图片");`: 这行代码定义了目标目录的路径。
            Path targetDirectory = Paths.get("E:\\java\\Java练习\\ChatRoom\\测试图片");
            //2. `Files.createDirectories(targetDirectory);`: 这行代码确保目标目录存在。如果目录不存在，它会被创建。
            Files.createDirectories(targetDirectory);
            //3. `String fileName = sourceFile.getName();`: 这行代码获取源文件的文件名。
            String fileName = sourceFile.getName();
            //4. `Path targetPath = targetDirectory.resolve(fileName);`: 这行代码获取目标文件的完整路径。它通过将文件名添加到目标目录的路径来实现。
            Path targetPath = targetDirectory.resolve(fileName);
            //5. `Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);`: 这行代码将源文件复制到目标路径。
            // 如果目标文件已经存在，它会被替换（因为使用了 `StandardCopyOption.REPLACE_EXISTING` 选项）。
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            //6. `return targetPath.toString();`: 这行代码返回新文件的路径。
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

    //验证用户名是否正确
    private boolean isValidUserName(String username){
        return username.matches("^[\u4e00-\u9fa5\\d]{3,18}$");
    }
}

