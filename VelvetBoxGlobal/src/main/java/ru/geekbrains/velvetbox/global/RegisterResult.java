package ru.geekbrains.velvetbox.global;

public class RegisterResult {

    private Boolean result = false;
    private String resultMsg;
    private int resultCode;

    public RegisterResult(Boolean result, String resultMsg, int resultCode) {
        this.result = result;
        this.resultMsg = resultMsg;
        this.resultCode = resultCode;
    }

    public Boolean getResult() {
        return result;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public int getResultCode() {
        return resultCode;
    }
}
