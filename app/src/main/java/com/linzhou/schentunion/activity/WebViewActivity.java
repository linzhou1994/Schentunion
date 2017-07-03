package com.linzhou.schentunion.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.AppConfig;
import com.linzhou.schentunion.base.BaseActivity;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.utils.L;
import com.linzhou.schentunion.websocekt.MyWebSocket;

import org.json.JSONException;
import org.json.JSONObject;


/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.activity
 *创建者:  linzhou
 *创建时间:17/05/05
 *描述:   
 */


public class WebViewActivity extends BaseActivity {

    //进度
    private ProgressBar mProgressBar;
    //网页
    private WebView mWebView;
    @Override
    protected int getlayout() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initview() {
        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        mWebView = (WebView) findViewById(R.id.mWebView);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        final String url = intent.getStringExtra("url");
        L.i("url:" + url);

        //设置标题
        getSupportActionBar().setTitle(title+"官网");


        //支持JS
        mWebView.getSettings().setJavaScriptEnabled(true);
        //支持缩放
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        //接口回调
        mWebView.setWebChromeClient(new WebViewClient());
        //加载网页
        mWebView.loadUrl("http://"+url);

        //本地显示
        mWebView.setWebViewClient(new android.webkit.WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                //我接受这个事件
                return true;
            }
        });
    }

    public class WebViewClient extends WebChromeClient {

        //进度变化的监听
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress == 100){
                mProgressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        super.setListener();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void remessage(String mes) {
        super.remessage(mes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
