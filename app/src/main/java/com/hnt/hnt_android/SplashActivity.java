package com.hnt.hnt_android;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hnt.hnt_android.handler.BackpressHandler;
import com.hnt.hnt_android.manager.PreferenceManager;
import com.pedro.library.AutoPermissions;

public class SplashActivity extends AppCompatActivity {

    private Button wifiSetting;
    private ConstraintLayout splashLinear;
    private WifiManager wifiManager;

    private BackpressHandler backpressHandler;
    private long backBtnTime = 0;

    private String currentSsid;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        wifiManager = (WifiManager) getApplicationContext().getSystemService((Context.WIFI_SERVICE));
        splashLinear = (ConstraintLayout) findViewById(R.id.splash_linear);
        wifiSetting = (Button) findViewById(R.id.wifi_setting);

        splashLinear.setBackgroundColor(Color.WHITE);

        wifiSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                //startActivityForResult(settingIntent, 1);
                startActivityResult.launch(settingIntent);

                //Intent hntMain = new Intent(SplashActivity.this, HntMainActivity.class);
                //startActivityResult.launch(hntMain);
            }
        });

        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        AutoPermissions.Companion.loadAllPermissions(this,101);
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d("sensor", "ok");
                } else {
                    if(wifiManager.isWifiEnabled()) {
                        Log.d("sensor", "result : " + result.getResultCode());
                        WifiInfo currentConnection = wifiManager.getConnectionInfo();
                        currentSsid = currentConnection.getSSID();
                        Log.d("sensor", "current ssid : " + currentSsid);

                        if(null != currentSsid && !"".equals(currentSsid) && !currentSsid.contains("unknown")) {
                            PreferenceManager.setString(getApplicationContext(), "ssid", currentSsid);

                            Intent hntMain = new Intent(SplashActivity.this, HntMainActivity.class);
                            startActivityResult.launch(hntMain);
                            finish();
                        } else {
                            Intent settingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            //startActivityForResult(settingIntent, 1);
                            startActivityResult.launch(settingIntent);
                        }
                    } else {
                        Intent settingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        //startActivityForResult(settingIntent, 1);
                        startActivityResult.launch(settingIntent);
                    }
                }
            }
    });

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;
        backBtnTime = curTime;
        //Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }
}