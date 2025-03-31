package com.avmalin.keepintouch.logic;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.avmalin.keepintouch.android.NotificationManage;
import com.avmalin.keepintouch.types.MyContactTable;
import com.avmalin.keepintouch.types.MyContact;

import java.util.ArrayList;
import java.util.List;

public class DailyWorker extends Worker {
    private MyContactTable myContactTable;

    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.d("DailyWorker", "Work is running at 12:00!");
        myContactTable = new MyContactTable(getApplicationContext());

        ArrayList<MyContact> contactList = myContactTable.getContactList();
        NotificationManage mNotificationManager = NotificationManage.getInstance();
        Context context = getApplicationContext();
        //TODO: CHECK IF NEEDS TO update all
        for (MyContact contact : contactList) {
            if (needsToNotification(contact, myContactTable)) {
                mNotificationManager.createNotificationFromContact((context), contact);
                Log.d("DailyWorker", "notification " + contact.getName());
                myContactTable.setLastNotificationById(contact.getContactId(), System.currentTimeMillis());
            }
        }
        //TODO: handle birthday notification
        List<Long> birthdayListID = myContactTable.getTodayBirthday();
        for (Long id : birthdayListID) {
            createBirthdayNotification(myContactTable.getContactById(id));
        }

        return Result.success();
    }

    private void createBirthdayNotification(MyContact contact) {
        NotificationManage mNotificationManager = NotificationManage.getInstance();
        Context context = getApplicationContext();
        mNotificationManager.createBirthdayNotification((context), contact);
    }

    private boolean needsToNotification(MyContact contact, MyContactTable myContactTable) {
        long lastCall = contact.getLastCall();
        int priority = (contact.getPriorityType().compValue());
        long lastNotification = myContactTable.getLastNotificationById(contact.getContactId());
        long timeToNotification = lastCall + ((long) priority * 1000 * 60 * 60 * 24); // 1 day = 24 hours = 24 * 60 * 60 * 1000 milliseconds
        long currentTime = System.currentTimeMillis();
        long timeToNotification2 = lastNotification + (7 * 1000 * 60 * 60 * 24); // 1 day = 24 hours = 24 * 60 * 60 * 1000 milliseconds
        return timeToNotification < currentTime && timeToNotification2 <currentTime ;

    }
}

