package controller;

import client.Group;
import client.User;
import com.google.gson.Gson;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;

public class GroupInformation1 {

    @FXML
    private Button cancel;

    @FXML
    private Button Sure;

    @FXML
    private Button HomePage;

    @FXML
    private ImageView avatar;

    @FXML
    private Button Member;

    @FXML
    private ListView<User> people;

    @FXML
    private TextArea Introduce;

    @FXML
    private TextField nickname;

    private Group group;

    private String newAvatarFilePath;
    public ObjectProperty<Group> updatedGroupProperty = new SimpleObjectProperty<>();

    private User currentUser;
    public void setGroup(Group group,User currentUser) {
        this.group = group;
        this.newAvatarFilePath = group.getAvatar();
        this.currentUser = currentUser;
        System.out.println("用户是："+currentUser.getId());
        // 设置群组成员列表
        System.out.println("群成员："+group.getMembers());
        avatar.setImage(new Image(group.getAvatar()));  // getAvatar 方法返回头像的 URL
        ObservableList<User> memberList = FXCollections.observableArrayList(group.getMembers());
        people.setItems(memberList);
    }
    @FXML
    void cancel1(ActionEvent event) {
        Member.getScene().getWindow().hide();
    }

    private int currentGroupId;  // 新添加的成员变量
    public void  setcurrentGroupId(int currentGroupId){
        System.out.println("群号是1："+currentGroupId);
        this.currentGroupId = currentGroupId;
    }

    @FXML
    void Member1(ActionEvent event) {
        people.setVisible(true);
        System.out.println("成员按钮被点击");
        avatar.setVisible(false);
        Sure.setVisible(false);
        cancel.setVisible(false);

    }

    @FXML
    void HomePage1(ActionEvent event) {;
        people.setVisible(false);
        avatar.setVisible(true);
        Sure.setVisible(true);
        cancel.setVisible(true);
        System.out.println("首页被点击");
    }

