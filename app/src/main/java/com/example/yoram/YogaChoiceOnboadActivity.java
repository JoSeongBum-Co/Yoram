package com.example.yoram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class YogaChoiceOnboadActivity extends AppCompatActivity {
    Button next_button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga_choice_onboad);

        next_button2 = (Button) findViewById(R.id.next_button2);

        next_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YogaChoiceOnboadActivity.this, YogaSetOnboadActivity.class);
                startActivity(intent);
            }
        });




    }
}