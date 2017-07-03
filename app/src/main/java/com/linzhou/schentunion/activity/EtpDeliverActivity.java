package com.linzhou.schentunion.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.linzhou.schentunion.Adapter.EdeliverAdapter;
import com.linzhou.schentunion.Adapter.EpostlistAdapter;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.BaseActivity;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Enterprise;
import com.linzhou.schentunion.data.Post;
import com.linzhou.schentunion.utils.PicassoUtils;
import com.linzhou.schentunion.utils.RxZxing;
import com.linzhou.schentunion.utils.UtilTools;
import com.linzhou.schentunion.view.CustomDialog;
import com.linzhou.schentunion.view.ExpandableTextView;
import com.linzhou.schentunion.websocekt.MyWebSocket;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import rx.android.schedulers.AndroidSchedulers;

/*
 *企业详情
 */

public class EtpDeliverActivity extends BaseActivity {

    private LinearLayout etpdeliver;

    private ImageView epicture;

    private TextView ename;

    private TextView webaddress;

    private TextView etype;

    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private EdeliverAdapter mpagerAdapter;

    private CustomDialog mQrcodeDialog;
    private ImageView zxing;
    private ImageView zxingdialog;

    private ImageView speak;


    private View view1, view2;
    private List<View> mViews = new ArrayList<>();
    //Title
    private List<String> mTitle = new ArrayList<>();

    private List<Post> mPost = new ArrayList<>();

    private ExpandableTextView eintroduce;

    private ListView epolist;
    private EpostlistAdapter mListAdapter;

    private AlertDialog dialog;

    private Enterprise metp = new Enterprise();

    private SpeechSynthesizer mTts;

    @Override
    protected int getlayout() {
        return R.layout.activity_etpdeliver;
    }

    @Override
    protected void initview() {
        Intent i = getIntent();
        metp.id = i.getIntExtra("eid", -1);

        JSONObject jo = new JSONObject();
        try {
            jo.put(StaticClass.TYPE, StaticClass.EDETAIL);
            JSONObject content = new JSONObject();
            content.put("eid", metp.id);
            jo.put(StaticClass.CONTENT, content);
            MyWebSocket.webSocket.sendMessage(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog = new SpotsDialog(this);
        dialog.show();

        etpdeliver = (LinearLayout) findViewById(R.id.etpdeliver);
        epicture = (ImageView) findViewById(R.id.epicture);
        ename = (TextView) findViewById(R.id.ename);
        webaddress = (TextView) findViewById(R.id.webaddress);
        etype = (TextView) findViewById(R.id.etype);
        mTabLayout = (TabLayout) findViewById(R.id.mTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);

        view1 = View.inflate(this, R.layout.eintroduce, null);
        view2 = View.inflate(this, R.layout.epostlist, null);

        eintroduce = (ExpandableTextView) view1.findViewById(R.id.eintroduce);
        speak = (ImageView) view1.findViewById(R.id.speak);

        epolist = (ListView) view2.findViewById(R.id.epolist);

        mViews.add(view1);
        mViews.add(view2);

        mTitle.add("公司介绍");
        mTitle.add("公司职位");

        mListAdapter = new EpostlistAdapter(this, mPost);
        epolist.setAdapter(mListAdapter);

        mpagerAdapter = new EdeliverAdapter(mViews, mTitle);
        mViewPager.setAdapter(mpagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        zxing = (ImageView) findViewById(R.id.zxing);
        mQrcodeDialog = new CustomDialog(this, 0, 0, R.layout.dialog_qrcode
                , R.style.Theme_dialog, Gravity.CENTER, R.style.pop_anim_style);
        zxingdialog = (ImageView) mQrcodeDialog.findViewById(R.id.zxingdialog);


        mTts = SpeechSynthesizer.createSynthesizer(this, null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "linzhou");
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100

    }


    private Vibrator vibrator;
    Bitmap qrCodeBitmap;
    String title;
    @Override
    protected void initData() {
        JSONObject jo = new JSONObject();
        try {
            jo.put(StaticClass.TYPE, "eid");
            jo.put("id", metp.id);
            title=jo.toString();
            qrCodeBitmap = EncodingUtils.createQRCode(title, 350, 350,
                    BitmapFactory.decodeResource(getResources(), R.mipmap.logo));
            zxing.setImageBitmap(qrCodeBitmap);
            zxingdialog.setImageBitmap(qrCodeBitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void setListener() {
        super.setListener();

        mListAdapter.addEpostlvListener(new EpostlistAdapter.EpostlvListener() {
            @Override
            public void onClick(int i) {
                Intent intent = new Intent(EtpDeliverActivity.this, PostDetailActivity.class);
                intent.putExtra("poid", mPost.get(i).id);
                startActivity(intent);
            }
        });

        webaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EtpDeliverActivity.this, WebViewActivity.class);
                intent.putExtra("title", metp.name);
                intent.putExtra("url", metp.webaddress);
                startActivity(intent);
            }
        });

        zxing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQrcodeDialog.show();
            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mTts.isSpeaking())
                    mTts.startSpeaking(metp.introduce, null);
            }
        });

        zxingdialog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //震动50毫秒
                vibrator.vibrate(50);
                RxZxing.saveImageAndGetPathObservable(EtpDeliverActivity.this, qrCodeBitmap, title)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(uri -> UtilTools.shareImage(EtpDeliverActivity.this, uri,
                                "二维码分享"),
                                error -> Toast.makeText(EtpDeliverActivity.this,"error",Toast.LENGTH_SHORT).show());
                return false;
            }
        });

    }


    @Override
    public void remessage(String mes) {
        super.remessage(mes);

        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.EDETAIL)) {
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                JSONObject enterprise = content.optJSONObject("enterprise");
                JSONArray posts = content.optJSONArray("posts");
                metp.name = enterprise.optString("ename");
                metp.address = enterprise.optString("address");
                metp.webaddress = enterprise.optString("website");
                // L.d(metp.webaddress+"-----------------------------");
                metp.type = enterprise.optString("etype");
                metp.introduce = enterprise.optString("introduce");
                metp.picture = enterprise.optString("picture");
                metp.name = enterprise.optString("ename");
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.getJSONObject(i);
                    Post p = new Post();
                    p.id = post.optInt("poid");
                    p.address = post.optString("address");
                    p.workep = post.optString("workep");
                    p.startsalary = post.getInt("startsalary");
                    p.endsalary = post.optInt("endsalary");
                    p.emanger = post.optString("manager");
                    p.poname = post.optString("poname");
                    p.picture = post.optString("epicture");
                    p.education = post.optString("education");
                    mPost.add(p);
                }
                setdata();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setdata() {
        etpdeliver.setVisibility(View.VISIBLE);
        PicassoUtils.loadImageViewSize(this, StaticClass.HTTPIMAGE + metp.picture, 88, 88, epicture);
        ename.setText(metp.name);
        webaddress.setText(metp.webaddress);
        etype.setText(metp.type);
        eintroduce.setText(metp.introduce);
        mTitle.remove(1);
        mTitle.add("公司职位（" + mPost.size() + "）");
        mpagerAdapter.notifyDataSetChanged();
        mListAdapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTts.isSpeaking())
            mTts.stopSpeaking();
    }
}
