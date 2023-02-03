package com.hnt.hnt_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hnt.hnt_android.api.RetroClient;
import com.hnt.hnt_android.api.RetroInterface;
import com.hnt.hnt_android.api.model.LoginResult;
import com.hnt.hnt_android.api.model.LoginVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button login, join;
    private ImageButton naverLogin, kakaoLogin;
    private EditText userId, userPass;

    private String user_id, user_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.login);
        join = (Button) findViewById(R.id.join);
        naverLogin = (ImageButton) findViewById(R.id.naverlogin);
        kakaoLogin = (ImageButton) findViewById(R.id.kakaologin);
        userId = (EditText) findViewById(R.id.userid);
        userPass = (EditText) findViewById(R.id.userpass);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = userId.getText().toString();
                user_pass = userPass.getText().toString();

                if(null != user_id && !"".equals(user_id) && null != user_pass && !"".equals(user_pass)) {
                    // 사용자 아이디, 비밀번호가 있을 경우 로그인 처리 및 세션 처리 진행
                    RetroInterface retroInterface = RetroClient.getApiService();
                    LoginVO loginVO = new LoginVO(user_id, user_pass);
                    loginVO.setUserId(user_id);
                    loginVO.setUserPass(user_pass);

                    try {
                        Call<LoginResult> loginResult = retroInterface.login(loginVO);
                        loginResult.enqueue(new Callback<LoginResult>() {
                            @Override
                            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                                if(response.isSuccessful()) {
                                    LoginResult result = response.body();
                                    Log.i("API", "success");
                                    Log.i(":API", result.getResultCode());
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResult> call, Throwable t) {

                            }
                        });
                    } catch(Exception e) {

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "사용자 아이디 또는 비밀번호가 없습니다.", Toast.LENGTH_LONG).show();
                }

            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "register", Toast.LENGTH_LONG).show();
            }
        });

        naverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "click naver login", Toast.LENGTH_LONG).show();
            }
        });

        kakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "click kakao login", Toast.LENGTH_LONG).show();
            }
        });
    }
}