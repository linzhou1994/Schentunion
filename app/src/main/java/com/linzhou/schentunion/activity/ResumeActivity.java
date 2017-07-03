package com.linzhou.schentunion.activity;
/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.fragment
 *创建者:  linzhou
 *创建时间:17/04/23
 *描述:   简历页
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.AppConfig;
import com.linzhou.schentunion.base.BaseActivity;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Resume;
import com.linzhou.schentunion.emoji.KeyboardUtils;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.utils.PicassoUtils;
import com.linzhou.schentunion.websocekt.MyWebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResumeActivity extends BaseActivity implements View.OnClickListener{
    private Button btn_update;
    private Button btn_update_ok;
    private Button btn_update_cancel;

    private RadioGroup mRadioGroup;
    private RadioButton rb_boy;
    private RadioButton rb_girl;

    private EditText et_username;
    private EditText et_sex;
    private EditText et_tel;
    private EditText et_email;
    private EditText et_idcard;
    private EditText et_introduce;

    private CircleImageView profile_image;

    Resume resume = new Resume();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        KeyboardUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
//        KeyboardUtils.hideKeyboard(getCurrentFocus());
    }

    @Override
    protected int getlayout() {
        return R.layout.activity_resume;
    }

    @Override
    protected void initview() {
        JSONObject jo = new JSONObject();
        try {
            jo.put(StaticClass.TYPE, StaticClass.GETRESUME);
            jo.put(StaticClass.CONTENT,new JSONObject());
            MyWebSocket.webSocket.sendMessage(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        profile_image= (CircleImageView) findViewById(R.id.profile_image);

        btn_update = (Button) findViewById(R.id.btn_update);
        btn_update_ok = (Button) findViewById(R.id.btn_update_ok);
        btn_update_cancel = (Button) findViewById(R.id.btn_update_cancel);
        et_username = (EditText) findViewById(R.id.et_username);
        et_sex = (EditText) findViewById(R.id.et_sex);
        et_tel = (EditText) findViewById(R.id.et_tel);
        et_email = (EditText) findViewById(R.id.et_email);
        et_idcard = (EditText) findViewById(R.id.et_idcard);
        et_introduce = (EditText) findViewById(R.id.et_introduce);

        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);
        rb_boy= (RadioButton) findViewById(R.id.rb_boy);
        rb_girl= (RadioButton) findViewById(R.id.rb_girl);

        setEnabled(false);
        setvisibility(true);

    }

    //控制焦点
    private void setEnabled(boolean is) {
        et_username.setEnabled(is);
        et_sex.setEnabled(is);
        et_tel.setEnabled(is);
        et_email.setEnabled(is);
        et_idcard.setEnabled(is);
        et_introduce.setEnabled(is);

    }

    @Override
    protected void initData() {
        PicassoUtils.loadImageViewSize(this,StaticClass.HTTPIMAGE+ AppConfig.student.prcture,96,96,profile_image);


    }

    @Override
    protected void setListener() {
        super.setListener();
        btn_update.setOnClickListener(this);
        btn_update_ok.setOnClickListener(this);
        btn_update_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                setvisibility(false);
                setEnabled(true);
                break;
            case R.id.btn_update_ok:
                update();
                setvisibility(true);
                setEnabled(false);
                break;
            case R.id.btn_update_cancel:
                setvisibility(true);
                setEnabled(false);
                setData();
                break;
        }

    }

    private int sex;

    private void update() {
        JSONObject jo = new JSONObject();
        try {
            jo.put(StaticClass.TYPE, StaticClass.UPDATERESUME);
            JSONObject content = new JSONObject();
            content.put("name", et_username.getText().toString().trim());
            content.put("tel", et_tel.getText().toString().trim());
            if (rb_boy.isChecked())
                sex=0;
            else sex=1;
            L.d(sex+" ");
            content.put("sex", sex);
            content.put("email", et_email.getText().toString().trim());
            content.put("idcard", et_idcard.getText().toString().trim());
            content.put("introduce", et_introduce.getText().toString());
            content.put("status", resume.status);
            content.put("rid", resume.id);
            jo.put(StaticClass.CONTENT, content);
            MyWebSocket.webSocket.sendMessage(jo.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        resume.name = et_username.getText().toString().trim();
        resume.tel = et_tel.getText().toString().trim();
        resume.sex = sex;
        resume.email = et_email.getText().toString().trim();
        resume.idcard = et_idcard.getText().toString().trim();
        resume.introduce = et_introduce.getText().toString();
        setData();

    }

    public void setvisibility(boolean is) {
        if (is) {
            btn_update.setVisibility(View.VISIBLE);
            btn_update_ok.setVisibility(View.GONE);
            btn_update_cancel.setVisibility(View.GONE);
            mRadioGroup.setVisibility(View.GONE);
            et_sex.setVisibility(View.VISIBLE);
        } else {
            btn_update.setVisibility(View.GONE);
            btn_update_ok.setVisibility(View.VISIBLE);
            btn_update_cancel.setVisibility(View.VISIBLE);
            mRadioGroup.setVisibility(View.VISIBLE);
            et_sex.setVisibility(View.GONE);


        }

    }


    @Override
    public void remessage(String mes) {
        super.remessage(mes);
        //L.d(mes);
        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.GETRESUME)) {
                JSONObject content = jo.getJSONObject(StaticClass.CONTENT);
                L.d(content.toString());

                resume.name = content.optString("name");
                resume.id = content.optInt("rid");
                resume.sex = content.getInt("sex");
                resume.tel = content.getString("tel");
                resume.status = content.getInt("status");
                resume.email = content.getString("email");
                resume.idcard = content.getString("idcard");
                resume.introduce = content.getString("introduce");
                L.d(resume.name + " " + resume.introduce);
                setData();

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setData() {
        et_username.setText(resume.name);
        et_sex.setText(resume.sex == 1 ? "女" : "男");
        et_tel.setText(resume.tel);
        et_email.setText(resume.email);
        et_idcard.setText(resume.idcard);
        et_introduce.setText(resume.introduce);
        if (resume.sex == 1){
            rb_girl.setChecked(true);
        }else rb_boy.setChecked(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
