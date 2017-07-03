package com.linzhou.schentunion.websocekt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.linzhou.schentunion.R;
import java.util.ArrayList;
import java.util.List;
/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.application
 *创建者:  linzhou
 *创建时间:17/04/20
 *描述:   WebSocket测试类
 */
public class WebsocketActivity extends AppCompatActivity implements MyWebSocket.TextMessageListener{

    private ListView mLsListView;
    private EditText mEtEditText;
    private Button mBtButton;

    private aptedater aptedate;

    private List<String> date =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websocket);
        mLsListView = (ListView) findViewById(R.id.ls);
        mEtEditText = (EditText) findViewById(R.id.et);
        mBtButton = (Button) findViewById(R.id.bt);

        MyWebSocket.webSocket.openWebsocket();
        MyWebSocket.webSocket.addTextMessageListeners(this);
        aptedate= new aptedater();
        mLsListView.setAdapter(aptedate);

        mBtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mEtEditText.getText().toString();
                date.add(s);
                notifyDataSetChanged();
                mEtEditText.setText("");
                MyWebSocket.webSocket.sendMessage(s);
            }
        });
    }

    @Override
    public void remessage(String mes) {
        date.add(mes);
        notifyDataSetChanged();

    }
    public void notifyDataSetChanged(){
        aptedate.notifyDataSetChanged();
        mLsListView.setSelection(mLsListView.getBottom());
    }


    @Override
    protected void onDestroy() {
        MyWebSocket.webSocket.removeTextMessageListener(this);
        super.onDestroy();
    }

    public class aptedater extends BaseAdapter{

        @Override
        public int getCount() {
            return date.size();
        }

        @Override
        public Object getItem(int position) {
            return date.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tx ;
            if(convertView==null){
                convertView = LayoutInflater.from(WebsocketActivity.this).inflate(R.layout.list_item,null);
                tx = (TextView) convertView.findViewById(R.id.tx);

            }else
                tx = (TextView) convertView.getTag();
            tx.setText(date.get(position));
            convertView.setTag(tx);
            return convertView;
        }

    }

}
