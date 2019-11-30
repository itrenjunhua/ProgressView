package com.renj.test;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.renj.progress.ScaleView;
import com.renj.progress.SemicircleSeekBar;

import androidx.appcompat.app.AppCompatActivity;

/**
 * ======================================================================
 * <p>
 * 作者：Renj
 * <p>
 * 创建时间：2019-11-30   22:04
 * <p>
 * 描述：
 * <p>
 * 修订历史：
 * <p>
 * ======================================================================
 */
public class TouchActivity extends AppCompatActivity {
    private Button btSetValue;
    private ScaleView scaleView;
    private SemicircleSeekBar semicircleSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);

        btSetValue = findViewById(R.id.bt_set_value);
        scaleView = findViewById(R.id.scale_view);
        semicircleSeekBar = findViewById(R.id.semicircle_sb_view);

        btSetValue.setOnClickListener(v -> {
            scaleView.setValue(10, 180, 10, 45, "kg")
                    .takeEffect();
            semicircleSeekBar.setValue(100, 60).takeEffect();
        });


        /***************ScaleView***************/
        scaleView.setOnScaleChangeListener((scaleView, currentValue, unit, startValue, endValue, stepLengthValue) ->
                Log.i("TouchActivity", "currentValue = [" + currentValue + "]," +
                        " unit = [" + unit + "], " + "startValue = [" + startValue + "], " +
                        "endValue = [" + endValue + "], " + "stepLengthValue = [" + stepLengthValue + "]"));

        /***************SemicircleSeekBar***************/
        semicircleSeekBar.setOnProgressChangeListener((semicircleProgressView, currentValue) ->
                Log.i("TouchActivity", "currentValue = [" + currentValue + "]"));
    }
}
