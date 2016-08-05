package com.buslink.busjie.driver.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2015/10/8.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int top, leftRight;

    public SpaceItemDecoration(int top, int leftRight) {
        this.top = top;
        this.leftRight = leftRight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = leftRight;
        outRect.right = leftRight;
        outRect.top = top;
    }
}
