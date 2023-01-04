package com.hnt.hnt_android.script;


import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.hnt.hnt_android.manager.PreferenceManager;

public class WebAppInterface extends AppCompatActivity {

    class Handler {
        @JavascriptInterface
        public void printLog() {
            Log.d("Handler", "handler");
        }
    }

    @JavascriptInterface
    public Object getHandler() {
        return new Handler();
    }

    @JavascriptInterface
    public String saveUserInfo(String userId) {
        String result = "fail";
        Log.d("javascript", "userId : " + userId);
        if(null != userId && !"".equals(userId)) {
            PreferenceManager.setString(getApplicationContext(), "hnt", userId);
            result = "success";
        }

        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

        return result;
    }

    @JavascriptInterface
    public String saveSensorInfo(String sensorInfo) {
        String result = "fail";

        if(null != sensorInfo && !"".equals(sensorInfo)) {
            PreferenceManager.setString(getApplicationContext(), "hnt", sensorInfo);
            result = "success";
        }

        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

        return result;
    }
}
