package com.linzhou.schentunion.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.linzhou.schentunion.R;
import com.linzhou.schentunion.activity.DelivertActivity;
import com.linzhou.schentunion.activity.LoginActivity;
import com.linzhou.schentunion.activity.ResumeActivity;
import com.linzhou.schentunion.activity.UpdateStudentActivity;
import com.linzhou.schentunion.base.AppConfig;
import com.linzhou.schentunion.base.BaseFragment;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.utils.PicassoUtils;
import com.linzhou.schentunion.utils.ShareUtils;
import com.linzhou.schentunion.view.CustomDialog;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.fragment
 *创建者:  linzhou
 *创建时间:17/04/21
 *描述:   个人中心
 */


public class MyFragment extends BaseFragment implements View.OnClickListener
        , MyWebSocket.TextMessageListener {
    private Button btn_exit_user;
    private TextView edit_user;

    private EditText et_username;
    private EditText et_sex;
    private EditText et_age;

    //圆形头像
    private CircleImageView profile_image;

    //电话
    private EditText et_tel;

    //查看简历
    private TextView tv_resume;

    //快递查询
    private TextView deliver;

    private CustomDialog mDialog;
    private Button cancel;
    private Button ok;

    @Override
    public int getlayoutId() {

        return R.layout.my_fragment;
    }

    @Override
    protected void initView(View view) {
        JSONObject jo = new JSONObject();
        try {
            jo.put(StaticClass.TYPE, StaticClass.DETAIL);
            jo.put(StaticClass.CONTENT, new JSONObject());
            MyWebSocket.webSocket.sendMessage(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        L.d("MyFragment:oncreate");

        btn_exit_user = (Button) view.findViewById(R.id.btn_exit_user);
        edit_user = (TextView) view.findViewById(R.id.edit_user);
        deliver = (TextView) view.findViewById(R.id.deliver);
        tv_resume = (TextView) view.findViewById(R.id.tv_resume);
        et_tel = (EditText) view.findViewById(R.id.et_tel);

        et_username = (EditText) view.findViewById(R.id.et_username);
        et_sex = (EditText) view.findViewById(R.id.et_sex);
        et_age = (EditText) view.findViewById(R.id.et_age);

        profile_image = (CircleImageView) view.findViewById(R.id.profile_image);

        mDialog = new CustomDialog(getActivity(), 0, 0, R.layout.exit_dialog
                , R.style.Theme_dialog, Gravity.CENTER, R.style.pop_anim_style2);
        cancel = (Button) mDialog.findViewById(R.id.cancel);
        ok = (Button) mDialog.findViewById(R.id.ok);


        //默认是不可点击的
        setEnabled(false);
    }

    //控制焦点
    private void setEnabled(boolean is) {
        et_username.setEnabled(is);
        et_sex.setEnabled(is);
        et_age.setEnabled(is);
        et_tel.setEnabled(is);
    }

    @Override
    protected void initData() {

        PicassoUtils.loadImageViewSize(getActivity(), StaticClass.HTTPIMAGE + AppConfig.student.prcture, 96, 96, profile_image);
        et_username.setText(AppConfig.student.username);
        et_sex.setText(AppConfig.student.sex == 0 ? "男" : "女");
        et_age.setText(AppConfig.student.age + "");
        et_tel.setText(AppConfig.student.tel);

    }

    @Override
    protected void setListener() {
        btn_exit_user.setOnClickListener(this);
        edit_user.setOnClickListener(this);
        deliver.setOnClickListener(this);

        tv_resume.setOnClickListener(this);
        MyWebSocket.webSocket.addTextMessageListeners(this);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicassoUtils.loadImageViewSize(getActivity(), StaticClass.HTTPIMAGE + AppConfig.student.prcture, 96, 96, profile_image);
                L.d("profile_image");
            }
        });

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //退出登录
            case R.id.btn_exit_user:
                mDialog.show();
                break;
            //编辑资料
            case R.id.edit_user:
                Intent intent = new Intent(getActivity(), UpdateStudentActivity.class);
                startActivity(intent);
                break;

            case R.id.deliver:
                Intent mintent = new Intent(getActivity(), DelivertActivity.class);
                startActivity(mintent);
                break;
            case R.id.tv_resume:
                startActivity(new Intent(getActivity(), ResumeActivity.class));
                break;
            case R.id.cancel:
                mDialog.dismiss();
                break;
            case R.id.ok:
                Intent i = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(i);
                MyWebSocket.webSocket.update(getActivity().getApplicationContext());
                getActivity().finish();
                mDialog.dismiss();
                break;


        }
    }



    @Override
    public void remessage(String mes) {
        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.DETAIL)) {
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                JSONObject student = content.optJSONObject("student");
                //L.d("student : "+student.toString());
                AppConfig.student.id = student.optInt("sid");
                AppConfig.student.tel = student.optString("tel");
                AppConfig.student.username = student.optString("username");
                AppConfig.student.profession = student.optString("pname");
                AppConfig.student.college = student.optString("cname");
                AppConfig.student.gdata = new Date(student.optLong("gdate"));
                AppConfig.student.introduce = student.optString("introduce");
                AppConfig.student.prcture = student.optString("picture");
                ShareUtils.putString(getActivity(), "picture", AppConfig.student.prcture);
                AppConfig.student.sex = student.optInt("sex");
                AppConfig.student.nummber = student.optString("number");
                AppConfig.student.idcard = student.optString("idcard");
                AppConfig.student.collegeid = student.optInt("cid");
                AppConfig.student.age = student.optInt("age");
                AppConfig.student.email = student.optString("email");
                L.d(AppConfig.student.username);
                initData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyWebSocket.webSocket.removeTextMessageListener(this);
    }
}
