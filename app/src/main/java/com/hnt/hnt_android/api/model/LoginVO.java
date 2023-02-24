package com.hnt.hnt_android.api.model;

public class LoginVO {

    private String userId;
    private String userPass;
    private String token;

    public LoginVO(String userId, String userPass, String token) {
        this.userId = userId;
        this.userPass = userPass;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getToken() { return token; }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public void setToken(String token) { this.token = token; }
}
