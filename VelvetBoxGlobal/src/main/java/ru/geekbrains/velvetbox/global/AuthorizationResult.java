package ru.geekbrains.velvetbox.global;

public class AuthorizationResult {
    private Boolean result = false;
    private User user = null;
    private int resultCode;

    public AuthorizationResult(Boolean result, User user, int resultCode) {
        this.result = result;
        this.user = user;
        this.resultCode = resultCode;
    }

    public Boolean getResult() {
        return result;
    }

    public User getUser() {
        return user;
    }

    public int getResultCode() {
        return resultCode;
    }
}
