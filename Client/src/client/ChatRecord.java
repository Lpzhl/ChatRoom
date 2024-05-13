package client;

import java.sql.Timestamp;

public class ChatRecord {
    private int id;
    private int userId;
    private int messageId;
    private String readStatus;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public ChatRecord() {
    }

    public ChatRecord(int id, int userId, int messageId, String readStatus, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.messageId = messageId;
        this.readStatus = readStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 获取
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取
     * @return userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * 设置
     * @param userId
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * 获取
     * @return messageId
     */
    public int getMessageId() {
        return messageId;
    }

    /**
     * 设置
     * @param messageId
     */
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    /**
     * 获取
     * @return readStatus
     */
    public String getReadStatus() {
        return readStatus;
    }

    /**
     * 设置
     * @param readStatus
     */
    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    /**
     * 获取
     * @return createdAt
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置
     * @param createdAt
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取
     * @return updatedAt
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置
     * @param updatedAt
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toString() {
        return "ChatRecord{id = " + id + ", userId = " + userId + ", messageId = " + messageId + ", readStatus = " + readStatus + ", createdAt = " + createdAt + ", updatedAt = " + updatedAt + "}";
    }

    // Getters and setters for each field

}