package com.linzhou.schentunion.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.service.Myservice;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.utils.PicassoUtils;
import com.linzhou.schentunion.utils.ShareUtils;
import com.linzhou.schentunion.view.CustomDialog;
import com.linzhou.schentunion.websocekt.MyWebSocket;
import org.json.JSONException;
import org.json.JSONObject;
import de.hdodenhof.circleimageview.CircleImageView;

/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.activity
 *创建者:  linzhou
 *创建时间:17/04/22
 *描述:   登录
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,MyWebSocket.TextMessageListener{
    private CircleImageView profile_image;
    private EditText etname,etpassword;
    private Button btlogin,btregistered;
    private CheckBox keep_password;
    private TextView tvforget;
    private CustomDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initview();
        setListener();
        initData();
    }

    private void initview(){
        String picture =ShareUtils.getString(this, "picture", "");
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        L.d(picture+"-------------------------");
        if (!TextUtils.isEmpty(picture))
            PicassoUtils.loadImageViewSize(this
                    ,StaticClass.HTTPIMAGE+picture,96,96,profile_image);
        etname = (EditText) findViewById(R.id.et_name);
        etpassword = (EditText) findViewById(R.id.et_password);
        btlogin = (Button) findViewById(R.id.btnLogin);
        btregistered = (Button) findViewById(R.id.btn_registered);
        keep_password = (CheckBox) findViewById(R.id.keep_password);
        tvforget = (TextView) findViewById(R.id.tv_forget);
        mDialog=new CustomDialog(this, 0, 0, R.layout.dialog_loding,
                R.style.Theme_dialog,Gravity.CENTER,R.style.pop_anim_style);
        mDialog.setCancelable(false);

        keep_password.setChecked(ShareUtils.getBoolean(LoginActivity.this, "keeppass", false));
    }
    private void initData(){
        etname.setText(ShareUtils.getString(this, "name", ""));
        etname.setSelection(etname.getText().toString().length());
        if (ShareUtils.getBoolean(LoginActivity.this, "keeppass", false)){
            etpassword .setText(ShareUtils.getString(this, "password", ""));
        }


    }

    private void setListener(){
        btlogin.setOnClickListener(this);
        btregistered.setOnClickListener(this);
        tvforget.setOnClickListener(this);
        MyWebSocket.webSocket.addTextMessageListeners(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:
                login();
                break;
            case R.id.btn_registered:
                registered();
                break;
            case R.id.tv_forget:
                forget();
                break;
        }
    }
    private void login() {
        String name = etname.getText().toString();
        String password = etpassword.getText().toString();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
            JSONObject json = new JSONObject();
            try {
                json.put(StaticClass.TYPE, StaticClass.LOGIN);
                JSONObject content = new JSONObject();
                content.put("tel", name);
                content.put("password", password);
                json.put(StaticClass.CONTENT, content);
                mDialog.show();
                MyWebSocket.webSocket.sendMessage(json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else Toast.makeText(this,"账号或密码不能为空！",Toast.LENGTH_SHORT).show();

    }

    private void forget() {

    }

    private void registered() {
        startActivity(new Intent(LoginActivity.this,RegisteredAvtivity.class));
    }


    @Override
    public void remessage(String mes) {
        //L.d(mes);
        try {

            JSONObject jo = new JSONObject(mes);
            if(jo.optString(StaticClass.RETYPE).equals(StaticClass.LOGIN)){
                JSONObject content= jo.optJSONObject(StaticClass.CONTENT);
                L.d(content.toString());
                int result = content.optInt("result");
                mDialog.dismiss();
                switch (result){
                    case 0:
                        Toast.makeText(this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(this,"登录成功！",Toast.LENGTH_SHORT).show();
                        //保存状态
                        ShareUtils.putBoolean(this, "keeppass", keep_password.isChecked());


                        //记住用户名和密码
                        ShareUtils.putString(this, "name", etname.getText().toString().trim());
                        ShareUtils.putString(this, "password",etpassword.getText().toString().trim());
                        if (ShareUtils.getBoolean(LoginActivity.this,"mes_no",true))
                            startService(new Intent(LoginActivity.this, Myservice.class));
                        Intent i = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);

                        finish();
                        break;
                }
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

    //防止按返回键返回上一个页面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)

    {

        if (keyCode == KeyEvent.KEYCODE_BACK )
        return false;
        else return super.onKeyDown(keyCode,event);


    }
}
