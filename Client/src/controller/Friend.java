package controller;

import javafx.scene.image.Image;

public class Friend {
    private Image avatar;
    private String username;

    public Friend(Image avatar, String username) {
        this.avatar = avatar;
        this.username = username;
    }

    public Image getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }
}
