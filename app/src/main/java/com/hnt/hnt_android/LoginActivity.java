package com.hnt.hnt_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private ImageButton naverLogin, kakaoLogin;
    private EditText userId, userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.login);
        naverLogin = (ImageButton) findViewById(R.id.naverlogin);
        kakaoLogin = (ImageButton) findViewById(R.id.kakaologin);
        userId = (EditText) findViewById(R.id.userid);
        userPass = (EditText) findViewById(R.id.userpass);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), userId.getText() + " / " +  userPass.getText(), Toast.LENGTH_LONG).show();
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