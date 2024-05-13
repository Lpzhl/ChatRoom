package server;

import java.sql.Timestamp;

public class Message1 {
    private int id;
    private int senderId;
    private Integer receiverId;
    private Integer groupId;
    private String content;
    private String contentType;
    private Timestamp createdAt;

    public Message1() {
    }

    public Message1(int id, int senderId, Integer receiverId, Integer groupId, String content, String contentType, Timestamp createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.groupId = groupId;
        this.content = content;
        this.contentType = contentType;
        this.createdAt = createdAt;
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
     * @return senderId
     */
    public int getSenderId() {
        return senderId;
    }

    /**
     * 设置
     * @param senderId
     */
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    /**
     * 获取
     * @return receiverId
     */
    public Integer getReceiverId() {
        return receiverId;
    }

    /**
     * 设置
     * @param receiverId
     */
    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * 获取
     * @return groupId
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * 设置
     * @param groupId
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     * 获取
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取
     * @return contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * 设置
     * @param contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public String toString() {
        return "Message{id = " + id + ", senderId = " + senderId + ", receiverId = " + receiverId + ", groupId = " + groupId + ", content = " + content + ", contentType = " + contentType + ", createdAt = " + createdAt + "}";
    }


    // Getters and setters for each field
}
