package com.hnt.hnt_android.api;

import com.hnt.hnt_android.api.model.Result;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetroInterface {

    @FormUrlEncoded
    @POST("data/insertSensorInfo")
    Call<Result> insertSensorInfo(
            @Field("userId") String userId
            , @Field("sensorInfo") String sensorInfo
    );
}
