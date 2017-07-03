package com.linzhou.schentunion.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.ProgressListener;
import com.kymjs.rxvolley.http.VolleyError;
import com.kymjs.rxvolley.toolbox.FileUtils;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.BaseActivity;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.service.Myservice;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.utils.ShareUtils;
import com.linzhou.schentunion.utils.UtilTools;
import com.linzhou.schentunion.view.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.activity
 *创建者:  linzhou
 *创建时间:17/04/21
 *描述:   设置页
 */


public class SettingActivity extends BaseActivity implements View.OnClickListener {

    //正在下载
    public static final int HANDLER_LODING = 10001;
    //下载完成
    public static final int HANDLER_OK = 10002;
    //下载失败
    public static final int HANDLER_ON = 10003;

    private Switch mes_no;
    private Switch login;

    private TextView tv_version;
    private TextView tv_new;

    private LinearLayout ll_update;

    private String versionName;
    private int versionCode;
    private String ct;

    private CustomDialog mUpdateDialog;
    private Button cancel;
    private Button update;
    private TextView content;
    private NumberProgressBar number_progress_bar;

    private String url;
    private String path;

    private boolean isupdate = false;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_LODING:
                    //实时更新进度
                    Bundle bundle = msg.getData();
                    long transferredBytes = bundle.getLong("transferredBytes");
                    long totalSize = bundle.getLong("totalSize");
                    number_progress_bar.setProgress((int) (((float) transferredBytes / (float) totalSize) * 100));
                    break;
                case HANDLER_OK:

                    //启动这个应用安装
                    L.d("download:success------------------------");
                    startInstallApk();
                    break;
                case HANDLER_ON:

                    cancel.setEnabled(true);
                    update.setEnabled(true);
                    break;
            }
        }
    };

    @Override
    protected int getlayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initview() {
        mes_no = (Switch) findViewById(R.id.mes_no);
        login = (Switch) findViewById(R.id.login);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_new = (TextView) findViewById(R.id.tv_new);
        ll_update = (LinearLayout) findViewById(R.id.ll_update);
        mUpdateDialog = new CustomDialog(this, 0, 0, R.layout.dialog_update
                , R.style.Theme_dialog, Gravity.CENTER, R.style.pop_anim_style2);
        //提示框以外点击无效
        mUpdateDialog.setCancelable(false);

        cancel = (Button) mUpdateDialog.findViewById(R.id.cancel);
        update = (Button) mUpdateDialog.findViewById(R.id.update);
        content = (TextView) mUpdateDialog.findViewById(R.id.content);
        number_progress_bar = (NumberProgressBar) mUpdateDialog.findViewById(R.id.number_progress_bar);


        RxVolley.get(StaticClass.CHECK_UPDATE_URL, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                L.d("t:---------" + t);
                parsingJson(t);
            }
        });
    }

    private void parsingJson(String t) {
        try {
            JSONObject jsonObject = new JSONObject(t);
            int code = jsonObject.optInt("code");
            getVersionNameCode();

            L.d("-------------code:" + code + "  versionCode:" + versionCode);
            if (code > versionCode) {
                isupdate = true;
                //mUpdateDialog.show();
                tv_new.setVisibility(View.VISIBLE);
                ct = (jsonObject.optString("content"));
                url = jsonObject.getString("url");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        mes_no.setChecked(ShareUtils.getBoolean(this, "mes_no", true));
        login.setChecked(ShareUtils.getBoolean(this, "login", true));
        try {
            getVersionNameCode();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tv_version.setText("当前版本为：" + versionName);
        L.d("versionCode:" + versionCode);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mes_no.setOnClickListener(this);
        login.setOnClickListener(this);
        ll_update.setOnClickListener(this);
        cancel.setOnClickListener(this);
        update.setOnClickListener(this);
    }

    @Override
    public void remessage(String mes) {
        super.remessage(mes);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                //切换相反
                login.setSelected(!login.isSelected());
                //保存状态
                ShareUtils.putBoolean(this, "login", login.isChecked());
                break;
            case R.id.mes_no:
                //切换相反
                mes_no.setSelected(!mes_no.isSelected());
                //保存状态
                ShareUtils.putBoolean(this, "mes_no", mes_no.isChecked());
                if (mes_no.isChecked()) {
                    startService(new Intent(this, Myservice.class));
                } else {
                    stopService(new Intent(this, Myservice.class));
                }
                break;
            case R.id.ll_update:
                if (isupdate) {
                    mUpdateDialog.show();
                    content.setText(ct);
                } else Toast.makeText(this, "已经是最新版本啦！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cancel:
                mUpdateDialog.dismiss();
                break;
            case R.id.update:
                download(StaticClass.HTTPIMAGE + url);
                cancel.setEnabled(false);
                update.setEnabled(false);
                break;
        }
    }

    private void download(String url) {
        path = FileUtils.getSDCardPath() + "/" + System.currentTimeMillis() + ".apk";

        //下载

        if (!TextUtils.isEmpty(url)) {
            //下载
            RxVolley.download(path, url, new ProgressListener() {

                @Override
                public void onProgress(long transferredBytes, long totalSize) {

                    Message msg = new Message();
                    msg.what = HANDLER_LODING;
                    Bundle bundle = new Bundle();
                    bundle.putLong("transferredBytes", transferredBytes);
                    bundle.putLong("totalSize", totalSize);
                    L.d(transferredBytes + "/" + totalSize);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }, new HttpCallback() {

                @Override
                public void onSuccess(String t) {
                    L.d("onSuccess----------------");
                    handler.sendEmptyMessage(HANDLER_OK);
                }

                @Override
                public void onFailure(VolleyError error) {
                    L.d(error.toString());
                    handler.sendEmptyMessage(HANDLER_ON);
                }
            });
        }
    }

    //启动安装
    private void startInstallApk() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        startActivity(i);
        finish();
    }

    //获取版本号/Code
    private void getVersionNameCode() throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();
        PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
        versionName = info.versionName;
        versionCode = info.versionCode;
    }
}
