package com.example.keepintouch;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.keepintouch.android.NotificationManage;
import com.example.keepintouch.types.MyContactTable;
import com.example.keepintouch.types.PriorityType;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Create Notify", "Received the timed intent");
        long contactId = intent.getLongExtra("id",-1);
        if (contactId !=-1) {
            MyContactTable myContactTable = new MyContactTable(context);
            long lastCall = 0;//myContactTable.getLastCallById(contactId);
            int priority = intent.getIntExtra("priority", -1);
            if (priority != -1) {
                long timeToNote = lastCall + (long) priority * 1000 * 60 * 60 * 24; //1 day =  1000 * 60 * 60 * 24
                String name = intent.getStringExtra("name");

                if (timeToNote < System.currentTimeMillis()) {
                    String text = name + " מחכה כבר הרבה זמן לשיחה ממך!";
                    Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.applogo);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyKeepInTouch")
                            .setSmallIcon(R.mipmap.applogo)
                            .setContentTitle("הרבה זמן לא התקשרת")
                            .setContentText(text)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setLargeIcon(bMap);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    Intent intent1 = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent =  PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_MUTABLE);
                    builder.setContentIntent(pendingIntent);
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        notificationManager.notify((int) contactId, builder.build());

                    }
                }
                //set the next notification
                NotificationManage notificationManage = NotificationManage.getInstance();
                notificationManage.createNotification(context,contactId,name,lastCall, PriorityType.fromInt(priority));

            }
        }

    }
}
