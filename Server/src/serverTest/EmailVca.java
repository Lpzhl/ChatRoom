package serverTest;


import javafx.concurrent.Task;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;

public class EmailVca implements Callable<Void> {

    private String toEmail;
    private String generatedCode;

    public EmailVca(String toEmail, String generatedCode) {
        this.toEmail = toEmail;
        this.generatedCode = generatedCode;
    }
    public Void call() throws Exception {
        // 从 email.properties 文件中读取邮件服务器配置信息
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("Server/src/fxml/email.properties")) {
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
            message.setText("验证码: " + " "+generatedCode +" "+ "于QQ邮箱身份验证，3分钟内有效，请勿泄露和转发。如非本人操作，请忽略此短信。");
            System.out.println(generatedCode);
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
}
