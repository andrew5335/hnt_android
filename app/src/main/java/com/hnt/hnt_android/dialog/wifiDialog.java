package com.hnt.hnt_android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hnt.hnt_android.R;

import org.greenrobot.eventbus.EventBus;

public class wifiDialog {

    private Context context;
    private EditText message;
    private TextView title;
    private Button okButton;
    private Button cancelButton;



    public wifiDialog(Context mContext) {
        this.context = mContext;
    }


    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(final String ssid) {


        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);
        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dlg.setContentView(R.layout.enter_pw_dialog);

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        message = (EditText) dlg.findViewById(R.id.message);
        title = (TextView) dlg.findViewById(R.id.title);
        okButton = (Button) dlg.findViewById(R.id.okButton);
        cancelButton = (Button) dlg.findViewById(R.id.cancelButton);
        title.setText(ssid);



        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        final String[] pw = new String[1];
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시
                pw[0] = message.getText().toString();


                Log.d("wifi","wifiDialog\npw : " + pw[0]);
                //ssid와 pw전달
                EventBus.getDefault().post(new WifiData(ssid, pw[0]));
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });

    }

    //Event Bus
    public class WifiData {

        public final String ssid;
        public final String pw;

        public WifiData(String ssid, String pw) {
            this.ssid = ssid;
            this.pw = pw;
        }
    }
}