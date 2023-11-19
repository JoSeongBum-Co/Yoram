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
    String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_yoram_home, container, false);

        buttonSetyoga = view.findViewById(R.id.buttonSetyoga);
        timePicker = view.findViewById(R.id.timePicker);

        buttonSetyoga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 클릭 로직
            }
        });

        int[] buttonIds = {R.id.Mon, R.id.Tue, R.id.Wed, R.id.Thu, R.id.Fri, R.id.Sat, R.id.Sun};
        for (int id : buttonIds) {
            Button dayButton = view.findViewById(id);
            dayButton.setOnClickListener(dayButtonClickListener);
            dayButtonStates.put(dayButton, false);
        }

        loadTimeData(); // 저장된 시간 데이터를 불러옵니다.

        return view;
    }

    private View.OnClickListener dayButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button dayButton = (Button) v;
            String day = dayButton.getText().toString();
            boolean isOn = !dayButtonStates.get(dayButton);
            dayButtonStates.put(dayButton, isOn);

            dayButton.setBackground(new ColorDrawable(isOn ? Color.parseColor(onColor) : Color.parseColor(offColor)));
            timePicker.setEnabled(isOn);

            // 나머지 로직...
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        saveTimeData(); // 시간 데이터를 저장합니다.
    }

    private void saveTimeData() {
        SharedPreferences prefs = getActivity().getSharedPreferences("YoramHomePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String dayTimeMapJson = gson.toJson(dayTimeMap);
        editor.putString("dayTimeMap", dayTimeMapJson);
        editor.apply();
    }

    private void loadTimeData() {
        SharedPreferences prefs = getActivity().getSharedPreferences("YoramHomePrefs", Context.MODE_PRIVATE);
        String dayTimeMapJson = prefs.getString("dayTimeMap", null);
        if (dayTimeMapJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, Calendar>>(){}.getType();
            dayTimeMap = gson.fromJson(dayTimeMapJson, type);
        } else {
            dayTimeMap = new HashMap<>();
        }
    }
}
