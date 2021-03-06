package com.example.user.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.myapplication.R;
import com.example.user.myapplication.entity.MyMessage;
import com.example.user.myapplication.entity.OthersMessage;
import com.example.user.myapplication.entity.User;
import com.example.user.myapplication.utils.Logger;
import com.example.user.myapplication.utils.StaticClass;

import org.w3c.dom.Text;

import java.util.List;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.adapter
 * User:           ${User}
 * Date&Time:      2018/4/25 13:59
 * Description:    TODO
 **/

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private BmobIMUserInfo userInfo;
    private List<BmobIMMessage> mList;
    private Context mContext;
    private LayoutInflater mInflater;

    private static final int TYPE_MY = 0;
    private static final int TYPE_OTHERS = 1;

    public ChatAdapter(Context context, List<BmobIMMessage> list,BmobIMUserInfo userInfo) {
        this.mContext = context;
        this.mList = list;
        this.userInfo=userInfo;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        BmobIMMessage message = mList.get(position);
        if (message.getMsgType().equals(MyMessage.MSG_TYPE)) {
            //解析为自己的消息
            return TYPE_MY;
        } else if (message.getMsgType().equals(OthersMessage.MSG_TYPE)) {
            //解析为对方的消息
            return TYPE_OTHERS;
        } else {
            //错误消息
            return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_MY:
                view = mInflater.inflate(R.layout.item_recycle_my_message, null);
                MyViewHolder mHolder = new MyViewHolder(view);
                return mHolder;
            case TYPE_OTHERS:
                view = mInflater.inflate(R.layout.item_recycle_others_message, null);
                OtherViewHolder oHolder = new OtherViewHolder(view);
                return oHolder;
            default:
                Logger.error(StaticClass.LOGGER_TAG,"ChatAdapter得到错误消息1");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_MY:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                myViewHolder.mTextView.setText(mList.get(position).getContent());
                myViewHolder.mImageView.setImageResource(Integer.parseInt(
                        User.getCurrentUser().getAvatar()));
                break;
            case TYPE_OTHERS:
                OtherViewHolder otherViewHolder = (OtherViewHolder) holder;
                otherViewHolder.mTextView.setText(mList.get(position).getContent());
                otherViewHolder.mImageView.setImageResource(Integer.parseInt(userInfo.getAvatar()));
                break;
            case -1:
                Logger.error(StaticClass.LOGGER_TAG,"ChatAdapter得到错误消息2");
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private CircleImageView mImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_content);
            mImageView = itemView.findViewById(R.id.iv_icon);
        }
    }

    class OtherViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private CircleImageView mImageView;

        public OtherViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_content);
            mImageView = itemView.findViewById(R.id.iv_icon);
        }
    }
}
