package com.linzhou.schentunion.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.linzhou.schentunion.Adapter.MymeslvAdapter;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.activity.ChatActivity;
import com.linzhou.schentunion.base.AppConfig;
import com.linzhou.schentunion.base.BaseFragment;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Chat;
import com.linzhou.schentunion.data.Contact;
import com.linzhou.schentunion.data.Post;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.view.CustomDialog;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.fragment
 *创建者:  linzhou
 *创建时间:17/04/21
 *描述:   消息列表
 */


public class MessageFragment extends BaseFragment
        implements MyWebSocket.TextMessageListener, MymeslvAdapter.MeslvListener, View.OnClickListener {


    private MymeslvAdapter mymeslvAdapter;

    private ListView lv_mes;
    private CustomDialog dialog;
    private Button btn_delete;
    private Button btn_top;
    private SwipeRefreshLayout mSwipeRefresh;

    private int point = -1;


    private Vibrator vibrator;

    @Override
    public int getlayoutId() {

        return R.layout.message_fragment;
    }

    @Override
    protected void initView(View view) {
        sendgetdata();
        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.mSwipeRefresh);
        mSwipeRefresh.setRefreshing(true);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefresh.setDistanceToTriggerSync(400);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeRefresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小


        lv_mes = (ListView) view.findViewById(R.id.lv_mes);

        //初始化dialog
        dialog = new CustomDialog(getActivity(), 0, 0,
                R.layout.dialog_delete_contact, R.style.pop_anim_style, Gravity.BOTTOM, 0);
        dialog.setCanceledOnTouchOutside(true);
        btn_delete = (Button) dialog.findViewById(R.id.btn_delete);
        btn_top = (Button) dialog.findViewById(R.id.btn_top);
    }

    public void sendgetdata() {
        JSONObject jo = new JSONObject();
        try {
            jo.put(StaticClass.TYPE, StaticClass.SESSIONLIST);
            jo.put(StaticClass.CONTENT, new JSONObject());
            MyWebSocket.webSocket.sendMessage(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void initData() {

//        for (int i = 0; i < 100; i++) {
//            Contact contact = new Contact();
//            contact.id = i;
//            contact.type = i % 2;
//            contact.name = "name:" + i;
//            contact.message = "message:" + i;
//            contact.mesnew = i % 2 == 1 ? true : false;
//            contact.picture="test.png";
//            contact.time = new Date();
//            mContacts.add(contact);
//        }
//
//        L.d("MessageFragment:oncreate");
        mymeslvAdapter = new MymeslvAdapter(getActivity(), AppConfig.mContacts);
        lv_mes.setAdapter(mymeslvAdapter);
    }

    @Override
    protected void setListener() {
        MyWebSocket.webSocket.addTextMessageListeners(this);
        mymeslvAdapter.setMeslvListener(this);

        btn_delete.setOnClickListener(this);

        btn_top.setOnClickListener(this);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendgetdata();
            }
        });
    }

    /**
     * view点击事件方法实现
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_delete:
                if (point == -1) break;
                AppConfig.mContacts.remove(point);
                mymeslvAdapter.notifyDataSetChanged();
                dialog.dismiss();
                point = -1;
                break;
            case R.id.btn_top:
                if (point == -1) break;
                Contact contact = AppConfig.mContacts.get(point);
                AppConfig.mContacts.remove(point);
                AppConfig.mContacts.add(0, contact);
                mymeslvAdapter.notifyDataSetChanged();
                dialog.dismiss();
                point = -1;
                break;
        }
    }

    /**
     * 接收消息时处理
     *
     * @param mes 接收到的消息
     */
    @Override
    public void remessage(String mes) {
        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.SESSIONLIST)) {
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                JSONArray contacts = content.getJSONArray("sessionse");
                AppConfig.mContacts.clear();
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);
                    if (c.optInt("euid", -1) == -1) {
                        continue;
                    }
                    Contact contact = new Contact();
                    contact.id = c.optInt("id");
                    contact.eid = c.optInt("euid");
                    contact.type = 1;
                    contact.name = c.optString("ename");
                    contact.message = c.optString("fmsg");
                    contact.mesnew = i % 2 == 1 ? true : false;
                    contact.picture = c.optString("epicture");
                    contact.time = new Date(c.optLong("time"));
                    AppConfig.mContacts.add(contact);
                }
                //initData();

                mymeslvAdapter.notifyDataSetChanged();
                mSwipeRefresh.setRefreshing(false);
            }


            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.REV)) {
                sendgetdata();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * listview点击事件
     *
     * @param i 被点击的item
     */
    @Override
    public void onClick(int i) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        Bundle bundle = new Bundle();
        //bundle.putInt("id",mContacts.get(i).id);
        bundle.putInt("i", i);
//        bundle.putInt("type",AppConfig.mContacts.get(i).type);
//        bundle.putString("name",AppConfig.mContacts.get(i).name);
//        bundle.putString("picture",AppConfig.mContacts.get(i).picture);
        intent.putExtra("contacts", bundle);
        startActivity(intent);
        AppConfig.mContacts.get(i).mesnew = false;
        mymeslvAdapter.notifyDataSetChanged();
    }

    /**
     * listview长按删除事件
     *
     * @param i 被长按的item
     * @return
     */
    @Override
    public boolean onLongClick(int i) {

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        //震动50毫秒
        vibrator.vibrate(50);
        point = i;
        dialog.show();
        return false;
    }


    @Override
    public void onDestroy() {
        MyWebSocket.webSocket.removeTextMessageListener(this);
        super.onDestroy();
    }


}
