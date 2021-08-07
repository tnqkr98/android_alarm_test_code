package com.tnqkr98.alarmexample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 알람 시간 최초 설정
        Calendar calendar = Calendar.getInstance();                 // 캘린더 객체 생성, 디폴트 초기값은 현재 시간
        calendar.setTimeInMillis(System.currentTimeMillis());       // 캘린더 객체에 현재 시점 입력0
        calendar.set(Calendar.HOUR_OF_DAY,11);                      // 시간 지정
        calendar.set(Calendar.MINUTE,49);                           // 분 지정
        calendar.set(Calendar.SECOND,00);                            // 초 지정

        if(calendar.before(Calendar.getInstance())) {               // 현재시간이 지정된 시간보다 뒤인 경우
            calendar.add(Calendar.DATE, 1);                  // 지정된 시간에 1일 더한다
        }

        Date currentDateTime = calendar.getTime();
        Log.d("Alarm","알람 시간 : "+currentDateTime);

        SharedPreferences.Editor editor = getSharedPreferences("daily alarm",MODE_PRIVATE).edit();
        editor.putLong("nextNotifyTime",calendar.getTimeInMillis());        // 지정된 시간을 밀리초 단위로 저장
        editor.apply();


        dailyNotification(calendar);

        //calendar.clear();
    }

    void dailyNotification(Calendar calendar){
        Boolean dailyNotify = true;             // sharedReference - switch

        PackageManager pm = this.getPackageManager();

        ComponentName receiver = new ComponentName(this,DeviceBootReceiver.class);                              // DeviceBootReceiver
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);                                     // AlarmReceiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,alarmIntent,0);       // AlarmReceiver를 실행하는 팬딩인텐트(지금 앱이 아닌, 다른 프로세스에서 수행할때 사용)

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if(dailyNotify){    // 알람 허용했다면
            if(alarmManager != null)
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);     // 알람매니저에 AlarmReceiver PendingIntent 등록

            pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);    // 부팅 후 실행되는 DeviceBootReceiver 사용하게 설정
        }
        else{
            if(PendingIntent.getBroadcast(this,0,alarmIntent,0)!=null && alarmManager != null)
                alarmManager.cancel(pendingIntent);         // 알람 해제

            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);  // 부팅 후 실행되는 DeviceBootReceiver 사용하지않게 설정
        }

    }
}