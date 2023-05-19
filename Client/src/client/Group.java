package client;

import java.sql.Timestamp;
import java.util.List;

public class Group {
    private int id;
    private String name;
    private String description;
    private String avatar;
    private int createdBy;
    private String name_id;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<User> members;

    public Group(String name_id,String name, String description, String avatar, int createdBy) {
        this.name_id = name_id;
        this.name = name;
        this.description = description;
        this.avatar = avatar;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Group(int groupdId, String newAvatarPath, String newNickname, String newIntroduce) {
        this.id= groupdId;
        this.avatar = newAvatarPath;
        this.name = newNickname;
        this.description = newIntroduce;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return name_id;
    }

    public void setUsername(String username) {
        this.name_id = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", name_id='" + name_id + '\'' +
                ", description='" + description + '\'' +
                ", avatar='" + avatar + '\'' +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
