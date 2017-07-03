package com.linzhou.schentunion.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.ProgressListener;
import com.kymjs.rxvolley.http.VolleyError;
import com.kymjs.rxvolley.toolbox.FileUtils;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.application.MyApplication;
import com.linzhou.schentunion.base.AppConfig;
import com.linzhou.schentunion.base.BaseFragment;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.fragment.MessageFragment;
import com.linzhou.schentunion.fragment.MyFragment;
import com.linzhou.schentunion.fragment.RearchFragment;
import com.linzhou.schentunion.fragment.PushFragment;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.utils.ShareUtils;
import com.linzhou.schentunion.view.CustomDialog;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        MyWebSocket.TextMessageListener {

    private String versionName;
    private int versionCode;

    //正在下载
    public static final int HANDLER_LODING = 10001;
    //下载完成
    public static final int HANDLER_update_OK = 10002;
    //下载失败
    public static final int HANDLER_update_ON = 10003;
    //正在修复
    public static final int HANDLER_apatch_LODING = 10004;
    //修复成功
    public static final int HANDLER_apatch_OK = 10005;
    //修复失败
    public static final int HANDLER_apatch_ON = 10006;

    private ViewPager mViewPager;
    private LinearLayout mRearch;
    private LinearLayout mSpush;
    private LinearLayout mMessagge;
    private LinearLayout mMy;
    private ImageView mImReacrh;
    private ImageView mImSpush;
    private ImageView mImMessage;
    private ImageView mImMy;
    private FloatingActionButton mfatsetting;

    private TextView tv_reatch;
    private TextView tv_spush;
    private TextView tv_message;
    private TextView tv_my;
    private List<BaseFragment> mFragments = new ArrayList<>();

    private CustomDialog mDialog;
    private Button ok;

    private CustomDialog mUpdateDialog;
    private Button cancel;
    private Button update;
    private TextView content;
    private NumberProgressBar number_progress_bar;

    private CustomDialog mApacthDialog;
    private NumberProgressBar apatch_progress_bar;

    private String url;
    private String path;


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
                case HANDLER_update_OK:

                    //启动这个应用安装
                    L.d("download apk:success------------------------");
                    startInstallApk();
                    break;
                case HANDLER_update_ON:

                    cancel.setEnabled(true);
                    update.setEnabled(true);
                    break;
                case HANDLER_apatch_LODING:
                    //实时更新进度
                    Bundle bundle1 = msg.getData();
                    long transferredBytes1 = bundle1.getLong("transferredBytes");
                    long totalSize1 = bundle1.getLong("totalSize");
                    apatch_progress_bar.setProgress((int) (((float) transferredBytes1 / (float) totalSize1) * 100));
                    break;
                case HANDLER_apatch_OK:

                    //启动这个应用安装
                    L.d("download apatch:success------------------------");
                    try {
                        MyApplication.mPatchManager.addPatch(path);
                        ShareUtils.putString(MainActivity.this,"apatchname",ApatchName);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        mApacthDialog.dismiss();
                    }
                    break;
                case HANDLER_apatch_ON:

                    //启动这个应用安装
                    L.d("download:fail------------------------");

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListener();
        selectCheckBox(0);

    }

    private void initView() {

        try {
            JSONObject jo = new JSONObject();
            jo.put(StaticClass.TYPE, StaticClass.GETCODE);
            jo.put(StaticClass.CONTENT, new JSONObject());
            MyWebSocket.webSocket.sendMessage(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mRearch = (LinearLayout) findViewById(R.id.rearch);
        mSpush = (LinearLayout) findViewById(R.id.spush);
        mMessagge = (LinearLayout) findViewById(R.id.message);
        mMy = (LinearLayout) findViewById(R.id.my);
        mfatsetting = (FloatingActionButton) findViewById(R.id.fab_setting);
        mImMessage = (ImageView) findViewById(R.id.im_message);
        mImReacrh = (ImageView) findViewById(R.id.im_rearch);
        mImSpush = (ImageView) findViewById(R.id.im_spush);
        mImMy = (ImageView) findViewById(R.id.im_my);
        tv_reatch = (TextView) findViewById(R.id.tv_reatch);
        tv_spush = (TextView) findViewById(R.id.tv_spush);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_my = (TextView) findViewById(R.id.tv_my);
        mDialog = new CustomDialog(this, 0, 0, R.layout.logout_dialog
                , R.style.Theme_dialog, Gravity.CENTER, R.style.pop_anim_style2);
        //提示框以外点击无效
        mDialog.setCancelable(false);
        ok = (Button) mDialog.findViewById(R.id.ok);


        mUpdateDialog = new CustomDialog(this, 0, 0, R.layout.dialog_update
                , R.style.Theme_dialog, Gravity.CENTER, R.style.pop_anim_style2);
        //提示框以外点击无效
        mUpdateDialog.setCancelable(false);

        cancel = (Button) mUpdateDialog.findViewById(R.id.cancel);
        update = (Button) mUpdateDialog.findViewById(R.id.update);
        content = (TextView) mUpdateDialog.findViewById(R.id.content);
        number_progress_bar = (NumberProgressBar) mUpdateDialog.findViewById(R.id.number_progress_bar);

        mApacthDialog = new CustomDialog(this, 0, 0, R.layout.dialog_apatch
                , R.style.Theme_dialog, Gravity.CENTER, R.style.pop_anim_style2);
        //提示框以外点击无效
        mApacthDialog.setCancelable(false);

        apatch_progress_bar = (NumberProgressBar) mApacthDialog.findViewById(R.id.apatch_progress_bar);


    }

    private void setListener() {
        mSpush.setOnClickListener(this);
        mRearch.setOnClickListener(this);
        mMessagge.setOnClickListener(this);
        mMy.setOnClickListener(this);
        mfatsetting.setOnClickListener(this);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        update.setOnClickListener(this);


        MyWebSocket.webSocket.addTextMessageListeners(this);
        /**
         * viewpager的滚动监听
         */
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == mFragments.size() - 1)
                    mfatsetting.setVisibility(View.VISIBLE);
                else
                    mfatsetting.setVisibility(View.GONE);
                selectCheckBox(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initData() {
//        RxVolley.get(StaticClass.CHECK_UPDATE_URL, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                L.d("get ------------------:"+t);
//                parsingJson(t);
//            }
//        });

        new RxVolley.Builder()
                .url(StaticClass.CHECK_UPDATE_URL) //接口地址
                //请求类型，如果不加，默认为 GET 可选项：
                //POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
                .httpMethod(RxVolley.Method.GET)
                //是否缓存，默认是 get 请求 5 缓存分钟, post 请求不缓存
                .shouldCache(false)
                .callback(new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        parsingJson(t);
                        L.d("new get ------------------:"+t);
                    }
                }) //响应回调
                .encoding("UTF-8") //编码格式，默认为utf-8
                .doTask();  //执行请求操作

        mFragments.add(new RearchFragment());
        mFragments.add(new PushFragment());
        mFragments.add(new MessageFragment());
        mFragments.add(new MyFragment());
        //预加载
        mViewPager.setOffscreenPageLimit(mFragments.size());

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        });
    }

    private String ApatchName ;

    private void parsingJson(String t) {
        try {
            JSONObject jsonObject = new JSONObject(t);
            int code = jsonObject.optInt("code");
            String name = jsonObject.optString("name");

            getVersionNameCode();

            L.d("code:" + code + "  versionCode:" + versionCode);
            if (code > versionCode) {
                mUpdateDialog.show();
                content.setText(jsonObject.optString("content"));
                url = jsonObject.getString("apkurl");
            } else if (!name.equals(ShareUtils.getString(this,"apatchname",AppConfig.APATCHID))) {
                L.d("apatch-------------------:"+ShareUtils.getString(this,"apatchname",AppConfig.APATCHID));
                mApacthDialog.show();
                url = jsonObject.getString("apatchurl");
                ApatchName= name;
                download(StaticClass.HTTPIMAGE + url,APATCH);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rearch:
                mViewPager.setCurrentItem(0);
                selectCheckBox(0);
                break;
            case R.id.spush:
                mViewPager.setCurrentItem(1);
                selectCheckBox(1);
                break;
            case R.id.message:
                mViewPager.setCurrentItem(2);
                selectCheckBox(2);
                break;
            case R.id.my:
                mViewPager.setCurrentItem(3);
                selectCheckBox(3);
                break;
            case R.id.fab_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ok:
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
                mDialog.dismiss();
                break;
            case R.id.cancel:
                mUpdateDialog.dismiss();
                break;
            case R.id.update:
                download(StaticClass.HTTPIMAGE + url, APK);
                cancel.setEnabled(false);
                update.setEnabled(false);
                break;
        }
    }

    public void selectCheckBox(int i) {
        mImReacrh.setImageResource(R.mipmap.search1);
        tv_reatch.setTextColor(Color.BLACK);
        mImSpush.setImageResource(R.mipmap.spush1);
        tv_spush.setTextColor(Color.BLACK);
        mImMessage.setImageResource(R.mipmap.message1);
        tv_message.setTextColor(Color.BLACK);
        mImMy.setImageResource(R.mipmap.my1);
        tv_my.setTextColor(Color.BLACK);
        switch (i) {
            case 0:
                mImReacrh.setImageResource(R.mipmap.search22);
                tv_reatch.setTextColor(getResources().getColor(R.color.blue));
                break;
            case 1:
                mImSpush.setImageResource(R.mipmap.spush22);
                tv_spush.setTextColor(getResources().getColor(R.color.blue));
                break;
            case 2:
                mImMessage.setImageResource(R.mipmap.message22);
                tv_message.setTextColor(getResources().getColor(R.color.blue));
                break;
            case 3:
                mImMy.setImageResource(R.mipmap.my22);
                tv_my.setTextColor(getResources().getColor(R.color.blue));
                break;
        }
    }

    @Override
    public void remessage(String mes) {
        try {
            JSONObject jo = new JSONObject(mes);

            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.LOGOUT)) {
                mDialog.show();
            }
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.GETCODE)) {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static final int APK = 100001;
    private static final int APATCH = 100002;

    private void download(String url, int type) {
        switch (type) {
            case APK:
                path = FileUtils.getSDCardPath() + "/Schentunion/Apk/" + System.currentTimeMillis() + ".apk";
                break;
            case APATCH:
                path = FileUtils.getSDCardPath() + "/Schentunion/Apatch/" + System.currentTimeMillis() + ".apatch";
                break;
        }

        //下载
        //url = "http://192.168.137.128:8080/SchEntUnion/APK/APK.zip";
        if (!TextUtils.isEmpty(url)) {
            //下载
            RxVolley.download(path, url, new ProgressListener() {

                @Override
                public void onProgress(long transferredBytes, long totalSize) {
                    //L.i("transferredBytes:" + transferredBytes + "totalSize:" + totalSize);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();

                    switch (type) {
                        case APK:

                            msg.what = HANDLER_LODING;
                            bundle.putLong("transferredBytes", transferredBytes);
                            bundle.putLong("totalSize", totalSize);
                            L.d(transferredBytes + "/" + totalSize);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            break;
                        case APATCH:
                            msg.what = HANDLER_apatch_LODING;
                            bundle.putLong("transferredBytes", transferredBytes);
                            bundle.putLong("totalSize", totalSize);
                            L.d(transferredBytes + "/" + totalSize);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            break;
                    }

                }
            }, new HttpCallback() {

                @Override
                public void onSuccess(String t) {
                    L.d("onSuccess----------------");

                    switch (type) {
                        case APK:
                            handler.sendEmptyMessage(HANDLER_update_OK);
                            break;
                        case APATCH:
                            handler.sendEmptyMessage(HANDLER_apatch_OK);
                            break;
                    }
                }

                @Override
                public void onFailure(VolleyError error) {
                    L.d(error.toString());

                    switch (type) {
                        case APK:
                            handler.sendEmptyMessage(HANDLER_update_ON);
                            break;
                        case APATCH:
                            handler.sendEmptyMessage(HANDLER_apatch_ON);
                            break;
                    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyWebSocket.webSocket.removeTextMessageListener(this);
    }

    //防止按返回键将avtivity finish掉
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)

    {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            MainActivity.this.startActivity(intent);
            return false;
        } else return super.onKeyDown(keyCode, event);


    }
}
