package com.linzhou.schentunion.activity;

/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.activity
 *创建者:  linzhou
 *创建时间:17/04/24
 *描述:   注册页
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.linzhou.schentunion.Adapter.SpinnerAdapter;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.BaseActivity;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Profession;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.view.AutoFillEmailEditText;
import com.linzhou.schentunion.view.CustomDialog;
import com.linzhou.schentunion.view.kMPAutoComplTextView.KMPAutoComplTextView;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newsmssdk.BmobSMS;
import cn.bmob.newsmssdk.exception.BmobException;
import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;
import cn.bmob.newsmssdk.listener.SMSCodeListener;
import cn.bmob.newsmssdk.listener.VerifySMSCodeListener;

public class RegisteredAvtivity extends BaseActivity implements View.OnClickListener {

    private EditText et_user;
    private EditText et_age;
    private EditText et_tel;
    private RadioGroup mRadioGroup;
    private EditText et_pass;
    private EditText et_password;
    private AutoFillEmailEditText et_email;
    private EditText et_desc;
    private Button btnRegistered;
    private KMPAutoComplTextView profession;
    private KMPAutoComplTextView school;
    private CustomDialog mDialog;
    private Button bt_sms;
    private EditText et_sms;
    private ImageView email_erroy;

    private SpinnerAdapter mSpAdapter;
    private List<Profession> mProfessions = new ArrayList<>();
    private List<School> mschools = new ArrayList<>();

    private int sex = 0;

