package com.example.user.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.adapter
 * User:           ${User}
 * Date&Time:      2018/4/23 22:28
 * Description:    TODO
 **/

public abstract class AbstractRecycleAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>{
    protected Context mContext;
    protected List mList;
    protected LayoutInflater mInflater;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        public void onClick(View view,int position);
    }

    public AbstractRecycleAdapter(Context context,List itemList){
        mContext=context;
        mList=itemList;
        mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener=listener;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
