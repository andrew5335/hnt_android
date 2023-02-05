package com.hnt.hnt_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hnt.hnt_android.api.RetroClient;
import com.hnt.hnt_android.api.RetroInterface;
import com.hnt.hnt_android.api.model.JoinResult;
import com.hnt.hnt_android.api.model.JoinVO;
import com.hnt.hnt_android.manager.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {

    private Button register;
    private EditText userId, userPass, userNm, userEmail, userTel, deviceId;

    private String user_id, user_pass, user_nm, user_email, user_tel, device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        register = (Button) findViewById(R.id.register);
        userId = (EditText) findViewById(R.id.userid);
        userPass = (EditText) findViewById(R.id.userpass);
        userNm = (EditText) findViewById(R.id.usernm);
        userTel = (EditText) findViewById(R.id.usertel) ;
        userEmail = (EditText) findViewById(R.id.useremail);
        deviceId = (EditText) findViewById(R.id.deviceid);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_id = userId.getText().toString();
                user_pass = userPass.getText().toString();
                user_nm = userNm.getText().toString();
                user_tel = userTel.getText().toString();
                user_email = userEmail.getText().toString();
                device_id = deviceId.getText().toString();

                if(null != user_id && !"".equals(user_id) && null != user_pass && !"".equals(user_pass) && null != user_nm && !"".equals(user_nm)
                        && null != user_tel && !"".equals(user_tel) && null != user_email && !"".equals(user_email) && null != device_id && !"".equals(device_id)) {
                    // 모든 정보를 입력받은 경우 회원가입 진행
                    RetroInterface retroInterface = RetroClient.getApiService();
                    JoinVO joinVO = new JoinVO(user_id, user_pass, user_nm, user_tel, user_email, device_id);
                    joinVO.setUserId(user_id);
                    joinVO.setUserPass(user_pass);
                    joinVO.setUserNm(user_nm);
                    joinVO.setUserTel(user_tel);
                    joinVO.setUserEmail(user_email);
                    joinVO.setDeviceId(device_id);

                    try {
                        Call<JoinResult> joinResult = retroInterface.join(joinVO);
                        joinResult.enqueue(new Callback<JoinResult>() {
                            @Override
                            public void onResponse(Call<JoinResult> call, Response<JoinResult> response) {
                                if(response.isSuccessful()) {
                                    JoinResult result = response.body();

                                    if("200".equals(result.getResultCode())) {
                                        PreferenceManager.setString(getApplicationContext(), "userId", result.getUserInfo().userId);
                                        PreferenceManager.setString(getApplicationContext(), "userNm", result.getUserInfo().userNm);
                                        PreferenceManager.setString(getApplicationContext(), "userEmail", result.getUserInfo().userEmail);
                                        PreferenceManager.setString(getApplicationContext(), "userGrade", result.getUserInfo().userGrade);
                                        PreferenceManager.setString(getApplicationContext(), "userTel", result.getUserInfo().userTel);
                                        PreferenceManager.setString(getApplicationContext(), "deviceId", result.getUserInfo().deviceId);

                                        Intent intent = new Intent(JoinActivity.this, Step1Activity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JoinResult> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch(Exception e) {
                        Log.e("API", e.toString());
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "회원가입에 필요한 정보가 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}