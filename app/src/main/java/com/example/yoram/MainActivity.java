package com.example.yoram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment homeFragment, mypageFragment, settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 첫 시작 여부 확인
        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        boolean isFirstStart = prefs.getBoolean("firstStart", true);

        if (isFirstStart) {
            // 온보딩 액티비티 시작
            startActivity(new Intent(this, IntroduceOnboard.class));
            finish();
            return; // 이 부분을 추가하여 아래 코드가 실행되지 않도록 함
        }

        // 메인 액티비티 레이아웃 설정
        setContentView(R.layout.activity_main);

        // 프래그먼트 인스턴스 생성
        homeFragment = new HomeFragment();
        mypageFragment = new MypageFragment();
        settingFragment = new SettingFragment();

        // 바텀 네비게이션 뷰 초기화
        bottomNavigationView = findViewById(R.id.bottom_navigationbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, homeFragment).commitAllowingStateLoss();
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.info) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_layout, mypageFragment).commitAllowingStateLoss();
                    return true;
                } else if (id == R.id.home) {
                    System.out.println("2");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_layout, homeFragment).commitAllowingStateLoss();
                    return true;
                } else if (id == R.id.setting) {
                    System.out.println("3");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_layout, settingFragment).commitAllowingStateLoss();
                    return true;
                }
                return false;
            }
        });

    }
    // 데이터를 SharedPreferences에 저장하는 메서드
    private void saveDataInSharedPreferences(Map<String, Calendar> data) {
        SharedPreferences prefs = getSharedPreferences("YoramHomePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString("dayTimeMap", json);
        editor.apply();
    }

    // SharedPreferences에서 데이터를 불러오는 메서드
    public Map<String, Calendar> loadDataFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("YoramHomePrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("dayTimeMap", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, Calendar>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return new HashMap<>();
    }
}
