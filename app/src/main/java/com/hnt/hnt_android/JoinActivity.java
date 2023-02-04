package com.hnt.hnt_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class JoinActivity extends AppCompatActivity {

    private Button register;
    private EditText userId, userPass, userNm, userEmail, userTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        register = (Button) findViewById(R.id.register);
        userId = (EditText) findViewById(R.id.userid);
        userPass = (EditText) findViewById(R.id.userpass);
        userNm = (EditText) findViewById(R.id.usernm);
        userEmail = (EditText) findViewById(R.id.useremail);
        userTel = (EditText) findViewById(R.id.usertel);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}