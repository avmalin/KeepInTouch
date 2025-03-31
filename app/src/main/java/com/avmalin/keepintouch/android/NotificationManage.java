package com.avmalin.keepintouch.android;

import android.content.Context;

import com.avmalin.keepintouch.types.MyContact;
import com.avmalin.keepintouch.types.PriorityType;

public interface NotificationManage {
    static NotificationManage getInstance(){
        return NotificationManageImp.getInstance();
    }



    void createChannel(Context context);
    void createNotificationFromContact(Context context, MyContact contact);

    void createBirthdayNotification(Context context, MyContact contact);
}