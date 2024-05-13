package server;

import com.mysql.cj.x.protobuf.MysqlxCrud;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ChatMessage1 {
    private int id;
    private User1 sender;
    private User1 receiver;
    private String content;
    private int groupId;
    private boolean isCurrentUser;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private String fileName;
    private String contentType;


    public ChatMessage1(User1 sender, String content, String fileName ,String contentType, boolean isCurrentUser, LocalDateTime updatedAt) {
        this.sender = sender;
        this.content = content;
        this.fileName = fileName;
        this.contentType = contentType;
        this.isCurrentUser = isCurrentUser;
        this.updatedAt = updatedAt;
    }

    public ChatMessage1(User1 sender, String content, boolean isCurrentUser) {
        this.sender = sender;
        this.content = content;
        this.isCurrentUser = isCurrentUser;
    }

    public ChatMessage1(Message1 message, User1 sender, User1 receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt().toLocalDateTime();
    }

    public ChatMessage1(int id, User1 sender, String content, boolean b,LocalDateTime createdAt, String fileName) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.isCurrentUser = b;
        this.fileName = fileName;
        this.createdAt = createdAt;
    }

    public ChatMessage1(User1 sender, String content, boolean b, LocalDateTime updatedAt) {
        this.sender = sender;
        this.content = content;
        this.isCurrentUser = b;
        this.updatedAt = updatedAt;
    }

    public ChatMessage1(User1 sender,int  groupId, String messageContent, String fileName, String contentType,boolean b, LocalDateTime updatedAt) {
        this.sender = sender;
        this.groupId = groupId;
        this.content = messageContent;
        this.isCurrentUser = b;
        this.fileName = fileName;
        this.contentType = contentType;
        this.updatedAt = updatedAt;
    }

    public ChatMessage1(int id, User1 sender, String content, boolean b, LocalDateTime createdAt) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.isCurrentUser = b;
        this.createdAt = createdAt;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp up) {
        this.createdAt = createdAt;
    }

    public User1 getSender() {
        return sender;
    }

    public void setSender(User1 sender) {
        this.sender = sender;
    }

    public User1 getReceiver() {
        return receiver;
    }

    public void setReceiver(User1 receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        isCurrentUser = currentUser;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "ChatMessage1{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", content='" + content + '\'' +
                ", groupId=" + groupId +
                ", isCurrentUser=" + isCurrentUser +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
