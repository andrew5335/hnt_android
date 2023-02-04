package com.hnt.hnt_android.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceResult {

    @SerializedName("resultCode")
    @Expose
    private String resultCode;

    public String getResultCode() {
        return resultCode;
    }

    @SerializedName("resultMessage")
    @Expose
    private String resultMessage;

    public String getResultMessage() {
        return resultMessage;
    }
}
