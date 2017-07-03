package com.linzhou.schentunion.activity;
/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.activity
 *创建者:  linzhou
 *创建时间:17/04/29
 *描述:   
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.linzhou.schentunion.Adapter.PostlvAdapter;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.BaseActivity;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Post;
import com.linzhou.schentunion.utils.L;
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

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import rx.android.schedulers.AndroidSchedulers;

public class PostDetailActivity extends BaseActivity implements View.OnClickListener {

    private int eid;
    private Post post = new Post();

    private LinearLayout company;

    private CircleImageView eimage;
    private TextView poname;
    private TextView wage;
    private TextView ename;
    private TextView address;
    private TextView workep;
    private TextView education;
    private TextView emanger;
    private TextView etype;
    private TextView addressall;
    private ExpandableTextView introduce;
    private ImageView speak;


    private LinearLayout podetail;

    private Button delivery;

    private AlertDialog dialog;

    private CustomDialog mQrcodeDialog;
    private ImageView zxing;
    private ImageView zxingdialog;

    private SpeechSynthesizer mTts;

    private ListView star_post;
    private List<Post> mPosts = new ArrayList<>();
    private PostlvAdapter mPostlvAdapter;

    private ScrollView mScrollView;

    private Vibrator vibrator;


    @Override
    protected int getlayout() {
        return R.layout.activity_postdetail;
    }

