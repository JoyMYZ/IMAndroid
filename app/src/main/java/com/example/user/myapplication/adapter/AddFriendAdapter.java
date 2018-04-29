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
import com.example.user.myapplication.entity.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.adapter
 * User:           ${User}
 * Date&Time:      2018/4/20 21:39
 * Description:    TODO
 **/

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.MyViewHolder> {
    private Context mContext;
    private List<User> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener onItemClickListener;

    public AddFriendAdapter(Context context, List itemList) {
        mContext=context;
        mList=itemList;
        mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public interface OnItemClickListener {
        void onClick(View v, int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_recycle_add_friend, null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.avatar.setImageResource(Integer.valueOf(mList.get(position).getAvatar()));
        holder.username.setText(mList.get(position).getUsername());
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatar;
        private TextView username;
        private Button add;

        public MyViewHolder(View itemView) {
            super(itemView);
            avatar=itemView.findViewById(R.id.iv_icon);
            username=itemView.findViewById(R.id.tv_username);
            add=itemView.findViewById(R.id.btn_add);
        }
    }
}
