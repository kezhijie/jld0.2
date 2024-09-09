package com.sinpm.app.Utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildLayoutPosition(view)%3==0){
            outRect.left = 0;
        }else if (parent.getChildLayoutPosition(view)%3==1){
            outRect.left = space;
        }else {
            outRect.left = space*2;
        }
    }
}
