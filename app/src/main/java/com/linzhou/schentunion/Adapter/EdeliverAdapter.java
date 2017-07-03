package com.linzhou.schentunion.Adapter;

/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.Adapter
 *创建者:  linzhou
 *创建时间:17/05/05
 *描述:   
 */


import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class EdeliverAdapter extends PagerAdapter {
    private List<View> mViews=new ArrayList<>();
    //Title
    private List<String> mTitle=new ArrayList<>();

    public EdeliverAdapter(List<View> mViews,List<String> mTitle){
        this.mTitle=mTitle;
        this.mViews=mViews;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(mViews.get(position));
        //super.destroyItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle.get(position);
    }
}
