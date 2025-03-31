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

import com.avmalin.keepintouch.MainActivity;
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
    public void createBirthdayNotification(Context context, MyContact contact) {
        String text = "היום יום ההולדת של" + contact.getName() + "!";
        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.applogo_round);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyKeepInTouch")
                .setSmallIcon(R.drawable.applogo_bg_removed)
                .setContentTitle("מזל טוב!")
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

}