package com.example.yoram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent sIntent = new Intent(context, AlarmService.class);
        context.startService(sIntent);
        Log.d("리시버","리시브 완료");
    }
}