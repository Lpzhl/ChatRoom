package controller;

import Util.EditPhraseRequest;
import Util.UserCommonPhrase;
import client.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javax.sound.midi.Soundbank;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class CommonPhrasesController {

    public interface PhraseSelectionListener {
        void onPhraseSelected(String phrase);
    }
    private PhraseSelectionListener phraseSelectionListener;

    public void setPhraseSelectionListener(PhraseSelectionListener listener) {
        this.phraseSelectionListener = listener;
    }
    @FXML
    private ListView<String> phrasesList;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private TextField phraseInput;

    private User currentUser;

    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        phrasesList.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                String selectedPhrase = phrasesList.getSelectionModel().getSelectedItem();
                if (phraseSelectionListener != null) {
                    phraseSelectionListener.onPhraseSelected(selectedPhrase);
                }
            }
        });
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        System.out.println("用户666：" + currentUser);
        Platform.runLater(this::loadUserPhrases);
    }


    private void loadUserPhrases() {
        // 发送请求给服务器获取用户的常用语
        try (Socket socket = new Socket("127.0.0.1", 6000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("getUserPhrases:" + currentUser.getId());
            String response = in.readLine();
            System.out.println("接收：" + response);
            List<String> phrases = gson.fromJson(response, new TypeToken<List<String>>(){}.getType());
            for (String phrase : phrases) {
                System.out.println("currentUser: " + currentUser);
                System.out.println("phrases: " + phrases);
                System.out.println("phrasesList: " + phrasesList);
                phrasesList.getItems().add(phrase);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handlePhraseSelection() {
        // 处理用户从列表中选择了一个常用语的事件
    }

    @FXML
    public void handleAddPhrase(ActionEvent actionEvent) {
        String newPhrase = phraseInput.getText();
        if (newPhrase.isEmpty()) {
            // 用户没有输入任何内容
            return;
        }
        // 创建一个新的UserCommonPhrase对象，将新的常用语和当前用户的ID设置到这个对象中
        UserCommonPhrase newPhraseObj = new UserCommonPhrase();
        newPhraseObj.setUserId((int) currentUser.getId());
        newPhraseObj.setPhrase(newPhrase);
        // 发送请求给服务器
        try (Socket socket = new Socket("127.0.0.1", 6000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String request = gson.toJson(newPhraseObj);
            System.out.println("添加常用语： " + request);
            out.println("addPhrase:" + request);
            String response = in.readLine();
            System.out.println("接收：" + response);
            if ("success".equals(response)) {
                // 如果服务器成功添加了新的常用语，那么在ListView中也添加这个常用语
                System.out.println("newPhrase："+newPhrase);
                phrasesList.getItems().add(newPhrase);
                // 清空输入框
                phraseInput.clear();
            } else {
                // TODO: 显示错误信息
                System.out.println("添加常用语失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditPhrase(ActionEvent actionEvent) {
        String selectedPhrase = phrasesList.getSelectionModel().getSelectedItem();
        if (selectedPhrase == null) {
            // 用户没有选择任何常用语
            return;
        }
        String newPhrase = phraseInput.getText();
        if (newPhrase.isEmpty()) {
            // 用户没有输入任何内容
            return;
        }
        // 创建两个新的UserCommonPhrase对象，一个包含选定的常用语，一个包含新的常用语
        UserCommonPhrase selectedPhraseObj = new UserCommonPhrase();
        selectedPhraseObj.setUserId((int) currentUser.getId());
        selectedPhraseObj.setPhrase(selectedPhrase);
        UserCommonPhrase newPhraseObj = new UserCommonPhrase();
        newPhraseObj.setUserId((int) currentUser.getId());
        newPhraseObj.setPhrase(newPhrase);
        // 发送请求给服务器
        try (Socket socket = new Socket("127.0.0.1", 6000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String request = gson.toJson(new EditPhraseRequest(selectedPhraseObj, newPhraseObj));
            System.out.println("编辑常用语： " + request);
            out.println("editPhrase:" + request);
            String response = in.readLine();
            System.out.println("接收：" + response);
            if ("success".equals(response)) {
                // 如果服务器成功修改了常用语，那么在ListView中也修改这个常用语
                int selectedIndex = phrasesList.getSelectionModel().getSelectedIndex();
                phrasesList.getItems().set(selectedIndex, newPhrase);
                // 清空输入框
                phraseInput.clear();
            } else {
                System.out.println("编辑常用语失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeletePhrase(ActionEvent actionEvent) {
        String selectedPhrase = phrasesList.getSelectionModel().getSelectedItem();
        if (selectedPhrase == null) {
            // 用户没有选择任何常用语
            return;
        }
        UserCommonPhrase selectedPhraseObj = new UserCommonPhrase();
        selectedPhraseObj.setUserId((int) currentUser.getId());
        selectedPhraseObj.setPhrase(selectedPhrase);
        // 发送请求给服务器
        try (Socket socket = new Socket("127.0.0.1", 6000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            Gson gson = new Gson();
            String request = gson.toJson(selectedPhraseObj);
            System.out.println("删除常用语： " + request);
            out.println("deletePhrase:" + request);
            String response = in.readLine();
            System.out.println("接收：" + response);
            if ("success".equals(response)) {
                // 如果服务器成功删除了常用语，那么在ListView中也移除这个常用语
                phrasesList.getItems().remove(selectedPhrase);
            } else {
                // TODO: 显示错误信息
                System.out.println("删除常用语失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
