package com.hnt.hnt_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Step1Activity extends AppCompatActivity {

    private Button addDevice, goDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);

        addDevice = (Button) findViewById(R.id.add_device);
        goDashboard = (Button) findViewById(R.id.go_dashboard);

        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent deviceAdd = new Intent(Step1Activity.this, Step2Activity.class);
                startActivity(deviceAdd);
            }
        });

        goDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goDash = new Intent(Step1Activity.this, HntMainActivity.class);
                startActivity(goDash);
            }
        });
    }
}