package controller;


import Util.SnowflakeIdWorker;
import client.Group;
import client.User;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CreateGroupChat {

    @FXML
    private ImageView logoImage;

    @FXML
    private TextArea groupDescField;

    @FXML
    private Button createGroupButton;

    @FXML
    private TextField groupNameField;
    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(1, 1);

    private User currentUser; // 当前用户的用户名
    public void setCurrentUser(User user) {
        currentUser = user;
        System.out.println("创建群聊的用户名："+user);
    }
    @FXML
    void createGroup(ActionEvent event) {
        String groupName = groupNameField.getText().trim();
        String groupDesc = groupDescField.getText().trim();

        if (groupName.isEmpty() || groupDesc.isEmpty()) {
            // 弹出提示框，提示用户填写完整信息
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("注意");
            alert.setHeaderText(null);
            alert.setHeaderText("请填写完整信息");
            groupNameField.getScene().getWindow().hide();
            return;
        }
        boolean flag;
        String name_id;//雪花算法生成的ID
        while (true) {
            long snowflakeId = snowflakeIdWorker.nextId();
            name_id = String.format("%011d", snowflakeId % 100000000000L); //雪花算法生成的ID
            flag = false;

            try (Socket socket = new Socket("127.0.0.1", 6000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                //查询是否相同
                out.println("findDup1:" + name_id);

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
        System.out.println("雪花算法生成的群ID："+name_id);

        Group newGroup = new Group(name_id,groupName, groupDesc, "/image/默认头像.png", (int) currentUser.getId());
        Gson gson = new Gson();
        String groupJson = gson.toJson(newGroup);

        try (Socket socket = new Socket("127.0.0.1", 6000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("createGroup:" + groupJson);
            System.out.println("序列化后的群聊信息："+groupJson);
            String response = in.readLine();

            if ("success".equals(response)) {
                // 弹出提示框，提示用户创建成功
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("成功");
                alert.setHeaderText(null);
                alert.setContentText("恭喜你创建群聊成功！！\n"+"你的群聊ID为: "+name_id);
                alert.showAndWait();
                //关闭创建群聊窗口
                groupNameField.getScene().getWindow().hide();
            } else {
                // 弹出提示框，提示用户创建失败
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("失败");
                alert.setHeaderText(null);
                alert.setHeaderText("创建失败请重试！！！");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        // 关闭创建群聊窗口
        groupNameField.getScene().getWindow().hide();
    }
}


