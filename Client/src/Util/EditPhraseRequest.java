package Util;

public class EditPhraseRequest {
    private UserCommonPhrase oldPhrase;
    private UserCommonPhrase newPhrase;

    public EditPhraseRequest() {
    }

    public EditPhraseRequest(UserCommonPhrase oldPhrase, UserCommonPhrase newPhrase) {
        this.oldPhrase = oldPhrase;
        this.newPhrase = newPhrase;
    }

    public UserCommonPhrase getOldPhrase() {
        return oldPhrase;
    }

    public void setOldPhrase(UserCommonPhrase oldPhrase) {
        this.oldPhrase = oldPhrase;
    }

    public UserCommonPhrase getNewPhrase() {
        return newPhrase;
    }

    public void setNewPhrase(UserCommonPhrase newPhrase) {
        this.newPhrase = newPhrase;
    }
}
