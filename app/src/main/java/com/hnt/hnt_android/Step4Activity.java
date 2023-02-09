package com.hnt.hnt_android;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hnt.hnt_android.manager.PreferenceManager;

import java.util.List;

public class Step4Activity extends AppCompatActivity {

    private Button chooseHbee;

    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private String hbeeSsid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step4);

        chooseHbee = (Button) findViewById(R.id.choose_hbee);
        wifiManager = (WifiManager) getApplicationContext().getSystemService((Context.WIFI_SERVICE));
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        chooseHbee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivityResult.launch(settingIntent);
            }
        });
    }

    public int getIdForConfiguredNetwork(String ssid) {
        Log.d("wifi", "ssid : " + ssid);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration configNetwork : configuredNetworks) {
            if (configNetwork.SSID.equals(ssid)) {
                return configNetwork.networkId;
            }
        }
        return -1;
    }

    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            Log.d("wifi", "onAvailable");
            connectivityManager.bindProcessToNetwork(network);
        }

        @Override
        public void onUnavailable() {
            Log.d("wifi", "onUnavailable");
        }
    };

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

                            WifiConfiguration wifiConfig = new WifiConfiguration();
                            wifiConfig.SSID = "\"hbeeSsid\"";
                            wifiConfig.priority = 999999;

                            String targetSsid = wifiConfig.SSID;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                Log.d("sensor", "222");
                                WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                                        .setSsid(hbeeSsid) //SSID 이름
                                        .build();

                                NetworkRequest networkRequest = new NetworkRequest.Builder()
                                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI) //연결 Type
                                        .setNetworkSpecifier(wifiNetworkSpecifier)
                                        .build();

                                NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();
                                networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
                                networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
                                networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

                                connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                //connectivityManager.requestNetwork(networkRequest, networkCallback);
                            } else {
                                Log.d("sensor", "333");
                                Log.d("wifi", "targetSsid : " + targetSsid);
                                int networkId = getIdForConfiguredNetwork(targetSsid);
                                Log.d("wifi", "networkId : " + networkId);
                                if (networkId == -1) {
                                    networkId = wifiManager.addNetwork(wifiConfig);
                                }

                                //wifiManager.enableNetwork(networkId, true);
                            }

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