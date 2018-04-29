package com.example.user.myapplication.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.user.myapplication.R;

import java.util.List;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.adapter
 * User:           ${User}
 * Date&Time:      2018/4/27 11:13
 * Description:    TODO
 **/

public class SelectIconAdapter extends BaseAdapter {
    private Context mContext;
    private List<Integer> mList;
    private LayoutInflater mInflater;

    public SelectIconAdapter(Context context, List<Integer> imageResIdList){
        mContext=context;
        mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList=imageResIdList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view=mInflater.inflate(R.layout.item_adapter_single_image,null);
        ImageView imageView=view.findViewById(R.id.iv_option);
        imageView.setImageResource(mList.get(position));
        return view;
    }
}
