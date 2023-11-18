package com.example.yoram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OnboardSettingCompleteActivity extends AppCompatActivity {
    Button start_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard_setting_complete);
        start_button = (Button) findViewById(R.id.start_button);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // 'firstStart' 키의 값을 false로 변경
                editor.putBoolean("firstStart", false);
                editor.apply(); //
                Intent intent = new Intent(OnboardSettingCompleteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}