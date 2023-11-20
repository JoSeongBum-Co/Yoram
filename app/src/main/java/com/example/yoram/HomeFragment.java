package com.example.yoram;

import androidx.fragment.app.Fragment;

import android.app.Activity;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    TimePicker timePicker;
    Button button_Set_yoga;
    String offColor = "#FF4469";
    String onColor = "#A2D5F2";
    int[] buttonIds = {R.id.Mon, R.id.Tue, R.id.Wed, R.id.Thu, R.id.Fri, R.id.Sat, R.id.Sun};
    boolean is_button_selected = false;
    Button mon, tue, wed, thu, fri, sat, sun;

    public String getTimeInSharedPreference(String day) {
        SharedPreferences pref;
        SharedPreferences.Editor editor;

        pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        // active 는 요일에 알람이 설정되었는지.
        String active_key = day + "_active";
        String hour_key = day + "_hour";
        String minute_key = day + "_minute";


        int active = pref.getInt(active_key, 0);
        int hour = pref.getInt(hour_key, 0);
        int minute = pref.getInt(minute_key, 0);

        String result = active + ":" + Integer.toString(hour) + ":" + Integer.toString(minute);
        return result;
    }

    public boolean setTimeInSharedPreference(String day, int active, int hour, int minute) {
        SharedPreferences pref;
        SharedPreferences.Editor editor;

        pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        // active 는 요일에 알람이 설정되었는지.
        String active_key = day + "_active";
        String hour_key = day + "_hour";
        String minute_key = day + "_minute";

        editor.putInt(active_key, active);
        editor.putInt(hour_key, hour);
        editor.putInt(minute_key, minute);


        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_yoram_home, container, false);
        timePicker = view.findViewById(R.id.timePicker);
        button_Set_yoga = view.findViewById(R.id.buttonSetyoga);

//        getTimeInSharedPreference()

        // 월화수목금토일 버튼
        mon = (Button) getView().findViewById(buttonIds[0]);
        tue = (Button) getView().findViewById(buttonIds[1]);
        wed = (Button) getView().findViewById(buttonIds[2]);
        thu = (Button) getView().findViewById(buttonIds[3]);
        fri = (Button) getView().findViewById(buttonIds[4]);
        sat = (Button) getView().findViewById(buttonIds[5]);
        sun = (Button) getView().findViewById(buttonIds[6]);


//        timePicker.setOnTimeChangedListener();

        //길게 누르면 활성화되게 만든다. 하루를
        mon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String get_data = getTimeInSharedPreference("mon");
                String[] datas = get_data.split(":");
                int active, hour, minute;

                active = Integer.parseInt(datas[0]);
                hour = Integer.parseInt(datas[1]);
                minute = Integer.parseInt(datas[2]);

                if (active == 0) {
                    //현재 활성화가 안되어있으니까, 이제 활성화 되게.
                    active = 1;
                    mon.setBackgroundColor(Color.parseColor(onColor));
                    timePicker.setEnabled(true);
                    timePicker.setHour(hour);
                    timePicker.setMinute(minute);


                } else {
                    // 활성화 되어있었으니까, 이제 비활성화
                }
                return false;
            }
        });


        return view;
    }


}
