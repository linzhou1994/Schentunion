package com.linzhou.schentunion.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONException;
import org.json.JSONObject;


/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.bese
 *创建者:  linzhou
 *创建时间:17/04/21
 *描述:   activity的基类
 */


public abstract class BaseActivity extends AppCompatActivity implements MyWebSocket.TextMessageListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        //显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(getlayout());
        initview();
        initData();
        setListener();
    }

    //菜单栏操作
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract int getlayout();

    protected abstract void initview();

    protected abstract void initData();

    protected  void setListener(){
        MyWebSocket.webSocket.addTextMessageListeners(this);
    }

    @Override
    public void remessage(String mes) {
        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.LOGOUT)){
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyWebSocket.webSocket.removeTextMessageListener(this);
    }
}
