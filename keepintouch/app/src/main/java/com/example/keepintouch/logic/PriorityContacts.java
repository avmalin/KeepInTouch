package com.example.keepintouch.logic;

import com.example.keepintouch.types.Contact;
import com.example.keepintouch.types.PriorityType;

import java.util.Date;
import java.util.List;

public interface PriorityContacts {
    List<Contact> getContactsByPriority();
    void updateLastCall(Contact contact, Date lastCallDate);
    void setPriorityType(Contact contact, PriorityType prioritytype);
}
