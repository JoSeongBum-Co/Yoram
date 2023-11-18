package com.example.yoram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class YogaSetOnboadActivity extends AppCompatActivity {
    Button next_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga_set_onboad);

        next_button = (Button) findViewById(R.id.next_button);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(YogaSetOnboadActivity.this, OnboardSettingCompleteActivity.class);
                    startActivity(intent);
            }
        });
    }
}