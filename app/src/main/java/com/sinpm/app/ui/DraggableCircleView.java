package com.sinpm.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class DraggableCircleView extends View {

    private Paint paint;
    private float radiusPx; // 圆点半径，单位为px
    private float centerX;
    private float centerY;
    private float dX, dY;

    public DraggableCircleView(Context context) {
        super(context);
        init(context, null, 25, 50, 50); // 默认半径为25dp，初始坐标为(50, 50)
    }

    public DraggableCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 25, 50, 50); // 默认半径为25dp，初始坐标为(50, 50)
    }

    public DraggableCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, 25, 50, 50); // 默认半径为25dp，初始坐标为(50, 50)
    }

    public DraggableCircleView(Context context, AttributeSet attrs, float radiusDp, float initialX, float initialY) {
        super(context, attrs);
        init(context, attrs, radiusDp, initialX, initialY);
    }

    private void init(Context context, AttributeSet attrs, float radiusDp, float initialX, float initialY) {
        radiusPx = dpToPx(context, radiusDp); // 将半径设置为dp值对应的px

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAlpha(128); // 设置透明度，范围0-255
        paint.setAntiAlias(true);

        centerX = dpToPx(context, initialX); // 初始X坐标，转换为px
        centerY = dpToPx(context, initialY); // 初始Y坐标，转换为px
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, radiusPx, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getRawX();
        float eventY = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = eventX - centerX;
                dY = eventY - centerY;
                return true;
            case MotionEvent.ACTION_MOVE:
                centerX = eventX - dX;
                centerY = eventY - dY;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public void setRadius(float radiusDp) {
        radiusPx = dpToPx(getContext(), radiusDp);
        invalidate();
    }

    public void setCenter(float xDp, float yDp) {
        centerX = dpToPx(getContext(), xDp);
        centerY = dpToPx(getContext(), yDp);
        invalidate();
    }
}
