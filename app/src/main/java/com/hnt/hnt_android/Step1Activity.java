package com.hnt.hnt_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hnt.hnt_android.adapter.DeviceListDecoration;
import com.hnt.hnt_android.adapter.ListViewAdapter;
import com.hnt.hnt_android.api.RetroClient;
import com.hnt.hnt_android.api.RetroInterface;
import com.hnt.hnt_android.api.model.DeviceResult;
import com.hnt.hnt_android.api.model.LoginVO;
import com.hnt.hnt_android.manager.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Step1Activity extends AppCompatActivity {

    private Button addDevice, goDashboard;
    private String userId;

    private RecyclerView listview;
    private ListViewAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);

        addDevice = (Button) findViewById(R.id.add_device);
        goDashboard = (Button) findViewById(R.id.go_dashboard);
        listview = findViewById(R.id.device_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);

        userId = PreferenceManager.getString(getApplicationContext(), "userId");

        if(null != userId && !"".equals(userId)) {
            RetroInterface retroInterface = RetroClient.getApiService();
            LoginVO loginVO = new LoginVO(userId, "", "");
            loginVO.setUserId(userId);

            try {
                Call<DeviceResult> deviceResult = retroInterface.getDeviceList(loginVO);
                deviceResult.enqueue(new Callback<DeviceResult>() {
                    @Override
                    public void onResponse(Call<DeviceResult> call, Response<DeviceResult> response) {
                        if(response.isSuccessful()) {
                            DeviceResult result = response.body();

                            if("200".equals(result.getResultCode())) {
                                List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
                                itemList = result.getDeviceList();

                                adapter = new ListViewAdapter(getApplicationContext(), itemList, onClickItem);
                                listview.setAdapter(adapter);

                                DeviceListDecoration decoration = new DeviceListDecoration();
                                listview.addItemDecoration(decoration);
                            } else {
                                //Toast.makeText(getApplicationContext(), "기기 목록 조회 실패", Toast.LENGTH_LONG).show();
                                Log.e("API", "기기목록 조회 실패1");
                            }
                        } else {
                            //Toast.makeText(getApplicationContext(), "기기 목록 조회 실패", Toast.LENGTH_LONG).show();
                            Log.e("API", "기기목록 조회 실패2");
                        }
                    }

                    @Override
                    public void onFailure(Call<DeviceResult> call, Throwable t) {
                        //Toast.makeText(getApplicationContext(), "기기 목록 조회 실패", Toast.LENGTH_LONG).show();
                        Log.e("API", "기기목록 조회 실패3");
                        t.printStackTrace();
                    }
                });
            } catch(Exception e) {

            }

            /**
            itemList.add("0");
            itemList.add("1");
            itemList.add("2");
            itemList.add("3");
            itemList.add("4");
            itemList.add("5");
            itemList.add("6");
            itemList.add("7");
            itemList.add("8");
            itemList.add("9");
            itemList.add("10");
            itemList.add("11");
             **/
        }

        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent deviceAdd = new Intent(Step1Activity.this, Step2Activity.class);
                startActivity(deviceAdd);
            }
        });

        goDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goDash = new Intent(Step1Activity.this, HntMainActivity.class);
                startActivity(goDash);
            }
        });
    }

    private View.OnClickListener onClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sensorUuid, sensorName;
            String sensor = (String) v.getTag();
            String[] sensorInfo = sensor.split(",");
            sensorUuid = sensorInfo[0];
            sensorName = sensorInfo[1];
            String userId = PreferenceManager.getString(getApplicationContext(), "userId");
            //Toast.makeText(Step1Activity.this, sensor, Toast.LENGTH_SHORT).show();
            //Toast.makeText(Step1Activity.this, sensorUuid, Toast.LENGTH_SHORT).show();
            //Toast.makeText(Step1Activity.this, sensorName, Toast.LENGTH_SHORT).show();

            Intent device = new Intent(Step1Activity.this, DeviceActivity.class);
            device.putExtra("userId", userId);
            device.putExtra("sensorUuid", sensorUuid);
            device.putExtra("sensorName", sensorName);

            startActivity(device);
        }
    };
}