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
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.hnt.hnt_android.manager.PreferenceManager;
import com.hnt.hnt_android.socket.UDPClient;

import java.net.InetAddress;

public class Step5Activity extends AppCompatActivity {

    private WifiManager wifiManager;
    private String userId, hbeeSsid, ssid, wifiPass, result;
    private static final int port = 1113;
    private static final String host = "192.168.0.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step5);

        userId = PreferenceManager.getString(getApplicationContext(), "userId");
        hbeeSsid = PreferenceManager.getString(getApplicationContext(), "hbeeSsid");
        ssid = PreferenceManager.getString(getApplicationContext(), "ssid");
        wifiPass = PreferenceManager.getString(getApplicationContext(), "wifiPass");
        wifiManager = (WifiManager) getApplicationContext().getSystemService((Context.WIFI_SERVICE));

        Log.d("API", "userId : " + userId);
        Log.d("API", "hbeeSsid : " + hbeeSsid);
        Log.d("API", "ssid : " + ssid);
        Log.d("API", "wifiPass : " + wifiPass);

        if(null != userId && !"".equals(userId) && null != hbeeSsid && !"".equals(hbeeSsid) && null != ssid && !"".equals(ssid) && null != wifiPass && !"".equals(wifiPass)) {
            new Thread(() -> {
                try {
                    String getSensorInfoCmd = "CFG_GET";
                    String setSensorInfoCmd = "CFG_SET&userId=" + userId + "&ssid=" + ssid + "&passwd=" + wifiPass + "&dhcp=1&rtuip=192.168.10.250&submask=255.255.255.0&gwip=192.168.10.1&dns=8.8.8.8&subdns=1.1.1.1&brkdomain=hntnas.diskstation.me&brkport=1883&brkid=hnt1&brkpw=abcde&duty=5";
                    InetAddress address = InetAddress.getByName(host);
                    UDPClient client = new UDPClient(address);

                    Log.d("API", "Info : " + host);

                    result = client.sendEcho(getSensorInfoCmd, port);

                    Log.d("API", "Info : " + result);

                    if(null != result && !"".equals(result)) {
                        client.close();

                        Thread.sleep(1000);
                        UDPClient client2 = new UDPClient(address);
                        try {
                            result = client2.sendEcho(setSensorInfoCmd, port);
                            Log.d("API", "Info : " + result);
                        } catch (Exception e) {
                            Log.e("API", "Error : " + e.toString());
                        } finally {
                            client2.close();
                        }

                        // CFG_GET으로 받은 결과값 저장 후 센서 기기에 WIFI 정보 및 사용자 아이디 설정 처리 (1초 대기 후 처리)
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Result : " + result, Toast.LENGTH_LONG).show();
                                Intent main = new Intent(Step5Activity.this, HntMainActivity.class);
                                startActivityResult.launch(main);
                            }
                        }, 0);
                    }
                } catch(Exception e) {

                }
            }).start();
        }
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

                            Intent step5 = new Intent(Step5Activity.this, Step5Activity.class);
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