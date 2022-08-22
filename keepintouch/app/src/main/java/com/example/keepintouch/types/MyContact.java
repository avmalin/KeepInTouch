package com.example.keepintouch.types;

import android.provider.ContactsContract;

import java.util.Date;

public class MyContact {
    private final String mName;
    private final String mNumber;
    private final String mPhotoSrc;
    private final Date mBirthday;
    private  PriorityType mPriorityType;
    private  int mContactId;
    private  long mLastCall;


    public Date lastUpdate;

    public MyContact()
    {
        mName=null;
        mNumber=null;
        mBirthday = null;
        mPhotoSrc = null;
        mPriorityType = PriorityType.NEVER;

    }
    public MyContact(int id,  PriorityType priorityType, long lastCall, String number,String photoSrc,String name){
        mContactId = id;
        mLastCall = lastCall;
        mPriorityType = priorityType;
        mName=name;
        mNumber=number;
        mBirthday = null;
        mPhotoSrc = photoSrc;
    }
    public MyContact(String name, String number, Date birthday, String photoSrc) {
        mName = name;
        mNumber = number;
        mBirthday = birthday;
        mPhotoSrc = photoSrc;
    }
//TODO: update constructor ContactsContract.contacts
    public MyContact(ContactsContract.Contacts contacts) {
        mName=null;
        mNumber=null;
        mBirthday = null;
        mPhotoSrc = null;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
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

    public long getLastCall() {
        return mLastCall;
    }

    public void setLastCall(long lastCall) {
        mLastCall = lastCall;

    }

    public int getContactId() {
        return mContactId;
    }

    public PriorityType getPriorityType() {
        return mPriorityType;
    }

    public void setContactId(int contactId) {
        mContactId = contactId;
    }
}
