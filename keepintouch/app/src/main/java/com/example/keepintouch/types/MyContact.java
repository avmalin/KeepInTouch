package com.example.keepintouch.types;

import android.provider.ContactsContract;

import java.util.Date;

public class MyContact {
    private String mName;
    private String mNumber;
    private String mPhotoSrc;
    private Date mBirthday;
    private PriorityType mPriorityType;
    private long mContactId;
    private long mLastCall;

    public void setName(String name) {
        mName = name;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public void setPhotoSrc(String photoSrc) {
        mPhotoSrc = photoSrc;
    }

    public void setBirthday(Date birthday) {
        mBirthday = birthday;
    }

    public void setPriorityType(PriorityType priorityType) {
        mPriorityType = priorityType;
    }



    public Date lastUpdate;

    public MyContact()
    {
        mName=null;
        mNumber=null;
        mBirthday = null;
        mPhotoSrc = null;
        mPriorityType = PriorityType.NEVER;
        mLastCall = 0;

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
        mLastCall = 0;
    }
    public MyContact(long id, PriorityType type, String number)
    {
        mContactId = id;
        mPriorityType = type;
        mLastCall = 0;
        mNumber = number;
    }
    public MyContact(long id, PriorityType type)
    {
        mContactId = id;
        mPriorityType = type;
    }
//TODO: update constructor ContactsContract.contacts
// TODO: support few phone number to the same contacts,
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

    public long getContactId() {
        return mContactId;
    }

    public PriorityType getPriorityType() {
        return mPriorityType;
    }

    public void setContactId(int contactId) {
        mContactId = contactId;
    }
}
