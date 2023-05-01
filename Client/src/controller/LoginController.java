package controller;

import client.User;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Properties;


//登入界面控制器类

public class LoginController {
    @FXML
    private TextField UserNameInput;
    @FXML
    private PasswordField Userpassword;
    @FXML
    private ComboBox<String> UserNameHistory;
    @FXML
    private Button Loin;
    @FXML
    private Button Retrievepassword;
    @FXML
    private Button Register;
    @FXML
    private Button EmailIogin;
    @FXML
    private CheckBox Rememberpassword;
    @FXML
    private Button Cleardata;
    @FXML
    private Button CheckOpen;
    @FXML
    private ImageView Closeeye;
    @FXML
    private ImageView Openeye;
    @FXML
    private TextField Userpasswordi;



    // 登录文件名
    private static final String LOGIN_FILE = "loginCredentials.properties";
    private boolean passwordVisible = false;
    private User currentUser;



    // 弹出警告框的方法
    @FXML
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 初始化方法，在FXML加载完毕后自动调用
    public void initialize() {
        // 读取登录文件，将其中的用户名填充到ComboBox
        Properties loginCredentials = readLoginFxml();
        if (loginCredentials != null) {
            //将 loginCredentials 中所有的属性名都添加到 UserNameHistory 列表中。
            UserNameHistory.getItems().addAll(loginCredentials.stringPropertyNames());
            if (!UserNameHistory.getItems().isEmpty()) {
                //如果UserNameHistory中有用户名，它会获取最后一个用户名，并将其设置为用户名输入框UserNameInput的文本内容。
                String lastUser = UserNameHistory.getItems().get(UserNameHistory.getItems().size() - 1);
                //String lastUser = UserNameHistory.getItems().get(0);
                UserNameInput.setText(lastUser);
                //如果"记住密码"复选框Rememberpassword被选中，代码会从loginCredentials中获取最后一个用户名对应的密码，并将其设置为密码输入框Userpassword的文本内
                if (Rememberpassword.isSelected()) {
                    Userpassword.setText(loginCredentials.getProperty(lastUser));
                }
            }
        }

        //它主要是为了在 JavaFX 中设置 UI 控件的事件处理器
        // 为清空数据按钮设置事件处理器。     设置了一个事件处理器，当用户点击这个按钮时会触发 onClearData() 方法。
        Cleardata.setOnAction(e -> onClearData());

        // 为ComboBox设置事件处理器，以便在用户选择不同的用户名时更新用户名输入框和密码输入框
        UserNameHistory.setOnAction(e -> onSelectUserNameHistory());
    }


    //界面跳转
    private void showNewScene(String fxmlFileName,String name,Button button) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(name);
            Stage stage2 = (Stage) button.getScene().getWindow();
            stage2.close();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 当用户点击登录按钮时触发
    @FXML
// 当用户点击登录按钮时触发
    public void Luck() throws IOException {
        // 登录按钮事件处理代码
        System.out.println("登入按钮被点击");
        String username = UserNameInput.getText().trim();
        String password = Userpassword.getText().trim();
        User user = login(username, password);

        if (user != null) {
            System.out.println("登录成功");
            String currentUser = UserNameInput.getText();
            this.currentUser = user; // 保存登录成功的用户信息
            // 如果用户选择了"记住密码"选项
            if (Rememberpassword.isSelected()) {
                // 将当前用户名和密码保存到登录文件中
                saveLoginCredentials(currentUser, Userpassword.getText());
            } else {
            // 否则，仅保存用户名
                saveLoginCredentials(currentUser, "");
            }
            // 跳转到其他场景，例如主界面
            //showNewScene("/fxml/Main.fxml", "主界面");
            // 将当前登录的用户传递给聊天室控制器
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Chatmenuinterface.fxml"));
            Parent root = fxmlLoader.load();
            PleaseProvideController chatController = fxmlLoader.getController();
            chatController.setCurrentUser(user);
            chatController.updateHomeScreenAvatar(user.getAvatar());
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("聊天室");
            stage.setScene(scene);
            stage.show();
            Stage stage2 = (Stage) Loin.getScene().getWindow();
            stage2.close();
        } else {
            System.out.println("登录失败");
            showAlert(Alert.AlertType.ERROR, "错误", "用户名或密码错误");
        }
    }


