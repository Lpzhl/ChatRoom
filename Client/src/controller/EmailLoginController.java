package controller;

import Util.EmailUtil;
import client.User;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javafx.stage.Stage;
import javafx.util.Pair;

import javax.swing.text.AsyncBoxView;

public class EmailLoginController {

    @FXML
    private Button cancel;

    @FXML
    private TextField Verificationcode;

    @FXML
    private Button Getverificationcode;

    @FXML
    private TextField Mailboxnumber;

    @FXML
    private Button Login;

    @FXML
    private ImageView cancel1;

    private String serverGeneratedCode; // 存储从服务器接收到的验证码

    // 添加一个用于存储验证码及其过期时间的映射
    private Map<String, Pair<String, LocalDateTime>> generatedCodes = new HashMap<>();

    @FXML
    void Mailboxnumber1(ActionEvent event) {

    }

    @FXML
    void Verificationcode1(ActionEvent event) {

    }



    // 向服务器端发送获取验证码请求
    @FXML
    void Getverificationcode1(ActionEvent event) {
        String email = Mailboxnumber.getText();
        // 首先判断QQ邮箱是否有效
        if (EmailUtil.isValidEmail(email)) {
            try (Socket socket = new Socket("127.0.0.1", 6000);
                 //字符打印流
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);//true表是开启自动刷新
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {//字符缓冲流，用来读取数据

                // 向服务端请求发送验证码
                out.println("email_verification:" + email);

                // 从服务端接收响应
                String response = in.readLine();
                //看看验证码长度是否为6
                if (response.length() == 6) {
                    serverGeneratedCode = response;//把服务端发送的验证码获取
                    // 添加生成的验证码和过期时间到映射中
                    System.out.println(serverGeneratedCode);
                    // 添加生成的验证码和过期时间到映射中
                    System.out.println("发送邮件的时间为"+LocalDateTime.now());
                    System.out.println("过期的时间为"+ LocalDateTime.now().plusMinutes(EmailUtil.CODE_VALIDITY_MINUTES));
                    // 将生成的验证码和过期时间映射进去
                    generatedCodes.put(email, new Pair<>(serverGeneratedCode, LocalDateTime.now().plusMinutes(EmailUtil.CODE_VALIDITY_MINUTES)));

                    // 发送之后 开始倒计时
                    EmailUtil.startCountdown(Getverificationcode);
                } else {
                    showAlert(Alert.AlertType.ERROR, "错误", "验证码发送失败，请稍后重试");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("请输入有效的电子邮件地址！");
            alert.showAndWait();
        }
    }


    //邮箱登录
    @FXML
    void Login1(ActionEvent event) {
        User user = null;
        // 获取用户输入的邮箱号 以及输入的验证码
        String email = Mailboxnumber.getText();
        String verificationCode = Verificationcode.getText();

        System.out.println("email: " + email);
        System.out.println("verificationCode: " + verificationCode);

        if (email.isEmpty() || verificationCode.isEmpty()) {
            System.out.println("所有字段都不能为空");
            showAlert(Alert.AlertType.ERROR, "错误", "所有字段都不能为空");
            return;
        }
        if (!EmailUtil.isValidEmail(email)) {
            System.out.println("无效的QQ邮箱");
            showAlert(Alert.AlertType.ERROR, "错误", "无效的QQ邮箱");
            return;
        }

        // 向服务端发送 检查验证码是否正确的请求
        System.out.println("Generated codes: " + generatedCodes);//Generated codes: {2212384795@qq.com=156001=2023-05-03T15:16:34.785}

        //验证验证码是否有效
        if (EmailUtil.isCodeValid(email, verificationCode, generatedCodes)) {
            // 验证码正确，检查邮箱是否存在于数据库中
            try (Socket socket = new Socket("127.0.0.1", 6000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("emailLogin:" + email);

                String response = in.readLine();
                System.out.println(response);
                String[] resposeParts = response.split(":",2);
                if ("success".equals(resposeParts[0])) {
                    System.out.println(resposeParts[1]);
                    Gson gson = new Gson();
                    user = gson.fromJson(resposeParts[1], User.class);
                    System.out.println("反序列化后： "+user);
                    showAlert(Alert.AlertType.INFORMATION, "成功", "登录成功");
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Chatmenuinterface.fxml"));
                    Parent root = fxmlLoader.load();
                    ChatRoomController chatController = fxmlLoader.getController();
                    chatController.setCurrentUser(user);
                    chatController.updateHomeScreenAvatar(user.getAvatar());
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("聊天室");
                    stage.setScene(scene);
                    Stage stage1 = (Stage) Login.getScene().getWindow();
                    stage.show();
                    stage1.close();
                    //跳转 聊天室界面
                } else if ("error".equals(resposeParts[0])) {
                    System.out.println("登入回到的信息："+resposeParts[1]);
                    if ("该用户已经登录".equals(resposeParts[1])) {
                        // 在这里处理 "用户已登录" 错误
                        showAlert(Alert.AlertType.ERROR, "错误", "该用户已经登录");
                    }
                }
                else {
                    showAlert(Alert.AlertType.ERROR, "错误", "该邮箱未注册ID号，登录失败");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Incorrect code");
            showAlert(Alert.AlertType.ERROR, "错误", "验证码错误或已过期");
        }
    }



    @FXML
    void cancel1(ActionEvent event) {
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

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}