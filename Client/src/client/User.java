package client;

import controller.ChatRoomController;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.List;


public class User {
    private int id;
    private String username1;
    private String username;
    private String email;
    private String avatar;
    private String nickname;
    private String gender;
    private LocalDate birthday;
    private String signature;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String password;
    private List<User> friends;

    public User(String username1, String username) {
        this.username1 = username1;
        this.username = username;
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }


    //检测聊天室主界面的头像变化   transient表示不序列化
    private transient ChatRoomController mainController;

    //定义一个setMainController方法，用于mainController赋值。
    public void setMainController(ChatRoomController mainController) {
        this.mainController = mainController;
    }

    //它是一个PropertyChangeSupport类型的对象，用于管理属性更改事件的监听器。
    public User() {
        support = new PropertyChangeSupport(this);
    }

    //用于管理属性更改事件的监听器
    private transient PropertyChangeSupport support = new PropertyChangeSupport(this);


  //用于修改用户头像。当头像发生变化时，会通知mainController更新界面，顺便把头像也改了。
    public void setAvatar(String avatar) {
        String oldAvatar = this.avatar;
        this.avatar = avatar;
        if (mainController != null) {
            mainController.updateHomeScreenAvatar(avatar);//把主界面改了
        }
        support.firePropertyChange("avatar", oldAvatar, avatar);
    }

    //定义两个方法，addPropertyChangeListener和removePropertyChangeListener，分别用于添加和删除属性更改事件监听器。
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);//将传入的监听器pcl添加到support对象的监听器列表中。这样，当属性发生更改时，会通知所有已注册的监听器
    }
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    //下面都是监听
    public void setNickname(String nickname) {
        String oldNickname = this.nickname;
        this.nickname = nickname;
        support.firePropertyChange("nickname", oldNickname, nickname);
    }

    public void setSignature(String signature) {
        String oldSignature = this.signature;
        this.signature = signature;
        support.firePropertyChange("signature", oldSignature, signature);
    }

    public void setGender(String gender) {
        String oldGender = this.gender;
        this.gender = gender;
        support.firePropertyChange("gender", oldGender, gender);
    }

    public void setBirthday(LocalDate birthday) {
        LocalDate oldBirthday = this.birthday;
        this.birthday = birthday;
        support.firePropertyChange("birthday", oldBirthday, birthday);
    }
    public User(String username,String nickname, String avatar, String gender, String signature, LocalDate birthday) {
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.gender = gender;
        this.signature = signature;
        this.birthday = birthday;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }


    public String getGender() {
        return gender;
    }


    public LocalDate getBirthday() {
        return birthday;
    }


    public String getSignature() {
        return signature;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Year createdAt) {
        this.createdAt = LocalDate.parse(createdAt.toString());
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt( LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ",password='" +password+'\''+
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", signature='" + signature + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
