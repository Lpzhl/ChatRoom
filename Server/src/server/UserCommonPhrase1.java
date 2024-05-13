package server;

public class UserCommonPhrase1 {
    private int userId;
    private String phrase;

    public UserCommonPhrase1() {
    }

    public UserCommonPhrase1(int userId, String phrase) {
        this.userId = userId;
        this.phrase = phrase;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }
}