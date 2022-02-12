package com.example.keepintouch.types;

import java.util.Date;

public class Contact {
    private final String mName;
    private final String mNumber;
    private final Date mBirthday;

    public Contact(String name, String number, Date birthday) {
        mName = name;
        mNumber = number;
        mBirthday = birthday;
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
}
