package com.example.keepintouch.types;

import android.provider.ContactsContract;

import java.util.Date;

public class MyContact {
    private final String mName;
    private final String mNumber;
    private final String mPhotoSrc;
    private final Date mBirthday;

    public MyContact()
    {
        mName=null;
        mNumber=null;
        mBirthday = null;
        mPhotoSrc = null;
    }
    public MyContact(String name, String number, Date birthday, String photoSrc) {
        mName = name;
        mNumber = number;
        mBirthday = birthday;
        mPhotoSrc = photoSrc;
    }
//TODO: update ctor ContactsContract.contacts
    public MyContact(ContactsContract.Contacts contacts) {
        mName=null;
        mNumber=null;
        mBirthday = null;
        mPhotoSrc = null;
    }

    public String getName() {
        return mName;
    }

    public String getNumber() {
        return mNumber;
    }

    public Date getBirthday() {
        return mBirthday;
    }
    public String getPhotoSrc() {
        return mPhotoSrc;
    }
}
