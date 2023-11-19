package com.example.yoram;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SharedPreferenceUtil {
    public static Map<String, Calendar> loadDataFromSharedPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("YoramHomePrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("dayTimeMap", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, Calendar>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return new HashMap<>();
    }
}
