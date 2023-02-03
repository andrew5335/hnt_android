package com.hnt.hnt_android.api.model;

public class LoginVO {

    private String userId;
    private String userPass;

    public LoginVO(String userId, String userPass) {
        this.userId = userId;
        this.userPass = userPass;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
}
