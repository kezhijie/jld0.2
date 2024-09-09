package com.sinpm.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ArcProgressView extends View {

    private float progress = 0f;
    private float max = 100f;
    private int progressColor = Color.BLUE;
    private int backgroundColor = Color.GRAY;
    private float strokeWidth;

    private Paint progressPaint;
    private Paint backgroundPaint;

    public ArcProgressView(Context context) {
        super(context);
        init(context, null);
    }

    public ArcProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ArcProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        strokeWidth = dpToPx(context, 20); // default stroke width in dp

        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setAntiAlias(true);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        float radius = size / 2f - strokeWidth / 2;

        // Draw background arc
        RectF oval = new RectF(strokeWidth / 2, strokeWidth / 2, size - strokeWidth / 2, size - strokeWidth / 2);
        canvas.drawArc(oval, 140, 260, false, backgroundPaint);

        // Draw progress arc
        float sweepAngle = 260f * progress / max;
        canvas.drawArc(oval, 140, sweepAngle, false, progressPaint);
    }

    public void setProgress(float progress) {
        if (progress < 0) {
            progress = 0;
        }
        if (progress > max) {
            progress = max;
        }
        if (this.progress != progress) { // Only invalidate if the progress is actually different
            this.progress = progress;
        }
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    public void setMax(float max) {
        if (max > 0) {
            this.max = max;
            if (progress > max) {
                progress = max;
            }
        }
        invalidate();
    }

    public float getMax() {
        return max;
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
        progressPaint.setColor(color);
        invalidate();
    }

    @Override
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        backgroundPaint.setColor(color);
        invalidate();
    }

    public void setStrokeWidth(float widthDp) {
        this.strokeWidth = dpToPx(getContext(), widthDp);
        progressPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setStrokeWidth(strokeWidth);
        invalidate();
    }

    private float dpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
