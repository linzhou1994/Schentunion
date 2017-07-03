package com.linzhou.schentunion.service;

/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.service
 *创建者:  linzhou
 *创建时间:17/05/04
 *描述:   
 */


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.linzhou.schentunion.activity.LoginActivity;
import com.linzhou.schentunion.base.AppConfig;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Chat;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.utils.UtilTools;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Myservice extends Service implements MyWebSocket.TextMessageListener {

    @Override
    public void onCreate() {
        super.onCreate();
        L.d("Myservice:onCreate");
        MyWebSocket.webSocket.addTextMessageListeners(this);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void remessage(String mes) {
        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.REV)) {
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                String title = content.optString("ename");
                String ct = content.optString("content");
                UtilTools.showNotification(getApplicationContext(), title, ct);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyWebSocket.webSocket.removeTextMessageListener(this);
        L.d("onDestroy");
    }
}
