package com.example.yoram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class YogaChoiceOnboadActivity extends AppCompatActivity {
    Button neck, waist, hip_joint, leg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga_choice_onboad);

        neck = (Button) findViewById(R.id.neck);
        waist = (Button) findViewById(R.id.waist);
        hip_joint = (Button) findViewById(R.id.hip_joint);
        leg = (Button) findViewById(R.id.leg);


        neck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YogaChoiceOnboadActivity.this, YogaSetOnboadActivity.class);
                startActivity(intent);
            }
        });

        waist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YogaChoiceOnboadActivity.this, YogaSetOnboadActivity.class);
                startActivity(intent);
            }
        });

        hip_joint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YogaChoiceOnboadActivity.this, YogaSetOnboadActivity.class);
                startActivity(intent);
            }
        });

        leg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YogaChoiceOnboadActivity.this, YogaSetOnboadActivity.class);
                startActivity(intent);
            }
        });
    }
}