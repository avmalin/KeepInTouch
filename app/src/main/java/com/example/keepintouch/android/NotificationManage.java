package com.example.keepintouch.android;

import android.content.Context;

import com.example.keepintouch.types.MyContact;
import com.example.keepintouch.types.PriorityType;

public interface NotificationManage {
    static NotificationManage getInstance(){
        return NotificationManageImp.getInstance();
    }

    void createNotification(Context context, long contactId, String contactName, long lastCall, PriorityType pt);

    void createChannel(Context context);
    void createNotificationFromContact(Context context, MyContact contact);

}