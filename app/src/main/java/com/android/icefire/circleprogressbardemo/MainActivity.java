package com.android.icefire.circleprogressbardemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.icefire.circleprogressbar.CircleProgressBar;

public class MainActivity extends AppCompatActivity {

    CircleProgressBar progressBar;

    private TextView lastTime;
    private TextView lastTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar= (CircleProgressBar) findViewById(R.id.progress);
        lastTime= (TextView) findViewById(R.id.day);
        lastTag= (TextView) findViewById(R.id.tag);
        initProgress(420);
    }

    private void initProgress(int min){
        int hour = min / 60;
        //初始化显示时间
        if (hour < 24) {
            lastTime.setText(hour + "");
            lastTag.setText("小时");
        } else {
            int day = hour / 24;
            lastTime.setText(day + "");
            lastTag.setText("天");
        }
        //初始化进度条
        progressBar.update(hour, 3000);
    }
}
