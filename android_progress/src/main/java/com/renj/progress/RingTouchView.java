package com.renj.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.renj.progress.utils.DimensionUtils;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * 邮箱：itrenjunhua@163.com
 * <p>
 * 创建时间：2019-10-21   11:37
 * <p>
 * 描述：环形滑动进度控件
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class RingTouchView extends View {
    // 默认数据常量
    private final int DEFAULT_VIEW_WIDTH = DimensionUtils.dp2px(getContext(), 200);
    private final int DEFAULT_VIEW_HEIGHT = DimensionUtils.dp2px(getContext(), 100);

    private final int DEFAULT_MIN_VALUE = 0; // 默认最小值
    private final int DEFAULT_MAX_VALUE = 100; // 默认最大值
    private final boolean IS_SHOW_CURRENT = true; // 默认显示当前值
    // 默认颜色和大小
    private int DEFAULT_BG_COLOR = Color.GRAY;
    private int DEFAULT_FULL_COLOR = Color.RED;
    private int DEFAULT_TEXT_COLOR = Color.GRAY;
    private int DEFAULT_CURRENT_TEXT_COLOR = Color.RED;
    private float DEFAULT_CIRCLE_RING_WIDTH = DimensionUtils.dp2px(getContext(), 12);
    private float DEFAULT_TEXT_SIZE = DimensionUtils.sp2px(getContext(), 15);

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
    // 圆环厚度
    private float mRingWidth = DEFAULT_CIRCLE_RING_WIDTH;
    private float mOffset = mRingWidth / 2;
    private ValueAnimator mValueAnimator;
    // 数值
    private float mMinValue = DEFAULT_MIN_VALUE;
    private float mMaxValue = DEFAULT_MAX_VALUE;
    private float mCurrentValue = DEFAULT_MIN_VALUE;
    // 值相关
    private int mTotalProgress;
    private int mCurrentProgress;
    private float mResultProgress;
    private float drawAnimatedFraction;
    // 是否需要显示当前值
    private boolean mIsShowCurrent = IS_SHOW_CURRENT;

    public RingTouchView(Context context) {
        this(context, null);
    }

    public RingTouchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingTouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public RingTouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RingTouchView);
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

    @Override
    protected void onDraw(Canvas canvas) {
        // 画背景
        canvas.drawArc(mOffset, mOffset, mWidth - mOffset, mHeight - mOffset,
                180, 360, false, mBgPaint);

        // 画进度
        canvas.drawArc(mOffset, mOffset, mWidth - mOffset, mHeight - mOffset,
                180, 270, false, mFullPaint);
    }
}
