package controller;
import Util.EmailUtil;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Changepassword {

    @FXML
    private Button cancel;

    @FXML
    private Button CheckOpen;

    @FXML
    private ImageView Closeeye;

    @FXML
    private ImageView Openeye1;

    @FXML
    private ImageView Openeye2;

    @FXML
    private TextField Userpasswordi;

    @FXML
    private ImageView Openeye;

    @FXML
    private TextField OriginalPassword;

    @FXML
    private TextField UsernameID;

    @FXML
    private ImageView Closeeye2;

    @FXML
    private ImageView Closeeye1;

    @FXML
    private PasswordField Resetpassword2;

    @FXML
    private Button Obtainverification;

    @FXML
    private Button Sure;

    @FXML
    private Button CheckOpen1;

    @FXML
    private PasswordField OriginalPassword2;

    @FXML
    private PasswordField Resetpassword;

    @FXML
    private Button CheckOpen11;

    @FXML
    private TextField Inputverification;

    @FXML
    private TextField Userpasswordi2;

    @FXML
    private TextField Retrievepasswordemail;

    // 存储验证码及其过期时间的映射
    private Map<String, Pair<String, LocalDateTime>> generatedCodes = new HashMap<>();
    private boolean passwordVisible = false;

    @FXML
    void UsernameID1(ActionEvent event) {

    }

    @FXML
    void Retrievepasswordemail1(ActionEvent event) {

    }

    @FXML
    void Userpasswordi1(ActionEvent event) {

    }

    @FXML
    void Resetpassword1(ActionEvent event) {

    }

    @FXML
    void Userpasswordi2(ActionEvent event) {

    }

    @FXML
    void Resetpassword2(ActionEvent event) {

    }

    private String generatedCode;
    @FXML
    void Obtainverification1(ActionEvent event) {
        String emailAddress = Retrievepasswordemail.getText();
        if (EmailUtil.isValidEmail(emailAddress)) {
            try (Socket socket = new Socket("127.0.0.1", 6000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // 向服务端请求发送验证码
                out.println("email_verification:" + emailAddress);

                System.out.println("请求发送验证码的qq:"+emailAddress);

                // 从服务端接收响应
                String response = in.readLine();
                if (response.length() == 6)  {
                    generatedCode = response;
                    // 添加生成的验证码和过期时间到映射中
                    // 开始倒计时
                    EmailUtil.startCountdown(Obtainverification);
                    System.out.println("发送邮件的时间为"+LocalDateTime.now());
                    System.out.println("过期的时间为"+ LocalDateTime.now().plusMinutes(EmailUtil.CODE_VALIDITY_MINUTES));
                    generatedCodes.put(emailAddress, new Pair<>(generatedCode, LocalDateTime.now().plusMinutes(3)));
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
    @FXML
    void Inputverification1(ActionEvent event) {

    }


    @FXML
    void Sure1(ActionEvent event) throws IOException {
        String Orignal = OriginalPassword.getText().trim();//原始密码
        String email = Retrievepasswordemail.getText().trim();//邮箱
        String username = UsernameID.getText().trim();//ID
        String newPassword = Resetpassword.getText().trim();//新密码
        String confirmPassword = Resetpassword2.getText().trim();
        String verificationCode = Inputverification.getText().trim();//验证码
        boolean loginSuccess = login(username, Orignal);


        if (Orignal.isEmpty()||email.isEmpty() || username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() || verificationCode.isEmpty()) {
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
        if(loginSuccess){
            showAlert(Alert.AlertType.ERROR, "错误", "原始密码输入错误！！!");
            return;
        }
        if (EmailUtil.isCodeValid(email, verificationCode, generatedCodes)) {
            try (Socket socket = new Socket("127.0.0.1", 6000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // 发送重置密码请求给服务端
                out.println("resetPassword:" + username + ":" + newPassword + ":"+ email);

                // 从服务端接收响应
                String response = in.readLine();
                if (response.equals("success")) {
                    showAlert(Alert.AlertType.INFORMATION, "成功", "密码修改成功!!!");

                } else {
                    //showAlert(Alert.AlertType.ERROR, "错误", "密码重置失败，请检查您的信息是否正确");
                    showAlert(Alert.AlertType.ERROR, "错误", "密码修改失败，邮箱不匹配!!!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            showAlert(Alert.AlertType.ERROR, "错误", "验证码错误或已过期");
        }
    }

    private boolean login(String username, String password) {
        boolean loginSuccess = false;
        try (Socket socket = new Socket("127.0.0.1", 6000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

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

    @FXML
    void cancel1(ActionEvent event) {
        cancel.getScene().getWindow().hide();
    }

    public void CheckOpen1() {
        System.out.println("查看密码按钮被点击");
        if (!passwordVisible) {
            // 显示密码
            Userpasswordi.setText(Resetpassword.getText());
            Userpasswordi.setVisible(true);
            Resetpassword.setVisible(false);

            // 切换图片
            Closeeye.setVisible(false);
            Openeye.setVisible(true);
        } else {
            // 隐藏密码
            Resetpassword.setText(Userpasswordi.getText());
            Resetpassword.setVisible(true);
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
            Userpasswordi2.setText(Resetpassword2.getText());
            Userpasswordi2.setVisible(true);
            Resetpassword2.setVisible(false);

            // 切换图片
            Closeeye1.setVisible(false);
            Openeye1.setVisible(true);
        } else {
            // 隐藏密码
            Resetpassword2.setText(Userpasswordi2.getText());
            Resetpassword2.setVisible(true);
            Userpasswordi2.setVisible(false);

            // 切换图片
            Openeye1.setVisible(false);
            Closeeye1.setVisible(true);
        }
        passwordVisible = !passwordVisible;
    }

    @FXML
    void CheckOpen3(ActionEvent event) {
        System.out.println("查看密码按钮被点击");
        if (!passwordVisible) {
            // 显示密码
            OriginalPassword.setText(OriginalPassword2.getText());
            OriginalPassword.setVisible(true);
            OriginalPassword2.setVisible(false);

            // 切换图片
            Closeeye2.setVisible(false);
            Openeye2.setVisible(true);
        } else {
            // 隐藏密码
            OriginalPassword2.setText(OriginalPassword.getText());
            OriginalPassword2.setVisible(true);
            OriginalPassword.setVisible(false);

            // 切换图片
            Openeye2.setVisible(false);
            Closeeye2.setVisible(true);
        }
        passwordVisible = !passwordVisible;
    }



    @FXML
    void OriginalPassword1(ActionEvent event) {

    }

    @FXML
    void OriginalPassword2i(ActionEvent event) {

    }


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
