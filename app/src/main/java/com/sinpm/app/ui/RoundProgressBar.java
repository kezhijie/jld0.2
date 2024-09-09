package com.sinpm.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.sinpm.app.R;

public class RoundProgressBar extends View {
    private Paint paint;//画笔
    private int roundColor;//底层圆环的颜色
    private int roundProgressColor;//圆环进度的颜色
    private float roundWidth;//圆环的宽度
    private int textColor;//圆环中心显示的文字的颜色
    private float textSize;//圆环中心显示的文字的大小
    private int max;//最大进度
    private int progress;//设置的需要显示的进度
    private boolean textIsDisplayable;//是否显示中间的进度
    private int style;//进度的风格，实心或者空心
    public static final int STROKE = 0;
    public static final int FILL = 1;
    private boolean animate;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        // 获取自定义属性和默认值
        roundColor = mTypedArray.getColor(
                R.styleable.RoundProgressBar_roundColor, Color.RED);//圆圈底色
        roundProgressColor = mTypedArray.getColor(
                R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);//圆圈的进度颜色
        textColor = mTypedArray.getColor(
                R.styleable.RoundProgressBar_textColor, Color.GREEN);//字体的颜色
        textSize = mTypedArray.getDimension(
                R.styleable.RoundProgressBar_textSize, 15);//字体的大小
        roundWidth = mTypedArray.getDimension(
                R.styleable.RoundProgressBar_roundWidth, 10);//圆圈的宽度
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);//一圈代表的最大值
        textIsDisplayable = mTypedArray.getBoolean(
                R.styleable.RoundProgressBar_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);//圆圈的样式0-STROKE  1-FILL

        /**
         * 回收TypedArray，以便后面重用。在调用这个函数后，你就不能再使用这个TypedArray。
         * 在TypedArray后调用recycle主要是为了缓存。当recycle被调用后，
         * 这就说明这个对象从现在可以被重用了。TypedArray 内部持有部分数组，
         * 它们缓存在Resources类中的静态字段中，这样就不用每次使用前都需要分配内存
         */
        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画最外层的大圆环
         */
        int centre = getWidth() / 2; // 获取圆心的x坐标
        int radius = (int) (centre - roundWidth); // 圆环的半径
        paint.setColor(roundColor); // 设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); // 设置空心
        paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
        paint.setAntiAlias(true); // 消除锯齿
        paint.setAlpha(180);
        RectF oval2 = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);//绘制弧线
        canvas.drawArc(oval2, 0, 360, false, paint); // 根据进度画圆弧。这里也可以直接画圆

        /**
         * 画圆弧 ，画圆环的进度
         */
        paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
        paint.setStrokeCap(Paint.Cap.ROUND);//设置画笔的始末端是圆角
        paint.setColor(roundProgressColor); // 设置进度的颜色
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius); // 用于定义的圆弧的形状和大小的界限
        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                drawArc(canvas, oval);
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                drawArc(canvas, oval);
                break;
            }
        }
        /**
         * 画进度百分比文字
         */
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(0);
        paint.setAlpha(255);
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.STROKE);//不加这个不显示
        paint.setStrokeWidth(2);

        paint.setTextSize(this.textSize);
        paint.setSubpixelText(true);
        paint.setTypeface(Typeface.DEFAULT); // 设置字体
        float textWidth = paint.measureText(progress + ""); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        if (textIsDisplayable && style == STROKE) {
            canvas.drawText(progress + "", centre - textWidth / 2, centre
                    + textSize / 2, paint); // 画出进度百分比
        }
    }

    private void drawArc(Canvas canvas, RectF oval) {
        if (!animate) {
            canvas.drawArc(oval, -90, goalDegree, false, paint);
            return;
        }
        if (curDegree <= goalDegree) {
            canvas.drawArc(oval, -90, curDegree, false, paint); // 根据进度画圆弧
            /**
             * 这里如果不想使用handler机制，
             * 也可以在这里调用Invalidate方法。
             * 因为Invalidate方法会导致onDraw方法的调用，
             * 也可以实现相同的效果
             */
            handler.sendEmptyMessage(88);
        } else {
            canvas.drawArc(oval, -90, goalDegree, false, paint); // 根据进度画圆弧
        }


    }

    private float goalDegree = 0;//设置的要显示的最终度数
    private float curDegree = 0;//当前显示的度数
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            curDegree += 1;
            postInvalidate();//这里可以直接使用invalidate();方法。因为是通过Handler发的消息在UI线程中
        }

        ;
    };

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress, boolean animate) {
        this.animate = animate;
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            goalDegree = 360 * progress / max;
            curDegree = 0;
            postInvalidate();
        }

    }

    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

    public boolean isTextIsDisplayable() {
        return textIsDisplayable;
    }

    public void setTextIsDisplayable(boolean textIsDisplayable) {
        this.textIsDisplayable = textIsDisplayable;
    }

}