package com.example.keepintouch;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
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

import com.example.keepintouch.types.MyContactTable;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Create Notify", "Received the timed intent");
        long contactId = intent.getLongExtra("id",-1);
        if (contactId !=-1) {
            MyContactTable myContactTable = new MyContactTable(context);
            long lastCall = myContactTable.getLastCallById(contactId);
            int priority = intent.getIntExtra("priority", -1);
            if (priority != -1) {
                long timeToNote = lastCall + priority * 1000 * 60 * 60 * 24; //1 day =  1000 * 60 * 60 * 24
                String name = intent.getStringExtra("name");

                if (timeToNote < System.currentTimeMillis()) {
                    String text = name + " מחכה כבר הרבה זמן לשיחה ממך!";
                    Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.applogo_round);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyKeepInTouch")
                            .setSmallIcon(R.mipmap.applogo_round)
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
                else{
                    createNotification(context,contactId,name,lastCall,priority);
                }
            }
        }

    }
    public void createNotification(Context context, Long contactId, String contactName, long lastCall, int pt){
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("id",contactId);
        intent.putExtra("name",contactName);
        intent.putExtra("priority",pt);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        long timeToNote = lastCall + pt* 1000 * 60 * 60 *24; //1 day =  1000 * 60 * 60 * 24
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                timeToNote,
                pendingIntent);
    }
}
