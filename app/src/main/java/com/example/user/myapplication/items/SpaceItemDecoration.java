package com.example.user.myapplication.items;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.items
 * User:           ${User}
 * Date&Time:      2018/4/25 22:09
 * Description:    TODO
 **/

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int spaceNum;
    public SpaceItemDecoration(int space) {
        this.spaceNum=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left=spaceNum;
        outRect.right=spaceNum;
        outRect.bottom=spaceNum;
        //RecycleView的第一个View会有上行距
        if(parent.getChildAdapterPosition(view)==0){
            outRect.top=spaceNum;
        }
    }
}
