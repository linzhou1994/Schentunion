package com.linzhou.schentunion.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.service.Myservice;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.utils.ShareUtils;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 闪烁页
 */
public class SplashActivity extends AppCompatActivity implements MyWebSocket.TextMessageListener {

    public static final int SPLASH = 10001;

    public static final int LOGIN = 10002;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SPLASH:
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case LOGIN:
                    login();
                    break;
            }
        }
    };

    private void login() {


        JSONObject json = new JSONObject();
        try {
            json.put(StaticClass.TYPE, StaticClass.LOGIN);
            JSONObject content = new JSONObject();
            content.put("tel", ShareUtils.getString(this, "name", ""));
            content.put("password", ShareUtils.getString(this, "password", ""));
            json.put(StaticClass.CONTENT, content);
            MyWebSocket.webSocket.sendMessage(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d("SplashActivity : onCreate");
        setContentView(R.layout.activity_splash);
        MyWebSocket.webSocket.addTextMessageListeners(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ShareUtils.getBoolean(SplashActivity.this,"login",true)){
            if (ShareUtils.getBoolean(SplashActivity.this, "keeppass", false)
                    && !TextUtils.isEmpty(ShareUtils.getString(this, "name", ""))
                    && !TextUtils.isEmpty(ShareUtils.getString(this, "password", ""))) {
                mHandler.sendEmptyMessageDelayed(LOGIN, 2000);

            } else {
                //延迟两秒
                mHandler.sendEmptyMessageDelayed(SPLASH, 2000);
            }
        }else {
            mHandler.sendEmptyMessageDelayed(SPLASH, 2000);
        }
    }

    @Override
    public void remessage(String mes) {
        try {

            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.LOGIN)) {
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                L.d(content.toString());
                int result = content.optInt("result");
                switch (result) {
                    case 0:
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 1:
                        Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();

                        if (ShareUtils.getBoolean(SplashActivity.this,"mes_no",true))
                            startService(new Intent(SplashActivity.this, Myservice.class));
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d("SplashActivity : onDestroy");
        MyWebSocket.webSocket.removeTextMessageListener(this);
    }
}