    private static final int HAND = 10001;
    private static final int TEXT = 10002;
    private static final int REGISTERED = 10003;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HAND:
                    bt_sms.setText("重新获取");
                    bt_sms.setEnabled(true);
                    break;
                case TEXT:
                    int i = msg.getData().getInt("i");
                    if (i == 0) {
                        handler.sendEmptyMessage(HAND);
                        break;
                    }
                    bt_sms.setText(i + "s");
                    Message mes1 = new Message();
                    mes1.what = TEXT;
                    Bundle b = new Bundle();
                    b.putInt("i", --i);
                    mes1.setData(b);
                    handler.sendMessageDelayed(mes1, 1000);
                    break;
                case REGISTERED:
                    Toast.makeText(RegisteredAvtivity.this, "验证通过", Toast.LENGTH_SHORT).show();
                    registered();
                    break;

            }
        }
    };

    @Override
    protected int getlayout() {
        return R.layout.activity_registered;
    }

    @Override
    protected void initview() {
        //请求获取所专业数据
        JSONObject jo = new JSONObject();
        try {
            jo.put(StaticClass.TYPE, StaticClass.PCLIST);
            jo.put(StaticClass.CONTENT, new JSONObject());
            MyWebSocket.webSocket.sendMessage(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bt_sms = (Button) findViewById(R.id.bt_sms);
        et_sms = (EditText) findViewById(R.id.et_sms);

        et_user = (EditText) findViewById(R.id.et_user);
        et_age = (EditText) findViewById(R.id.et_age);
        et_tel = (EditText) findViewById(R.id.et_tel);
        et_pass = (EditText) findViewById(R.id.et_pass);
        et_password = (EditText) findViewById(R.id.et_password);
        et_email = (AutoFillEmailEditText) findViewById(R.id.et_email);
        et_desc = (EditText) findViewById(R.id.et_desc);
        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);
        btnRegistered = (Button) findViewById(R.id.btnRegistered);
        profession = (KMPAutoComplTextView) findViewById(R.id.profession);
        school = (KMPAutoComplTextView) findViewById(R.id.school);
        email_erroy = (ImageView) findViewById(R.id.email_erroy);

        mDialog = new CustomDialog(this, 0, 0, R.layout.dialog_registered,
                R.style.Theme_dialog, Gravity.CENTER, R.style.pop_anim_style);
        mDialog.setCancelable(false);

        BmobSMS.initialize(getApplicationContext(), StaticClass.SMSID, new MySMSCodeListener());


    }

    @Override
    protected void initData() {

    }
    private int selectid = -1;
    private int selectids = -1;
    @Override
    protected void setListener() {
        super.setListener();
        btnRegistered.setOnClickListener(this);
        bt_sms.setOnClickListener(this);

        et_email.addEmailboolean(new AutoFillEmailEditText.Emailboolean() {
            @Override
            public void emailfalse() {
                email_erroy.setVisibility(View.VISIBLE);
            }

            @Override
            public void focus() {
                email_erroy.setVisibility(View.GONE);
            }
        });
        profession.setOnPopupItemClickListener(new KMPAutoComplTextView.OnPopupItemClickListener() {
            @Override
            public void onPopupItemClick(String str) {
                for (int i = 0;i<mprofessiondata.size();i++) {
                    if (mprofessiondata.get(i).equals(str)){
                        selectid=i;
                        L.d("selectid:"+selectid);
                        break;
                    }
                }
            }
        });
        school.setOnPopupItemClickListener(new KMPAutoComplTextView.OnPopupItemClickListener() {
            @Override
            public void onPopupItemClick(String str) {
                for (int i = 0;i<mschooldata.size();i++) {
                    if (mschooldata.get(i).equals(str)){
                        selectids=i;
                        L.d("selectid:"+selectids);
                        break;
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegistered:
                if (et_email.getText().toString().matches("^[a-zA-Z0-9_]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$"))
                    //isSMS();
                    registered();
                else {
                    Toast.makeText(RegisteredAvtivity.this, "邮箱格式错误！", Toast.LENGTH_SHORT).show();
                    email_erroy.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.bt_sms:
                sendSMS();
                break;
        }
    }

    public void isSMS() {

        BmobSMS.verifySmsCode(RegisteredAvtivity.this, et_tel.getText().toString()
                , et_sms.getText().toString(), new VerifySMSCodeListener() {

                    @Override
                    public void done(BmobException ex) {
                        // TODO Auto-generated method stub
                        if (ex == null) {//短信验证码已验证成功
                            Log.i("bmob", "验证通过");
                            handler.sendEmptyMessage(REGISTERED);
                        } else {
                            Log.i("bmob", "验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                        }
                    }
                });
    }

    private void sendSMS() {
        BmobSMS.requestSMSCode(RegisteredAvtivity.this, et_tel.getText().toString(),
                StaticClass.SMSNAME, new RequestSMSCodeListener() {

                    @Override
                    public void done(Integer smsId, BmobException ex) {
                        // TODO Auto-generated method stub
                        if (ex == null) {//验证码发送成功
                            Log.i("bmob", "短信id：" + smsId);//用于查询本次短信发送详情
                            bt_sms.setEnabled(false);
                            bt_sms.setBackgroundColor(getResources().getColor(R.color.Grey));
                            Message mes = new Message();
                            mes.what = TEXT;
                            Bundle b = new Bundle();
                            b.putInt("i", 59);
                            mes.setData(b);
                            handler.sendMessageDelayed(mes, 1000);

                        }
                    }
                });

    }

    private void registered() {
        if (selectid==-1||selectids==-1){
            Toast.makeText(this,"学校或专业填写错误",Toast.LENGTH_SHORT).show();
        }

        String name = et_user.getText().toString().trim();
        String age = et_age.getText().toString().trim();
        String desc = et_desc.getText().toString().trim();
        String pass = et_pass.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String tel = et_tel.getText().toString().trim();


        if (!TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(pass)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(tel)
                && !TextUtils.isEmpty(email)) {
            if (pass.equals(password)) {
                if (TextUtils.isEmpty(desc))
                    desc = "你还没有对自己评价..........";
                mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                        if (checkedId == R.id.rb_boy) {
                            sex = 0;
                        } else if (checkedId == R.id.rb_girl) {
                            sex = 1;
                        }
                    }
                });

                try {
                    JSONObject jo = new JSONObject();
                    jo.put(StaticClass.TYPE, StaticClass.REGISTERED);
                    JSONObject content = new JSONObject();

                    content.put("name", name);
                    content.put("age", age);
                    content.put("pass", pass);
                    content.put("email", email);
                    content.put("tel", tel);
                    content.put("desc", desc);
                    content.put("sex", sex);
                    content.put("cid", mschools.get(selectids).id);
                    content.put("profession", mProfessions.get(selectid).id);

                    jo.put(StaticClass.CONTENT, content);
                    MyWebSocket.webSocket.sendMessage(jo.toString());
                    mDialog.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();

        } else Toast.makeText(this, "必填选项不能为空", Toast.LENGTH_SHORT).show();
    }

    private List<String> mprofessiondata = new ArrayList<>();
    private List<String> mschooldata = new ArrayList<>();

    @Override
    public void remessage(String mes) {
        super.remessage(mes);
        try {
            JSONObject jo = new JSONObject(mes);
            //注册结果回掉
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.REGISTERED)) {
                mDialog.dismiss();
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                int result = content.optInt("result");
                switch (result) {
                    case 0:
                        Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisteredAvtivity.this, MainActivity.class));
                        finish();
                        break;
                }
            }
            //获取所有专业数据
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.PCLIST)) {
                JSONArray professions = jo.optJSONObject(StaticClass.CONTENT).optJSONArray("professions");
                mprofessiondata.clear();
                for (int i = 0; i < professions.length(); i++) {
                    JSONObject profession = professions.getJSONObject(i);
                    Profession p = new Profession();
                    p.id = profession.optInt("pid");
                    p.name = profession.optString("pname");
                    mprofessiondata.add(profession.optString("pname"));
                    mProfessions.add(p);
                }
                profession.setDatas(mprofessiondata);

                JSONArray cname = jo.optJSONObject(StaticClass.CONTENT).optJSONArray("colleges");
                mschooldata.clear();
                for (int i = 0; i < cname.length(); i++) {
                    JSONObject c = cname.getJSONObject(i);
                    School s = new School();
                    s.id = c.optInt("cid");
                    s.name = c.optString("cname");
                    mschooldata.add(c.optString("cname"));
                    mschools.add(s);
                }
                school.setDatas(mschooldata);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class School{
        public int id;
        public String name;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    class MySMSCodeListener implements SMSCodeListener {

        @Override
        public void onReceive(String content) {
            if (et_sms != null) {
                et_sms.setText(content);
            }
        }

    }
}
