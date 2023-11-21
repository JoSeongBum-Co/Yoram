package com.example.yoram;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    TimePicker timePicker;
    Button button_Set_yoga;
    String offColor = "#FF4469";
    String onColor = "#A2D5F2";
    int[] buttonIds = {R.id.Mon, R.id.Tue, R.id.Wed, R.id.Thu, R.id.Fri, R.id.Sat, R.id.Sun};
    Map<String, Button> dayButtons = new HashMap<>();
    String currentActiveDay = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_yoram_home, container, false);
        timePicker = view.findViewById(R.id.timePicker);
        button_Set_yoga = view.findViewById(R.id.buttonSetyoga);

        initializeDayButtons(view);
        setDayButtonListeners();
        restoreState();
        setSetYogaButtonListener();

        return view;
    }

    private void initializeDayButtons(View view) {
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < days.length; i++) {
            Button dayButton = view.findViewById(buttonIds[i]);
            dayButtons.put(days[i], dayButton);
        }
    }


    // 요가 선택 버튼 눌렀을 때
    private void setSetYogaButtonListener() {
        button_Set_yoga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AlarmStartActivity.class);
                startActivity(intent);
//                startActivity(intent);

            }
        });
    }

    private void setDayButtonListeners() {
        for (Map.Entry<String, Button> entry : dayButtons.entrySet()) {
            String day = entry.getKey();
            Button dayButton = entry.getValue();

            dayButton.setOnLongClickListener(view -> {
                toggleDayButton(day, dayButton);
                return false;
            });

            dayButton.setOnClickListener(view -> {
                saveTimePickerTime(); // 현재 시간 저장
                currentActiveDay = day; // 현재 활성화된 요일 변경
                displayTimeForDay(day);
            });
        }

        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            if (currentActiveDay != null) {
                setAlarmTime(currentActiveDay, 1, hourOfDay, minute);
            }
        });
    }

    private void toggleDayButton(String day, Button dayButton) {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        int active = pref.getInt(day + "_active", 0);
        int hour = pref.getInt(day + "_hour", 0);
        int minute = pref.getInt(day + "_minute", 0);

        active = active == 0 ? 1 : 0;
        dayButton.setBackgroundColor(active == 1 ? Color.parseColor(onColor) : Color.parseColor(offColor));
        timePicker.setEnabled(active == 1);
        if (active == 1) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        }
        setAlarmTime(day, active, hour, minute);
    }

    private void saveTimePickerTime() {
        if (currentActiveDay != null) {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            setAlarmTime(currentActiveDay, 1, hour, minute);
        }
    }

    private void displayTimeForDay(String day) {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        int active = pref.getInt(day + "_active", 0);
        int hour = pref.getInt(day + "_hour", 0);
        int minute = pref.getInt(day + "_minute", 0);

        if (active == 1) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        }
    }

    private void setAlarmTime(String day, int active, int hour, int minute) {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(day + "_active", active);
        editor.putInt(day + "_hour", hour);
        editor.putInt(day + "_minute", minute);
        editor.apply();
    }

    private void restoreState() {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        boolean isAnyDayActive = false;

        for (Map.Entry<String, Button> entry : dayButtons.entrySet()) {
            String day = entry.getKey();
            Button dayButton = entry.getValue();

            int active = pref.getInt(day + "_active", 0);
            int hour = pref.getInt(day + "_hour", 0);
            int minute = pref.getInt(day + "_minute", 0);

            dayButton.setBackgroundColor(active == 1 ? Color.parseColor(onColor) : Color.parseColor(offColor));
            if (active == 1 && !isAnyDayActive) {
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
                isAnyDayActive = true;
                currentActiveDay = day; // 현재 활성화된 요일 초기화
            }
        }

        timePicker.setEnabled(isAnyDayActive);
    }
}
