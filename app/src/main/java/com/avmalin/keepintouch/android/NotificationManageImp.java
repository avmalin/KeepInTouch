package com.avmalin.keepintouch.android;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.avmalin.keepintouch.MainActivity;
import com.avmalin.keepintouch.NotificationReceiver;
import com.avmalin.keepintouch.R;
import com.avmalin.keepintouch.types.MyContact;
import com.avmalin.keepintouch.types.PriorityType;

public class NotificationManageImp implements NotificationManage {
    private static final NotificationManageImp instance = new NotificationManageImp();

    public static NotificationManageImp getInstance() {
        return instance;
    }



    @Override
    public void createChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                "notifyKeepInTouch",
                "remainder to keep in touch",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("channel for remainder to keep in touch your friends");

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void createNotificationFromContact(Context context, MyContact contact) {
        String text = contact.getName() + " מחכה כבר הרבה זמן לשיחה ממך!";
        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.applogo_round);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyKeepInTouch")
                .setSmallIcon(R.drawable.applogo_bg_removed)
                .setContentTitle("הרבה זמן לא התקשרת")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(bMap);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent intent1 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent =  PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify((int) contact.getContactId(), builder.build());
            Log.d("notification", "notification sent");

        }
        else Log.d("notification", "no permission");
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

}