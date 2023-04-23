package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatRoomApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //裂开调试了半天才找到正确的路径
        /*
        路径前面添加了一个斜杠（/）。这表示从类路径的根目录开始查找资源。
         */
       Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/jiemian2.fxml"));
        primaryStage.setTitle("聊天室");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
