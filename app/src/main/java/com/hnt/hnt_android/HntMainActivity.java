package com.hnt.hnt_android;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hnt.hnt_android.adapter.AccessPointAdapter;
import com.hnt.hnt_android.databinding.ActivityHntmainBinding;
import com.hnt.hnt_android.vo.AccessPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class HntMainActivity extends AppCompatActivity {

    Vector<AccessPoint> accessPoints;
    LinearLayoutManager linearLayoutManager;
    AccessPointAdapter accessPointAdapter;
    WifiManager wifiManager;
    List<ScanResult> scanResult;
    ActivityHntmainBinding binding;
    /* Location permission 을 위한 필드 */
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    // 원하는 권한을 배열로 넣어줍니다.
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    /* Location permission 을 위한 필드 */

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissions()) {
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hntmain);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.accessPointRecyclerView.setHasFixedSize(true);
        binding.accessPointRecyclerView.setLayoutManager(linearLayoutManager);

        accessPoints = new Vector<>();

        if (wifiManager != null) {
            Log.e("check", "111");
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(mWifiScanReceiver, filter);
            wifiManager.startScan();
        }

        accessPointAdapter = new AccessPointAdapter(accessPoints, HntMainActivity.this);
        binding.accessPointRecyclerView.setAdapter(accessPointAdapter);
        accessPointAdapter.notifyDataSetChanged();
    }

    private BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null) {
                Log.e("check", "222");
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    getWIFIScanResult();
                    wifiManager.startScan();
                    Log.e("check", "333");
                } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    getWIFIScanResult();
                    wifiManager.startScan();
                    context.sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
                    Log.e("check", "444");
                }

            }

        }
    };

    public void getWIFIScanResult() {
        scanResult = wifiManager.getScanResults();
        if (accessPoints.size() != 0) {
            accessPoints.clear();
        }
        for (int i = 0; i < scanResult.size(); i++) {
            ScanResult result = scanResult.get(i);
            //if (result.frequency < 3000) {
                Log.e(". SSID : " + result.SSID,
                        result.level + ", " + result.BSSID);
            Log.e("check", "555");
                accessPoints.add(new AccessPoint(result.SSID, result.BSSID, String.valueOf(result.level)));
            //}
        }
        //accessPointAdapter = new AccessPointAdapter(accessPoints, HntMainActivity.this);
        //binding.accessPointRecyclerView.setAdapter(accessPointAdapter);
        accessPointAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiScanReceiver);
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(HntMainActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(HntMainActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "granted");
                }
            }
        }
    }
}
