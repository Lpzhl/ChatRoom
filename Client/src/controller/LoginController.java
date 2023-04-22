package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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


    private static final String LOGIN_CREDENTIALS_FILE = "loginCredentials.properties";


    @FXML
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void initialize() {
        Properties loginCredentials = readLoginCredentials();
        if (loginCredentials != null) {
            UserNameHistory.getItems().addAll(loginCredentials.stringPropertyNames());
            if (!UserNameHistory.getItems().isEmpty()) {
                String lastUser = UserNameHistory.getItems().get(UserNameHistory.getItems().size() - 1);
                UserNameInput.setText(lastUser);
                if (Rememberpassword.isSelected()) {
                    Userpassword.setText(loginCredentials.getProperty(lastUser));
                }
            }
        }
        Cleardata.setOnAction(e -> onClearData());

        UserNameHistory.setOnAction(e -> onSelectUserNameHistory());
    }


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
    public void Luck() {
        // 在这里实现登录逻辑
        // 登录按钮事件处理代码
        System.out.println("登入按钮被点击");
        String username = UserNameInput.getText().trim();
        String password = Userpassword.getText().trim();
        boolean loginSuccess = login(username, password);

        if (loginSuccess) {
            System.out.println("登录成功");
            String currentUser = UserNameInput.getText();
            if (Rememberpassword.isSelected()) {
                saveLoginCredentials(currentUser, Userpassword.getText());
            } else {
                saveLoginCredentials(currentUser, "");
            }
            // 跳转到其他场景，例如主界面
            //showNewScene("/fxml/Main.fxml", "主界面");
        } else {
            System.out.println("登录失败");
            showAlert(Alert.AlertType.ERROR, "错误", "用户名或密码错误");
        }
    }


    private boolean login(String username, String password) {
        boolean loginSuccess = false;
        try (Socket socket = new Socket("127.0.0.1", 6000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 检查Socket的状态
            if (socket.isClosed()) {
                System.out.println("Socket is closed before sending request to server");
            } else {
                System.out.println("Socket is open before sending request to server");
            }
            // 发送登录请求给服务端
            out.println("login:" + username + ":" + password);

            // 从服务端接收响应
            String response = in.readLine();
            if ("success".equals(response)) {
                loginSuccess = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "错误", "无法连接到服务器，请稍后再试");
        }
        return loginSuccess;
    }

    private Properties readLoginCredentials() {
        Properties loginCredentials = new Properties();
        try {
            File file = new File(LOGIN_CREDENTIALS_FILE);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                loginCredentials.load(fis);
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginCredentials;
    }

    private void saveLoginCredentials(String currentUser, String password) {
        Properties loginCredentials = readLoginCredentials();
        loginCredentials.setProperty(currentUser, password);
        try {
            FileOutputStream fos = new FileOutputStream(LOGIN_CREDENTIALS_FILE);
            loginCredentials.store(fos, null);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 当用户从ComboBox选择用户名时，将其填充到输入框中
    @FXML
    public void onSelectUserNameHistory() {
        String selectedUser = UserNameHistory.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            UserNameInput.setText(selectedUser);
            if (!Rememberpassword.isSelected()) {
                Userpassword.clear();
            } else {
                String savedPassword = getPasswordForUser(selectedUser);
                Userpassword.setText(savedPassword);
            }
        }
    }

    private String getPasswordForUser(String username) {
        Properties loginCredentials = readLoginCredentials();
        if (loginCredentials != null) {
            return loginCredentials.getProperty(username);
        }
        return "";
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

    public void UserNameInput(ActionEvent actionEvent) {

    }

    public void onClearData() {
        try {
            Properties loginCredentials = new Properties();
            FileOutputStream fos = new FileOutputStream(LOGIN_CREDENTIALS_FILE);
            loginCredentials.store(fos, null);
            fos.close();
            showAlert(Alert.AlertType.INFORMATION, "账号记录", "清空成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}