package com.renj.progress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntRange;

import com.renj.progress.utils.DimensionUtils;
import com.renj.progress.utils.NumberUtils;


/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2022-04-14   14:50
 * <p>
 * 描述：方形进度条
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class SquareProgressBar extends View {

    /**
     * 中间进度文字样式类型(progressTextType)——不显示当前值，默认
     */
    public static final int PROGRESS_TEXT_NONE = 0;
    /**
     * 中间进度文字样式类型(progressTextType)——小数点形式显示
     */
    public static final int PROGRESS_TEXT_DECIMAL = 1;
    /**
     * 中间进度文字样式类型(progressTextType)——百分比形式显示
     */
    public static final int PROGRESS_TEXT_PERCENTAGE = 2;


    /**
     * 进度条样式(progressBarType)——只显示边框
     */
    public static final int PROGRESS_BAR_STROKE = 0;
    /**
     * 进度条样式(progressBarType)——只填充背景
     */
    public static final int PROGRESS_BAR_FILL = 1;
    /**
     * 进度条样式(progressBarType)——显示边框和背景
     */
    public static final int PROGRESS_BAR_STROKE_AND_FILL = 2;


    /**
     * 填充背景变化样式(progressBarBgType)——由有背景色变透明(进度为0时显示背景，进度为100时，无背景)
     */
    public static final int PROGRESS_BAR_BG_BG_TO_TRAN = 0;
    /**
     * 填充背景变化样式(progressBarBgType)——由透明变有背景色(进度为0时无背景，进度为100时，有背景色)
     */
    public static final int PROGRESS_BAR_BG_TRAN_TO_BG = 1;

    /**
     * 最大进度，默认100
     */
    private int maxProgress;
    /**
     * 当前进度
     */
    private int progress;
    /**
     * 边框进度宽度
     */
    private float strokeWidth;
    /**
     * 边框进度颜色
     */
    private int strokeColor;
    /**
     * 中间进度文字样式
     */
    private int progressTextType;
    /**
     * 进度条样式
     */
    private int progressBarType;
    /**
     * 填充背景变化样式
     */
    private int progressBarBgType;
    /**
     * 背景色，默认半透明黑色
     */
    private int bgColor;
    /**
     * 圆角大小，默认0
     */
    private float radius;

    /**
     * 进度文字颜色
     */
    private int textColor;
    /**
     * 进度文字大小
     */
    private float textSize;

    // 控件范围
    private RectF mViewRectF;
    // 边框画笔
    private final Paint mProgressStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // 进度背景画笔
    private final Paint mProgressBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // 进度文字画笔
    private final Paint mProgressTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // 样式由背景色变透明使用到的 PorterDuff.Mode
    private final PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    // 用于背景路径处理
    private final PathMeasure mBgPathMeasure = new PathMeasure();
    // 用于边框路径处理
    private final PathMeasure mStockPathMeasure = new PathMeasure();

    public SquareProgressBar(Context context) {
        this(context, null);
    }

    public SquareProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        @SuppressLint("CustomViewStyleable")
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SquareProgressbar);

        // 获取自定义属性和默认值
        maxProgress = mTypedArray.getInteger(R.styleable.SquareProgressbar_square_pb_maxProgress, 100);
        progress = mTypedArray.getInteger(R.styleable.SquareProgressbar_square_pb_progress, 0);
        radius = mTypedArray.getDimension(R.styleable.SquareProgressbar_square_pb_radius, 0);
        bgColor = mTypedArray.getColor(R.styleable.SquareProgressbar_square_pb_bgColor, Color.parseColor("#88000000"));

        progressBarBgType = mTypedArray.getInteger(R.styleable.SquareProgressbar_circle_pb_progressBarBgType, PROGRESS_BAR_BG_BG_TO_TRAN);
        progressBarType = mTypedArray.getInteger(R.styleable.SquareProgressbar_circle_pb_progressBarType, PROGRESS_BAR_FILL);

        strokeWidth = mTypedArray.getDimension(R.styleable.SquareProgressbar_square_pb_strokeWidth, 10);
        strokeColor = mTypedArray.getColor(R.styleable.SquareProgressbar_square_pb_StrokeColor, Color.GREEN);

        textColor = mTypedArray.getColor(R.styleable.SquareProgressbar_square_pb_textColor, Color.RED);
        textSize = mTypedArray.getDimension(R.styleable.SquareProgressbar_square_pb_textSize, DimensionUtils.sp2px(context, 12));
        progressTextType = mTypedArray.getInteger(R.styleable.SquareProgressbar_square_pb_progressTextType, PROGRESS_TEXT_NONE);

        mTypedArray.recycle();
        // 设置画笔信息
        setPaintInfo();
    }

    // 设置画笔信息
    private void setPaintInfo() {
        mProgressBgPaint.setStyle(Paint.Style.FILL);
        mProgressBgPaint.setColor(bgColor);

        mProgressStrokePaint.setStyle(Paint.Style.STROKE);
        mProgressStrokePaint.setStrokeWidth(strokeWidth);
        mProgressStrokePaint.setColor(strokeColor);

        mProgressTxtPaint.setStrokeWidth(0);
        mProgressTxtPaint.setColor(textColor);
        mProgressTxtPaint.setTextSize(textSize);
        mProgressTxtPaint.setTypeface(Typeface.DEFAULT_BOLD); // 设置字体加粗
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mViewRectF = new RectF(0, 0, getWidth(), getHeight());

        // ==================== 说明 ==================== //
        // 使用addRoundRect时；
        // 顺时针（CW）从左下角向上偏移左下角圆角距离开始，逆时针（CCW）从左上角向下偏移左上角圆角距离开始
        // 使用addRect时：
        // 不管顺时针（CW）还是逆时针（CCW）都是从左上角开始
        // src.addRect(rectF,  Path.Direction.CCW);

        if (progressBarType == PROGRESS_BAR_STROKE
                || progressBarType == PROGRESS_BAR_STROKE_AND_FILL) {
            // 有边框时，需要处理边框。因为正常情况下，直接绘制，边框在画布上只显示一半，
            // 所以有边框时对路径进行处理

            Path srcPath = new Path();
            float halfStrokeWidth = strokeWidth / 2f;
            srcPath.addRoundRect(mViewRectF.left + halfStrokeWidth,
                    mViewRectF.top + halfStrokeWidth,
                    mViewRectF.right - halfStrokeWidth,
                    mViewRectF.bottom - halfStrokeWidth,
                    radius, radius, Path.Direction.CW);
            mStockPathMeasure.setPath(srcPath, true);
        }

        if (progressBarType == PROGRESS_BAR_FILL
                || progressBarType == PROGRESS_BAR_STROKE_AND_FILL) {
            Path srcPath = new Path();
            srcPath.addRoundRect(mViewRectF, radius, radius, Path.Direction.CW);
            mBgPathMeasure.setPath(srcPath, true);
        }
    }

    // ============================= 绘制部分 ============================= //

    @Override
    protected void onDraw(Canvas canvas) {
        // 设置画笔信息
        setPaintInfo();
        // 保证开始位置在上方位置，需要进行偏移，根据上面说明来处理的
        float startOffset = mViewRectF.height() + (mViewRectF.centerX() - radius * 1.4f);

        // 绘制进度背景
        int saveLayer = canvas.saveLayer(mViewRectF, mProgressBgPaint);
        drawProgressBg(canvas, startOffset);
        canvas.restoreToCount(saveLayer);

        // 绘制进度边框
        saveLayer = canvas.saveLayer(mViewRectF, mProgressStrokePaint);
        drawProgressStock(canvas, startOffset);
        canvas.restoreToCount(saveLayer);

        // 绘制进度文字
        saveLayer = canvas.saveLayer(mViewRectF, mProgressTxtPaint);
        drawProgressText(canvas);
        canvas.restoreToCount(saveLayer);
    }

    // 绘制进度背景
    private void drawProgressBg(Canvas canvas, float startOffset) {
        if (progressBarType == PROGRESS_BAR_FILL
                || progressBarType == PROGRESS_BAR_STROKE_AND_FILL) {

            // 如果需要绘制背景，并且背景样式是由 有背景色往无背景色变化，先画一层背景
            if (progressBarBgType == PROGRESS_BAR_BG_BG_TO_TRAN) {
                canvas.drawRoundRect(mViewRectF, radius, radius, mProgressBgPaint);
            }

            // 根据进度计算结束位置
            float length = mBgPathMeasure.getLength();
            float pathOffset = length * (progress * 1f / maxProgress);

            // 背景绘制路径
            Path renderBgPaths = new Path();
            // 将路径移动到中心位置
            renderBgPaths.moveTo(mViewRectF.centerX(), mViewRectF.centerY());
            // 因为开始位置进行了便宜，所以这里要判断绘制的结束位置是否已经大于路径的总长度了
            if (pathOffset + startOffset < length) {
                renderBgPaths.reset();
                if (mBgPathMeasure.getSegment(startOffset, startOffset + pathOffset, renderBgPaths, true)) {
                    reallyDrawProgressBg(canvas, renderBgPaths);
                }
            } else {
                renderBgPaths.reset();
                if (mBgPathMeasure.getSegment(startOffset, length, renderBgPaths, true)) {
                    reallyDrawProgressBg(canvas, renderBgPaths);
                }
                float end = pathOffset + startOffset - length;
                renderBgPaths.reset();
                if (mBgPathMeasure.getSegment(0, end, renderBgPaths, true)) {
                    reallyDrawProgressBg(canvas, renderBgPaths);
                }
            }
        }
    }

    // 实际背景绘制方法
    private void reallyDrawProgressBg(Canvas canvas, Path renderBgPaths) {
        // 使路径连接到中心位置
        renderBgPaths.lineTo(mViewRectF.centerX(), mViewRectF.centerY());
        // 背景样式是由有背景变透明，需要通过 PorterDuff.Mode 处理
        if (progressBarBgType == PROGRESS_BAR_BG_BG_TO_TRAN) {
            mProgressBgPaint.setXfermode(porterDuffXfermode);
            canvas.drawPath(renderBgPaths, mProgressBgPaint);
            mProgressBgPaint.setXfermode(null);
        } else {
            canvas.drawPath(renderBgPaths, mProgressBgPaint);
        }
    }

    // 绘制进度边框
    private void drawProgressStock(Canvas canvas, float startOffset) {
        if (progressBarType == PROGRESS_BAR_STROKE
                || progressBarType == PROGRESS_BAR_STROKE_AND_FILL) {

            startOffset -= strokeWidth;
            float length = mStockPathMeasure.getLength();
            float pathOffset = length * (progress * 1f / maxProgress);

            // 边框线绘制路径
            Path renderStockPaths = new Path();
            // 因为开始位置进行了便宜，所以这里要判断绘制的结束位置是否已经大于路径的总长度了
            if (pathOffset + startOffset < length) {
                renderStockPaths.reset();
                if (mStockPathMeasure.getSegment(startOffset, startOffset + pathOffset, renderStockPaths, true)) {
                    canvas.drawPath(renderStockPaths, mProgressStrokePaint);
                }
            } else {
                renderStockPaths.reset();
                if (mStockPathMeasure.getSegment(startOffset, length, renderStockPaths, true)) {
                    canvas.drawPath(renderStockPaths, mProgressStrokePaint);
                }

                float end = pathOffset + startOffset - length;
                renderStockPaths.reset();
                if (mStockPathMeasure.getSegment(0, end, renderStockPaths, true)) {
                    canvas.drawPath(renderStockPaths, mProgressStrokePaint);
                }
            }
        }
    }

    // 绘制进度文字
    private void drawProgressText(Canvas canvas) {
        if (progressTextType == PROGRESS_TEXT_DECIMAL) {
            String percent = NumberUtils.decimalFloat(progress * 1.0f / maxProgress);
            // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
            float textWidth = mProgressTxtPaint.measureText(percent);
            // 画出进度百分比
            canvas.drawText(percent, mViewRectF.centerX() - textWidth / 2f,
                    mViewRectF.centerY() + textSize / 2f, mProgressTxtPaint);
        } else if (progressTextType == PROGRESS_TEXT_PERCENTAGE) {
            // 中间的进度百分比，先转换成float在进行除法运算，不然都为0
            float percentValue = progress * 1.0f / maxProgress;
            String percent = NumberUtils.decimalInt(percentValue * 100) + "%";
            // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
            float textWidth = mProgressTxtPaint.measureText(percent);
            // 画出进度百分比
            canvas.drawText(percent, mViewRectF.centerX() - textWidth / 2f,
                    mViewRectF.centerY() + textSize / 2f, mProgressTxtPaint);
        }
    }

    // ============================= 属性设置和属性获取 ============================= //

    /**
     * 获取最大进度
     */
    public int getMaxProgress() {
        return maxProgress;
    }

    /**
     * 设置最大进度
     */
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    /**
     * 获取当前进度
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 设置当前进度
     */
    public void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
    }

    /**
     * 获取进度边框宽度 px
     */
    public float getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * 设置进度边框宽度 dp
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = DimensionUtils.dp2px(getContext(), strokeWidth);
    }

    /**
     * 获取进度边框颜色
     */
    public int getStrokeColor() {
        return strokeColor;
    }

    /**
     * 设置进度边框颜色
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    /**
     * 获取进度条样式
     */
    public int getProgressBarType() {
        return progressBarType;
    }

    /**
     * 设置进度条样式
     *
     * @param progressBarType {@link #PROGRESS_BAR_STROKE}、{@link #PROGRESS_BAR_FILL}、{@link #PROGRESS_BAR_STROKE_AND_FILL}
     */
    public void setProgressBarType(
            @IntRange(from = PROGRESS_BAR_STROKE, to = PROGRESS_BAR_STROKE_AND_FILL)
                    int progressBarType) {
        this.progressBarType = progressBarType;
    }

    /**
     * 获取进度背景样式
     */
    public int getProgressBarBgType() {
        return progressBarBgType;
    }

    /**
     * 设置进度背景样式
     *
     * @param progressBarBgType {@link #PROGRESS_BAR_BG_BG_TO_TRAN}、{@link #PROGRESS_BAR_BG_TRAN_TO_BG}
     */
    public void setProgressBarBgType(
            @IntRange(from = PROGRESS_BAR_BG_BG_TO_TRAN, to = PROGRESS_BAR_BG_TRAN_TO_BG)
                    int progressBarBgType) {
        this.progressBarBgType = progressBarBgType;
    }

    /**
     * 获取进度背景颜色
     */
    public int getBgColor() {
        return bgColor;
    }

    /**
     * 设置进度背景颜色
     */
    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    /**
     * 获取圆角大小 px
     */
    public float getRadius() {
        return radius;
    }

    /**
     * 设置圆角大小 dp
     */
    public void setRadius(float radius) {
        this.radius = DimensionUtils.dp2px(getContext(), radius);
    }

    /**
     * 获取进度文字样式
     */
    public int getProgressTextType() {
        return progressTextType;
    }

    /**
     * 设置进度文字样式
     *
     * @param progressTextType {@link #PROGRESS_TEXT_NONE}、{@link #PROGRESS_TEXT_DECIMAL}、{@link #PROGRESS_TEXT_PERCENTAGE}
     */
    public void setProgressTextType(
            @IntRange(from = PROGRESS_TEXT_NONE, to = PROGRESS_TEXT_PERCENTAGE)
                    int progressTextType) {
        this.progressTextType = progressTextType;
    }

    /**
     * 获取进度文字大小 px
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * 设置进度文字大小 sp
     */
    public void setTextSize(float textSize) {
        this.textSize = DimensionUtils.sp2px(getContext(), textSize);
    }

    /**
     * 获取进度文字颜色
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * 设置进度文字颜色
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
