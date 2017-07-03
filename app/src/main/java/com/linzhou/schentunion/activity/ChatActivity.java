package com.linzhou.schentunion.activity;

/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.fragment
 *创建者:  linzhou
 *创建时间:17/04/25
 *描述:   聊天界面
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cooloongwu.emoji.entity.Emoji;
import com.cooloongwu.emoji.utils.EmojiTextUtils;
import com.linzhou.schentunion.Adapter.ChatAdapter;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.AppConfig;
import com.linzhou.schentunion.base.BaseActivity;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Chat;
import com.linzhou.schentunion.data.Contact;
import com.linzhou.schentunion.emoji.EmojiFragment;
import com.linzhou.schentunion.emoji.KeyboardUtils;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends BaseActivity implements View.OnClickListener
        , EmojiFragment.OnEmojiClickListener {

    public static final int MESSAGELENGTH = 300;
    private boolean isemoji = false;

    private EditText et_text;
    private ImageButton imgbtn_send;
    private LinearLayout ll;
    private ImageView emoji;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    //private int chatid;//聊天对象id
    private Contact contact = new Contact();



    private ArrayList<Chat> mChat = new ArrayList<>();

    private EmojiFragment ef = new EmojiFragment();

    @Override
    protected int getlayout() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initview() {

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("contacts");
//        String title = bundle.getString("name", "null");
        contact = AppConfig.mContacts.get(bundle.getInt("i"));
//        picture=bundle.getString("picture");
        AppConfig.ONWCHAT=true;
        AppConfig.ONWCHATID=contact.eid;
        getSupportActionBar().setTitle(contact.name);
        sengetdata();
        et_text = (EditText) findViewById(R.id.et_text);
        et_text.requestFocus();
        //让键盘消失
        KeyboardUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        KeyboardUtils.hideKeyboard(getCurrentFocus());


        imgbtn_send = (ImageButton) findViewById(R.id.imgbtn_send);
        imgbtn_send.setEnabled(false);
        imgbtn_send.setImageResource(R.mipmap.conversation_btn_messages_send_disable);

        ll = (LinearLayout) findViewById(R.id.ll);
        emoji = (ImageView) findViewById(R.id.emoji);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter=new ChatAdapter(ChatActivity.this,mChat);
        recyclerView.setAdapter(chatAdapter);
    }

    private void sengetdata(){
        JSONObject jo = new JSONObject();
        try {
            jo.put(StaticClass.TYPE,StaticClass.MSG);
            JSONObject content = new JSONObject();
            content.put("euid",contact.eid);
            jo.put(StaticClass.CONTENT,content);
            MyWebSocket.webSocket.sendMessage(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void initData() {
//        for (int i=0;i<50;i++){
//            Chat chat = new Chat();
//            chat.id=i;
//            chat.chatid=contact.eid;
//            chat.picture=i%2==0?contact.picture:AppConfig.student.prcture;
//            chat.message="message:"+i;
//            chat.type=i%2;
//            chat.time=new Date();
//            mChat.add(chat);
//        }
//        chatAdapter.notifyDataSetChanged();
//        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }

    @Override
    protected void setListener() {
        super.setListener();
        et_text.addTextChangedListener(new TextWatcher() {

            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                selectionStart = et_text.getSelectionStart();
                selectionEnd = et_text.getSelectionEnd();
                if (temp.length() == 0) {
                    imgbtn_send.setEnabled(false);
                    imgbtn_send.setImageResource(R.mipmap.conversation_btn_messages_send_disable);

                } else {
                    imgbtn_send.setEnabled(true);
                    imgbtn_send.setImageResource(R.mipmap.conversation_btn_messages_send);

                    if (temp.length() >= MESSAGELENGTH) {
                        Toast.makeText(ChatActivity.this, "内容长度超出限制", Toast.LENGTH_SHORT).show();
                        editable.delete(selectionStart - 1, selectionEnd);
                        int tempSelection = selectionEnd;
                        et_text.setText(editable);
                        et_text.setSelection(tempSelection);
                    }
                }

            }
        });
        imgbtn_send.setOnClickListener(this);
        emoji.setOnClickListener(this);
        et_text.setOnClickListener(this);
        ef.addOnEmojiClickListener(this);
    }

    @Override
    public void remessage(String mes) {
        super.remessage(mes);

        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.REV)){
                JSONObject content1 = jo.optJSONObject(StaticClass.CONTENT);
                if (contact.eid != content1.optInt("euid")) {
                    return;
                }
                Chat chat =new Chat();
                chat.picture= contact.picture;
                chat.chatid = content1.optInt("euid");
                chat.time = new Date(content1.optLong("time"));
                chat.message=content1.optString(StaticClass.CONTENT);
                chat.type=0;
                mChat.add(chat);
                chatAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }

            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.MSG)){
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                JSONArray ses = content.optJSONArray("ses");
                for (int i = 0; i<ses.length();i++){
                    JSONObject msg = ses.getJSONObject(i);
                    Chat chat =new Chat();
                    chat.chatid = msg.optInt("euid");
                    chat.time = new Date(msg.optLong("time"));
                    chat.message=msg.optString(StaticClass.CONTENT);
                    chat.type=msg.optBoolean("msgtype")?1:0;
                    chat.picture= chat.type==0?contact.picture:AppConfig.student.prcture;
                    mChat.add(chat);
                }
                chatAdapter.notifyDataSetChanged();
                if (mChat.size()>0)
                    recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bt:
                break;
            case R.id.emoji:
                if (!isemoji) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.ll, ef);
                    fragmentTransaction.commit();
                    showMultiLayout();
                } else {
                    hideMultiLayout();

                }
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount()==0? 0 : chatAdapter.getItemCount()- 1);
                break;
            case R.id.et_text:
                hideMultiLayout();
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount()==0? 0 : chatAdapter.getItemCount()- 1);

                break;
            case R.id.imgbtn_send:
                sendMessage();
                break;

        }

    }

    private void sendMessage(){
        Chat chat =new Chat();
        chat.id=mChat.size();
        chat.picture= AppConfig.student.prcture;
        chat.chatid=contact.eid;
        chat.message=et_text.getText().toString();
        chat.time=new Date();
        chat.type=1;
        mChat.add(chat);
        JSONObject jo=new JSONObject();
        try {
            jo.put(StaticClass.TYPE,StaticClass.CHAT);
            JSONObject content = new JSONObject();
            content.put("euid",contact.eid);
            content.put("content",chat.message);
            jo.put(StaticClass.CONTENT,content);
            MyWebSocket.webSocket.sendMessage(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        chatAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount()==0? 0 : chatAdapter.getItemCount()- 1);

        et_text.setText("");
        imgbtn_send.setEnabled(false);
    }

    private void showMultiLayout() {
        isemoji = true;
        //显示多功能布局，隐藏键盘
        KeyboardUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        KeyboardUtils.hideKeyboard(getCurrentFocus());
        ll.setVisibility(View.VISIBLE);
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount()==0? 0 : chatAdapter.getItemCount()- 1);

    }

    /**
     * 隐藏多功能布局
     */
    private void hideMultiLayout() {
        L.d("hideMultiLayout");
        isemoji = false;
        ll.setVisibility(View.GONE);
        KeyboardUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onEmojiDelete() {
        String str = et_text.getText().toString();
        int selection = et_text.getSelectionStart();
        if (str.isEmpty()||selection==0) {
            return;
        }


        String text1 = str.substring(0, selection);
        String text2 = str.substring(selection, str.length());
        if (!text1.substring(selection - 1).equals("]")) {
            text1 = text1.substring(0, selection - 1);
            text1 = text1 + text2;
            et_text.setText(EmojiTextUtils.getEditTextContent(text1, this, et_text));
            et_text.setSelection(selection - 1);
        } else {
            L.d("text1:" + text1 + "  text2:" + text2 + " selection:" + selection);
            int index = text1.lastIndexOf("[");
            L.d("index:" + index);

                text1 = text1.substring(0, index);
                text1 = text1 + text2;


            et_text.setText(EmojiTextUtils.getEditTextContent(text1, this, et_text));
            et_text.setSelection(index);
        }
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        L.d("onEmojiClick");
        int selection = et_text.getSelectionStart();
        String str = et_text.getText().toString();
        String text = str.substring(0, selection) + emoji.getContent()
                + str.substring(selection, str.length());
        et_text.setText(EmojiTextUtils.getEditTextContent(text, this, et_text));
        et_text.setSelection(selection + emoji.getContent().length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConfig.ONWCHAT=false;
        AppConfig.ONWCHATID=-1;
    }
}