    @Override
    protected void initview() {
        dialog = new SpotsDialog(this);
        dialog.show();
        Intent intent = getIntent();
        post.id = intent.getIntExtra("poid", -1);
        JSONObject jo = new JSONObject();
        try {
            jo.put(StaticClass.TYPE, StaticClass.PODETAIL);
            JSONObject content = new JSONObject();
            content.put("poid", post.id);
            jo.put(StaticClass.CONTENT, content);
            MyWebSocket.webSocket.sendMessage(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mScrollView = (ScrollView) findViewById(R.id.mScrollView);

        company = (LinearLayout) findViewById(R.id.company);
        eimage = (CircleImageView) findViewById(R.id.eimage);
        poname = (TextView) findViewById(R.id.poname);
        wage = (TextView) findViewById(R.id.wage);
        ename = (TextView) findViewById(R.id.ename);
        address = (TextView) findViewById(R.id.address);
        workep = (TextView) findViewById(R.id.workep);
        education = (TextView) findViewById(R.id.education);
        emanger = (TextView) findViewById(R.id.emanger);
        etype = (TextView) findViewById(R.id.etype);
        addressall = (TextView) findViewById(R.id.addressall);
        introduce = (ExpandableTextView) findViewById(R.id.introduce);
        zxing = (ImageView) findViewById(R.id.zxing);
        speak = (ImageView) findViewById(R.id.speak);

        podetail = (LinearLayout) findViewById(R.id.podetail);
        //introduce.setText("2016年12月30日下午消息，今日网易传媒针对昨日内部动员大会上宣布的各频道被取消，成立直播事业群一事进行了详细公示，公示显");

        delivery = (Button) findViewById(R.id.delivery);


        mQrcodeDialog = new CustomDialog(this, 0, 0, R.layout.dialog_qrcode
                , R.style.Theme_dialog, Gravity.CENTER, R.style.pop_anim_style);
        zxingdialog = (ImageView) mQrcodeDialog.findViewById(R.id.zxingdialog);

        star_post = (ListView) findViewById(R.id.star_post);

        mPostlvAdapter = new PostlvAdapter(this, mPosts);
        star_post.setAdapter(mPostlvAdapter);

        mTts = SpeechSynthesizer.createSynthesizer(this, null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "linzhou");
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100


    }

    @Override
    protected void initData() {


    }
    Bitmap qrCodeBitmap;
    String title;

    @Override
    protected void setListener() {
        super.setListener();
        company.setOnClickListener(this);
        delivery.setOnClickListener(this);
        zxing.setOnClickListener(this);
        speak.setOnClickListener(this);

        mPostlvAdapter.addPostlvListener(new PostlvAdapter.PostlvListener() {
            @Override
            public void onClick(int i) {
                Intent intent = new Intent(PostDetailActivity.this, PostDetailActivity.class);
                intent.putExtra("poid", mPosts.get(i).id);
                startActivity(intent);
            }
        });

        zxingdialog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //震动50毫秒
                vibrator.vibrate(50);
                RxZxing.saveImageAndGetPathObservable(PostDetailActivity.this, qrCodeBitmap, title)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(uri -> UtilTools.shareImage(PostDetailActivity.this, uri,
                                "二维码分享"),
                                error -> Toast.makeText(PostDetailActivity.this,"error",Toast.LENGTH_SHORT).show());
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.company:
                Intent intent = new Intent(PostDetailActivity.this, EtpDeliverActivity.class);
                intent.putExtra("eid", eid);
                startActivity(intent);
                break;
            case R.id.delivery:
                JSONObject jo = new JSONObject();
                try {
                    jo.put(StaticClass.TYPE, StaticClass.DELIVER);
                    JSONObject content = new JSONObject();
                    content.put("poid", post.id);
                    jo.put(StaticClass.CONTENT, content);
                    MyWebSocket.webSocket.sendMessage(jo.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.zxing:
                mQrcodeDialog.show();
                break;
            case R.id.speak:
                if (!mTts.isSpeaking())
                    mTts.startSpeaking(post.introduce, null);
                break;
        }
    }

    @Override
    public void remessage(String mes) {
        super.remessage(mes);
        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.PODETAIL)) {
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);

                post.id = content.optInt("poid");
                post.address = content.optString("address");
                post.workep = content.optString("workep");
                post.startsalary = content.optInt("startsalary");
                post.endsalary = content.optInt("endsalary");
                post.emanger = content.optString("manager");
                post.poname = content.optString("poname");
                post.ename = content.optString("enterprise");
                post.picture = content.optString("epicture");
                post.education = content.optString("education");
                post.etype = content.optString("etype");
                post.eaddress = content.optString("eaddress");
                post.introduce = content.optString("introduce");
                eid = content.optInt("eid");
                JSONArray posts = content.getJSONArray("posts");
                mPosts.clear();
                if (posts != null)
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
                        p.ename = post.optString("enterprise");
                        p.picture = post.optString("epicture");
                        p.education = post.optString("education");
                        mPosts.add(p);
                    }
                setData();
                podetail.setVisibility(View.VISIBLE);
                dialog.dismiss();


            }
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.DELIVER)) {
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                int result = content.optInt("result");
                switch (result) {
                    case 0:
                        Toast.makeText(this, "简历投递失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(this, "简历投递成功", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.LOGOUT)) {
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        if (!TextUtils.isEmpty(post.picture))
            PicassoUtils.loadImageViewSize(this, StaticClass.HTTPIMAGE + post.picture, 25, 25, eimage);

        poname.setText(post.poname);
        if (post.startsalary == 0)
            wage.setText("待遇面议");
        else if (post.endsalary == 0) {
            wage.setText("【" + post.startsalary + "k】");
        } else wage.setText("【" + post.startsalary + "k-" + post.endsalary + "k】");

        ename.setText(post.ename);
        address.setText(post.address);
        workep.setText(post.workep);
        education.setText(post.education);
        emanger.setText(post.emanger);
        etype.setText(post.etype);
        addressall.setText(post.eaddress);
        introduce.setText(post.introduce);
        mPostlvAdapter.notifyDataSetChanged();
        setListViewHeight(star_post);


        L.d("2");
        JSONObject jo = new JSONObject();
        try {
            jo.put(StaticClass.TYPE, "poid");
            jo.put("id", post.id);
            title=jo.toString();
            qrCodeBitmap = EncodingUtils.createQRCode(title, 350, 350,
                    BitmapFactory.decodeResource(getResources(), R.mipmap.logo));
            zxing.setImageBitmap(qrCodeBitmap);
            zxingdialog.setImageBitmap(qrCodeBitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    /**
     * 重新计算ListView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
     *
     * @param listView
     */
    public void setListViewHeight(final ListView listView) {

        // 获取ListView对应的Adapter

        PostlvAdapter listAdapter = (PostlvAdapter) listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mScrollView.scrollTo(0, 0);
                // getWidth or getHeight;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTts.isSpeaking())
            mTts.stopSpeaking();

    }


}
