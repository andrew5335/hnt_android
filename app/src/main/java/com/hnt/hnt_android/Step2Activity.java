package com.hnt.hnt_android;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hnt.hnt_android.manager.PreferenceManager;

public class Step2Activity extends AppCompatActivity {

    private Button chooseNetwork;

    private WifiManager wifiManager;
    private String currentSsid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);

        chooseNetwork = (Button) findViewById(R.id.choose_network);
        wifiManager = (WifiManager) getApplicationContext().getSystemService((Context.WIFI_SERVICE));

        chooseNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivityResult.launch(settingIntent);
            }
        });
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d("API", "ok");
                } else {
                    Log.d("API", "result : " + result.getResultCode());
                    WifiInfo currentConnection = wifiManager.getConnectionInfo();
                    currentSsid = currentConnection.getSSID();
                    currentSsid = currentSsid.replace("\"", "");
                    Log.d("API", "current ssid : " + currentSsid);

                    //if(null != currentSsid && !"".equals(currentSsid) && !currentSsid.contains("unknown")) {
                    PreferenceManager.setString(getApplicationContext(), "ssid", currentSsid);

                    Intent step3 = new Intent(Step2Activity.this, Step3Activity.class);
                    startActivityResult.launch(step3);
                    finish();
                }
            }
    });
}