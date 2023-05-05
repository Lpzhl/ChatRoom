package controller;

import Util.EmailUtil;
import Util.SnowflakeIdWorker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.swing.*;
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
    private TextField nickname;

    @FXML
    private PasswordField userpassword;
    @FXML
    private String generatedCode;
    @FXML
    private TextField Userpasswordi;
    @FXML
    private TextField Userpasswordi2;

    @FXML
    private ImageView Openeye;
    @FXML
    private ImageView Closeeye;

    @FXML
    private Button CheckOpen;
    @FXML
    private Button CheckOpen1;
    @FXML
    private ImageView Openeye1;
    @FXML
    private ImageView Closeeye1;

    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0, 0);
    private boolean passwordVisible = false;

    // 添加一个用于存储验证码及其过期时间的映射
    private Map<String, Pair<String, LocalDateTime>> generatedCodes = new HashMap<>();
    @FXML
    void nickname1(ActionEvent event) {

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
        System.out.println("输入的QQ邮箱是:"+emailAddress);
        if (EmailUtil.isValidEmail(emailAddress)) {
            try (Socket socket = new Socket("127.0.0.1", 6000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                // 向服务端请求发送验证码
                out.println("email_verification:" + emailAddress);

                // 从服务端接收响应
                String response = in.readLine();
                if (response.length() == 6) {
                    generatedCode = response;
                    // 添加生成的验证码和过期时间到映射中
                    // 开始倒计时
                    EmailUtil.startCountdown(GetEmail);
                    System.out.println("发送邮件的时间为"+LocalDateTime.now());
                    System.out.println("过期的时间为"+ LocalDateTime.now().plusMinutes(EmailUtil.CODE_VALIDITY_MINUTES));
                    generatedCodes.put(emailAddress, new Pair<>(generatedCode, LocalDateTime.now().plusMinutes(EmailUtil.CODE_VALIDITY_MINUTES)));
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

    /*
    这个示例方法使用 SecureRandom 类生成一个介于 0 和 999999 之间的随机整数（含两端点），
    然后将该整数转换为一个 6 位数的字符串（不足 6 位时在左侧补 0）。
    这样就可以得到一个随机的 6 位数验证码。
     */

    @FXML
// 在 RegisterController 类中的 register() 方法中，使用 Socket 连接服务端
    private void Register1() throws IOException {
        String email = Email.getText().trim();
        String userNameInput = nickname.getText().trim(); // 修改变量名称
        String password = userpassword.getText().trim();
        String confirmPassword = userpassword1.getText().trim();
        String verificationCode = PutCode.getText().trim();
        //注意判重
        boolean flag;
        String username;//雪花算法生成的ID
        while (true) {
            long snowflakeId = snowflakeIdWorker.nextId();
            username = String.format("%010d", snowflakeId % 10000000000L); //雪花算法生成的ID
            flag = false;

            try (Socket socket = new Socket("127.0.0.1", 6000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                //查询是否相同
                out.println("findDup:" + username);

                String response = in.readLine();
                if ("success".equals(response)) {
                    break;
                } else {
                    flag = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
        System.out.println("雪花算法生成的ID："+username);
        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "错误", "密码必须包含英文字母和数字，长度在8-18之间");
            return;
        }
        if(!isValidUserName(userNameInput)){
            showAlert(Alert.AlertType.ERROR,"错误","用户名允许包含字母、数字，长度为3-18");
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
                out.println("register:" + username + ":" +password  + ":" + email +":"+ userNameInput);

                // 从服务端接收响应
                String response = in.readLine();
                if ("success".equals(response)) {
                    showAlert(Alert.AlertType.INFORMATION, "注册成功", "恭喜你注册成功！\n\n你的ID号为 ："+username);
                    // 在接收到服务端发送的注册成功消息后：
                    //JOptionPane.showMessageDialog(null, "注册成功！您的用户名是：" + username, "注册成功", JOptionPane.INFORMATION_MESSAGE);
                    // 跳转到登录页面
                } else {
                    showAlert(Alert.AlertType.ERROR, "错误", "注册失败，该邮箱已被绑定！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            showAlert(Alert.AlertType.ERROR, "错误", "错误原因可能是：\n1. 验证码错误或已过期\n2. 你在发送验证码之后修改了邮箱号");
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
    void Userpasswordi2(ActionEvent event) {

    }
    @FXML
    void Userpasswordi1(ActionEvent event) {

    }

    @FXML
    void userpassword1(ActionEvent event) {

    }

    @FXML
    public void CheckOpen1() {
        System.out.println("查看密码按钮被点击");
        if (!passwordVisible) {
            // 显示密码
            Userpasswordi.setText(userpassword.getText());
            Userpasswordi.setVisible(true);
            userpassword.setVisible(false);

            // 切换图片
            Closeeye.setVisible(false);
            Openeye.setVisible(true);
        } else {
            // 隐藏密码
            userpassword.setText(Userpasswordi.getText());
            userpassword.setVisible(true);
            Userpasswordi.setVisible(false);

            // 切换图片
            Openeye.setVisible(false);
            Closeeye.setVisible(true);
        }
        passwordVisible = !passwordVisible;
    }

    @FXML
    public void CheckOpen2() {
        System.out.println("查看密码按钮被点击");
        if (!passwordVisible) {
            // 显示密码
            Userpasswordi2.setText(userpassword1.getText());
            Userpasswordi2.setVisible(true);
            userpassword1.setVisible(false);

            // 切换图片
            Closeeye1.setVisible(false);
            Openeye1.setVisible(true);
        } else {
            // 隐藏密码
            userpassword1.setText(Userpasswordi2.getText());
            userpassword1.setVisible(true);
            Userpasswordi2.setVisible(false);

            // 切换图片
            Openeye1.setVisible(false);
            Closeeye1.setVisible(true);
        }
        passwordVisible = !passwordVisible;
    }

    //验证用户名是否正确
    private boolean isValidUserName(String username){
        return username.matches("^[\u4e00-\u9fa5\\d]{3,18}$");
    }

    //判断密码是否合格
    private boolean isValidPassword(String password) {
        return password.matches("(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,18}");
    }


}

