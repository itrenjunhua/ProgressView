package com.renj.test.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2022-04-13   19:27
 * <p>
 * 描述：用于测试的圆角控件
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class TestRadiusView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public TestRadiusView(Context context) {
        super(context);
    }

    public TestRadiusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.parseColor("#FF00FF"));
        canvas.drawRoundRect(0,0,getWidth(),getHeight(),30,30,paint);
    }
}
