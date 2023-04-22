package controller;

import Util.EmailUtil;
import Util.EmailVerificationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static Util.EmailUtil.generateRandomCode;

public class RegisterController {

    @FXML
    private Button cancel;

    @FXML
    private TextField PutCode;

    @FXML
    private PasswordField userpassword1;

    @FXML
    private TextField Email;

    @FXML
    private Button Register;

    @FXML
    private Button GetEmail;

    @FXML
    private TextField username;

    @FXML
    private PasswordField userpassword;
    @FXML
    private String generatedCode;

    // 添加一个用于存储验证码及其过期时间的映射
    private Map<String, Pair<String, LocalDateTime>> generatedCodes = new HashMap<>();
    @FXML
    void username1(ActionEvent event) {

    }

    @FXML
    void Email1(ActionEvent event) {

    }
    public void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void GetEmail1(ActionEvent event) {
        String emailAddress = Email.getText();
        if (EmailUtil.isValidEmail(emailAddress)) {
            generatedCode = generateRandomCode();
            EmailUtil emailUtil = new EmailUtil(emailAddress, generatedCode);
            new Thread(emailUtil).start();
            EmailUtil.startCountdown(GetEmail);

            // 添加生成的验证码和过期时间到映射中
            generatedCodes.put(emailAddress, new Pair<>(generatedCode, LocalDateTime.now().plusMinutes(5)));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("请输入有效的电子邮件地址！");
            alert.showAndWait();
        }
    }

    /*
    这个示例方法使用 SecureRandom 类生成一个介于 0 和 999999 之间的随机整数（含两端点），
    然后将该整数转换为一个 6 位数的字符串（不足 6 位时在左侧补 0）。
    这样就可以得到一个随机的 6 位数验证码。
     */

    @FXML
// 在 RegisterController 类中的 register() 方法中，使用 Socket 连接服务端
    private void Register1() {
        String email = Email.getText().trim();
        String userNameInput = username.getText().trim(); // 修改变量名称
        String password = userpassword.getText().trim();
        String confirmPassword = userpassword1.getText().trim();
        String verificationCode = GetEmail.getText().trim();

        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "错误", "密码必须包含英文字母和数字，长度在8-18之间");
            return;
        }
        if (email.isEmpty() || userNameInput.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || verificationCode.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "错误", "所有字段都不能为空");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "错误", "两次密码输入不一致");
            return;
        }

        if (!EmailUtil.isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "错误", "无效的电子邮件地址");
            return;
        }
        if (EmailUtil.isCodeValid(email, verificationCode, generatedCodes)) {
            try (Socket socket = new Socket("127.0.0.1", 6000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // 发送注册请求给服务端
                out.println("register:" + email + ":" + username + ":" + password + ":" + verificationCode);

                // 从服务端接收响应
                String response = in.readLine();
                if (response.equals("success")) {
                    showAlert(Alert.AlertType.INFORMATION, "成功", "注册成功");
                    // 跳转到登录页面
                    // ...
                } else {
                    showAlert(Alert.AlertType.ERROR, "错误", "注册失败，请检查您的信息是否正确");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            showAlert(Alert.AlertType.ERROR, "错误", "验证码错误或已过期");
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        try {
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
            Stage stage1 = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            stage1.setTitle("聊天室");
            stage1.setScene(new Scene(root));
            stage1.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void PutCode1(ActionEvent event) {

    }

    @FXML
    void userpassword1(ActionEvent event) {

    }

    //用户
    private boolean isValidUsername(String username) {
        return username.matches("\\d{10}");
    }

    private boolean isValidPassword(String password) {
        return password.matches("(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,18}");
    }


}

