package com.example.keepintouch.types;

import java.util.Date;

public class Contact {
    private final String mName;
    private final String mNumber;
    private final String mPhotoSrc;

    private final Date mBirthday;

    public Contact(String name, String number, Date birthday, String photoSrc) {
        mName = name;
        mNumber = number;
        mBirthday = birthday;
        mPhotoSrc = photoSrc;
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
