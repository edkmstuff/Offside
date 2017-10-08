package com.offsidegame.offside.helpers;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by user on 10/3/2017.
 */

public class VerticalSpace extends RecyclerView.ItemDecoration {

    int space;

    public VerticalSpace(int space) {
        this.space = space;
    }



    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left =space;
        outRect.bottom =space;
        outRect.right =space;
        if(parent.getChildLayoutPosition(view)==0)
            outRect.top= space;


    }
}
