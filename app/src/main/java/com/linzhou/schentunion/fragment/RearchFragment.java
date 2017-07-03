package com.linzhou.schentunion.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.linzhou.schentunion.Adapter.EtplistAdapter;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.activity.EtpDeliverActivity;
import com.linzhou.schentunion.activity.EtpSearchAcitvity;
import com.linzhou.schentunion.activity.PostDetailActivity;
import com.linzhou.schentunion.base.BaseFragment;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Enterprise;
import com.linzhou.schentunion.view.CustomDialog;
import com.linzhou.schentunion.websocekt.MyWebSocket;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;


/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.fragment
 *创建者:  linzhou
 *创建时间:17/04/21
 *描述:   搜索
 */


public class RearchFragment extends BaseFragment implements MyWebSocket.TextMessageListener,View.OnClickListener{

    public static final int QRCODE = 1001;

    private ListView elist;
    private SwipeRefreshLayout mSwipeRefresh;

    private EtplistAdapter mAdapter;

    private List<Enterprise> mData=new ArrayList<>();
    private ImageView scan;

    private ImageView search;

    private CustomDialog mScanDialog;
    private Button zx_xj;
    private Button zx_xc;

    @Override
    public int getlayoutId() {
        return R.layout.rearch_fragment;
    }

    @Override
    protected void initView(View view) {
        sendgetData();
        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.mSwipeRefresh);
        mSwipeRefresh.setRefreshing(true);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefresh.setDistanceToTriggerSync(400);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeRefresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小

        elist = (ListView) view.findViewById(R.id.elist);
        mAdapter=new EtplistAdapter(getActivity(),mData);
        elist.setAdapter(mAdapter);

        scan = (ImageView) view.findViewById(R.id.scan);

        search = (ImageView) view.findViewById(R.id.search);

        mScanDialog = new CustomDialog(getActivity(), 0, 0, R.layout.dialog_zxing
                , R.style.Theme_dialog, Gravity.CENTER, R.style.pop_anim_style);

        zx_xj = (Button) mScanDialog.findViewById(R.id.zx_xj);
        zx_xc = (Button) mScanDialog.findViewById(R.id.zx_xc);


    }

    private void sendgetData() {
        JSONObject jo = new JSONObject();
        try {
            jo.put(StaticClass.TYPE,StaticClass.ELIST);
            jo.put(StaticClass.CONTENT,new JSONObject());
            MyWebSocket.webSocket.sendMessage(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendgetData();
            }
        });

        MyWebSocket.webSocket.addTextMessageListeners(this);
        mAdapter.addEtplistListener(new EtplistAdapter.EtplistListener() {
            @Override
            public void itemOnClick(int position) {
                Intent intent = new Intent(getActivity(), EtpDeliverActivity.class);
                intent.putExtra("eid",mData.get(position).id);
                startActivity(intent);
            }
        });

        scan.setOnClickListener(this);
        search.setOnClickListener(this);
        zx_xc.setOnClickListener(this);
        zx_xj.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scan:
                mScanDialog.show();
                break;
            case R.id.search:
                Intent intent = new Intent(getActivity(),EtpSearchAcitvity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_in,R.anim.activity_out);
                break;
            case R.id.zx_xc:
                toPicture();
                mScanDialog.dismiss();
                break;
            case R.id.zx_xj:
                Intent openCameraIntent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(openCameraIntent, QRCODE);
                mScanDialog.dismiss();
                break;
        }

    }
    public static final int IMAGE_REQUEST_CODE = 101;
    //跳转相册
    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != getActivity().RESULT_CANCELED) {
            switch (requestCode) {
                //相册数据
                case IMAGE_REQUEST_CODE:
                    String[] proj = { MediaStore.Images.Media.DATA };

                    // 好像是android多媒体数据库的封装接口，具体的看Android文档
                    Cursor cursor = getActivity().managedQuery(data.getData(), proj, null, null, null);

                    // 按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    // 最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    //Toast.makeText(getActivity(), QRCodeDecoder.syncDecodeQRCode(path),Toast.LENGTH_SHORT).show();
                    scanresult(QRCodeDecoder.syncDecodeQRCode(path));
                    break;
                case QRCODE:
                    //处理扫描结果（在界面上显示）
                    if (null != data) {
                        Bundle bundle = data.getExtras();
                        String scanResult = bundle.getString("result");
                        scanresult(scanResult);
                        //Toast.makeText(getActivity(), "解析结果:" + scanResult, Toast.LENGTH_LONG).show();
                        Log.d("linzhou123","解析结果:" + scanResult);
                    }
                    break;

            }
        }

    }



    private void scanresult(String str){
        try {
            JSONObject jo = new JSONObject(str);

            String type = jo.optString(StaticClass.TYPE);
            int id = jo.optInt("id");
            switch (type){
                case "eid":
                    Intent intent = new Intent(getActivity(), EtpDeliverActivity.class);
                    intent.putExtra("eid",id);
                    startActivity(intent);
                    break;
                case "poid":
                    Intent intent1 = new Intent(getActivity(),PostDetailActivity.class);
                    intent1.putExtra("poid",id);
                    startActivity(intent1);
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void remessage(String mes) {
        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.ELIST)){
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                JSONArray etps = content.getJSONArray("enterprises");
                mData.clear();
                for (int i = 0;i<etps.length();i++){
                    JSONObject etp = etps.getJSONObject(i);
                    Enterprise et=new Enterprise();
                    et.id = etp.optInt("eid");
                    et.address = etp.optString("address");
                    et.picture = etp.optString("epicture");
                    et.type = etp.getString("etype");
                    et.name = etp.optString("ename");
                    et.poname = etp.optString("fpo");
                    et.posize = etp.optInt("size");
                    mData.add(et);
                }
                mSwipeRefresh.setRefreshing(false);
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
