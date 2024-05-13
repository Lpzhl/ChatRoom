package client;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ChatMessage {
    private User sender;
    private User receiver;
    private String content;
    private int groupId;
    private boolean isCurrentUser;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private String fileName;
    private String contentType;

    public ChatMessage(User sender, String content, boolean isCurrentUser) {
        this.sender = sender;
        this.content = content;
        this.isCurrentUser = isCurrentUser;
    }

    public ChatMessage(User sender, User receiver, String messageContent) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = messageContent;
    }

    public ChatMessage(User sender, String content,LocalDateTime localDateTime,String fileName) {
        this.sender = sender;
        this.content = content;
        this.updatedAt= localDateTime;
        this.fileName = fileName;
    }
    public ChatMessage(User sender, String content,LocalDateTime localDateTime) {
        this.sender = sender;
        this.content = content;
        this.updatedAt= localDateTime;
    }
    private boolean sentByCurrentUser;

    public ChatMessage(User sender, String content, LocalDateTime dateTimeFormatter, int groupId) {
        this.sender = sender;
        this.content = content;
        this.updatedAt = dateTimeFormatter;
        this.groupId = groupId;
    }

    public boolean isSentByCurrentUser() {
        return sentByCurrentUser;
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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public void setSentByCurrentUser(boolean sentByCurrentUser) {
        this.sentByCurrentUser = sentByCurrentUser;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
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
    private boolean isFile;
    public boolean getIsFile() {
        return isFile;
    }
    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "sender=" + sender +
                ", receiver=" + receiver +
                ", content='" + content + '\'' +
                ", groupId=" + groupId +
                ", isCurrentUser=" + isCurrentUser +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", sentByCurrentUser=" + sentByCurrentUser +
                ", isFile=" + isFile +
                '}';
    }
}
