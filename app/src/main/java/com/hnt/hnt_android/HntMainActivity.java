package com.hnt.hnt_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hnt.hnt_android.adapter.wifiAdapter;
import com.hnt.hnt_android.api.RetroClient;
import com.hnt.hnt_android.api.model.Result;
import com.hnt.hnt_android.dialog.wifiDialog;
import com.hnt.hnt_android.handler.BackpressHandler;
import com.hnt.hnt_android.manager.PreferenceManager;
import com.hnt.hnt_android.script.WebAppInterface;
import com.hnt.hnt_android.socket.UDPClient;
import com.pedro.library.AutoPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HntMainActivity extends AppCompatActivity {

    private Context mContext;
    private Button btnConnect;
    private PopupWindow mPopupWindow;

    private int mCurrentX = Gravity.CENTER_HORIZONTAL;
    private int mCurrentY = Gravity.CENTER_VERTICAL;

    private WifiManager wifiManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private View popupLayout;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private int port = 113;
    private String host = "192.168.0.1";

    public String wifi_ssid;
    public String wifi_pw ;

    private ConnectivityManager connectivityManager;

    private WebView webView =  null;

    private BackpressHandler backpressHandler;
    private long backBtnTime = 0;

    private String result = "";

    private Call<Result> call;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hnt_main);

        btnConnect = (Button) findViewById(R.id.btn_connect);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupLayout = inflater.inflate(R.layout.popup_wifi, null);
        recyclerView = popupLayout.findViewById(R.id.rv_recyclerview);
        mPopupWindow = new PopupWindow(popupLayout, 800, 900, true);
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService((Context.WIFI_SERVICE));

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());  // 새 창 띄우기 않기
        webView.setWebChromeClient(new WebChromeClient());
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

            }
        });
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(false);  // 줌 설정 여부
        webView.getSettings().setBuiltInZoomControls(false);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new AndroidBridge(), "hntInterface");

        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.setWebContentsDebuggingEnabled(true);
        //webView.addJavascriptInterface(new WebAppInterface(), "hntInterface");

        mContext = getApplicationContext();

        backpressHandler = new BackpressHandler(this);

        boolean wifiResult = wifiManager.isWifiEnabled();
        if(!wifiResult) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent settingIntent = new Intent(Settings.Panel.ACTION_WIFI);
                startActivityForResult(settingIntent, 1);
            } else {
                wifiManager.setWifiEnabled(true);
            }
        }



        try{
            EventBus.getDefault().register(this);
        } catch (Exception e){

        }

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

        AutoPermissions.Companion.loadAllPermissions(this,101);

        webView.loadUrl("http://hntnas.diskstation.me:8820/main/main");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    wifiScan();
                } else {
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setMyPopupWindow(){
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

        //mPopupWindow = new PopupWindow(popupLayout, 800, 900, true);
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
        List<ScanResult> tmpWifiList = new ArrayList<ScanResult>();
        List<ScanResult> wifiList = new ArrayList<ScanResult>();

        if(null != results && 0 < results.size()) {
            for(int i=0; i < results.size(); i++) {
                if(null != results.get(i).SSID && !"".equals(results.get(i).SSID)) {
                    tmpWifiList.add(results.get(i));
                }
            }
            HashSet<ScanResult> duplicate = new HashSet<ScanResult>(tmpWifiList);
            wifiList = new ArrayList<ScanResult>(duplicate);

        }

        mAdapter = new wifiAdapter(wifiList);
        recyclerView.setAdapter(mAdapter);

        Log.d("wifi", "scan success");
        StringBuffer st = new StringBuffer();
        for(ScanResult r : wifiList ){
            if(null != r.SSID && !"".equals(r.SSID)) {
                Log.d("wifi", "" + r);
                Log.d("wifi", ":" + r.SSID);
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

        if(null != wifi_ssid && !"".equals(wifi_ssid)) {
            if(null != wifi_pw && !"".equals(wifi_pw)) {
                //wifi ssid와 비밀번호가 있을 경우 wifi 접속 시도
                PreferenceManager.setString(getApplicationContext(), "ssid", wifi_ssid);
                PreferenceManager.setString(getApplicationContext(), "pw", wifi_pw);

                String userId = PreferenceManager.getString(getApplicationContext(), "userid");
                //connectToAp(wifi_ssid, wifi_pw);

                new Thread(() -> {
                    setSensor(userId, wifi_ssid, wifi_pw);
                }).start();
            }
        }

        mPopupWindow.dismiss();
    }

    public void setSensor(String userId, String ssid, String passWord) {
        Log.d("sensor", "Info : userId - " + userId + "/ ssid - " + ssid + "/ password - " + passWord);
        if(null != userId && !"".equals(userId)) {
            if(null != ssid && !"".equals(ssid)) {
                if(null != passWord && !"".equals(passWord)) {
                    try {
                        String getSensorInfoCmd = "CFG_GET";
                        String setSensorInfoCmd = "CFG_SET&user=" + userId + "&ssid=" + ssid + "&passwd=" + passWord + "&dhcp=1&rtuip=192.168.10.250&submask=255.255.255.0&gwip=192.168.10.1&dns=8.8.8.8&subdns=1.1.1.1&brkdomain=hntnas.diskstation.me&brkport=1883&brkid=hnt1&brkpw=abcde&duty=5";
                        InetAddress address = InetAddress.getByName(host);
                        UDPClient client = new UDPClient(address);

                        result = client.sendEcho(getSensorInfoCmd, port);
                        Log.d("sensor", "Info : " + result);

                        if(null != result && !"".equals(result)) {
                            client.close();

                            // CFG_GET으로 받은 결과값 저장 처리 -> 서버 api 호출하여 DB 저장
                            // CFG_GET으로 수신되는 정보 저장 필요없어 아래 내용 주석 처리
                            /**
                            call = RetroClient.getApiService().insertSensorInfo(userId, result);
                            call.enqueue(new Callback<Result>() {
                                @Override
                                public void onResponse(Call<Result> call, Response<Result> response) {
                                    Result result = response.body();
                                    String str = result.getResult();

                                    if(null != str && !"".equals(str) && "success".equals(str)) {
                                        try {
                                            String setResult = "";
                                            setResult = client2.sendEcho(setSensorInfoCmd, port);
                                        } catch(Exception e) {
                                            Log.e("HNT Error", "Error : " + e.toString());
                                        } finally {
                                            client2.close();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Result> call, Throwable t) {
                                    Log.e("HNT Error", "Error : " + t.toString());
                                }
                            });
                             **/

                            // CFG_GET으로 받은 결과값 저장 후 센서 기기에 WIFI 정보 및 사용자 아이디 설정 처리 (1초 대기 후 처리)
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    UDPClient client2 = new UDPClient(address);
                                    try {
                                        result = client2.sendEcho(setSensorInfoCmd, port);
                                    } catch(Exception e) {
                                        Log.e("Error", "Error : " + e.toString());
                                    } finally {
                                        client2.close();
                                    }
                                    //Toast.makeText(getApplicationContext(), "Result : " + result, Toast.LENGTH_LONG).show();
                                }
                            }, 500);
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                        Log.e("Error", "Error : " + e.toString());
                    }
                }
            }
        }
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

    public void connectToAp(String ssid, String password) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"ssid\"";
        wifiConfig.wepKeys[0] = "\"password\"";
        wifiConfig.priority = 999999;

        String targetSsid = wifiConfig.SSID;

        WifiInfo currentConnection = wifiManager.getConnectionInfo();
        if(currentConnection.getSSID().equals(targetSsid)) {

        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                        .setSsid(ssid) //SSID 이름
                        .setWpa2Passphrase(password) //비밀번호, 보안설정 WPA2
                        .build();

                NetworkRequest networkRequest = new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI) //연결 Type
                        .setNetworkSpecifier(wifiNetworkSpecifier)
                        .build();

                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                connectivityManager.requestNetwork(networkRequest, networkCallback);
            } else {
                Log.d("wifi", "targetSsid : " + targetSsid);
                int networkId = getIdForConfiguredNetwork(targetSsid);
                Log.d("wifi", "networkId : " + networkId);
                if (networkId == -1) {
                    networkId = wifiManager.addNetwork(wifiConfig);
                }

                wifiManager.enableNetwork(networkId, true);
            }
        }
    }

    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            Log.d("wifi", "onAvailable");
        }

        @Override
        public void onUnavailable() {
            Log.d("wifi", "onUnavailable");
        }
    };

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

    public void bindProcessToNetwork() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network network= getNetworkObjectForCurrentWifiConnection();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.bindProcessToNetwork(network);
            } else {
                ConnectivityManager.setProcessDefaultNetwork(network);
            }
        }
    }
    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Network getNetworkObjectForCurrentWifiConnection() {
        List<Network> networks = Arrays.asList(connectivityManager.getAllNetworks());
        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities.hasTransport
                    (NetworkCapabilities.TRANSPORT_WIFI)) {
                return network;
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;
        if (webView.canGoBack()) {
            webView.goBack();
        } else if (0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        } else {
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }


    }

    class AndroidBridge {
        @JavascriptInterface
        public void saveUserInfo(String str) {
            Log.d("javascript", "userId : " + str);
            PreferenceManager.setString(getApplicationContext(), "userid", str);
        }
    }
}

