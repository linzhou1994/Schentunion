package com.linzhou.schentunion.Adapter;

/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.Adapter
 *创建者:  linzhou
 *创建时间:17/04/26
 *描述:   
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cooloongwu.emoji.utils.EmojiTextUtils;
import com.linzhou.schentunion.R;
import com.linzhou.schentunion.base.StaticClass;
import com.linzhou.schentunion.data.Chat;
import com.linzhou.schentunion.utils.PicassoUtils;
import com.linzhou.schentunion.utils.UtilTools;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<Chat> listData;

    public ChatAdapter(Context context ,ArrayList<Chat> listData){
        this.mContext = context;
        this.listData = listData;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getItemViewType(int position) {
        return listData.get(position).type;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 0:
                view = inflater.inflate(R.layout.left_chat,parent,false);
                return new LeftViewHolder(view);
            case 1:
                view = inflater.inflate(R.layout.right_chat,parent,false);
                return new RightViewHolder(view);
            default:return null;
        }
        //return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((AbstractViewHolder)((RecyclerView.ViewHolder) holder)).band(position);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    private abstract class AbstractViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView imageView;
        protected TextView tv_text;
        protected TextView tv_time;

        public AbstractViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void band(int i);
    }

    private class LeftViewHolder extends AbstractViewHolder{

        public LeftViewHolder(View itemView) {
            super(itemView);
            imageView = (CircleImageView) itemView.findViewById(R.id.imageView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_text = (TextView) itemView.findViewById(R.id.tv_left_text);
        }

        @Override
        public void band(int i) {
            Chat chat = listData.get(i);
            PicassoUtils.loadImageViewSize(mContext,
                    StaticClass.HTTPIMAGE+chat.picture,50,50,imageView);
            //tv_time.setText(UtilTools.dateToString(chat.time,"HH:mm:ss"));
            if (UtilTools.getStartTime().getTime()<chat.time.getTime())
                tv_time.setText(UtilTools.dateToString(chat.time,"HH:mm"));
            else tv_time.setText(UtilTools.dateToString(chat.time));
            tv_text.setText(EmojiTextUtils.getEditTextContent(chat.message, mContext, tv_text));

        }
    }

    private class RightViewHolder extends AbstractViewHolder{

        public RightViewHolder(View itemView) {
            super(itemView);
            imageView = (CircleImageView) itemView.findViewById(R.id.imageView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_text = (TextView) itemView.findViewById(R.id.tv_right_text);
        }

        @Override
        public void band(int i) {
            Chat chat = listData.get(i);
            PicassoUtils.loadImageViewSize(mContext,
                    StaticClass.HTTPIMAGE+chat.picture,50,50,imageView);
            if (UtilTools.getStartTime().getTime()<chat.time.getTime())
                tv_time.setText(UtilTools.dateToString(chat.time,"HH:mm"));
            else tv_time.setText(UtilTools.dateToString(chat.time));
            tv_text.setText(EmojiTextUtils.getEditTextContent(chat.message, mContext, tv_text));

        }
    }




}
