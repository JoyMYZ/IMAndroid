package com.example.user.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.myapplication.R;
import com.example.user.myapplication.entity.Group;

import java.util.List;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.adapter
 * User:           ${User}
 * Date&Time:      2018/4/25 23:32
 * Description:    TODO
 **/

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {
    private List<Group> mList;
    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        public void onItemClick(View view,int position);
    }

    public GroupAdapter(List<Group> list, Context context,OnItemClickListener listener ) {
        this.mList = list;
        this.mContext = context;
        this.mListener=listener;
        this.mInflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.item_recycle_group,null);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //TODO:设置群头像
        holder.mTextView.setText(mList.get(position).getGroupName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView mImaView;
        private TextView mTextView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mImaView=itemView.findViewById(R.id.iv_avatar);
            mTextView=itemView.findViewById(R.id.tv_groupname);
        }
    }
}
