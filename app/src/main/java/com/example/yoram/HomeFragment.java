package com.example.yoram;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    TimePicker timePicker;
    Button buttonSetyoga;
    Map<Button, Boolean> dayButtonStates = new HashMap<>();
    Map<String, Calendar> dayTimeMap = new HashMap<>();
    String offColor = "#FF4469";
    String onColor = "#A2D5F2";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_yoram_home, container, false);
        timePicker = view.findViewById(R.id.timePicker);
        buttonSetyoga = view.findViewById(R.id.buttonSetyoga);

        int[] buttonIds = {R.id.Mon, R.id.Tue, R.id.Wed, R.id.Thu, R.id.Fri, R.id.Sat, R.id.Sun};

        return view;
    }


}
