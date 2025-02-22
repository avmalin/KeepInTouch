package com.example.keepintouch.android;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import com.example.keepintouch.NotificationReceiver;
import com.example.keepintouch.types.PriorityType;

public class NotificationManageImp implements NotificationManage {
    private static NotificationManageImp instance = new NotificationManageImp();

    public static NotificationManageImp getInstance() {
        return instance;
    }


    @Override
    public void createNotification(Context context, long contactId, String contactName, long lastCall, PriorityType pt) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("id", contactId);
        intent.putExtra("name", contactName);
        intent.putExtra("priority", pt.compValue());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) contactId,
                intent,
                PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        long timeToNote = lastCall + (long) pt.compValue() * 1000 * 60 * 60 * 24; //1 day =  1000 * 60 * 60 * 24
        //timeToNote = System.currentTimeMillis(); // for checks
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                timeToNote,
                pendingIntent);
    }

    @Override
    public void createChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                "notifyKeepInTouch",
                "My Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("channel for keepInTouchNotifications");
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}