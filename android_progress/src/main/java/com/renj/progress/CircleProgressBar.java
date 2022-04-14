package com.renj.progress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.renj.progress.utils.NumberUtils;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2021-10-21   22:07
 * <p>
 * 描述：圆形进度条
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class CircleProgressBar extends View {
    public static final int PROGRESS_TEXT_NONE = 0; // 不显示当前值
    public static final int PROGRESS_TEXT_DECIMAL = 1; // 小数点形式显示
    public static final int PROGRESS_TEXT_PERCENTAGE = 2; // 百分比形式显示，默认

    public static final int PROGRESS_BAR_STROKE = 0; // 空心
    public static final int PROGRESS_BAR_FILL = 1; // 填充

    /**
     * 画笔对象的引用
     */
    private final Paint paint;

    /**
     * 圆环的宽度
     */
    private float circlePbWidth;

    /**
     * 圆环的颜色
     */
    private int circlePbColor;

    /**
     * 圆环进度的颜色
     */
    private int circlePbProgressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的文字大小
     */
    private float textSize;

    /**
     * 最大进度
     */
    private int maxProgress;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 中间进度文字样式
     */
    private int progressTextType;
    /**
     * 进度条样式
     */
    private int progressBarType;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();

        @SuppressLint("CustomViewStyleable")
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressbar);

        //获取自定义属性和默认值
        circlePbColor = mTypedArray.getColor(R.styleable.CircleProgressbar_circle_pb_color, Color.RED);
        circlePbProgressColor = mTypedArray.getColor(R.styleable.CircleProgressbar_circle_pb_progress_color, Color.GREEN);
        textColor = mTypedArray.getColor(R.styleable.CircleProgressbar_circle_pb_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(R.styleable.CircleProgressbar_circle_pb_textSize, 15);
        circlePbWidth = mTypedArray.getDimension(R.styleable.CircleProgressbar_circle_pb_width, 5);
        maxProgress = mTypedArray.getInteger(R.styleable.CircleProgressbar_circle_pb_max, 100);
        progress = mTypedArray.getInteger(R.styleable.CircleProgressbar_circle_pb_progress, 0);
        progressBarType = mTypedArray.getInteger(R.styleable.CircleProgressbar_circle_pb_progress_bar_type, PROGRESS_BAR_STROKE);
        progressTextType = mTypedArray.getInteger(R.styleable.CircleProgressbar_circle_pb_progress_text_type, PROGRESS_TEXT_NONE);

        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*
         * 画最外层的大圆环
         */
        int centreX = getWidth() / 2; // 获取圆心的x坐标
        int centreY = getHeight() / 2; // 获取圆心的x坐标
        int radius = (int) (Math.min(getWidth(), getHeight()) / 2 - circlePbWidth); // 圆环的半径
        paint.setColor(circlePbColor); // 设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); // 设置空心
        paint.setStrokeWidth(circlePbWidth); // 设置圆环的宽度
        paint.setAntiAlias(false);  // 消除锯齿
        canvas.drawCircle(centreX, centreY, radius, paint); // 画出圆环

        /*
         * 画圆弧 ，画圆环的进度
         */

        // 设置进度是实心还是空心
        paint.setStrokeWidth(circlePbWidth); // 设置圆环的宽度
        paint.setColor(circlePbProgressColor);  // 设置进度的颜色

        /*
         * 可以绘制不同效果的圆弧
         */
        if (progressBarType == PROGRESS_BAR_STROKE) {
            paint.setStyle(Paint.Style.STROKE);
            @SuppressLint("DrawAllocation")
            RectF oval = new RectF(centreX - radius, centreY - radius,
                    centreX + radius, centreY + radius);
            // 根据进度画圆弧
            canvas.drawArc(oval, 0, 360f * progress / maxProgress, false, paint);
        } else {
            paint.setStyle(Paint.Style.FILL);
            @SuppressLint("DrawAllocation")
            RectF oval = new RectF(centreX - radius + circlePbWidth / 2, centreY - radius + circlePbWidth / 2,
                    centreX + radius - circlePbWidth / 2, centreY + radius - circlePbWidth / 2);
            // 根据进度画圆弧
            if (progress != 0)
                canvas.drawArc(oval, 0, 360f * progress / maxProgress, true, paint);
        }

        /*
         * 可以绘制不同效果的中间进度文字
         */
        if (progressTextType == PROGRESS_TEXT_DECIMAL) {
            paint.setStrokeWidth(0);
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置字体
            String percent = NumberUtils.decimalFloat(progress * 1.0f / maxProgress);
            // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
            float textWidth = paint.measureText(percent);
            // 画出进度百分比
            canvas.drawText(percent, centreX - textWidth / 2, centreY + textSize / 2, paint);
        } else if (progressTextType == PROGRESS_TEXT_PERCENTAGE) {
            paint.setStrokeWidth(0);
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置字体
            // 中间的进度百分比，先转换成float在进行除法运算，不然都为0
            float percentValue = progress * 1.0f / maxProgress;
            String percent = NumberUtils.decimalFloat(percentValue * 100) + "%";
            // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
            float textWidth = paint.measureText(percent);
            // 画出进度百分比
            canvas.drawText(percent, centreX - textWidth / 2, centreY + textSize / 2, paint);
        }
    }

    /**
     * 获取最大进度
     *
     * @return 最大进度
     */
    public int getMaxProgress() {
        return maxProgress;
    }

    /**
     * 设置进度的最大值
     *
     * @param maxProgress 最大进度
     */
    public void setMaxProgress(int maxProgress) {
        if (maxProgress < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.maxProgress = maxProgress;
    }

    /**
     * 获取进度.需要同步
     *
     * @return 当前进度
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress 当前进度
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > maxProgress) {
            progress = maxProgress;
        }
        this.progress = progress;
        postInvalidate();
    }

    /**
     * 获取圆环的宽度
     *
     * @return 圆环的宽度
     */
    public float getCirclePbWidth() {
        return circlePbWidth;
    }

    /**
     * 设置圆环的宽度
     *
     * @param circlePbWidth 圆环的宽度
     */
    public void setCirclePbWidth(float circlePbWidth) {
        this.circlePbWidth = circlePbWidth;
    }

    /**
     * 获取圆环的颜色
     *
     * @return 圆环的颜色
     */
    public int getCirclePbColor() {
        return circlePbColor;
    }

    /**
     * 设置圆环的颜色
     *
     * @param circlePbColor 圆环的颜色
     */
    public void setCirclePbColor(int circlePbColor) {
        this.circlePbColor = circlePbColor;
    }

    /**
     * 获取圆环进度的颜色
     *
     * @return 圆环进度的颜色
     */
    public int getCirclePbProgressColor() {
        return circlePbProgressColor;
    }

    /**
     * 设置圆环进度的颜色
     *
     * @param circlePbProgressColor 圆环进度的颜色
     */
    public void setCirclePbProgressColor(int circlePbProgressColor) {
        this.circlePbProgressColor = circlePbProgressColor;
    }

    /**
     * 获取中间进度百分比的字符串的颜色
     *
     * @return 中间进度百分比的字符串的颜色
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * 设置中间进度百分比的字符串的颜色
     *
     * @param textColor 中间进度百分比的字符串的颜色
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * 获取中间进度百分比的字符串的文字大小
     *
     * @return 中间进度百分比的字符串的文字大小
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * 设置中间进度百分比的字符串的文字大小
     *
     * @param textSize 中间进度百分比的字符串的文字大小
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    /**
     * 获取中间进度文字样式
     *
     * @return 中间进度文字样式  {@link #PROGRESS_TEXT_NONE}、{@link #PROGRESS_TEXT_DECIMAL}、{@link #PROGRESS_TEXT_PERCENTAGE}
     */
    public int getProgressTextType() {
        return progressTextType;
    }

    /**
     * 设置中间进度文字样式
     *
     * @param progressTextType 中间进度文字样式 {@link #PROGRESS_TEXT_NONE}、{@link #PROGRESS_TEXT_DECIMAL}、{@link #PROGRESS_TEXT_PERCENTAGE}
     */
    public void setProgressTextType(int progressTextType) {
        this.progressTextType = progressTextType;
    }

    /**
     * 获取进度条样式
     *
     * @return 进度条样式  {@link #PROGRESS_BAR_STROKE}、{@link #PROGRESS_BAR_FILL}
     */
    public int getProgressBarType() {
        return progressBarType;
    }

    /**
     * 设置进度条样式
     *
     * @param progressBarType 进度条样式  {@link #PROGRESS_BAR_STROKE}、{@link #PROGRESS_BAR_FILL}
     */
    public void setProgressBarType(int progressBarType) {
        this.progressBarType = progressBarType;
    }
}
