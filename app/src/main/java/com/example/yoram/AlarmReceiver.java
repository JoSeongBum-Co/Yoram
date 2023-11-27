package com.example.yoram;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "채널 ID를 입력하세요";
    private static final int NOTIFICATION_ID = 1;
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer player = MediaPlayer.create(context, R.raw.blueming);
        player.start();
        Log.d("리시버", "리스브 완료");
        Toast.makeText(context, "Alarm! Wake up!", Toast.LENGTH_SHORT).show();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, AlarmStartActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent,PendingIntent.FLAG_IMMUTABLE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                // Notification Channel 생성
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Test Notification", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setDescription("Notification from Mascot");
                notificationManager.createNotificationChannel(notificationChannel);

                // Notification을 만들어 NotificationManager를 통해 표시
                NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("Yoram")
                        .setContentText("일어나세요!!!!!.")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(notificationPendingIntent)
                        .setAutoCancel(true);
                notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
            }
        }

        Log.d("리시버","리시브 완료");

    }
}