package com.linzhou.schentunion.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Enterprise;
import com.linzhou.schentunion.data.Post;
import com.linzhou.schentunion.utils.L;
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


public class EtplistAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Enterprise> metp;
    private Context mContext;
    private EtplistListener mEtplistListener;

    public EtplistAdapter(Context mContext, List<Enterprise> metp) {
        this.mContext = mContext;
        this.metp = metp;
        //获取系统服务
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public interface EtplistListener{
        public void itemOnClick(int position);
    }
    public void addEtplistListener(EtplistListener mEtplistListener){
        this.mEtplistListener = mEtplistListener;
    }


    @Override
    public int getCount() {
        return metp.size();
    }

    @Override
    public Object getItem(int position) {
        return metp.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Enterprise etp = metp.get(position);
        ViewHolder mViewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.elist_item, null);
            mViewHolder = new ViewHolder();

            mViewHolder.epicture = (ImageView) convertView.findViewById(R.id.epicture);
            mViewHolder.eaddress = (TextView) convertView.findViewById(R.id.eaddress);
            mViewHolder.ename = (TextView) convertView.findViewById(R.id.ename);
            mViewHolder.etype = (TextView) convertView.findViewById(R.id.etype);
            mViewHolder.posize = (TextView) convertView.findViewById(R.id.posize);
            mViewHolder.poname = (TextView) convertView.findViewById(R.id.poname);


        } else mViewHolder = (ViewHolder) convertView.getTag();
        if (!TextUtils.isEmpty(etp.picture))
            PicassoUtils.loadImageViewSize(mContext, StaticClass.HTTPIMAGE + etp.picture, 66, 66, mViewHolder.epicture);
        //L.d("--------------------------------"+StaticClass.HTTPIMAGE + etp.picture);
        mViewHolder.eaddress.setText(etp.address);
        mViewHolder.ename .setText(etp.name);
        mViewHolder.etype.setText(etp.type);
        mViewHolder.posize.setText(etp.posize+"");
        mViewHolder.poname .setText(etp.poname);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEtplistListener!=null)
                    mEtplistListener.itemOnClick(position);
            }
        });
        convertView.setTag(mViewHolder);
        return convertView;
    }


    public class ViewHolder {
        public ImageView epicture;
        public TextView ename;
        public TextView eaddress;
        public TextView etype;
        public TextView poname;
        public TextView posize;

    }


}
