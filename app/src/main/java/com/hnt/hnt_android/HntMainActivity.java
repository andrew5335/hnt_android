package com.hnt.hnt_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hnt.hnt_android.adapter.wifiAdapter;
import com.hnt.hnt_android.dialog.wifiDialog;
import com.pedro.library.AutoPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class HntMainActivity extends AppCompatActivity {

    private Context mContext;
    private Button btnConnect;
    private PopupWindow mPopupWindow;

    private int mCurrentX = Gravity.CENTER_HORIZONTAL;
    private int mCurrentY = Gravity.CENTER_VERTICAL;

    private WifiManager wifiManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public String wifi_ssid;
    public String wifi_pw ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hnt_main);

        btnConnect = (Button) findViewById(R.id.btn_connect);
        mContext = getApplicationContext();

        try{
            EventBus.getDefault().register(this);
        } catch (Exception e){

        }

        //AutoPermissions.Companion.loadAllPermissions((Activity) mContext,101);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //가운데 버튼 클릭 시 wifi list를 보여줄 popupwindow를 띄운다.
                setMyPopupWindow();
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    wifiScan();
                    //Log.d("wifi", "In this");

                } else {
                    // Permission Denied
                    //Log.d("wifi", "permission denied");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setMyPopupWindow(){

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupLayout = inflater.inflate(R.layout.popup_wifi, null);

        recyclerView = popupLayout.findViewById(R.id.rv_recyclerview);

        popupLayout.setOnTouchListener(new View.OnTouchListener() {

            private float mDx;
            private float mDy;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mDx = mCurrentX - motionEvent.getRawX();
                        mDy = mCurrentY - motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurrentX = (int) (motionEvent.getRawX() + mDx);
                        mCurrentY = (int) (motionEvent.getRawY() + mDy);
                        mPopupWindow.update(mCurrentX, mCurrentY, -1, -1);
                        break;
                }
                return true;
            }
        });

        mPopupWindow = new PopupWindow(popupLayout, 800, 900, true);
        mPopupWindow.showAtLocation(popupLayout, Gravity.CENTER, 0, 0);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.i("POP-UP", "onDismiss: ");
            }
        });

        wifiScan(); //popupwindow에서 wifi scan을 시작한다.
    }

    public void wifiScan(){
        wifiManager = (WifiManager)
                mContext.getSystemService(WIFI_SERVICE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }
    }

    private void scanSuccess() {
        // 스캔 성공시 저장된 list를 recycler view를 통해 보여줌
        List<ScanResult> results = wifiManager.getScanResults();
        mAdapter = new wifiAdapter(results);
        recyclerView.setAdapter(mAdapter);

        Log.d("wifi", "scan success");
        StringBuffer st = new StringBuffer();
        for(ScanResult r : results ){
            if(null != r.SSID && !"".equals(r.SSID)) {
                Log.d("wifi", "" + r);
                st.append(r.SSID.trim());
                st.append("\n");
            }
        }
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        Log.d("wifi", "scanFailure");
        Toast.makeText(mContext,"wifi scan에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        // potentially use older scan results ...
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void enterPWEvent(wifiDialog.WifiData event){
        wifi_ssid = event.ssid;
        wifi_pw = event.pw;

        Log.d("wifi","setting\nssid : " + wifi_ssid + "   pw : " + wifi_pw);
        mPopupWindow.dismiss();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}