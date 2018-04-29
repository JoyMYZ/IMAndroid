package com.example.user.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.entity.User;

import java.util.List;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.adapter
 * User:           ${User}
 * Date&Time:      2018/4/21 17:24
 * Description:    TODO
 **/

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    private List<User> mList;
    private LayoutInflater inflater;
    private Context mContext;

    private onItemClickListener listener;

    public ContactsAdapter(Context context, List<User> userList, onItemClickListener listener){
        mContext=context;
        mList=userList;
        this.listener=listener;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public interface onItemClickListener{
        public void onClick(View view,int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_username;
        private ImageView iv_icon;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_username=itemView.findViewById(R.id.tv_username);
            iv_icon=itemView.findViewById(R.id.iv_icon);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.item_recycle_contacks,null);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        User user=mList.get(position);
        holder.tv_username.setText(user.getUsername());
        holder.iv_icon.setImageResource(Integer.parseInt(user.getAvatar()));
        //TODO:设置最近消息

        //为主View绑定监听事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
