package com.hnt.hnt_android.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("result")
    @Expose
    private String result;

    public String getResult() {
        return result;
    }
}
