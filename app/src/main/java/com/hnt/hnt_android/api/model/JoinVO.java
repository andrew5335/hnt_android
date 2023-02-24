package com.hnt.hnt_android.api.model;

public class JoinVO {

    private String userId;
    private String userPass;
    private String userNm;
    private String userTel;
    private String userEmail;
    private String deviceId;
    private String token;

    public JoinVO(String userId, String userPass, String userNm, String userTel, String userEmail, String deviceId, String token) {
        this.userId = userId;
        this.userPass = userPass;
        this.userNm = userNm;
        this.userTel = userTel;
        this.userEmail = userEmail;
        this.deviceId = deviceId;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getUserNm() { return userNm; }

    public String getUserTel() { return userTel; }

    public String getUserEmail() { return userEmail; }

    public String getDeviceId() { return deviceId; }

    public String getToken() { return token; }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setToken(String token) { this.token = token; }
}
