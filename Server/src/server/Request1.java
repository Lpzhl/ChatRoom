package server;

public class Request1 {
    private int id;
    private String username;
    private User1 sender;
    private User1 receiver;
    private String status;
    private String requestType;
    private Integer groupId;

    public Request1(int id, User1 sender, User1 receiver, String status) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    public Request1(String username, String status, String requestType,Integer groupId) {
        this.username = username;
        this.status = status;
        this.requestType = requestType;
        this.groupId = groupId;

    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
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

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public void setSender(User1 sender) {
        this.sender = sender;
    }

    public void setReceiver(User1 receiver) {
        this.receiver = receiver;
    }

    public User1 getSender() {
        return sender;
    }

    public User1 getReceiver() {
        return receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Request1{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", status='" + status + '\'' +
                ", requestType='" + requestType + '\'' +
                ", groupId=" + groupId +
                '}';
    }
}
