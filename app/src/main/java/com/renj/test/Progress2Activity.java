package com.renj.test;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.renj.progress.CircleProgressBar;
import com.renj.progress.SquareProgressBar;

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
    private CircleProgressBar circleProgressBar3;

    private SquareProgressBar squareProgressBar1;
    private SquareProgressBar squareProgressBar2;
    private SquareProgressBar squareProgressBar3;

    private boolean progressFinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress2);

        btUpdate = findViewById(R.id.bt_update);
        circleProgressBar1 = findViewById(R.id.circle_pb1);
        circleProgressBar2 = findViewById(R.id.circle_pb2);
        circleProgressBar3 = findViewById(R.id.circle_pb3);

        squareProgressBar1 = findViewById(R.id.square_bar1);
        squareProgressBar2 = findViewById(R.id.square_bar2);
        squareProgressBar3 = findViewById(R.id.square_bar3);

        btUpdate.setOnClickListener(v -> {
            if (!progressFinish) return;
            new Thread(() -> {
                progressFinish = false;
                for (int i = 0; i <= 100; i++) {
                    final int progress = i;
                    runOnUiThread(() -> {
                        circleProgressBar1.setProgress(progress);
                        circleProgressBar2.setProgress(progress);
                        circleProgressBar3.setProgress(progress);

                        squareProgressBar1.setProgress(progress);
                        squareProgressBar2.setProgress(progress);
                        squareProgressBar3.setProgress(progress);
                    });
                    SystemClock.sleep(50);
                }
                progressFinish = true;
            }).start();
        });
    }
}
