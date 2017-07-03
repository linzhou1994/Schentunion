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
import com.linzhou.schentunion.data.Post;
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


public class PostlvAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Post> mPosts;
    private Context mContext;
    private PostlvListener mPostlvListener;

    public PostlvAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        //获取系统服务
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addPostlvListener(PostlvListener mPostlvListener){
        this.mPostlvListener = mPostlvListener;
    }


    @Override
    public int getCount() {
        return mPosts.size();
    }

    @Override
    public Object getItem(int i) {
        return mPosts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Post post = mPosts.get(i);
        ViewHolder mViewHolder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.post_list_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.poname = (TextView) view.findViewById(R.id.poname);
            mViewHolder.eimage = (CircleImageView) view.findViewById(R.id.eimage);
            mViewHolder.wage = (TextView) view.findViewById(R.id.wage);
            mViewHolder.ename = (TextView) view.findViewById(R.id.ename);
            mViewHolder.address = (TextView) view.findViewById(R.id.address);
            mViewHolder.workep = (TextView) view.findViewById(R.id.workep);
            mViewHolder.education = (TextView) view.findViewById(R.id.education);
            mViewHolder.emanger = (TextView) view.findViewById(R.id.emanger);
        } else mViewHolder = (ViewHolder) view.getTag();
        if (!TextUtils.isEmpty(post.picture))
            PicassoUtils.loadImageViewSize(mContext, StaticClass.HTTPIMAGE+post.picture, 25, 25, mViewHolder.eimage);

        mViewHolder.poname.setText(post.poname);
        if (post.startsalary==0)
            mViewHolder.wage.setText("价格面议");
        else if (post.endsalary==0){
            mViewHolder.wage.setText(post.startsalary+"k");
        }
        else mViewHolder.wage.setText(post.startsalary+"k-"+post.endsalary+"k");

        mViewHolder.ename.setText(post.ename);
        mViewHolder.address.setText(post.address);
        mViewHolder.workep .setText(post.workep);
        mViewHolder.education .setText(post.education);
        mViewHolder.emanger.setText(post.emanger);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPostlvListener!=null)
                    mPostlvListener.onClick(i);
            }
        });

        view.setTag(mViewHolder);

        return view;
    }

    public interface PostlvListener {

        public void onClick(int i);

    }

    public class ViewHolder {
        public CircleImageView eimage;
        public TextView poname;
        public TextView wage;
        public TextView ename;
        public TextView address;
        public TextView workep;
        public TextView education;
        public TextView emanger;

    }
}