    @FXML
    void Sure1(ActionEvent event) throws IOException {
        //获取更新后的群聊信息
        int groupdId = group.getId();
        System.out.println("头像："+newAvatarFilePath);
       // String newAvatarPath = newAvatarFilePath == null || newAvatarFilePath.isEmpty() ? group.getAvatar() : "file:/"+newAvatarFilePath;
        String newAvatarPath;
        if (newAvatarFilePath == null || newAvatarFilePath.isEmpty()) {
            newAvatarPath = group.getAvatar();
        } else {
            newAvatarPath = newAvatarFilePath.startsWith("file:/") ? newAvatarFilePath : "file:/" + newAvatarFilePath;
        }
        String newNickname = nickname.getText();
        if(newNickname == null|| newNickname.isEmpty()){
            newNickname = group.getName();
        }
        String newIntroduce = Introduce.getText();
        if(newIntroduce == null || newIntroduce.isEmpty()){
            newIntroduce = group.getDescription();
        }
        Group group1 = new Group(groupdId,newAvatarPath,newNickname,newIntroduce);
        try(Socket socket = new Socket("127.0.0.1", 6000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            Gson gson = new Gson();
            String request = gson.toJson(group1);
            out.println("update1: "+ request);

            String response = in.readLine();
            System.out.println("回来的信息："+response);
            if("success".equals(response)){
                //更新成功
                group.setAvatar(newAvatarPath);
                group.setName(newNickname);
                group.setDescription(newIntroduce);
                updatedGroupProperty.set(group);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("成功");
                alert.setHeaderText(null);
                alert.setContentText("修改成功");
                alert.showAndWait();
                Member.getScene().getWindow().hide();
            }else{
                System.out.println("更新失败");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

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
    void initialize() {
        people.setCellFactory(param -> new GroupInformation1.UserCell());
    }
    class UserCell extends ListCell<User> {
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

            // 创建一个上下文菜单
            ContextMenu contextMenu = new ContextMenu();

            // 创建 "设置管理员" 菜单项
            MenuItem setAdminItem = new MenuItem("设置管理员");
            setAdminItem.setOnAction(event -> {
                User user = getItem();
                // 在这里添加设置管理员的代码
                try(Socket socket = new Socket("127.0.0.1", 6000);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
                    System.out.println("设置管理员:"+"群号："+group.getId()+"用户："+user);
                    out.println("setAdministrator:"+ group.getId()+":"+user.getId()+":"+currentUser.getId());

                    String response = in.readLine();
                    System.out.println("回来的信息："+response);
                    if("success".equals(response)){
                        System.out.println(user.getUsername() + " 已设置为管理员");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("成功");
                        alert.setHeaderText(null);
                        alert.setContentText(user.getUsername()+" 已设置为管理员");
                        alert.showAndWait();
                        Member.getScene().getWindow().hide();
                    } else if("6".equals(response)) {
                        System.out.println("失败");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("失败");
                        alert.setHeaderText(null);
                        alert.setContentText("操作失败 不能操作自己");
                        alert.showAndWait();
                        Member.getScene().getWindow().hide();
                    } else{
                        System.out.println("设置失败");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("失败");
                        alert.setHeaderText(null);
                        alert.setContentText("操作失败");
                        alert.showAndWait();
                        Member.getScene().getWindow().hide();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            });

            // 创建 "提出群聊" 菜单项
            MenuItem kickOutItem = new MenuItem("踢出群聊");
            kickOutItem.setOnAction(event -> {
                User user = getItem();
                // 在这里添加提出群聊的代码
                try(Socket socket = new Socket("127.0.0.1", 6000);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
                    out.println("KickOutGroup:"+ group.getId()+":"+user.getId()+":"+currentUser.getId());
                    System.out.println("踢人操作："+group.getId()+":"+user.getId()+":"+currentUser.getId());

                    String response = in.readLine();
                    System.out.println("回来的信息："+response);
                    if("success".equals(response)){
                        System.out.println(user.getUsername() + " 已被踢出群聊");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("成功");
                        alert.setHeaderText(null);
                        alert.setContentText(user.getUsername()+" 已被踢出群聊");
                        alert.showAndWait();
                        Member.getScene().getWindow().hide();
                    }else if("6".equals(response)){
                        System.out.println("失败");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("失败");
                        alert.setHeaderText(null);
                        alert.setContentText("操作失败 不能踢自己");
                        alert.showAndWait();
                        Member.getScene().getWindow().hide();
                    } else{
                        System.out.println("失败");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("失败");
                        alert.setHeaderText(null);
                        alert.setContentText("操作失败");
                        alert.showAndWait();
                        Member.getScene().getWindow().hide();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            });

            MenuItem setMemberItem = new MenuItem("设置成员");
            setMemberItem.setOnAction(event -> {
                User user = getItem();
                // 在这里添加设置成员的代码
                try(Socket socket = new Socket("127.0.0.1", 6000);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
                    out.println("setMember:"+ group.getId()+":"+user.getId()+":"+currentUser.getId());

                    String response = in.readLine();
                    System.out.println("回来的信息："+response);
                    if("success".equals(response)){
                        System.out.println(user.getUsername() + " 已设置为成员");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("成功");
                        alert.setHeaderText(null);
                        alert.setContentText(user.getUsername()+" 已设置为成员");
                        alert.showAndWait();
                        Member.getScene().getWindow().hide();
                    }else if("6".equals(response)) {
                        System.out.println("失败");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("失败");
                        alert.setHeaderText(null);
                        alert.setContentText("操作失败 不能操作自己");
                        alert.showAndWait();
                        Member.getScene().getWindow().hide();
                    } else{
                        System.out.println("设置失败");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("失败");
                        alert.setHeaderText(null);
                        alert.setContentText("设置失败");
                        alert.showAndWait();
                        Member.getScene().getWindow().hide();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            });


            // 将菜单项添加到上下文菜单中
            contextMenu.getItems().addAll(setAdminItem, kickOutItem, setMemberItem);

            // 将上下文菜单设置为 ListCell 的上下文菜单
            this.setContextMenu(contextMenu);
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

