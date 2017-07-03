package com.linzhou.schentunion.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Contact;
import com.linzhou.schentunion.utils.PicassoUtils;
import com.linzhou.schentunion.utils.UtilTools;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.fragment.Adapter
 *创建者:  linzhou
 *创建时间:17/04/25
 *描述:   
 */


public class MymeslvAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Contact> mContacts;
    private Context mContext;
    private MeslvListener meslvListener;

    public MymeslvAdapter(Context mContext, List<Contact> mContacts) {
        this.mContext = mContext;
        this.mContacts = mContacts;
        //获取系统服务
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setMeslvListener(MeslvListener meslvListener) {
        this.meslvListener = meslvListener;
    }

    @Override
    public int getCount() {
        return mContacts.size();
    }

    @Override
    public Object getItem(int i) {
        return mContacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Contact mContact = mContacts.get(i);
        ViewHolder mViewHolder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.message_lv_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.mesnew = view.findViewById(R.id.mesnew);
            mViewHolder.profile_image = (CircleImageView) view.findViewById(R.id.profile_image);
            mViewHolder.tv_type = (TextView) view.findViewById(R.id.tv_type);
            mViewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            mViewHolder.tv_mes = (TextView) view.findViewById(R.id.tv_mes);
            mViewHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            mViewHolder.mes_lv_item = (LinearLayout) view.findViewById(R.id.mes_lv_item);
        } else mViewHolder = (ViewHolder) view.getTag();

        if (!TextUtils.isEmpty(mContact.picture))
            PicassoUtils.loadImageViewSize(mContext, StaticClass.HTTPIMAGE+mContact.picture, 96, 96, mViewHolder.profile_image);
        mViewHolder.tv_type.setText(mContact.type == 0 ? "校" : "企");
        mViewHolder.tv_name.setText(mContact.name);
        mViewHolder.tv_mes.setText(mContact.message);
        if (UtilTools.getStartTime().getTime()<mContact.time.getTime())
            mViewHolder.tv_time.setText(UtilTools.dateToString(mContact.time,"HH:mm"));
        else mViewHolder.tv_time.setText(UtilTools.dateToString(mContact.time));
        //mViewHolder.tv_time.setText(UtilTools.dateToString(mContact.time));
        mViewHolder.mesnew.setVisibility(mContact.mesnew ? View.VISIBLE : View.INVISIBLE);

        mViewHolder.mes_lv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meslvListener.onClick(i);
            }
        });

        mViewHolder.mes_lv_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return meslvListener.onLongClick(i);
            }
        });

        view.setTag(mViewHolder);

        return view;
    }

    public interface MeslvListener {

        public void onClick(int i);

        public boolean onLongClick(int i);

    }

    public class ViewHolder {
        public CircleImageView profile_image;
        public TextView tv_type;
        public TextView tv_name;
        public TextView tv_mes;
        public TextView tv_time;
        public View mesnew;
        public LinearLayout mes_lv_item;

    }
}
