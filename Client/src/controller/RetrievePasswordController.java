package controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javafx.util.Pair;
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

import static Util.EmailUtil.generateRandomCode;

public class RetrievePasswordController {

    @FXML
    private PasswordField Resetpassword2;

    @FXML
    private Button cancel;

    @FXML
    private Button Obtainverification;

    @FXML
    private Button Sure;

    @FXML
    private PasswordField Resetpassword;

    @FXML
    private TextField Inputverification;

    @FXML
    private TextField UsernameID;

    @FXML
    private TextField Retrievepasswordemail;

    // 添加一个用于存储验证码及其过期时间的映射
    private Map<String, Pair<String, LocalDateTime>> generatedCodes = new HashMap<>();

    @FXML
    void UsernameID1(ActionEvent event) {

    }

    @FXML
    //输入邮箱
    void Retrievepasswordemail1(ActionEvent event) {

    }

    @FXML
    //输入新密码
    void Resetpassword1(ActionEvent event) {

    }

    @FXML
    //确认密码
    void Resetpassword2(ActionEvent event) {

    }

    private String generatedCode;
    @FXML
    void Obtainverification1(ActionEvent event) {
        String emailAddress = Retrievepasswordemail.getText();
        if (EmailUtil.isValidEmail(emailAddress)) {
            generatedCode = generateRandomCode();
            EmailUtil emailUtil = new EmailUtil(emailAddress, generatedCode);
            new Thread(emailUtil).start();
            EmailVerificationUtil.startCountdown(Obtainverification);

            // 添加生成的验证码和过期时间到映射中
            generatedCodes.put(emailAddress, new Pair<>(generatedCode, LocalDateTime.now().plusMinutes(3)));
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("请输入有效的电子邮件地址！");
            alert.showAndWait();
        }
    }
    @FXML
    //输入验证码
    void Inputverification1(ActionEvent event) {

    }

    @FXML
    //确认
    void Sure1(ActionEvent event) {
        String email = Retrievepasswordemail.getText().trim();
        String username = UsernameID.getText().trim();
        String newPassword = Resetpassword.getText().trim();
        String confirmPassword = Resetpassword2.getText().trim();
        String verificationCode = Inputverification.getText().trim();

        if (email.isEmpty() || username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() || verificationCode.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "错误", "所有字段都不能为空");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "错误", "两次密码输入不一致");
            return;
        }

        if (!EmailUtil.isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "错误", "无效的电子邮件地址");
            return;
        }

        if (EmailUtil.isCodeValid(email, verificationCode, generatedCodes)) {
            try (Socket socket = new Socket("127.0.0.1", 5000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // 发送重置密码请求给服务端
                out.println("resetPassword:" + email + ":" + username + ":" + newPassword + ":" + verificationCode);

                // 从服务端接收响应
                String response = in.readLine();
                if (response.equals("success")) {
                    showAlert(Alert.AlertType.INFORMATION, "成功", "密码重置成功");
                    // 跳转到登录页面
                    // ...
                } else {
                    showAlert(Alert.AlertType.ERROR, "错误", "密码重置失败，请检查您的信息是否正确");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            showAlert(Alert.AlertType.ERROR, "错误", "验证码错误或已过期");
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void cancel1(ActionEvent event) {
        try {
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
            Stage stage1 = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            stage.setTitle("聊天室");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}