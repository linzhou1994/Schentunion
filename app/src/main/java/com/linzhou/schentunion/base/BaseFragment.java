package com.linzhou.schentunion.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.fragment
 *创建者:  linzhou
 *创建时间:17/04/21
 *描述:   fragment的基类
 */


public abstract class BaseFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        int layoutId = getlayoutId();
        View view = inflater.inflate(layoutId,container,false);
        initView(view);
        initData();
        setListener();
        return view;
    }

    /**
     * 获取布局id
     * @return fragment的布局id
     */
    public abstract int getlayoutId();

    /**
     * 初始化控件
     */
    protected abstract void initView(View view);

    /**
     * 加载数据
     */
    protected abstract void initData();

    /**
     * 设置监听事件
     */
    protected abstract void setListener();
}
