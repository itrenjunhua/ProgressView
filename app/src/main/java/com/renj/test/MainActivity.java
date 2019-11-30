package com.renj.test;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_progress).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProgressActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.bt_touch).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TouchActivity.class);
            startActivity(intent);
        });
    }
}
