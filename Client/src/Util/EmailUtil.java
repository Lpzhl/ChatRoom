package Util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.util.Duration;
import javafx.util.Pair;


import java.time.LocalDateTime;
import java.util.Map;


public class EmailUtil {

    // 验证码有效期限，以分钟为单位
    public static final int CODE_VALIDITY_MINUTES = 3;

    // 检查指定电子邮件地址和验证码是否匹配，且验证码未过期
    public static boolean isCodeValid(String email, String code, Map<String, Pair<String, LocalDateTime>> generatedCodes) {
        // 从生成的验证码映射中获取电子邮件对应的验证码和过期时间
        Pair<String, LocalDateTime> codeAndExpiration = generatedCodes.get(email);

        // 如果没有找到相关的验证码和过期时间，返回false
        if (codeAndExpiration == null) {
            return false;
        }

        // 获取生成的验证码和过期时间
        String generatedCode = codeAndExpiration.getKey();
        LocalDateTime expirationTime = codeAndExpiration.getValue();

        // 检查验证码是否已过期，如果是，则从映射中删除该电子邮件的条目并返回false
        if (LocalDateTime.now().isAfter(expirationTime)) {
            generatedCodes.remove(email);
            return false;
        }

        // 检查提供的验证码是否与生成的验证码匹配 //在于isCodeValid方法只有在验证码匹配成功的情况下，才会从generatedCodes映射中删除验证码。
        if (code.equals(generatedCode)) {
            // 如果匹配成功，从映射中删除该电子邮件的条目并返回true
            generatedCodes.remove(email);
            return true;
        }

        // 如果验证码不匹配，返回false
        return false;
    }

    // 检查验证码是否在有效期内
    //正则判断QQ邮箱是否正确
    public static boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }

    //匿名内部类，验证码每60秒获取一次
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