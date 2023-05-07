package server;

import java.time.LocalDate;

public class User1 {
    private int id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private String nickname;
    private String gender;
    private LocalDate birthday;
    private String signature;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public User1() {
    }

    public User1(int id, String username, String email, String avatar, String nickname, String gender, LocalDate birthday, String signature, String status, LocalDate createdAt, LocalDate updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.nickname = nickname;
        this.gender = gender;
        this.birthday = birthday;
        this.signature = signature;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User1(int userId, String username) {
        this.id = userId;
        this.username = username;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
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

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }




    // toString()方法，用于方便地显示User对象的信息
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", signature='" + signature + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

