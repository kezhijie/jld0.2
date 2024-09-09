package com.sinpm.app.Utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.sinpm.app.R;

public class FlipAnimationHelper {

    private final LinearLayout layout1;
    private final LinearLayout layout2;
    private boolean isShowingLayout1 = true;

    private final Animation flipIn;
    private Animation flipOut;

    public FlipAnimationHelper(Context context, LinearLayout layout1, LinearLayout layout2) {
        this.layout1 = layout1;
        this.layout2 = layout2;

        // 初始化动画
        flipIn = AnimationUtils.loadAnimation(context, com.xuexiang.xupdate.R.anim.xupdate_app_window_in);
//        flipOut = AnimationUtils.loadAnimation(context, R.anim.flip_out);

        // 设置动画监听器
        flipIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 动画结束时切换 LinearLayout 的可见性
                layout1.setVisibility(View.INVISIBLE);
                layout2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        flipOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 动画结束时切换 LinearLayout 的可见性
                layout2.setVisibility(View.INVISIBLE);
                layout1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // 设置点击监听器
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flip();
            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flip();
            }
        });
    }

    private void flip() {
        if (isShowingLayout1) {
            layout1.startAnimation(flipOut);
        } else {
            layout2.startAnimation(flipIn);
        }
        isShowingLayout1 = !isShowingLayout1;
    }
}
