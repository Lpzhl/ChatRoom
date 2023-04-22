package Util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.util.Duration;
import javafx.util.Pair;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class EmailUtil extends Task<Void> {

    // 验证码有效期限，以分钟为单位
    public static final int CODE_VALIDITY_MINUTES = 5;
    private String toEmail;
    private String generatedCode;

    public EmailUtil(String toEmail, String generatedCode) {
        this.toEmail = toEmail;
        this.generatedCode = generatedCode;
    }

    @Override
    protected Void call() throws Exception {
        // 从 email.properties 文件中读取邮件服务器配置信息
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("E:\\\\java\\\\Java练习\\\\ChatRoom\\\\Client\\\\src\\\\file\\\\email.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // 获取发送方的邮箱地址和密码
        String fromEmail = properties.getProperty("mail.from.email");
        String emailPassword = properties.getProperty("mail.from.password");

        // 创建一个用于发送邮件的 session
        Session session = Session.getInstance(properties, new Authenticator() {
            // 创建一个新的 Authenticator 对象来进行身份验证
            protected PasswordAuthentication getPasswordAuthentication() {
                // 返回包含发送方邮箱地址和密码的新的 PasswordAuthentication 对象
                return new PasswordAuthentication(fromEmail, emailPassword);
            }
        });

        // 组装邮件内容并发送邮件
        try {
            // 创建一个新的 MimeMessage 对象
            Message message = new MimeMessage(session);
            // 设置邮件的发送方
            message.setFrom(new InternetAddress(fromEmail));
            // 设置邮件的接收方
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            // 设置邮件的标题
            message.setSubject("【双创科技】");
            // 设置邮件的正文
            message.setText("验证码: " + generatedCode + "用于QQ邮箱身份验证，请勿泄露和转发。如非本人操作，请忽略此短信。");

            // 发送邮件
            Transport.send(message);
            System.out.println("发送成功!");
            // 将验证码及其过期时间添加到映射中
        } catch (MessagingException e) {
            // 如果发送过程中出现异常，则打印异常堆栈信息
            e.printStackTrace();
        }
        // 返回空值
        return null;
    }


    // 生成一个随机六位数验证码
    public static String generateRandomCode() {
        Random random = new SecureRandom();
        int code = random.nextInt(1_000_000);
        return String.format("%06d", code);
    }

    // 检查指定电子邮件地址和验证码是否匹配，且验证码未过期
    public static boolean isCodeValid(String email, String code, Map<String, Pair<String, LocalDateTime>> generatedCodes) {
        Pair<String, LocalDateTime> codeAndExpiration = generatedCodes.get(email);
        if (codeAndExpiration == null) {
            return false;
        }

        String generatedCode = codeAndExpiration.getKey();
        LocalDateTime expirationTime = codeAndExpiration.getValue();

        if (LocalDateTime.now().isAfter(expirationTime)) {
            generatedCodes.remove(email);
            return false;
        }

        if (code.equals(generatedCode)) {
            generatedCodes.remove(email);
            return true;
        }

        return false;
    }
    // 检查验证码是否在有效期内
    public static boolean isCodeValidWithinValidityPeriod(LocalDateTime codeGenerationTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutesBetween = ChronoUnit.MINUTES.between(codeGenerationTime, now);
        return minutesBetween <= CODE_VALIDITY_MINUTES;
    }
    public static boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }

    //匿名内部类
    public static void startCountdown(Button button) {
        Timeline timeline = new Timeline();
        final int[] countdown = {60};
        KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (countdown[0] > 0) {
                    button.setText("获取验证码 (" + countdown[0] + ")");
                    countdown[0]--;
                } else {
                    button.setText("获取验证码");
                }
            }
        });
        timeline.getKeyFrames().add(frame);
        timeline.setCycleCount(60);
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                button.setDisable(false); // 启用按钮
                button.setText("获取验证码");
            }
        });

        button.setDisable(true); // 禁用按钮
        timeline.play();
    }
}