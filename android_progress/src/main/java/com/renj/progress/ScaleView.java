package com.renj.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * 邮箱：itrenjunhua@163.com
 * <p>
 * 创建时间：2019-08-30   14:46
 * <p>
 * 描述：
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class ScaleView extends View {
    private final int SMALL_SCALE_IN_SCALE_COUNT = 10;
    // 默认数据常量
    private final int DEFAULT_VIEW_WIDTH = dp2px(200);
    private final int DEFAULT_VIEW_HEIGHT = dp2px(80);

    private final int DEFAULT_COLOR = Color.BLACK; // 默认线和文字颜色
    private final int DEFAULT_MAIN_LINE_WIDTH = dp2px(1); // 默认主线宽度
    private final int DEFAULT_SCALE_LINE_WIDTH = DEFAULT_MAIN_LINE_WIDTH / 2; // 默认刻度线宽度
    private final int DEFAULT_SCALE_LINE_HEIGHT_LONG = dp2px(8); // 默认长刻度线高度
    private final int DEFAULT_SCALE_LINE_HEIGHT_SHORT = DEFAULT_SCALE_LINE_HEIGHT_LONG / 2; // 默认短刻度线高度
    private final int DEFAULT_SCALE_CELL_WIDTH = dp2px(50); // 默认每一个刻度单元格宽度

    private final int DEFAULT_SCALE_TEXT_SIZE = sp2px(13); // 默认刻度文字大小
    private final int DEFAULT_CURRENT_TEXT_SIZE = sp2px(15); // 默认显示当前刻度文字大小
    private final int DEFAULT_CURRENT_COLOR = Color.RED; // 默认当前刻度和文字颜色
    private final int DEFAULT_SCALE_TEXT_SPACE = dp2px(4); // 文字和刻度线之间的距离

    // 控件的宽和高
    private int mWidth = DEFAULT_VIEW_WIDTH;
    private int mHeight = DEFAULT_VIEW_HEIGHT;

    // 刻度线变量
    private Paint mLinePaint;
    private int mLineColor = DEFAULT_COLOR;
    private int mLineMainWidth = DEFAULT_MAIN_LINE_WIDTH;
    private int mLineScaleWidth = DEFAULT_SCALE_LINE_WIDTH;
    private int mLineScaleHeightLong = DEFAULT_SCALE_LINE_HEIGHT_LONG;
    private int mLineScaleHeightShort = DEFAULT_SCALE_LINE_HEIGHT_SHORT;
    private int mScaleCellWidth = DEFAULT_SCALE_CELL_WIDTH;
    private float mSmallScaleCellWidth = mScaleCellWidth / SMALL_SCALE_IN_SCALE_COUNT; // 每一个小刻度的宽度

    // 文字变量
    private Paint mTextPaint;
    private int mTextColor = DEFAULT_COLOR;
    private int mScaleTextSize = DEFAULT_SCALE_TEXT_SIZE;
    private int mCurrentTextSize = DEFAULT_CURRENT_TEXT_SIZE;
    private int mTextCurrentColor = DEFAULT_CURRENT_COLOR;

    // 文字和刻度线之间的距离
    private int mScaleTextSpace = DEFAULT_SCALE_TEXT_SPACE;

    // 数据和单位
    private String unit = "cm";
    private int startValue = 120; // 开始值
    private int endValue = 250; // 结束值
    private int stepLengthValue = 10; // 步长
    private int currentValue = 160;
    private List<Integer> scaleCellList = new ArrayList<>();

    // 手势识别器
    private GestureDetector gestureDetector = new GestureDetector(getContext(),
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    // 正在滑动

                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    // 快速滑动结束
                    scrollBy((int) velocityX, 0);
                    return true;
                }
            });

    public ScaleView(Context context) {
        this(context, null);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * 初始化数据
     */
    private void init(Context context, AttributeSet attrs) {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setDither(true);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineMainWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setDither(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mScaleTextSize);
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

    /**
     * 确定数据
     */
    private void initData() {
        paramsCheck();

        for (int i = startValue; i <= endValue; ) {
            scaleCellList.add(i * SMALL_SCALE_IN_SCALE_COUNT);
            i += stepLengthValue;
        }
        currentValue = currentValue * SMALL_SCALE_IN_SCALE_COUNT;
    }

    // 参数检验
    private void paramsCheck() {
        if (stepLengthValue <= 0)
            throw new IllegalArgumentException("步长(stepLengthValue)必须为大于0的整数.");
        if (currentValue < startValue || currentValue > endValue)
            throw new IllegalArgumentException("currentValue 范围应该在 startValue 和 endValue 之间.");
        if (currentValue % stepLengthValue != 0)
            throw new IllegalStateException("当前值(currentValue)应该为步长(stepLengthValue)的整数倍.");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawScaleLineAndText(canvas);
        drawCurrentData(canvas);
    }

    /**
     * 画当前数据文字
     */
    private void drawCurrentData(Canvas canvas) {
        mTextPaint.setTextSize(mCurrentTextSize);
        mTextPaint.setColor(mTextCurrentColor);
        String text = currentValue * 1.0f / SMALL_SCALE_IN_SCALE_COUNT + "";
        float textWidth = mTextPaint.measureText(text);
        canvas.drawText(text, mWidth / 2 - textWidth / 2, mHeight / 2 - mScaleTextSpace * 2, mTextPaint);
        canvas.drawText(unit, mWidth / 2 + textWidth / 2 + dp2px(4), mHeight / 2 - mScaleTextSpace * 3, mTextPaint);

        mLinePaint.setStrokeWidth(mLineScaleWidth);
        mLinePaint.setColor(mTextCurrentColor);
        canvas.drawLine(mWidth / 2, mHeight / 2 - mScaleTextSpace, mWidth / 2, mHeight / 2 + mLineScaleHeightLong * 2, mLinePaint);
    }

    /**
     * 画刻度线
     */
    private void drawScaleLineAndText(Canvas canvas) {
        // 设置画笔
        mLinePaint.setColor(mLineColor);
        mTextPaint.setTextSize(mScaleTextSize);
        mTextPaint.setColor(mTextColor);

        // 绘制刻度线，从控件中心向两边绘制，保证居中
        int middleHeight = mHeight / 2;
        int centerX = mWidth / 2;
        // 绘制左边
        float leftX = centerX;
        int leftValue = currentValue;
        mLinePaint.setStrokeWidth(mLineScaleWidth);
        while (leftValue > startValue * SMALL_SCALE_IN_SCALE_COUNT) {
            leftX -= mSmallScaleCellWidth;
            leftValue -= 1 * stepLengthValue;
            if (leftValue / stepLengthValue % SMALL_SCALE_IN_SCALE_COUNT == 0) {
                canvas.drawLine(leftX, middleHeight, leftX, middleHeight + mLineScaleHeightLong, mLinePaint);
                String text = (leftValue / SMALL_SCALE_IN_SCALE_COUNT) + "";
                float textWidth = mTextPaint.measureText(text);
                canvas.drawText(text, leftX - textWidth / 2, mHeight / 2 + mLineScaleHeightLong + mScaleTextSize + mScaleTextSpace, mTextPaint);
            } else {
                canvas.drawLine(leftX, middleHeight, leftX, middleHeight + mLineScaleHeightShort, mLinePaint);
            }
        }
        // 绘制中心点左边主轴
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineMainWidth);
        canvas.drawLine(leftX, middleHeight, centerX, middleHeight, mLinePaint);

        // 绘制右边
        float rightX = centerX;
        int rightValue = currentValue;
        mLinePaint.setStrokeWidth(mLineScaleWidth);
        while (rightValue < endValue * SMALL_SCALE_IN_SCALE_COUNT) {
            rightX += mSmallScaleCellWidth;
            rightValue += 1 * stepLengthValue;
            if (rightValue / stepLengthValue % SMALL_SCALE_IN_SCALE_COUNT == 0) {
                canvas.drawLine(rightX, middleHeight, rightX, middleHeight + mLineScaleHeightLong, mLinePaint);
                String text = (rightValue / SMALL_SCALE_IN_SCALE_COUNT) + "";
                float textWidth = mTextPaint.measureText(text);
                canvas.drawText(text, rightX - textWidth / 2, mHeight / 2 + mLineScaleHeightLong + mScaleTextSize + mScaleTextSpace, mTextPaint);
            } else {
                canvas.drawLine(rightX, middleHeight, rightX, middleHeight + mLineScaleHeightShort, mLinePaint);
            }
        }
        // 绘制中心点右边边主轴
        mLinePaint.setStrokeWidth(mLineMainWidth);
        canvas.drawLine(centerX, middleHeight, rightX, middleHeight, mLinePaint);

        // 绘制中间线
        if (currentValue / stepLengthValue % SMALL_SCALE_IN_SCALE_COUNT == 0) {
            canvas.drawLine(mWidth / 2, middleHeight, mWidth / 2, middleHeight + mLineScaleHeightLong, mLinePaint);
            float textWidth = mTextPaint.measureText(currentValue / SMALL_SCALE_IN_SCALE_COUNT + "");
            canvas.drawText(currentValue / SMALL_SCALE_IN_SCALE_COUNT + "", mWidth / 2 - textWidth / 2, mHeight / 2 + mLineScaleHeightLong + mScaleTextSize + mScaleTextSpace, mTextPaint);
        }
    }

    private float downX;

    @Override
//        Log.i("ScaleView", "++++++++++++++++++++++ " + event.getAction());
    public boolean onTouchEvent(MotionEvent event) {
        // 如果是手指弹起事件，需要自己处理，因为手势识别器没有处理
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            return true;
//        } else {
//            // 将事件交给手势识别器处理
//            Log.i("ScaleView", "---------------- " + event.getAction());
//            return gestureDetector.onTouchEvent(event);
//        }

        int action = event.getAction();
        if (MotionEvent.ACTION_DOWN == action) {
            downX = event.getX();
        } else if (MotionEvent.ACTION_MOVE == action) {
            float moveX = event.getX();
            float dx = downX - moveX;
            currentValue += Math.round(dx / mSmallScaleCellWidth) * stepLengthValue;
            if (currentValue > endValue * SMALL_SCALE_IN_SCALE_COUNT && dx > 0)
                currentValue = endValue * SMALL_SCALE_IN_SCALE_COUNT;
            if (currentValue < startValue * SMALL_SCALE_IN_SCALE_COUNT && dx < 0)
                currentValue = startValue * SMALL_SCALE_IN_SCALE_COUNT;

            invalidate();
            downX = moveX;
        } else if (MotionEvent.ACTION_UP == action) {
            // 计算位置
        }
        return true;
    }

    /**
     * dp转换成px
     */
    public int dp2px(float dipValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * sp转换成px
     */
    public int sp2px(float spValue) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
