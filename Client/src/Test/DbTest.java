package Test;

import Util.EmailUtil;
import client.User;
import com.google.gson.Gson;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DbTest {
    /**
     * 登录测试
     */
    @Test
    public void login(){
        try (Socket socket = new Socket("127.0.0.1", 6000);//IP:127.0.0.1   端口6000
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String username = "7358009344";
            String password = "a123456789";
            out.println("login:" + username + ":" + password);

            String response = in.readLine();
            System.out.println(response);
            String[] responseParts = response.split(":", 2);
            if ("success".equals(responseParts[0])) {
                System.out.println(responseParts[1]);
                Gson gson = new Gson();
                System.out.println("gson = " + gson);
            } else if ("error".equals(responseParts[0])&&responseParts.length==2) {
                if ("该用户已经登录".equals(responseParts[1])) {
                    // 在这里处理 "用户已登录" 错误
                    System.out.println("用户已登录");
                }else {

                }
            }
        } catch (IOException e) {
            System.err.println("网络错误: " + e.getMessage());
        }
    }
    /**
     * 发送验证码测试
     */
    private String serverGeneratedCode; // 存储从服务器接收到的验证码
    @Test
    public void sendEmail(){


        try (Socket socket = new Socket("127.0.0.1", 6000);
             //字符打印流
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);//true表是开启自动刷新
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {//字符缓冲流，用来读取数据
            String email = "2212384795@qq.com";
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
                System.out.println("发送邮件的时间为"+ LocalDateTime.now());
                System.out.println("过期的时间为"+ LocalDateTime.now().plusMinutes(EmailUtil.CODE_VALIDITY_MINUTES));
            } else {
                System.out.println("验证码发送失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
