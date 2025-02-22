package com.example.keepintouch.types;

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
    public MyContact(long id,  PriorityType priorityType, long lastCall, String number,String photoSrc,String name){
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
        mPriorityType = null;
    }
    public MyContact(long id, PriorityType type, String number, String name, String photoSrc)
    {
        mContactId = id;
        mPriorityType = type;
        mLastCall = 0;
        mPhotoSrc = photoSrc;
        mNumber = number;
        mName = name;
    }
    public MyContact(long id, PriorityType type, String name, String photoSrc)
    {
        mContactId = id;
        mPriorityType = type;
        mLastCall = 0;
        mPhotoSrc = photoSrc;
        mNumber = null;
        mName = name;
    }
    public MyContact(long id, PriorityType type)
    {
        mContactId = id;
        mPriorityType = type;
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
