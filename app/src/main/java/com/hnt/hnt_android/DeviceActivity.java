package com.hnt.hnt_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hnt.hnt_android.api.RetroClient;
import com.hnt.hnt_android.api.RetroInterface;
import com.hnt.hnt_android.api.model.DeviceChgResult;
import com.hnt.hnt_android.api.model.DeviceVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceActivity extends AppCompatActivity {

    private String userId, sensorUuid, sensorName, chgSensorName;
    private EditText sensor_name;
    private Button chgName, deleteDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        userId = getIntent().getStringExtra("userId");
        sensorUuid = getIntent().getStringExtra("sensorUuid");
        sensorName = getIntent().getStringExtra("sensorName");
        sensor_name = (EditText) findViewById(R.id.sensor_name);
        chgName = (Button) findViewById(R.id.chg_name);
        deleteDevice = (Button) findViewById(R.id.delete_device);

        Log.d("API", "userId : " + userId);
        Log.d("API", "sensorUuid : " + sensorUuid);
        Log.d("API", "sensorName : " + sensorName);

        chgName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != userId && !"".equals(userId) & null != sensorUuid && !"".equals(sensorUuid) && null != sensorName && !"".equals(sensorName)) {
                    chgSensorName = String.valueOf(sensor_name.getText());

                    if(null != chgSensorName && !"".equals(chgSensorName)) {
                        RetroInterface retroInterface = RetroClient.getApiService();
                        DeviceVO deviceVO = new DeviceVO(userId, sensorName, sensorUuid, chgSensorName);
                        deviceVO.setUserId(userId);
                        deviceVO.setSensorName(sensorName);
                        deviceVO.setSensorUuid(sensorUuid);
                        deviceVO.setChgSensorName(chgSensorName);

                        try {
                            Call<DeviceChgResult> deviceChgResult = retroInterface.updateSensorInfo(deviceVO);
                            deviceChgResult.enqueue(new Callback<DeviceChgResult>() {
                                @Override
                                public void onResponse(Call<DeviceChgResult> call, Response<DeviceChgResult> response) {
                                    if(response.isSuccessful()) {
                                        DeviceChgResult result = response.body();

                                        if("200".equals(result.getResultCode())) {
                                            Toast.makeText(getApplicationContext(), "장치명 변경 성공", Toast.LENGTH_SHORT).show();
                                            Intent step1 = new Intent(DeviceActivity.this, Step1Activity.class);
                                            startActivity(step1);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "장치명 변경 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.e("API", "장치명 변경 실패1");
                                    }
                                }

                                @Override
                                public void onFailure(Call<DeviceChgResult> call, Throwable t) {
                                    Log.e("API", "장치명 변경 실패2");
                                }
                            });
                        } catch(Exception e) {
                            Log.e("API", "장치명 변경 실패3 - " + e.toString());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "변경할 장치명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "장치명 변경에 필요한 필수값이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != userId && !"".equals(userId) & null != sensorUuid && !"".equals(sensorUuid) && null != sensorName && !"".equals(sensorName)) {
                    RetroInterface retroInterface = RetroClient.getApiService();
                    DeviceVO deviceVO = new DeviceVO(userId, sensorName, sensorUuid, "");
                    deviceVO.setUserId(userId);
                    deviceVO.setSensorName(sensorName);
                    deviceVO.setSensorUuid(sensorUuid);
                    deviceVO.setChgSensorName("");

                    try {
                        Call<DeviceChgResult> deviceDelResult = retroInterface.deleteSensorInfo(deviceVO);
                        deviceDelResult.enqueue(new Callback<DeviceChgResult>() {
                            @Override
                            public void onResponse(Call<DeviceChgResult> call, Response<DeviceChgResult> response) {
                                DeviceChgResult result = response.body();

                                if(response.isSuccessful()) {
                                    if("200".equals(result.getResultCode())) {
                                        Toast.makeText(getApplicationContext(), "장치 삭제 성공", Toast.LENGTH_SHORT).show();
                                        Intent step1 = new Intent(DeviceActivity.this, Step1Activity.class);
                                        startActivity(step1);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "장치 삭제 실패", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.e("API", "장치 삭제 실패1");
                                    Toast.makeText(getApplicationContext(), "장치 삭제 실패", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<DeviceChgResult> call, Throwable t) {
                                Log.e("API", "장치 삭제 실패2");
                                Toast.makeText(getApplicationContext(), "장치 삭제 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch(Exception e) {
                        Log.e("API", "장치 삭제 실패3");
                    }
                }
            }
        });
    }
}