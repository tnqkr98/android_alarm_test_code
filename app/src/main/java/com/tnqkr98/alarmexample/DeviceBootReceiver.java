package com.tnqkr98.alarmexample;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
// 기기 재부팅 후에도 알람 동작 유도

public class DeviceBootReceiver extends BroadcastReceiver {         // 이 리시버는 pm 을 통해 실행되도록 등록됨
    @Override
    public void onReceive(Context context, Intent intent) {
       if(Objects.equals(intent.getAction(),"android.intent.action.BOOT_COMPLETED")) {                      // 재부팅이 완료되었다는 메시지를 받는다면
           Intent alarmIntent = new Intent(context,AlarmReceiver.class);
           PendingIntent  pendingIntent = PendingIntent.getBroadcast(context,0,alarmIntent,0);      // AlarmReceiver 실행 pendingIntent

           AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

           SharedPreferences sharedPreferences = context.getSharedPreferences("daily alarm",MODE_PRIVATE);
           long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());     // 저장된 알람 시간 불러오기

           Calendar current_calendar = Calendar.getInstance();          // 캘린더 객체 생성
           Calendar nextNotifyTime = new GregorianCalendar();           // 그레고리안 캘린더(현 위치 시스템 표준시에 맞춰진 캘린더 객체)
           nextNotifyTime.setTimeInMillis(sharedPreferences.getLong("nextNotifyTime",millis));      // 캘린더 객체의 오늘 날짜에, 불러온 시간으로 알람 등록

           if(current_calendar.after(nextNotifyTime)){      // 알람시간이 이미 지났다면
               nextNotifyTime.add(Calendar.DATE,1);  // 다음날 같은 시간으로 알람 시간 변경
           }

           Date currentDateTime = nextNotifyTime.getTime();
           Log.d("Alarm","다음 알람 시간 : "+currentDateTime);

           if(manager != null)
               manager.setRepeating(AlarmManager.RTC_WAKEUP, nextNotifyTime.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);     // 알람등록


       }
    }
}
