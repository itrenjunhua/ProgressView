package com.renj.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;

import com.renj.progress.utils.DimensionUtils;
import com.renj.progress.utils.NumberUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2019-09-15   0:54
 * <p>
 * 描述：圆形进度条效果。<b>注意：该控件每次设置进度后都会重新开始绘制，
 * 如果需要直接在上一次进度上进行累加，请使用 {@link CircleProgressBar} 控件</b>
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class CircleProgressView extends View {

    // 默认数据常量
    private final int DEFAULT_VIEW_WIDTH = DimensionUtils.dp2px(getContext(), 200);
    private final int DEFAULT_VIEW_HEIGHT = DimensionUtils.dp2px(getContext(), 200);
    private final int PROGRESS_TEXT_NONE = 0; // 不显示当前值
    private final int PROGRESS_TEXT_DECIMAL = 1; // 小数点形式显示
    private final int PROGRESS_TEXT_PERCENTAGE = 2; // 百分比形式显示，默认

    private final int DEFAULT_MAX_PROGRESS = 100; // 默认最大进度
    // 默认颜色和大小
    private final int DEFAULT_BG_COLOR = Color.GRAY;
    private final int DEFAULT_FULL_COLOR = Color.RED;
    private final int DEFAULT_TEXT_COLOR = Color.RED;
    private final float DEFAULT_CIRCLE_RING_WIDTH = DimensionUtils.dp2px(getContext(), 12);
    private final float DEFAULT_TEXT_SIZE = DimensionUtils.sp2px(getContext(), 15);

    // 进度开始位置集合
    private static final ArrayMap<Integer, Integer> mStartPositionMap = new ArrayMap<>(8);

    static {
        mStartPositionMap.put(0, -90);
        mStartPositionMap.put(1, -45);
        mStartPositionMap.put(2, 0);
        mStartPositionMap.put(3, 45);
        mStartPositionMap.put(4, 90);
        mStartPositionMap.put(5, 135);
        mStartPositionMap.put(6, 180);
        mStartPositionMap.put(7, 225);
    }

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
    // 值相关
    private int mTotalProgress;
    private int mCurrentProgress;
    private float mResultProgress;
    private float drawAnimatedFraction;
    // 当前结果显示形式 0：不显示  1：小数形式 2：百分比 0：不显示  1：小数形式 2：百分比
    private int mProgressTextType = PROGRESS_TEXT_PERCENTAGE;
    // 进度开始位置
    private int mProgressStartPositionValue = mStartPositionMap.get(0);

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);

        mBgColor = typedArray.getColor(R.styleable.CircleProgressView_circle_pv_bg_color, DEFAULT_BG_COLOR);
        mFullColor = typedArray.getColor(R.styleable.CircleProgressView_circle_pv_color, DEFAULT_FULL_COLOR);
        mTextColor = typedArray.getColor(R.styleable.CircleProgressView_circle_pv_text_color, DEFAULT_TEXT_COLOR);
        mTextSize = typedArray.getDimension(R.styleable.CircleProgressView_circle_pv_text_size, DEFAULT_TEXT_SIZE);
        mRingWidth = typedArray.getDimension(R.styleable.CircleProgressView_circle_pv_width, DEFAULT_CIRCLE_RING_WIDTH);

        mTotalProgress = typedArray.getInteger(R.styleable.CircleProgressView_circle_pv_total, DEFAULT_MAX_PROGRESS);
        mCurrentProgress = typedArray.getInteger(R.styleable.CircleProgressView_circle_pv_current, 0);

        mProgressTextType = typedArray.getInt(R.styleable.CircleProgressView_circle_pv_progress_text_type, PROGRESS_TEXT_PERCENTAGE);
        mProgressStartPositionValue = mStartPositionMap.get(typedArray.getInt(R.styleable.CircleProgressView_circle_pv_start_point, 0));

        typedArray.recycle();
    }

    private void initPaint() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeWidth(mRingWidth);
        mBgPaint.setColor(mBgColor);

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

        if (mResultProgress != 0)
            startAnimationDraw();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 画背景
        canvas.drawArc(mOffset, mOffset, mWidth - mOffset, mHeight - mOffset,
                0, 360, false, mBgPaint);

        // 画进度
        canvas.drawArc(mOffset, mOffset, mWidth - mOffset, mHeight - mOffset,
                mProgressStartPositionValue, mResultProgress * 360 * drawAnimatedFraction, false, mFullPaint);

        // 当前进度
        float currentProgressValue = mResultProgress * drawAnimatedFraction;
        if (mProgressTextType != PROGRESS_TEXT_NONE) {
            String currentValue = "";
            if (mProgressTextType == PROGRESS_TEXT_DECIMAL) {
                currentValue = NumberUtils.decimalFloat(currentProgressValue);
            } else if (mProgressTextType == PROGRESS_TEXT_PERCENTAGE) {
                currentValue = NumberUtils.decimalFloat(currentProgressValue * 100) + " %";
            }
            float measureText = mTextPaint.measureText(currentValue);
            canvas.drawText(currentValue, (mWidth - measureText) / 2, (mHeight + mTextSize) / 2, mTextPaint);
        }

        if (onProgressChangeListener != null) {
            onProgressChangeListener.onProgressChange(this, currentProgressValue);
        }
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
    public CircleProgressView setValue(int totalProgress, int currentProgress) {
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
        void onProgressChange(@NonNull CircleProgressView circleProgressView, float currentValue);
    }
}
