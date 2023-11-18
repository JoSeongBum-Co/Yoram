package com.example.yoram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {// 앱의 일반 Theme로 변경
                setContentView(R.layout.activity_splash);
            }
        }, 1000); //
        //처음 들어오는 경우와 아닌 경우를 정해줌.
        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        boolean isFirstStart = prefs.getBoolean("firstStart", true);



        if (isFirstStart) {
            // 소개, 요가선택, 시간선택, 마지막 온보딩 버튼 누를때 finish하게 만들자.
            startActivity(new Intent(this, IntroduceOnboard.class));
            finish();
        } else {
            setContentView(R.layout.activity_yoram_home);
        }

    }
}