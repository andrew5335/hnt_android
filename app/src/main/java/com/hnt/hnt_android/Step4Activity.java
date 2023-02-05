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

public class Step4Activity extends AppCompatActivity {

    private Button chooseHbee;

    private WifiManager wifiManager;
    private String hbeeSsid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step4);

        chooseHbee = (Button) findViewById(R.id.choose_hbee);
        wifiManager = (WifiManager) getApplicationContext().getSystemService((Context.WIFI_SERVICE));

        chooseHbee.setOnClickListener(new View.OnClickListener() {
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
                        hbeeSsid = currentConnection.getSSID();
                        hbeeSsid = hbeeSsid.replace("\"", "");
                        Log.d("API", "hbeeSsid : " + hbeeSsid);

                        //if(null != currentSsid && !"".equals(currentSsid) && !currentSsid.contains("unknown")) {
                        if(hbeeSsid.startsWith("HBee")) {
                            PreferenceManager.setString(getApplicationContext(), "hbeeSsid", hbeeSsid);

                            Intent step5 = new Intent(Step4Activity.this, Step5Activity.class);
                            startActivityResult.launch(step5);
                            finish();
                        } else {
                            Intent settingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivityResult.launch(settingIntent);
                        }
                    }
                }
            });
}