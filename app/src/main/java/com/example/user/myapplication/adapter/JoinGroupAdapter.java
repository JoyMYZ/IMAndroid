package com.example.user.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.myapplication.R;
import com.example.user.myapplication.entity.Group;

import java.util.List;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.adapter
 * User:           ${User}
 * Date&Time:      2018/4/26 17:45
 * Description:    TODO
 **/

public class JoinGroupAdapter extends RecyclerView.Adapter<JoinGroupAdapter.MyViewHolder>{
    private List<Group> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        public void onItemClick(View view,int position);
    }

    public JoinGroupAdapter(Context context,List<Group> groupList,OnItemClickListener listener){
        this.mContext=context;
        this.mList=groupList;
        this.mListener=listener;
        mLayoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=mLayoutInflater.inflate(R.layout.item_recycle_join_group,null);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //TODO:holder.avatar.setImageResource()
        holder.groupName.setText(mList.get(position).getGroupName());
        holder.join.setOnClickListener(new View.OnClickListener() {
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
        private ImageView avatar;
        private Button join;
        private TextView groupName;


        public MyViewHolder(View itemView) {
            super(itemView);
            avatar=itemView.findViewById(R.id.iv_avatar);
            groupName=itemView.findViewById(R.id.tv_groupname);
            join=itemView.findViewById(R.id.btn_join);
        }
    }
}
