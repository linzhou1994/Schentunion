package com.linzhou.schentunion.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Post;
import com.linzhou.schentunion.utils.PicassoUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.Adapter
 *创建者:  linzhou
 *创建时间:17/05/05
 *描述:   
 */


public class EpostlistAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Post> mPosts;
    private Context mContext;
    private EpostlvListener mEpostlvListener;

    public EpostlistAdapter(Context mContext, List<Post> mPosts){
        this.mContext = mContext;
        this.mPosts = mPosts;
        //获取系统服务
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addEpostlvListener(EpostlvListener mEpostlvListener){
        this.mEpostlvListener = mEpostlvListener;
    }



    @Override
    public int getCount() {
        return mPosts.size();
    }

    @Override
    public Object getItem(int position) {
        return mPosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Post post = mPosts.get(position);
        ViewHolder mViewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.epost_list_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.poname = (TextView) convertView.findViewById(R.id.poname);
            mViewHolder.eimage = (CircleImageView) convertView.findViewById(R.id.eimage);
            mViewHolder.wage = (TextView) convertView.findViewById(R.id.wage);
            mViewHolder.address = (TextView) convertView.findViewById(R.id.address);
            mViewHolder.workep = (TextView) convertView.findViewById(R.id.workep);
            mViewHolder.education = (TextView) convertView.findViewById(R.id.education);
            mViewHolder.emanger = (TextView) convertView.findViewById(R.id.emanger);
        } else mViewHolder = (ViewHolder) convertView.getTag();
        if (!TextUtils.isEmpty(post.picture))
            PicassoUtils.loadImageViewSize(mContext, StaticClass.HTTPIMAGE+post.picture, 25, 25, mViewHolder.eimage);

        mViewHolder.poname.setText(post.poname);
        if (post.startsalary==0)
            mViewHolder.wage.setText("价格面议");
        else if (post.endsalary==0){
            mViewHolder.wage.setText(post.startsalary+"k");
        }
        else mViewHolder.wage.setText(post.startsalary+"k-"+post.endsalary+"k");


        mViewHolder.address.setText(post.address);
        mViewHolder.workep .setText(post.workep);
        mViewHolder.education .setText(post.education);
        mViewHolder.emanger.setText(post.emanger);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEpostlvListener!=null)
                    mEpostlvListener.onClick(position);
            }
        });

        convertView.setTag(mViewHolder);
        return convertView;
    }





    public interface EpostlvListener {

        public void onClick(int i);

    }





    public class ViewHolder {
        public CircleImageView eimage;
        public TextView poname;
        public TextView wage;
        public TextView address;
        public TextView workep;
        public TextView education;
        public TextView emanger;

    }





}
