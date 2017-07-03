package com.linzhou.schentunion.activity;
/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.fragment
 *创建者:  linzhou
 *创建时间:17/05/14
 *描述:   
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.linzhou.schentunion.Adapter.EtplistAdapter;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Enterprise;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EtpSearchAcitvity extends AppCompatActivity implements MyWebSocket.TextMessageListener,View.OnClickListener{

    private ImageView back;
    private EditText et_search;
    private TextView search;
    private TextView search_etp_null;
    private ListView search_etp;

    private EtplistAdapter mAdapter;

    private List<Enterprise> mData=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_etpsearch);
        initview();
        setListener();
    }

    private void initview() {
        back = (ImageView) findViewById(R.id.back);
        et_search = (EditText) findViewById(R.id.et_search);
        search = (TextView) findViewById(R.id.search);
        search_etp_null = (TextView) findViewById(R.id.search_etp_null);
        search_etp = (ListView) findViewById(R.id.search_etp);

        mAdapter = new EtplistAdapter(this,mData);
        search_etp.setAdapter(mAdapter);
    }

    private void setListener() {
        MyWebSocket.webSocket.addTextMessageListeners(this);
        back.setOnClickListener(this);
        search.setOnClickListener(this);
        mAdapter.addEtplistListener(new EtplistAdapter.EtplistListener() {
            @Override
            public void itemOnClick(int position) {
                Intent intent = new Intent(EtpSearchAcitvity.this, EtpDeliverActivity.class);
                intent.putExtra("eid",mData.get(position).id);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.search:
                String str = et_search.getText().toString();
                if (TextUtils.isEmpty(str))
                    return;
                try {
                    JSONObject jo = new JSONObject();
                    jo.put(StaticClass.TYPE, StaticClass.SEARCHETP);
                    JSONObject content = new JSONObject();
                    content.put("searchstr",str);
                    jo.put(StaticClass.CONTENT, content);
                    MyWebSocket.webSocket.sendMessage(jo.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void remessage(String mes) {
        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.SEARCHETP)){
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                if (content==null) {
                    search_etp_null.setVisibility(View.VISIBLE);
                    search_etp_null.setText("搜索异常");
                    return;
                }
                JSONArray etps = content.getJSONArray("enterprises");
                if (etps==null) {
                    search_etp_null.setVisibility(View.VISIBLE);
                    search_etp_null.setText("搜索异常");
                    return;
                }
                mData.clear();
                if (etps.length()==0){
                    search_etp_null.setVisibility(View.VISIBLE);
                    search_etp.setVisibility(View.GONE);
                    search_etp_null.setText("无搜索结果");
                }
                else {
                    search_etp_null.setVisibility(View.GONE);
                    search_etp.setVisibility(View.VISIBLE);
                    for (int i = 0; i < etps.length(); i++) {
                        JSONObject etp = etps.getJSONObject(i);
                        Enterprise et = new Enterprise();
                        et.id = etp.optInt("eid");
                        et.address = etp.optString("address");
                        et.picture = etp.optString("epicture");
                        et.type = etp.getString("etype");
                        et.name = etp.optString("ename");
                        et.poname = etp.optString("fpo");
                        et.posize = etp.optInt("size");
                        mData.add(et);
                    }
                }
                mAdapter.notifyDataSetChanged();
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
