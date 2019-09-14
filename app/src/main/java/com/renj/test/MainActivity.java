package com.renj.test;

import android.os.Bundle;
import android.util.Log;

import com.renj.progress.ScaleView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ScaleView scaleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scaleView = findViewById(R.id.scale_view);

        scaleView
                .setValue(30, 180, 10, 45, "kg")
                .takeEffect();
        scaleView
                .setOnScaleChangeListener(new ScaleView.OnScaleChangeListener() {
                    @Override
                    public void onScaleChange(@NonNull ScaleView scaleView, float currentValue, String unit, int startValue, int endValue, int stepLengthValue) {
                        Log.i("MainActivity", "currentValue = [" + currentValue + "], unit = [" + unit + "], " +
                                "startValue = [" + startValue + "], endValue = [" + endValue + "], " +
                                "stepLengthValue = [" + stepLengthValue + "]");
                    }
                });
    }
}
