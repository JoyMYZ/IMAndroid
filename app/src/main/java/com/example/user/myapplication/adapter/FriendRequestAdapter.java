package com.example.user.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.myapplication.R;
import com.example.user.myapplication.entity.NewFriendRequest;
import com.example.user.myapplication.entity.NewFriendRequest.State;

import java.util.List;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.adapter
 * User:           ${User}
 * Date&Time:      2018/4/22 22:34
 * Description:    TODO
 **/

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder> {
    private List<NewFriendRequest> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    private OnItemClickListener onItemClickListener;

    public FriendRequestAdapter(Context context,List<NewFriendRequest> friendRequestList,
                                OnItemClickListener onItemClickListener){
        mList=friendRequestList;
        mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext=context;
        this.onItemClickListener=onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(View view,int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView username;
        private TextView content;
        private ImageView avatar;
        private Button accept,refuse;
        public MyViewHolder(View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.tv_username);
            content=itemView.findViewById(R.id.tv_content);
            avatar=itemView.findViewById(R.id.iv_avatar);
            accept=itemView.findViewById(R.id.btn_accept);
            refuse=itemView.findViewById(R.id.btn_refuse);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getStatus().ordinal();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //解析同一个布局
        View view=mInflater.inflate(R.layout.item_recycle_request_info,null);
        MyViewHolder holder=new MyViewHolder(view);
        Button accept=view.findViewById(R.id.btn_accept);
        Button refuse=view.findViewById(R.id.btn_refuse);

        switch (viewType){
            //状态：未读、已读、已添加、已拒绝等
            case 0:
            case 1:
                break;
            case 2:
                refuse.setVisibility(View.GONE);
                accept.setBackgroundColor(Color.GRAY);
                accept.setText("已添加");
                accept.setClickable(false);
                break;
            case 3:
                accept.setVisibility(View.GONE);
                refuse.setBackgroundColor(Color.GRAY);
                refuse.setText("已拒绝");
                refuse.setClickable(false);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //TODO:设置avatar
        holder.username.setText(mList.get(position).getName());
        holder.content.setText(mList.get(position).getMsg());
        holder.avatar.setImageResource(Integer.parseInt(mList.get(position).getAvatar()));
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v,position);
            }
        });

        holder.refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
