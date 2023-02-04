package com.hnt.hnt_android.api;

import com.hnt.hnt_android.api.model.JoinResult;
import com.hnt.hnt_android.api.model.JoinVO;
import com.hnt.hnt_android.api.model.LoginResult;
import com.hnt.hnt_android.api.model.LoginVO;
import com.hnt.hnt_android.api.model.Result;

import retrofit2.Call;
import retrofit2.http.Body;
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

    @POST("login/loginProcess")
    Call<LoginResult> login(
        @Body LoginVO loginVO
    );

    @POST("login/joinProcess")
    Call<JoinResult> join(
        @Body JoinVO joinVO
    );
}
