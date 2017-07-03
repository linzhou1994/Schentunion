package com.linzhou.schentunion.activity;
/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.activity
 *创建者:  linzhou
 *创建时间:17/04/29
 *描述:   我投过的岗位页
 */

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.linzhou.schentunion.Adapter.PostlvAdapter;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.BaseActivity;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Post;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DelivertActivity extends BaseActivity {

    private ListView pushlv;
    private List<Post> mPosts = new ArrayList<>();
    private PostlvAdapter mPostlvAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    @Override
    protected int getlayout() {
        return R.layout.activity_delivert;
    }

    @Override
    protected void initview() {
        sendgetData();

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefresh);
        mSwipeRefresh.setRefreshing(true);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefresh.setDistanceToTriggerSync(400);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeRefresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小

        pushlv = (ListView) findViewById(R.id.pushlv);
        mPostlvAdapter = new PostlvAdapter(this,mPosts);
        pushlv.setAdapter(mPostlvAdapter);
    }
    private void sendgetData(){
        JSONObject jo =new JSONObject();
        try {
            jo.put(StaticClass.TYPE,StaticClass.SPOLIST);
            jo.put(StaticClass.CONTENT,new JSONObject());
            MyWebSocket.webSocket.sendMessage(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
//        L.d("PushFragment:oncreate");
//
//        for (int i=0;i<20;i++){
//            Post p = new Post();
//            p.id = i;
//            p.poname = "android高级:"+i;
//            p.address = "浙江:"+i;
//            p.workep = i+"年";
//            p.startsalary = i;
//            p.endsalary = (i+10)%20;
//            p.emanger = "马云:"+i;
//            p.education = "本科:"+i;
//            p.ename = "阿里巴巴:"+i;
//            mPosts.add(p);
//        }
//        mPostlvAdapter.notifyDataSetChanged();
    }

    @Override
    protected void setListener() {
        super.setListener();
        mPostlvAdapter.addPostlvListener(new PostlvAdapter.PostlvListener() {
            @Override
            public void onClick(int i) {
                Intent intent = new Intent(DelivertActivity.this,PostDetailActivity.class);
                intent.putExtra("poid",mPosts.get(i).id);
                startActivity(intent);
            }
        });


        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendgetData();
            }
        });

    }

    @Override
    public void remessage(String mes) {
        super.remessage(mes);
        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.SPOLIST)){
                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                JSONArray posts = content.getJSONArray("posts");
                mPosts.clear();
                for (int i = 0;i<posts.length();i++){
                    JSONObject post = posts.getJSONObject(i);
                    Post p=new Post();
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
                mSwipeRefresh.setRefreshing(false);
                mPostlvAdapter.notifyDataSetChanged();
                //initData();
            }
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.LOGOUT)){
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
