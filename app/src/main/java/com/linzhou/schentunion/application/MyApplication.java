package com.linzhou.schentunion.application;

import android.app.Application;

import com.alipay.euler.andfix.patch.PatchManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.linzhou.schentunion.utils.UtilTools;
import com.linzhou.schentunion.websocekt.MyWebSocket;


/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.application
 *创建者:  linzhou
 *创建时间:17/04/20
 *描述:   
 */


public class MyApplication extends Application {

    public static PatchManager mPatchManager;

    /**
     * 在app创建时调用
     */
    @Override
    public void onCreate() {
        super.onCreate();
        MyWebSocket.webSocket.init(getApplicationContext());
        //TTS
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID +"=5912ba0d");
        //Andfix
        mPatchManager =new PatchManager(this);
        mPatchManager.init(UtilTools.getVersion(getApplicationContext()));
        mPatchManager.loadPatch();


    }

    /**
     * 再app退出时调用
     */
    @Override
    public void onTerminate() {
        MyWebSocket.webSocket.close();
        super.onTerminate();
    }
}
