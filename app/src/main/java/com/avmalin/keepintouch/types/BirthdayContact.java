package com.avmalin.keepintouch.types;

import androidx.annotation.NonNull;

public class BirthdayContact {
    private long contactId;
    private String birthday;
    private int isBirthday;

    public BirthdayContact(long contactId, String birthday, int isBirthday) {
        this.contactId = contactId;
        this.birthday = birthday;
        this.isBirthday = isBirthday;
    }

    @NonNull
    @Override
    public String toString() {
        String[] parts = birthday.split("-");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

       return HebrewDateConverter.convertToHebrewDate(year,month,day);
    }


    public BirthdayContact(long contactId, String birthday) {
        this.contactId = contactId;
        this.birthday = birthday;
        this.isBirthday = 0;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int isBirthday() {
        return isBirthday;
    }

    public void setBirthday(int birthday) {
        isBirthday = birthday;
    }
}