    private User login(String username, String password) {
        User user = null;
        try (Socket socket = new Socket("127.0.0.1", 6000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            if (socket.isClosed()) {
                System.out.println("Socket is closed before sending request to server");
            } else {
                System.out.println("Socket is open before sending request to server");
            }
            out.println("login:" + username + ":" + password);
            String response = in.readLine();
            System.out.println(response);
            String[] responseParts = response.split(":", 2);
            if ("success".equals(responseParts[0])) {
                System.out.println(responseParts[1]);
                Gson gson = new Gson();
                user = gson.fromJson(responseParts[1], User.class);
                System.out.println("反序列化后："+user);
            }
        } catch (IOException e) {
            System.err.println("网络错误: " + e.getMessage());
        }
        return user;
    }

    // 从登录文件中读取登录信息
    private Properties readLoginFxml() {
        // 创建一个Properties对象，用于存储登录信息
        Properties loginCredentials = new Properties();
        try {
            // 创建一个File对象，用于表示登录文件
            File file = new File(LOGIN_FILE);
            // 如果登录文件存在
            if (file.exists()) {
                // 创建一个FileInputStream对象，用于读取登录文件的内容
                FileInputStream fis = new FileInputStream(file);
                // 从文件输入流中加载登录信息到Properties对象
                loginCredentials.load(fis);
                // 关闭文件输入流
                fis.close();
            }
        } catch (Exception e) {
            // 如果在读取登录文件过程中出现异常，打印堆栈跟踪信息
            e.printStackTrace();
        }
        // 返回存储登录信息的Properties对象
        return loginCredentials;
    }

    // 将登录信息保存到登录文件中
    private void saveLoginCredentials(String currentUser, String password) {
        // 从登录文件中读取登录信息
        Properties loginCredentials = readLoginFxml();
        // 将当前用户的用户名和密码添加到登录信息中
        loginCredentials.setProperty(currentUser, password);
        try {
            // 创建一个FileOutputStream对象，用于将登录信息写入登录文件
            FileOutputStream fos = new FileOutputStream(LOGIN_FILE);
            // 将登录信息从Properties对象存储到文件输出流中
            loginCredentials.store(fos, null);
            // 关闭文件输出流
            fos.close();
        } catch (Exception e) {
            // 如果在保存登录信息过程中出现异常，打印堆栈跟踪信息
            e.printStackTrace();
        }
    }

    // 当用户从ComboBox选择用户名时，将其填充到输入框中
    @FXML
    public void onSelectUserNameHistory() {
        // 获取用户从ComboBox中选择的用户名
        String selectedUser = UserNameHistory.getSelectionModel().getSelectedItem();
        // 如果用户选择了一个用户名
        if (selectedUser != null) {
            // 将选定的用户名设置为用户名输入框的文本内容
            UserNameInput.setText(selectedUser);
            // 如果"记住密码"选项未被选中
            if (!Rememberpassword.isSelected()) {
                // 清除密码输入框的内容
                Userpassword.clear();
            } else {
                // 获取选定用户名对应的保存的密码
                String savedPassword = getPasswordForUser(selectedUser);
                // 将保存的密码设置为密码输入框的文本内容
                Userpassword.setText(savedPassword);
            }
        }
    }

    // 获取指定用户名对应的密码
    private String getPasswordForUser(String username) {
        // 从登录文件中读取登录信息
        Properties loginCredentials = readLoginFxml();
        // 如果登录信息不为空
        if (loginCredentials != null) {
            // 根据指定的用户名从登录信息中获取对应的密码，并返回
            return loginCredentials.getProperty(username);
        }
        // 如果登录信息为空，返回空字符串
        return "";
    }


    //清空记录
    public void onClearData() {
        try {
            Properties loginFxml = new Properties();
            FileOutputStream fos = new FileOutputStream(LOGIN_FILE);
            loginFxml.store(fos, null);
            fos.close();
            showAlert(Alert.AlertType.INFORMATION, "账号记录", "清空成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //查看密码按钮
    public void CheckOpen1() {
        System.out.println("查看密码按钮被点击");
        if (!passwordVisible) {
            // 显示密码
            Userpasswordi.setText(Userpassword.getText());
            Userpasswordi.setVisible(true);
            Userpassword.setVisible(false);

            // 切换图片
            Closeeye.setVisible(false);
            Openeye.setVisible(true);
        } else {
            // 隐藏密码
            Userpassword.setText(Userpasswordi.getText());
            Userpassword.setVisible(true);
            Userpasswordi.setVisible(false);

            // 切换图片
            Openeye.setVisible(false);
            Closeeye.setVisible(true);
        }
        passwordVisible = !passwordVisible;
    }




    public void UserNameInput(ActionEvent actionEvent) {

    }

    public void Userpasswordi1(ActionEvent actionEvent) {

    }

    // 当用户在用户名输入框按下回车时触发
    @FXML
    public void Username1() {
        // 在这里实现用户名输入框的事件处理逻辑
        System.out.println("用户名输入框被点击");
    }

    // 当用户在密码输入框按下回车时触发
    @FXML
    public void Userpassword1() {
        // 在这里实现密码输入框的事件处理逻辑
        System.out.println("密码输入框被点击");
    }

    // 当用户点击找回密码按钮时触发
    @FXML
    public void Retrievepassword1() {
        System.out.println("找回密码按钮被点击");
        // 在这里实现找回密码的逻辑
        showNewScene("/fxml/RetrievePassword.fxml","找回密码",Retrievepassword);
    }

    // 当用户点击注册账号按钮时触发
    @FXML
    public void Register1() {
        // 在这里实现注册账号的逻辑
        System.out.println("注册账号按钮被点击");
        showNewScene("/fxml/Register.fxml","注册账号",Register);
    }

    // 当用户点击邮箱登录按钮时触发
    @FXML
    public void EmailIogin1() {
        // 在这里实现邮箱登录的逻辑
        System.out.println("邮箱登录按钮被点击");
        showNewScene("/fxml/EmailLogin.fxml","邮箱登入",EmailIogin);
    }

    // 当用户点击记住密码复选框时触发
    @FXML
    public void Rememberpassword1() {
        // 在这里实现记住密码的逻辑
        System.out.println("记住密码被点击");
    }
}