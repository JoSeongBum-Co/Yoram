package com.example.yoram;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import com.example.yoram.ThemeUtil;
import android.app.Dialog;

public class Moddialog extends Activity{
    Button close_btn;
    RadioButton r_btn_light, r_btn_dark;
    String themeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_moddialog);

        r_btn_light = findViewById(R.id.r_btn_light);
        r_btn_dark = findViewById(R.id.r_btn_dark);

        r_btn_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeColor = ThemeUtil.LIGHT_MODE;
                ThemeUtil.applyTheme(themeColor);
                ThemeUtil.modSave(getApplicationContext(), themeColor);
            }
        });

        r_btn_dark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeColor = ThemeUtil.DARK_MODE;
                ThemeUtil.applyTheme(themeColor);
                ThemeUtil.modSave(getApplicationContext(), themeColor);
            }
        });
    }
}