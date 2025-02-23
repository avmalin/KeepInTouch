package com.example.keepintouch.logic;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.keepintouch.types.MyContact;
import com.example.keepintouch.types.MyContactTable;
import com.example.keepintouch.types.PriorityType;

import java.util.ArrayList;

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
        //TODO: CHECK IF NEEDS TO update all
        for (MyContact contact : contactList) {
            if(needsToNotification(contact,myContactTable)){
                //TODO: notification
            }
        }
        return Result.success();
    }

    private boolean needsToNotification(MyContact contact, MyContactTable myContactTable) { {
        long lastCall = contact.getLastCall();
        int priority = (contact.getPriorityType().compValue());
        long lastNotification = contact.getLastNotification();
        return false;
    }
}
