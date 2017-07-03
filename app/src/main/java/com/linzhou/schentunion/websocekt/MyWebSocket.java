package com.linzhou.schentunion.websocekt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.utils.L;

import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.tavendo.autobahn.WebSocketOptions;
/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.application
 *创建者:  linzhou
 *创建时间:17/04/20
 *描述:   WebSocket功能封装
 */
public class MyWebSocket {
    public static MyWebSocket webSocket = new MyWebSocket(StaticClass.WEBRUL);
    //重连并发送消息：handler
    public static final int WHAT_RTMES = 100;
    //重新连接：handler
    public static final int WHAT_RT = 101;

    private WebSocketConnection conn = new WebSocketConnection();
    private WebSocketOptions option = new WebSocketOptions();

    private String url;
    private Context context;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_RTMES:
                    //Toast.makeText(context, "重连成功", Toast.LENGTH_SHORT).show();
                    Bundle b = msg.getData();
                    String mes = b.getString("mes");
                    MyWebSocket.this.sendMessage(mes);
                    break;
                case WHAT_RT:
                    openWebsocket();
                    if (!conn.isConnected())
                        handler.sendEmptyMessageDelayed(WHAT_RT, 500);
                    //else Toast.makeText(context, "重连成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private MyWebSocket(String url) {
        this.url = url;
    }

    /**
     * 初始化操作，放在自定义application之中
     * @param context
     */
    public void init(Context context){
        setContext(context);
        openWebsocket();

    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 打开长连接
     */
    public void openWebsocket() {
        try {
            conn.connect(url, new WebSocketHandler() {

                //打开websocket时调用
                @Override
                public void onOpen() {
                    Log.d("websocket", "onOpen");
                    super.onOpen();
                }

                //接收到消息时调用
                @Override
                public void onTextMessage(String payload) {
                    L.d(payload);
                    for (TextMessageListener listener : textMessageListeners) {
                        listener.remessage(payload);
                    }
                }

                //websocket关闭时调用
                @Override
                public void onClose(int code, String reason) {
                    Log.d("websocket", "onClose");
                    super.onClose(code, reason);
                }
            }, option);
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭长连接
     */
    public void close() {
        //关闭长链接
        if (conn.isConnected() && conn != null)
            conn.disconnect();

        Log.d("websocket", "close");
    }

    public void update(Context context){
        close();
        init(context);
    }


    /**
     * 重新链接，并将消息发送出去
     * @param mes 重连时要发送的数据
     */
    private void reconnection(final String mes) {

        new Thread() {
            @Override
            public void run() {
                while (!conn.isConnected()) {
                    try {
                        Thread.sleep(500);
                        openWebsocket();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Bundle b = new Bundle();
                b.putString("mes", mes);
                Message message = new Message();
                message.setData(b);
                message.what = WHAT_RTMES;
                //重连成功后再次发送上次未发送成功的消息，以和吐司一起改到Handler中去发送
                //sendMessage(mes);
                handler.sendMessage(message);
            }
        }.start();

    }

    /**
     * 重新链接
     */
    private void reconnection() {
        handler.sendEmptyMessageDelayed(WHAT_RT, 500);
    }

    /**
     * 发送消息
     *
     * @param mes 消息内容json串
     */
    public void sendMessage(String mes) {
        if (!isNetworkAvailable()) {
            Toast.makeText(context, "网络异常,请连接网络", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!conn.isConnected()) {
            //Toast.makeText(context, "连接异常,正在尝试重连！", Toast.LENGTH_SHORT).show();
            //handler.sendEmptyMessageDelayed(101,500);
            reconnection(mes);

            return;
        }
        Log.d("websocket", "isConnected:" + conn.isConnected());
        conn.sendTextMessage(mes);
        return;
    }

    /**
     * 当网络处于没有外网的WLAN状态时此方法会失效
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @return true 表示网络可用
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 接收消息时监听接口容器
     */
    private List<TextMessageListener> textMessageListeners = new ArrayList<>();

    /**
     * 移除监听
     *
     * @param listener 要移除的监听
     */
    public void removeTextMessageListener(TextMessageListener listener) {
        textMessageListeners.remove(listener);
    }

    /**
     * 添加监听
     *
     * @param listener 要添加的监听
     */
    public void addTextMessageListeners(TextMessageListener listener) {
        textMessageListeners.add(listener);
    }

    /**
     * 监听接口
     */
    public interface TextMessageListener {
        public void remessage(String mes);
    }
}
