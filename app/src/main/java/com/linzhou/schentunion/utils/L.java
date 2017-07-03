package com.linzhou.schentunion.utils;

import android.util.Log;
/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.application
 *创建者:  linzhou
 *创建时间:17/04/20
 *描述:   Log封装类
 */
public class L {

    //开关
    public static final  boolean DEBUG = true;
    //TAG
    public static final String TAG = "linzhou123";

    //五个等级  DIWE

    public static void d(String text){
        if(DEBUG){
            Log.d(TAG,text);
        }
    }

    public static void i(String text){
        if(DEBUG){
            Log.i(TAG,text);
        }
    }

    public static void w(String text){
        if(DEBUG){
            Log.w(TAG,text);
        }
    }

    public static void e(String text){
        if(DEBUG){
            Log.e(TAG,text);
        }
    }
}
