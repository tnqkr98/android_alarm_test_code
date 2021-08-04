package com.tnqkr98.alarmexample;

import static android.content.Context.MODE_PRIVATE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;


// 알람 매니저에 의해 관리되는 알람 리시버 (매니저에 의해 메시지 받으면, 노티 실행)
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notificationIntent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"default");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setSmallIcon(R.drawable.ic_launcher_foreground); // mipmap 불가 ( Oreo 이상 시스템에선 )

            NotificationChannel channel = new NotificationChannel("default","매일 알람",NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("매일 정해진 알람");

            if(notificationManager != null)
                notificationManager.createNotificationChannel(channel);

        }
        else
            builder.setSmallIcon(R.mipmap.ic_launcher);  // Oreo 이전 시스템은 무조건 mipmap 써야함

        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setTicker("{Time to watch some cool stuff}")
                .setContentTitle("상대바 드래그시 보이는 타이틀")
                .setContentText("상태바 드래그시 보이는 서브타이틀")
                .setContentInfo("INFO")
                .setContentIntent(pendingIntent);

        if(notificationManager !=null){
            notificationManager.notify(1234,builder.build());   // 노티 동작

            Calendar nextNotifyTime = Calendar.getInstance();
            nextNotifyTime.add(Calendar.DATE,1);            // 내일 같은 시간으로 알람 시간 설정

            SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm",MODE_PRIVATE).edit();      // 다음 알람시간 SP에 저장
            editor.putLong("nextNotifyTime",nextNotifyTime.getTimeInMillis());
            editor.apply();

            Date currentDateTime = nextNotifyTime.getTime();
            Log.d("Alarm","다음 알람 시간 : "+currentDateTime);
        }
    }
}
