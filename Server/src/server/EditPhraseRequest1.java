package server;

public class EditPhraseRequest1 {
    private UserCommonPhrase1 oldPhrase;
    private UserCommonPhrase1 newPhrase;

    public EditPhraseRequest1() {
    }

    public EditPhraseRequest1(UserCommonPhrase1 oldPhrase, UserCommonPhrase1 newPhrase) {
        this.oldPhrase = oldPhrase;
        this.newPhrase = newPhrase;
    }

    public UserCommonPhrase1 getOldPhrase() {
        return oldPhrase;
    }

    public void setOldPhrase(UserCommonPhrase1 oldPhrase) {
        this.oldPhrase = oldPhrase;
    }

    public UserCommonPhrase1 getNewPhrase() {
        return newPhrase;
    }

    public void setNewPhrase(UserCommonPhrase1 newPhrase) {
        this.newPhrase = newPhrase;
    }
}
