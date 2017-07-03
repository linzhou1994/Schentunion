package com.linzhou.schentunion.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linzhou.schentunion.R;
import com.linzhou.schentunion.data.Profession;

import java.util.List;


/*
 *项目名： MyApplication
 *包名：   com.linzhou.myapplication.spinner
 *创建者:  linzhou
 *创建时间:17/04/26
 *描述:   
 */


public class SpinnerAdapter extends BaseAdapter {
    private Context mcontext;
    private List<Profession> mList;
    private LayoutInflater inflater;
    //private ItemOnClick mItemOnClick;

    public SpinnerAdapter(Context context, List<Profession> mList) {
        this.mcontext = context;
        this.mList = mList;
        inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

//    public void addItemOnClick(ItemOnClick mItemOnClick) {
//        this.mItemOnClick = mItemOnClick;
//    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder view;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item, null);
            view = new ViewHolder();
            view.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        } else view = (ViewHolder) convertView.getTag();
        view.tv_name.setText(mList.get(position).name);
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mItemOnClick != null) mItemOnClick.itemOnClick(position);
//            }
//        });

        convertView.setTag(view);
        return convertView;
    }

//    public interface ItemOnClick {
//        public void itemOnClick(int i);
//    }


    public class ViewHolder {

        public TextView tv_name;
    }
}
