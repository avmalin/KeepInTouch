package com.example.keepintouch.logic;

import com.example.keepintouch.types.MyContact;
import com.example.keepintouch.types.PriorityType;

import java.util.Date;
import java.util.List;

public interface PriorityContacts {
    List<MyContact> getContactsByPriority();
    void updateLastCall(MyContact contact, Date lastCallDate);
    void setPriorityType(MyContact contact, PriorityType prioritytype);
}
