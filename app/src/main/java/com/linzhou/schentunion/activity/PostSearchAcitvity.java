package com.linzhou.schentunion.activity;
/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.activity
 *创建者:  linzhou
 *创建时间:17/05/14
 *描述:   
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.linzhou.schentunion.Adapter.PostlvAdapter;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Post;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostSearchAcitvity  extends AppCompatActivity implements MyWebSocket.TextMessageListener,View.OnClickListener{

    private ImageView back;
    private EditText et_search;
    private TextView search;
    private TextView search_post_null;
    private ListView search_post;

    private List<Post> mPosts = new ArrayList<>();
    private PostlvAdapter mPostlvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_postsearch);
        initview();
        setListener();
    }

    private void initview() {
        back = (ImageView) findViewById(R.id.back);
        et_search = (EditText) findViewById(R.id.et_search);
        search = (TextView) findViewById(R.id.search);
        search_post_null = (TextView) findViewById(R.id.search_post_null);
        search_post = (ListView) findViewById(R.id.search_post);

        mPostlvAdapter = new PostlvAdapter(this,mPosts);
        search_post.setAdapter(mPostlvAdapter);
    }

    private void setListener() {
        MyWebSocket.webSocket.addTextMessageListeners(this);
        back.setOnClickListener(this);
        search.setOnClickListener(this);
        mPostlvAdapter.addPostlvListener(new PostlvAdapter.PostlvListener() {
            @Override
            public void onClick(int i) {
                Intent intent = new Intent(PostSearchAcitvity.this,PostDetailActivity.class);
                intent.putExtra("poid",mPosts.get(i).id);
                startActivity(intent);
            }
        });
    }


    @Override
    public void remessage(String mes) {
        try {
            JSONObject jo = new JSONObject(mes);
            if (jo.optString(StaticClass.RETYPE).equals(StaticClass.SEARCHPO)){

                JSONObject content = jo.optJSONObject(StaticClass.CONTENT);
                if (content==null) {
                    search_post_null.setVisibility(View.VISIBLE);
                    search_post_null.setText("搜索异常");
                    return;
                }
                JSONArray posts = content.getJSONArray("posts");
                if (posts ==null){
                    search_post_null.setVisibility(View.VISIBLE);
                    search_post_null.setText("搜索异常");
                    return;
                }
                mPosts.clear();
                if (posts.length()==0){
                    search_post_null.setVisibility(View.VISIBLE);
                    search_post.setVisibility(View.GONE);
                    search_post_null.setText("无搜索结果");
                }
                else{
                    search_post_null.setVisibility(View.GONE);
                    search_post.setVisibility(View.VISIBLE);
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
                }
                mPostlvAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.search:
                String str = et_search.getText().toString();
                if (TextUtils.isEmpty(str))
                    return;
                try {
                    JSONObject jo = new JSONObject();
                    jo.put(StaticClass.TYPE, StaticClass.SEARCHPO);
                    JSONObject content = new JSONObject();
                    content.put("searchstr",str);
                    jo.put(StaticClass.CONTENT, content);
                    MyWebSocket.webSocket.sendMessage(jo.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyWebSocket.webSocket.removeTextMessageListener(this);

    }
}
