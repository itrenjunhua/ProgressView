package com.renj.test;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.renj.progress.CircleProgressBar;
import com.renj.progress.CircleProgressView;
import com.renj.progress.SemicircleProgressView;

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
public class Progress2Activity extends AppCompatActivity {
    private Button btUpdate;
    private CircleProgressBar circleProgressBar1;
    private CircleProgressBar circleProgressBar2;

    private boolean progressFinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress2);

        btUpdate = findViewById(R.id.bt_update);
        circleProgressBar1 = findViewById(R.id.circle_pb1);
        circleProgressBar2 = findViewById(R.id.circle_pb2);

        btUpdate.setOnClickListener(v -> {
            if (!progressFinish) return;
            new Thread(() -> {
                progressFinish = false;
                for (int i = 0; i <= 100; i++) {
                    final int progress = i;
                    runOnUiThread(() -> {
                        circleProgressBar1.setProgress(progress);
                        circleProgressBar2.setProgress(progress);
                    });
                    SystemClock.sleep(50);
                }
                progressFinish = true;
            }).start();
        });
    }
}
