package com.hnt.hnt_android;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
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

    private static final int port = 1113;
    private static final String host = "192.168.0.1";

    public String wifi_ssid;
    public String wifi_pw ;

    private ConnectivityManager connectivityManager;

    private WebView webView =  null;

    private BackpressHandler backpressHandler;
    private long backBtnTime = 0;

    private String result = "";

    private Call<Result> call;

    private EditText message;
    private TextView title;
    private Button okButton;
    private Button cancelButton;
    private String currentSsid;

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

        /**
        if(wifiResult) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setMyPopupWindow();
                }
            }, 1000);
        }
         **/

        try{
            EventBus.getDefault().register(this);
        } catch (Exception e){

        }

        if(wifiManager.isWifiEnabled()) {
            WifiInfo currentConnection = wifiManager.getConnectionInfo();
            currentSsid = currentConnection.getSSID();
            Log.d("sensor", "current ssid : " + currentSsid);
            //PreferenceManager.setString(getApplicationContext(), "ssid", currentSsid);
        } else {
            Intent settingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            //startActivityForResult(settingIntent, 1);
            startActivityResult.launch(settingIntent);
        }

        Dialog dlg = new Dialog(this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 액티비티의 타이틀바를 숨긴다.
                dlg.setContentView(R.layout.enter_pw_dialog);

                // 커스텀 다이얼로그의 각 위젯들을 정의한다.
                message = (EditText) dlg.findViewById(R.id.message);
                title = (TextView) dlg.findViewById(R.id.title);
                okButton = (Button) dlg.findViewById(R.id.okButton);
                cancelButton = (Button) dlg.findViewById(R.id.cancelButton);
                title.setText(currentSsid);

                // 커스텀 다이얼로그를 노출한다.
                dlg.show();

                final String[] pw = new String[1];
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WifiInfo currentConnection = wifiManager.getConnectionInfo();
                        String nowSsid = currentConnection.getSSID();

                        if(nowSsid.contains("HBee")) {
                            String userId = PreferenceManager.getString(getApplicationContext(), "userId");
                            pw[0] = message.getText().toString();
                            Log.d("wifi", "wifiDialog\npw : " + pw[0]);
                            String ssid = PreferenceManager.getString(getApplicationContext(), "ssid");

                            setSensor(userId, ssid, pw[0]);
                            dlg.dismiss();
                        } else {
                            Intent settingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            //startActivityForResult(settingIntent, 1);
                            startActivityResult.launch(settingIntent);
                            dlg.dismiss();
                        }
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "취소 했습니다.", Toast.LENGTH_SHORT).show();

                        // 커스텀 다이얼로그를 종료한다.
                        dlg.dismiss();
                    }
                });
                /**
                 if(wifiManager.isWifiEnabled()) {
                 WifiInfo currentConnection = wifiManager.getConnectionInfo();
                 if(!currentConnection.getSSID().contains("HBee")) {
                 setMyPopupWindow();
                 } else {
                 String userId = "";
                 String ssid = "";
                 String password = "";

                 userId = PreferenceManager.getString(getApplicationContext(), "userId");
                 ssid = PreferenceManager.getString(getApplicationContext(), "ssid");
                 password = PreferenceManager.getString(getApplicationContext(), "password");

                 if(null != userId && !"".equals(userId) && null != ssid && !"".equals(ssid) && null != password && !"".equals(password)) {
                 setSensor(userId, ssid, password);
                 } else {
                 setMyPopupWindow();
                 }
                 }
                 } else {
                 setMyPopupWindow();
                 }
                 **/
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
                //PreferenceManager.setString(getApplicationContext(), "ssid", wifi_ssid);
                //PreferenceManager.setString(getApplicationContext(), "pw", wifi_pw);

                String userId = PreferenceManager.getString(getApplicationContext(), "userid");
                if(!wifi_ssid.contains("HBee")) {
                    PreferenceManager.setString(getApplicationContext(), "ssid", wifi_ssid);
                    PreferenceManager.setString(getApplicationContext(), "password", wifi_pw);
                    connectToAp(wifi_ssid, wifi_pw);
                } else {
                    WifiInfo currentConnection = wifiManager.getConnectionInfo();
                    Log.d("sensor", "current ssid : " + currentConnection.getSSID());
                    if(currentConnection.getSSID().equals(wifi_ssid)) {

                    } else {
                        connectToAp(wifi_ssid, wifi_pw);
                    }

                    try {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setSensor(userId, wifi_ssid, wifi_pw);
                            }
                        }, 3000);
                    } catch(Exception e) {

                    }
                }
            }
        }

        mPopupWindow.dismiss();
    }

    public void setSensor(String userId, String ssid, String password) {
        if(ssid.contains("HBee")) {
            ssid = PreferenceManager.getString(getApplicationContext(), "ssid");
            password = PreferenceManager.getString(getApplicationContext(), "password");
        }

        Log.d("sensor", "Info : userId - " + userId + "/ ssid - " + ssid + "/ password - " + password);
        if(null != userId && !"".equals(userId)) {
            if(null != ssid && !"".equals(ssid)) {
                if(null != password && !"".equals(password)) {

                    try {
                        String finalSsid = ssid;
                        String filanPassword = password;
                        Log.d("sensor", "Info finalSsid: " + finalSsid);
                        Log.d("sensor", "Info finalPassword : " + filanPassword);
                        new Thread(() -> {
                            try {
                                String getSensorInfoCmd = "CFG_GET";
                                String setSensorInfoCmd = "CFG_SET&userId=" + userId + "&ssid=" + finalSsid + "&passwd=" + filanPassword + "&dhcp=1&rtuip=192.168.10.250&submask=255.255.255.0&gwip=192.168.10.1&dns=8.8.8.8&subdns=1.1.1.1&brkdomain=hntnas.diskstation.me&brkport=1883&brkid=hnt1&brkpw=abcde&duty=5";
                                InetAddress address = InetAddress.getByName(host);
                                UDPClient client = new UDPClient(address);

                                Log.d("sensor", "Info : " + host);

                                result = client.sendEcho(getSensorInfoCmd, port);

                                Log.d("sensor", "Info : " + result);

                                if(null != result && !"".equals(result)) {
                                    client.close();

                                    Thread.sleep(1000);
                                    UDPClient client2 = new UDPClient(address);
                                    try {
                                        result = client2.sendEcho(setSensorInfoCmd, port);
                                        Log.d("sensor", "Info : " + result);
                                    } catch (Exception e) {
                                        Log.e("Error", "Error : " + e.toString());
                                    } finally {
                                        client2.close();
                                    }

                                    // CFG_GET으로 받은 결과값 저장 후 센서 기기에 WIFI 정보 및 사용자 아이디 설정 처리 (1초 대기 후 처리)
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Result : " + result, Toast.LENGTH_LONG).show();
                                        }
                                    }, 0);
                                }
                            } catch(Exception e) {
                                Log.e("Error", "Error : " + e.toString());
                            }
                        }).start();

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
Log.d("sensor", "111");
        if(wifiManager.isWifiEnabled()) {
            WifiInfo currentConnection = wifiManager.getConnectionInfo();
            Log.d("sensor", "cur ssid : " + currentConnection.getSSID());
            if(!currentConnection.getSSID().contains("HBee")) {
                Intent settingIntent = new Intent(Settings.Panel.ACTION_WIFI);
                //startActivityForResult(settingIntent, 1);
                //startActivityResult.launch(settingIntent);
                //wifiManager.setWifiEnabled(false);
            }
        } else {
            Intent settingIntent = new Intent(Settings.Panel.ACTION_WIFI);
            //startActivityForResult(settingIntent, 1);
            startActivityResult.launch(settingIntent);
        }


        WifiInfo currentConnection = wifiManager.getConnectionInfo();

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"ssid\"";
        wifiConfig.wepKeys[0] = "\"password\"";
        wifiConfig.priority = 999999;

        String targetSsid = wifiConfig.SSID;

        if (currentConnection.getSSID().equals(targetSsid)) {

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.d("sensor", "222");
                WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                        .setSsid(ssid) //SSID 이름
                        .setWpa2Passphrase(password) //비밀번호, 보안설정 WPA2
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
                connectivityManager.requestNetwork(networkRequest, networkCallback);
            } else {
                Log.d("sensor", "333");
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
            super.onAvailable(network);
            Log.d("wifi", "onAvailable");
            connectivityManager.bindProcessToNetwork(network);
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
        } else {
            //backBtnTime = curTime;
            //Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            //super.onBackPressed();
            finishAffinity();
            System.runFinalization();
            System.exit(0);
        }


    }

    class AndroidBridge {
        @JavascriptInterface
        public void saveUserInfo(String str) {
            Log.d("javascript", "userId : " + str);
            PreferenceManager.setString(getApplicationContext(), "userid", str);
        }
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d("sensor", "ok");
                }
            }
    });
}

