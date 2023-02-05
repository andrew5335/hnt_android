package com.hnt.hnt_android.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfo {

    @SerializedName("no")
    @Expose
    public int no;    // 번호 - 사용자 고유 아이디

    public int getNo() {
        return no;
    }

    @SerializedName("userNm")
    @Expose
    public String userNm;    // 사용자명

    public String getUserNm() {
        return userNm;
    }

    @SerializedName("userTel")
    @Expose
    public String userTel;    // 사용자 전화번호

    public String getUserTel() {
        return userTel;
    }

    @SerializedName("userEmail")
    @Expose
    public String userEmail;    // 사용자 메일주소

    public String getUserEmail() {
        return userEmail;
    }

    @SerializedName("userId")
    @Expose
    public String userId;    // 사용자 아이디

    public String getUserId() {
        return userId;
    }

    @SerializedName("userPass")
    @Expose
    public String userPass;    // 사용자 비밀번호

    public String getUserPass() {
        return "";
    }

    @SerializedName("userGrade")
    @Expose
    public String userGrade;    // 사용자 등급 - A : Admin / U : User

    public String getUserGrade() {
        return userGrade;
    }

    @SerializedName("useYn")
    @Expose
    public String useYn;    // 사용 여부

    public String getUseYn() {
        return useYn;
    }

    @SerializedName("delYn")
    @Expose
    public String delYn;    // 삭제 여부

    public String getDelYn() {
        return delYn;
    }

    @SerializedName("instId")
    @Expose
    public String instId;    // 입력자 아이디

    public String getInstId() {
        return instId;
    }

    @SerializedName("mdfId")
    @Expose
    public String mdfId;    // 수정자 아이디

    public String getMdfId() {
        return mdfId;
    }

    @SerializedName("deviceId")
    @Expose
    public String deviceId;

    public String getDeviceId() { return deviceId; }
}

