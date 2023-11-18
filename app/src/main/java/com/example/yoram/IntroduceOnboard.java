package com.example.yoram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IntroduceOnboard extends AppCompatActivity {
    Button start_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_onboard);
        start_button = (Button)findViewById(R.id.start_button);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroduceOnboard.this, YogaChoiceOnboadActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}