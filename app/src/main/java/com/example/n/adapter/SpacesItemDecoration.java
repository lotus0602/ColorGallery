package com.example.n.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by N on 2016-06-22.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int mSpace;

    public SpacesItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;

        if (parent.getChildAdapterPosition(view) <= 1) {
            outRect.top = mSpace;
        }
    }
}
