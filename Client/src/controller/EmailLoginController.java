package controller;

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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static Util.EmailUtil.generateRandomCode;

public class EmailLoginController {
    @FXML
    private TextField Mailboxnumber;
    @FXML
    private TextField Verificationcode;
    @FXML
    private Button Getverificationcode;
    @FXML
    private Button Login;
    @FXML
    private Button cancel;

    private Map<String, Pair<String, LocalDateTime>> generatedCodes = new HashMap<>();
    public void Mailboxnumber1(ActionEvent event) {
    }

    public void Verificationcode1(ActionEvent event) {
    }

    public void Getverificationcode1(ActionEvent event) {
        String emailAddress = Mailboxnumber.getText();
        if (EmailUtil.isValidEmail(emailAddress)) {
            String generatedCode = generateRandomCode();
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EmailUtil.CODE_VALIDITY_MINUTES);
            // 将生成的验证码发送到服务器端
            EmailUtil emailUtil = new EmailUtil(emailAddress, generatedCode);
            new Thread(emailUtil).start();
            EmailUtil.startCountdown(Getverificationcode);
        } else {
            // 显示错误提示，要求用户输入有效的电子邮件地址
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("请输入有效的电子邮件地址！");
            alert.showAndWait();
        }
    }

    public void Login1(ActionEvent event) {
        String email = Mailboxnumber.getText().trim();
        String verificationCode = Verificationcode.getText().trim();

        if (email.isEmpty() || verificationCode.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "错误", "所有字段都不能为空");
            return;
        }

        if (!EmailUtil.isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "错误", "无效的电子邮件地址");
            return;
        }

        System.out.println("generatedCodes is null: " + (generatedCodes == null));
        System.out.println("generatedCodes.get(email) is null: " + (generatedCodes.get(email) == null));

        if (EmailUtil.isCodeValidWithinValidityPeriod(generatedCodes.get(email).getValue())){
            // 验证成功，执行登录操作
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
                out.println("emailLogin:" + email);
                System.out.println("发送请求: emailLogin:" + email);

                // 从服务端接收响应
                String response = in.readLine();
                System.out.println("接收到的响应: " + response);
                if (response.equals("success")) {
                    System.out.println("登入成功！");
                    // 切换界面
                } else {
                    System.out.println("登入失败！");
                    showAlert(Alert.AlertType.ERROR, "登录失败", "该邮箱未绑定账号!!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 验证失败，提示用户验证码无效或已过期
            showAlert(Alert.AlertType.ERROR, "错误", "验证码无效或已过期，请重新获取");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void cancel1(ActionEvent event) {
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
}
