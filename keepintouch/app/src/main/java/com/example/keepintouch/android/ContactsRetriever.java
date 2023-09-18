package com.example.keepintouch.android;

import com.example.keepintouch.types.MyContact;

import java.util.List;

public interface ContactsRetriever {
    List<MyContact> getContactsFromAndroid();
    MyContact getContactByIdFromAndroid(int id);
    void saveContactToDB(List<MyContact> myContacts);

    }