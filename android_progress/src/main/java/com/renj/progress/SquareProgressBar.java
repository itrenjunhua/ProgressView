package com.renj.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2022-04-13   14:50
 * <p>
 * 描述：方形进度条
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class SquareProgressBar extends View {

    private int radius = 30;

    /**
     * 宽
     */
    private int mWidth;
    /**
     * 高
     */
    private int mHeight;
    /**
     * 高
     */
    private Paint mPaint;
    /**
     * 进度（最大100）
     */
    private double mProgress = 0;
    private int bgColor;

    public SquareProgressBar(Context context) {
        this(context, null);
    }

    public SquareProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        bgColor = Color.parseColor("#66000000");
        mPaint.setColor(bgColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            mWidth = specSize;
        } else {
            mWidth = defaultLength();
        }
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            mHeight = specSize;
        } else {
            mHeight = defaultLength();
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    private int defaultLength() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56f, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mProgress < 0 || mProgress > 100) {
            return;
        }

        RectF rectF = new RectF(0, 0, mWidth, mHeight);
        Path src = new Path();
        // 使用addRoundRect时，
        // 顺时针（CW）从左下角向上偏移左下角圆角距离开始，逆时针（CCW）从左上角向下偏移左上角圆角距离开始
        src.addRoundRect(rectF, radius, radius, Path.Direction.CW);
        // 使用addRect时，
        // 不管顺时针（CW）还是逆时针（CCW）都是从左上角开始
        // src.addRect(rectF,  Path.Direction.CCW);

        PathMeasure mPathMeasure = new PathMeasure(src, true);
        float length = mPathMeasure.getLength();
        float startOffset = 450+225-radius; // 450+225  保证开始位置在指定位置，需要进行偏移
        float pathOffset = (float) (length * (mProgress / 100f));

        int saveLayer = canvas.saveLayer(rectF,mPaint);

        mPaint.setColor(bgColor);
        canvas.drawPath(src,mPaint);

        Path mRenderPaths = new Path();
        mRenderPaths.moveTo(mWidth / 2, mHeight / 2);
        if (pathOffset + startOffset < mPathMeasure.getLength()) {
            mRenderPaths.reset();
            if (mPathMeasure.getSegment(startOffset, startOffset + pathOffset, mRenderPaths, true)) {
                mRenderPaths.lineTo(mWidth / 2, mHeight / 2);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                canvas.drawPath(mRenderPaths, mPaint);
                mPaint.setXfermode(null);
            }
        } else {
            mRenderPaths.reset();
            if (mPathMeasure.getSegment(startOffset, mPathMeasure.getLength(), mRenderPaths, true)) {
                mRenderPaths.lineTo(mWidth / 2, mHeight / 2);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                canvas.drawPath(mRenderPaths, mPaint);
                mPaint.setXfermode(null);
            }
            float end = pathOffset + startOffset - mPathMeasure.getLength();
            mRenderPaths.reset();
            if (mPathMeasure.getSegment(0, end, mRenderPaths, true)) {
                mRenderPaths.lineTo(mWidth / 2, mHeight / 2);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                canvas.drawPath(mRenderPaths, mPaint);
                mPaint.setXfermode(null);
            }
        }
        canvas.restoreToCount(saveLayer);
    }

    /**
     * 设置进度 最大值100
     */
    public void setProgress(float progress) {
        this.mProgress = progress;
        postInvalidate();
    }
}
