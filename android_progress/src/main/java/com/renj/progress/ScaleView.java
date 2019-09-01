package com.renj.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
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

    // 刻度线变量
    private Paint mLinePaint;
    private int mLineColor = DEFAULT_COLOR;
    private int mLineMainWidth = DEFAULT_MAIN_LINE_WIDTH;
    private int mLineScaleWidth = DEFAULT_SCALE_LINE_WIDTH;
    private int mLineScaleHeightLong = DEFAULT_SCALE_LINE_HEIGHT_LONG;
    private int mLineScaleHeightShort = DEFAULT_SCALE_LINE_HEIGHT_SHORT;
    private int mScaleCellWidth = DEFAULT_SCALE_CELL_WIDTH;

    // 文字变量
    private Paint mTextPaint;
    private int mTextColor = DEFAULT_COLOR;
    private int mScaleTextSize = DEFAULT_SCALE_TEXT_SIZE;
    private int mCurrentTextSize = DEFAULT_CURRENT_TEXT_SIZE;
    private int mTextCurrentColor = DEFAULT_CURRENT_COLOR;

    private int mScaleTextSpace = DEFAULT_SCALE_TEXT_SPACE;

    // 需要绘制的区域
    private Rect mDrawRect = new Rect();
    // 控件的宽和高
    private int mWidth = DEFAULT_VIEW_WIDTH;
    private int mHeight = DEFAULT_VIEW_HEIGHT;
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


    // 数据列表和单位
    private String currentValue = "10";
    private String unit = "cm";
    private int startValue = 0; // 开始值
    private int endValue = 20; // 结束值
    private int stepLengthValue = 2; // 步长
    private List<String> scaleCellList = new ArrayList<>();

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
        Log.i("ScaleView", "w = [" + w + "], h = [" + h + "], oldWidth = [" + oldWidth + "], oldHeight = [" + oldHeight + "]");
        calculateRect();
    }

    /**
     * 计算绘制区域大小
     */
    private void calculateRect() {
        for (int i = startValue; i <= endValue; ) {
            scaleCellList.add(i + "");
            i += stepLengthValue;
        }

        mDrawRect.left = 0;
        mDrawRect.right = (scaleCellList.size() - 1) * mScaleCellWidth;
        mDrawRect.top = 0;
        mDrawRect.bottom = mHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLine(canvas);
        drawScaleText(canvas);
        drawCurrentData(canvas);
    }

    /**
     * 画当前数据文字
     */
    private void drawCurrentData(Canvas canvas) {
        mTextPaint.setTextSize(mCurrentTextSize);
        mTextPaint.setColor(mTextCurrentColor);
        float textWidth = mTextPaint.measureText(currentValue);
        canvas.drawText(currentValue, mWidth / 2 - textWidth / 2, mHeight / 2 - mScaleTextSpace * 2, mTextPaint);

        mLinePaint.setStrokeWidth(mLineScaleWidth);
        mLinePaint.setColor(mTextCurrentColor);
        canvas.drawLine(mWidth / 2, mHeight / 2 - mScaleTextSpace, mWidth / 2, mHeight / 2 + mLineScaleHeightLong * 2, mLinePaint);
    }

    /**
     * 画刻度文字
     */
    private void drawScaleText(Canvas canvas) {
        // 绘制刻度文字
        mTextPaint.setTextSize(mScaleTextSize);
        mTextPaint.setColor(mTextColor);

        for (int i = 0; i < scaleCellList.size(); i++) {
            float textWidth = mTextPaint.measureText(scaleCellList.get(i));
            canvas.drawText(scaleCellList.get(i), mScaleCellWidth * i - textWidth / 2, mDrawRect.height() / 2 + mLineScaleHeightLong + mScaleTextSize + mScaleTextSpace, mTextPaint);
        }
    }

    /**
     * 画刻度线
     */
    private void drawLine(Canvas canvas) {
        // 绘制主轴
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineMainWidth);
        int middleHeight = mDrawRect.height() / 2;
        canvas.drawLine(mDrawRect.left, middleHeight, mDrawRect.right, middleHeight, mLinePaint);

        // 计算刻度线相关
        int smallCell = mScaleCellWidth / 10;
        int scaleTotalCount = (scaleCellList.size() - 1) * 10;
        // 绘制刻度线
        mLinePaint.setStrokeWidth(mLineScaleWidth);
        for (int i = 0; i <= scaleTotalCount; i++) {
            if (i % 10 == 0)
                canvas.drawLine(smallCell * i, middleHeight, smallCell * i, middleHeight + mLineScaleHeightLong, mLinePaint);
            else
                canvas.drawLine(smallCell * i, middleHeight, smallCell * i, middleHeight + mLineScaleHeightShort, mLinePaint);
        }
    }

    private int downX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.i("ScaleView", "++++++++++++++++++++++ " + event.getAction());
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
            downX = (int) event.getX();
        } else if (MotionEvent.ACTION_MOVE == action) {
            int moveX = (int) event.getX();
            scrollBy(downX - moveX, 0);
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
