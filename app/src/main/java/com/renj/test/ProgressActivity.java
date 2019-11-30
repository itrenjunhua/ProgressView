package com.renj.test;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.renj.progress.CircleProgressView;
import com.renj.progress.SemicircleProgressView;

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
public class ProgressActivity extends AppCompatActivity {
    private Button btUpdate;
    private CircleProgressView circleProgressView;
    private SemicircleProgressView semicircleProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        btUpdate = findViewById(R.id.bt_update);
        circleProgressView = findViewById(R.id.circle_pv_view);
        semicircleProgressView = findViewById(R.id.semicircle_pv_view);

        btUpdate.setOnClickListener(v -> {
            circleProgressView.setValue(100, 60).takeEffect();
            semicircleProgressView.setValue(100, 70).takeEffect();
        });


        /***************CircleProgressView***************/
        circleProgressView.setOnProgressChangeListener((circleProgressView, currentValue) ->
                Log.i("ProgressActivity", "currentValue = [" + currentValue + "]"));

        /***************SemicircleProgressView***************/
        semicircleProgressView.setOnProgressChangeListener((semicircleProgressView, currentValue) ->
                Log.i("ProgressActivity", "currentValue = [" + currentValue + "]"));
    }
}
