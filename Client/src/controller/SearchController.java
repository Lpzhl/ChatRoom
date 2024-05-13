package controller;

import client.Group;
import client.User;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javax.naming.directory.SearchResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class SearchController {

    @FXML
    private Label GroupIntroduction;
    @FXML
    private Label GroupIntr;
    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private HBox resultContainer;

    @FXML
    private ImageView resultImage;

    @FXML
    private Label resultUsername;

    @FXML
    private Button addButton1;

    public static String receiverUsername;

    private User currentUser; // 当前用户的用户名
    public void setCurrentUser(User user) {
        currentUser = user;
        System.out.println("添加好友的用户名："+user);
    }

    private User searchFriend(String searchText) {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        User user = null;
        try {
            socket = new Socket("127.0.0.1", 6000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 发送查找用户请求
            System.out.println("文本框长度："+searchText.length());
                out.println("findUser:" + searchText);
            // 接收服务器响应
            String response = in.readLine();
            String[] responseParts = response.split(":", 2);
            if (responseParts[0].equals("success")) {
                // 将用户信息的JSON对象解析为User对象
                Gson gson = new Gson();
                user = gson.fromJson(responseParts[1], User.class);
            } else {
                System.out.println("Error: " + responseParts[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭输入流
                if (in != null) {
                    in.close();
                }

                // 关闭输出流
                if (out != null) {
                    out.close();
                }

                // 关闭套接字
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

private Group serrchGroup(String searchText) throws IOException {
        Group group = null;
        try(Socket socket = new Socket("127.0.0.1",6000);
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            out.println("findGroup:"+searchText);

            String response = in.readLine();
            String[] responseParts = response.split(":",2);
            if(responseParts[0].equals("success")){
                // 反序列化
                Gson gson = new Gson();
                group = gson.fromJson(responseParts[1], Group.class);
            }else {
                System.out.println("错误！！！！");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return group;
}
   @FXML
   void search(ActionEvent event) throws IOException {
       System.out.println("查找被点击");
       String searchText = searchField.getText();

       // 这里假设我们有一个查找好友的函数，函数返回一个Friend对象
       // 你需要根据你的实际情况来实现这个函数
       User user=null;
       Group group = null;
       if(searchText.length()==10) {
           user = searchFriend(searchText);
           if (user != null) {
               // 显示查找结果
               resultContainer.setVisible(true);
               resultUsername.setVisible(true);
               addButton1.setDisable(false); // enable the button
               addButton1.setVisible(true);
               // 你可能需要将用户的头像URL转换为Image对象，或者从其他地方获取Image对象
               resultImage.setImage(new Image(user.getAvatar(), true)); // second parameter for background loading
               resultUsername.setText(user.getNickname()); // 显示昵称，而不是用户名
           } else {
               // 隐藏查找结果
               resultContainer.setVisible(false);
               resultUsername.setVisible(false);
               addButton1.setDisable(true); // disable the button
               addButton1.setVisible(false);

               // 显示弹窗
               Alert alert = new Alert(Alert.AlertType.INFORMATION);
               alert.setTitle("查找失败");
               alert.setHeaderText(null);
               alert.setContentText("未找到对应的好友");
               alert.showAndWait();
           }
       }else if(searchText.length()==11){
           group = serrchGroup(searchText);
           if (group != null) {
               // 显示查找结果
               resultContainer.setVisible(true);
               resultUsername.setVisible(true);
               addButton1.setDisable(false); // enable the button
               addButton1.setVisible(true);
               GroupIntr.setVisible(true);
               GroupIntroduction.setVisible(true);
               // 你可能需要将用户的头像URL转换为Image对象，或者从其他地方获取Image对象
               resultImage.setImage(new Image(group.getAvatar(), true)); // second parameter for background loading
               resultUsername.setText(group.getName()); // 显示昵称，而不是用户名
               GroupIntroduction.setText(group.getDescription());
           } else {
               // 隐藏查找结果
               resultContainer.setVisible(false);
               resultUsername.setVisible(false);
               addButton1.setDisable(true); // disable the button
               addButton1.setVisible(false);

               // 显示弹窗
               Alert alert = new Alert(Alert.AlertType.INFORMATION);
               alert.setTitle("查找失败");
               alert.setHeaderText(null);
               alert.setContentText("未找到对应的好友");
               alert.showAndWait();
           }
       }

   }



   /* @FXML
   void add(ActionEvent event) {
       // 创建一个包含请求类型和用户信息的map
        Map<String, String> requestMap = new HashMap<>();
        System.out.println("当前登入者: "+currentUser.getUsername());
        requestMap.put("username1", currentUser.getUsername());
        requestMap.put("username2", searchField.getText());
       // 将map转换为JSON字符串
       Gson gson = new Gson();
       String requestJson = gson.toJson(requestMap);
       System.out.println("序列化后："+requestJson);
       // 创建请求字符串
       String request = "addFriend:" + requestJson;
       // 发送请求到服务器
       try {
           Socket socket = new Socket("127.0.0.1",6000); // 将"server address"和port替换为你的服务器地址和端口
           PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
           BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           out.println(request);
           // 接收服务器的响应
           String response = in.readLine();
           System.out.println("服务器响应：" + response);
           if("success".equals(response)){
               Alert alert = new Alert(Alert.AlertType.INFORMATION);
               alert.setTitle("添加成功");
               alert.setHeaderText(null);
               alert.setContentText("已成功添加" + resultUsername.getText() + "为好友");
               alert.showAndWait();
           } else {
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setTitle("添加失败");
               alert.setHeaderText(null);
               alert.setContentText("添加好友失败！！！请你检查是否存在以下原因：\n1.该账号不存在\n2.该用户已经是你的好友\n3.不能添加自己为好友");
               alert.showAndWait();
           }
           // 关闭连接
           out.close();
           in.close();
           socket.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }*/



    @FXML
   void add(ActionEvent event) {
       // 创建一个包含请求类型和用户信息的map
        if(searchField.getText().length()==10) {
            Map<String, String> requestMap = new HashMap<>();
            System.out.println("当前登入者: " + currentUser.getUsername());
            requestMap.put("username1", currentUser.getUsername());
            receiverUsername = searchField.getText();
            requestMap.put("username2", receiverUsername);
            // 将map转换为JSON字符串
            Gson gson = new Gson();
            String requestJson = gson.toJson(requestMap);
            System.out.println("序列化后：" + requestJson);
            // 创建请求字符串
            String request = "sendFriendRequest:" + requestJson; // 修改请求类型为sendFriendRequest
            // 发送请求到服务器
            try {
                Socket socket = new Socket("127.0.0.1",6000); // 将"server address"和port替换为你的服务器地址和端口
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(request);
                // 接收服务器的响应
                String response = in.readLine();
                System.out.println("服务器响应：" + response);
                if("success".equals(response)){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("请求发送成功");
                    alert.setHeaderText(null);
                    alert.setContentText("已向" + resultUsername.getText() + "发送好友请求");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("请求发送失败");
                    alert.setHeaderText(null);
                    alert.setContentText("发送好友请求失败，请检查输入的用户名是否正确");
                    alert.showAndWait();
                }
                // 关闭连接
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(searchField.getText().length() == 11) {
            // 添加群聊
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("username", currentUser.getUsername());
            requestMap.put("groupname", searchField.getText());
            // 将map转换为JSON字符串
            Gson gson = new Gson();
            String requestJson = gson.toJson(requestMap);
            System.out.println("序列化后：" + requestJson);
            // 创建请求字符串
            String request = "sendGroupRequest:" + requestJson; // 修改请求类型为joinGroupRequest
            // 发送请求到服务器
            try {
                Socket socket = new Socket("127.0.0.1", 6000); // 将"server address"和port替换为你的服务器地址和端口
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(request);
                // 接收服务器的响应
                String response = in.readLine();
                System.out.println("服务器响应：" + response);
                if("success".equals(response)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("请求发送成功");
                    alert.setHeaderText(null);
                    alert.setContentText("已向" + searchField.getText()+"发送入群请求");
                    alert.showAndWait();
                } else if ("failure1".equals(response)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("请求发送失败");
                    alert.setHeaderText(null);
                    alert.setContentText("已加入该群!!!");
                    alert.showAndWait();

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("请求发送失败");
                    alert.setHeaderText(null);
                    alert.setContentText("加入群聊失败，请检查输入的群聊名称是否正确");
                    alert.showAndWait();
                }
                // 关闭连接
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
   }

}
