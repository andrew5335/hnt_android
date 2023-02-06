package com.hnt.hnt_android.api.model;

public class DeviceVO {
    public String userId;
    public String sensorName;
    public String sensorUuid;
    public String chgSensorName;

    public DeviceVO(String userId, String sensorName, String sensorUuid, String chgSensorName) {
        this.userId = userId;
        this.sensorName = sensorName;
        this.sensorUuid = sensorUuid;
        this.chgSensorName = chgSensorName;
    }

    public String getUserId() { return userId; }

    public String getSensorName() { return sensorName; }

    public String getSensorUuid() { return sensorUuid; }

    public String getChgSensorName() { return chgSensorName; }

    public void setUserId(String userId) { this.userId = userId; }

    public void setSensorName(String sensorName) { this.sensorName = sensorName; }

    public void setSensorUuid(String sensorUuid) { this.sensorUuid = sensorUuid; }

    public void setChgSensorName(String chgSensorName) { this.chgSensorName = chgSensorName; }
}
