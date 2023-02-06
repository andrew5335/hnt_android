package com.hnt.hnt_android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hnt.hnt_android.api.RetroClient;
import com.hnt.hnt_android.api.RetroInterface;
import com.hnt.hnt_android.api.model.LoginResult;
import com.hnt.hnt_android.api.model.LoginVO;
import com.hnt.hnt_android.manager.PreferenceManager;
import com.pedro.library.AutoPermissions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button login, join;
    private ImageButton naverLogin, kakaoLogin;
    private EditText userId, userPass;

    private String user_id, user_pass;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.login);
        join = (Button) findViewById(R.id.join);
        naverLogin = (ImageButton) findViewById(R.id.naverlogin);
        kakaoLogin = (ImageButton) findViewById(R.id.kakaologin);
        userId = (EditText) findViewById(R.id.userid);
        userPass = (EditText) findViewById(R.id.userpass);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = userId.getText().toString();
                user_pass = userPass.getText().toString();

                if(null != user_id && !"".equals(user_id) && null != user_pass && !"".equals(user_pass)) {
                    // 사용자 아이디, 비밀번호가 있을 경우 로그인 처리 및 세션 처리 진행
                    RetroInterface retroInterface = RetroClient.getApiService();
                    LoginVO loginVO = new LoginVO(user_id, user_pass);
                    loginVO.setUserId(user_id);
                    loginVO.setUserPass(user_pass);

                    try {
                        Call<LoginResult> loginResult = retroInterface.login(loginVO);
                        loginResult.enqueue(new Callback<LoginResult>() {
                            @Override
                            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                                if(response.isSuccessful()) {
                                    LoginResult result = response.body();

                                    if("200".equals(result.getResultCode())) {
                                        PreferenceManager.setString(getApplicationContext(), "userId", result.getUserInfo().userId);
                                        PreferenceManager.setString(getApplicationContext(), "userNm", result.getUserInfo().userNm);
                                        PreferenceManager.setString(getApplicationContext(), "userEmail", result.getUserInfo().userEmail);
                                        PreferenceManager.setString(getApplicationContext(), "userGrade", result.getUserInfo().userGrade);
                                        PreferenceManager.setString(getApplicationContext(), "userTel", result.getUserInfo().userTel);

                                        Intent intent = new Intent(LoginActivity.this, Step1Activity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResult> call, Throwable t) {
                                //Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();
                                Log.d("API", t.toString());
                            }
                        });
                    } catch(Exception e) {
                        Log.e("API", e.toString());
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "사용자 아이디 또는 비밀번호가 없습니다.", Toast.LENGTH_LONG).show();
                }

            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "register", Toast.LENGTH_LONG).show();
                Intent joinIntent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(joinIntent);
            }
        });

        naverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "click naver login", Toast.LENGTH_LONG).show();
            }
        });

        kakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "click kakao login", Toast.LENGTH_LONG).show();
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
        chkInternet();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void chkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Service.CONNECTIVITY_SERVICE);

        /* you can print your active network via using below */
        Log.i("myNetworkType: ", connectivityManager.getActiveNetworkInfo().getTypeName());
        WifiManager wifiManager= (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);


        Log.i("routes ", connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getRoutes().toString());
        Log.i("dhcp server ", connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getDhcpServerAddress().toString().replace("/", ""));
        Log.i("ip address ", connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getLinkAddresses().toString());
        Log.i("dns address ", connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getDnsServers().toString());



        if(connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI) {
            Log.i("myType ", "wifi");
            DhcpInfo d =wifiManager.getDhcpInfo();
            Log.i("info", d.toString()+"");
        }
        else if(connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_ETHERNET) {
            /* there is no EthernetManager class, there is only WifiManager. so, I used this below trick to get my IP range, dns, gateway address etc */

            Log.i("myType ", "Ethernet");
            Log.i("routes ", connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getRoutes().toString());
            Log.i("domains ", connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getDomains().toString());
            Log.i("ip address ", connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getLinkAddresses().toString());
            Log.i("dns address ", connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getDnsServers().toString());

        }
        else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}