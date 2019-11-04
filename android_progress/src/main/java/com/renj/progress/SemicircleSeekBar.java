package com.renj.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.renj.progress.utils.DimensionUtils;
import com.renj.progress.utils.NumberUtils;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * 邮箱：itrenjunhua@163.com
 * <p>
 * 创建时间：2019-10-21   11:37
 * <p>
 * 描述：半圆形拖动控件
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class SemicircleSeekBar extends View {
    // 默认数据常量
    private final int DEFAULT_VIEW_WIDTH = DimensionUtils.dp2px(getContext(), 240);
    private final int DEFAULT_VIEW_HEIGHT = DimensionUtils.dp2px(getContext(), 160);

    private final int SHOW_TYPE_NONE = 0; // 不显示当前值
    private final int SHOW_TYPE_DECIMAL = 1; // 小数点形式显示
    private final int SHOW_TYPE_PERCENTAGE = 2; // 百分比形式显示，默认
    private final int DEFAULT_MAX_PROGRESS = 100; // 默认最大进度
    // 默认颜色和大小
    private int DEFAULT_BG_COLOR = Color.GRAY;
    private int DEFAULT_FULL_COLOR = Color.RED;
    private int DEFAULT_TEXT_COLOR = Color.BLACK;
    private int DEFAULT_CURRENT_TEXT_COLOR = Color.RED;
    private float DEFAULT_CIRCLE_RING_WIDTH = DimensionUtils.dp2px(getContext(), 12);
    private float DEFAULT_TEXT_SIZE = DimensionUtils.sp2px(getContext(), 15);
    private float DEFAULT_CURRENT_TEXT_SIZE = DimensionUtils.sp2px(getContext(), 18);

    // 默认控件的宽和高
    private int mWidth = DEFAULT_VIEW_WIDTH;
    private int mHeight = DEFAULT_VIEW_HEIGHT;

    // 背景画笔
    private Paint mBgPaint;
    private int mBgColor = DEFAULT_BG_COLOR;
    // 填充画笔
    private Paint mFullPaint;
    private int mFullColor = DEFAULT_FULL_COLOR;
    // 文字画笔
    private Paint mTextPaint;
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private float mTextSize = DEFAULT_TEXT_SIZE;
    private int mTextCurrentColor = DEFAULT_CURRENT_TEXT_COLOR;
    private float mTextCurrentSize = DEFAULT_CURRENT_TEXT_SIZE;
    // 圆环厚度
    private float mRingWidth = DEFAULT_CIRCLE_RING_WIDTH;
    private float mOffset = mRingWidth / 2;
    private ValueAnimator mValueAnimator;
    // 值相关
    private int mTotalProgress = DEFAULT_MAX_PROGRESS;
    private int mCurrentProgress = 0;
    private float mResultProgress;
    private float drawAnimatedFraction;
    // 当前结果显示形式 0：不显示  1：小数形式 2：百分比 0：不显示  1：小数形式 2：百分比
    private int mShowType = SHOW_TYPE_PERCENTAGE;

    public SemicircleSeekBar(Context context) {
        this(context, null);
    }

    public SemicircleSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SemicircleSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public SemicircleSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SemicircleSeekBar);

        mBgColor = typedArray.getColor(R.styleable.SemicircleSeekBar_semicircle_sb_bg_color, DEFAULT_BG_COLOR);
        mFullColor = typedArray.getColor(R.styleable.SemicircleSeekBar_semicircle_sb_color, DEFAULT_FULL_COLOR);
        mTextColor = typedArray.getColor(R.styleable.SemicircleSeekBar_semicircle_sb_text_color, DEFAULT_TEXT_COLOR);
        mTextSize = typedArray.getDimension(R.styleable.SemicircleSeekBar_semicircle_sb_text_size, DEFAULT_TEXT_SIZE);
        mTextCurrentColor = typedArray.getColor(R.styleable.SemicircleSeekBar_semicircle_sb_current_text_color, DEFAULT_CURRENT_TEXT_COLOR);
        mTextCurrentSize = typedArray.getDimension(R.styleable.SemicircleSeekBar_semicircle_sb_current_text_size, DEFAULT_CURRENT_TEXT_SIZE);
        mRingWidth = typedArray.getDimension(R.styleable.SemicircleSeekBar_semicircle_sb_width, DEFAULT_CIRCLE_RING_WIDTH);

        mTotalProgress = typedArray.getInteger(R.styleable.SemicircleSeekBar_semicircle_sb_total, DEFAULT_MAX_PROGRESS);
        mCurrentProgress = typedArray.getInteger(R.styleable.SemicircleSeekBar_semicircle_sb_current, 0);

        mShowType = typedArray.getInt(R.styleable.SemicircleSeekBar_semicircle_sb_show_type, SHOW_TYPE_PERCENTAGE);

        typedArray.recycle();
    }

    private void initPaint() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeWidth(mRingWidth);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setStrokeCap(Paint.Cap.ROUND);

        mFullPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFullPaint.setStyle(Paint.Style.STROKE);
        mFullPaint.setStrokeWidth(mRingWidth);
        mFullPaint.setColor(mFullColor);
        mFullPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        mWidth = w;
        mHeight = h;

        initData();
    }

    private void initData() {
        if (mTotalProgress <= 0)
            throw new IllegalArgumentException("最大进度必须大于0.");
        if (mCurrentProgress < 0 || mCurrentProgress > mTotalProgress)
            throw new IllegalArgumentException("当前进度必须大于等于0并且小于等于最大进度.");

        drawAnimatedFraction = 0;
        mOffset = mRingWidth / 2;
        mResultProgress = mCurrentProgress * 1.0f / mTotalProgress;

        x = (int) (mOffset + DimensionUtils.dp2px(getContext(), 10));
        y = mWidth / 2;

        if (mResultProgress != 0)
            startAnimationDraw();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_VIEW_WIDTH, DEFAULT_VIEW_HEIGHT);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_VIEW_WIDTH, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, DEFAULT_VIEW_HEIGHT);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private int x, y;

    @Override
    protected void onDraw(Canvas canvas) {
        // 环形四周间距,有文字时修改文字与环形的对齐方式
        int innerMargin = DimensionUtils.dp2px(getContext(), 10);
        float leftAndTop = mOffset + innerMargin;
        float rightAndBottom = mWidth - mOffset - innerMargin;
        // 画背景
        canvas.drawArc(leftAndTop, leftAndTop, rightAndBottom, rightAndBottom,
                180, 180, false, mBgPaint);

        // 画最小值和最大值
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        float measureMinText = mTextPaint.measureText(0 + "");
        float measureMaxText = mTextPaint.measureText(mTotalProgress + "");
        float textY = mWidth / 2 + (mOffset + innerMargin) + DimensionUtils.dp2px(getContext(), 6);
        canvas.drawText(0 + "", innerMargin + mOffset / 2 - measureMinText / 2, textY, mTextPaint);
        canvas.drawText(mTotalProgress + "", mWidth - measureMaxText - (innerMargin + mOffset) / 2, textY, mTextPaint);

        // 画进度
        canvas.drawArc(leftAndTop, leftAndTop, rightAndBottom, rightAndBottom,
                180, drawAnimatedFraction * mResultProgress * 180, false, mFullPaint);

        // 画滑动图标
        canvas.drawCircle(x, y - mOffset, mOffset, mFullPaint);

        // 画当前进度文字
        float currentProgressValue = mResultProgress * drawAnimatedFraction;
        if (mShowType != SHOW_TYPE_NONE) {
            String currentValue = "";
            if (mShowType == SHOW_TYPE_DECIMAL) {
                currentValue = NumberUtils.decimalFloat(currentProgressValue);
            } else if (mShowType == SHOW_TYPE_PERCENTAGE) {
                currentValue = NumberUtils.decimalFloat(currentProgressValue * 100) + " %";
            }
            mTextPaint.setColor(mTextCurrentColor);
            mTextPaint.setTextSize(mTextCurrentSize);
            float measureText = mTextPaint.measureText(currentValue);
            canvas.drawText(currentValue, (mWidth - measureText) / 2, mWidth / 4 + (mOffset + innerMargin) * 2, mTextPaint);
        }

        if (onProgressChangeListener != null) {
            onProgressChangeListener.onProgressChange(this, currentProgressValue);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 环形四周间距,有文字时修改文字与环形的对齐方式
        int innerMargin = DimensionUtils.dp2px(getContext(), 10);
        float leftAndTop = mOffset + innerMargin;
        float rightAndBottom = mWidth - mOffset - innerMargin;
        Path pathOut = new Path();
        pathOut.addArc(innerMargin - 20, innerMargin - 20, mWidth - innerMargin + 20, mWidth - innerMargin + 20, 180, 180);

        RectF boundsOut = new RectF();
        pathOut.computeBounds(boundsOut, true);
        Region regionOut = new Region();
        regionOut.setPath(pathOut, new Region((int) boundsOut.left, (int) boundsOut.top, (int) boundsOut.right, (int) boundsOut.bottom));

        Path pathInner = new Path();
        pathInner.addArc(leftAndTop + 20, leftAndTop + 20, rightAndBottom - 20, rightAndBottom - 20, 180, 180);
        RectF boundsInner = new RectF();
        pathInner.computeBounds(boundsInner, true);
        Region regionInner = new Region();
        regionInner.setPath(pathInner, new Region((int) boundsInner.left, (int) boundsInner.top, (int) boundsInner.right, (int) boundsInner.bottom));

        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean containsOut = regionOut.contains(x, y);
        boolean containsInner = regionInner.contains(x, y);

        if (containsOut && !containsInner) {
            this.x = x;
            this.y = y;
            invalidate();
        }
        return true;
    }

    private void startAnimationDraw() {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            return;
        }
        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawAnimatedFraction = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mValueAnimator.setDuration((long) Math.max(1000, Math.min(mResultProgress * 360 * 10, 2000)));
        mValueAnimator.start();
    }

    /**************动态设置数据部分**************/
    public SemicircleSeekBar setValue(int totalProgress, int currentProgress) {
        this.mTotalProgress = totalProgress;
        this.mCurrentProgress = currentProgress;
        return this;
    }

    /**
     * 使生效，所有设置的数据最后需要调用该方法才能使数据生效
     */
    public void takeEffect() {
        initData();
    }

    /**************监听部分**************/
    private OnProgressChangeListener onProgressChangeListener;

    public void setOnProgressChangeListener(OnProgressChangeListener onProgressChangeListener) {
        this.onProgressChangeListener = onProgressChangeListener;
    }

    public interface OnProgressChangeListener {
        void onProgressChange(@NonNull SemicircleSeekBar semicircleProgressView, float currentValue);
    }
}
