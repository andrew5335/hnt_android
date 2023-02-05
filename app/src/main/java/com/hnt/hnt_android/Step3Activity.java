package com.hnt.hnt_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hnt.hnt_android.manager.PreferenceManager;

public class Step3Activity extends AppCompatActivity {

    private TextView curSsid;
    private EditText wifiPass;
    private Button next;
    private String cur_ssid, conSsid, wifi_pass;

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);

        curSsid = (TextView) findViewById(R.id.curssid);
        wifiPass = (EditText) findViewById(R.id.wifipass);
        next = (Button) findViewById(R.id.next);
        wifiManager = (WifiManager) getApplicationContext().getSystemService((Context.WIFI_SERVICE));

        cur_ssid = PreferenceManager.getString(getApplicationContext(), "ssid");
        if(null != cur_ssid && !"".equals(cur_ssid)) {
            Log.d("API", "111");
            curSsid.setText(cur_ssid);
        } else {
            Log.d("API", "222");
            WifiInfo currentConnection = wifiManager.getConnectionInfo();
            conSsid = currentConnection.getSSID();
            conSsid = conSsid.replace("\"", "");
            curSsid.setText(conSsid);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifi_pass = wifiPass.getText().toString();

                if(null != wifi_pass && !"".equals(wifi_pass)) {
                    PreferenceManager.setString(getApplicationContext(), "wifiPass", wifi_pass);

                    Intent step4 = new Intent(Step3Activity.this, Step4Activity.class);
                    startActivity(step4);
                } else {
                    Toast.makeText(getApplicationContext(), "WIFI AP의 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}