package com.avmalin.keepintouch.logic;

import android.content.Context;

import com.avmalin.keepintouch.types.MyContact;
import com.avmalin.keepintouch.types.PriorityType;

import java.util.Date;
import java.util.List;

public interface PriorityContacts {
    List<MyContact> getContactsByPriority();

    void updateLastCall(Context context, MyContact contact, Date lastCallDate);

    void setPriorityType(MyContact contact, PriorityType prioritytype);
}
