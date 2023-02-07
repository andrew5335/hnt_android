package com.hnt.hnt_android.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

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

    @SerializedName("deviceList")
    @Expose
    private List<Map<String, Object>> deviceList;

    public List<Map<String, Object>> getDeviceList() { return deviceList; }
}