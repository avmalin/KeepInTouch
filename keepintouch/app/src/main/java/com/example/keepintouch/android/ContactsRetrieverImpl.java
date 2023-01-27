package com.example.keepintouch.android;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.example.keepintouch.types.MyContact;
import com.example.keepintouch.types.MyContactTable;

import java.util.List;

public class ContactsRetrieverImpl implements ContactsRetriever {
    private MyContactTable mMyContactTable;
    private List<MyContact> mMyContacts;
    private static long lastUpdate;
    private Context mContext;



    public ContactsRetrieverImpl(Context context){
        mMyContactTable = new MyContactTable(context);
        mMyContacts = mMyContactTable.getContactList();
        mContext = context;
    }
    @Override
    public List<MyContact> getContactsFromAndroid() {
        return null;
    }

    @Override
    public MyContact getContactByIdFromAndroid(int id) {
        return null;
    }

    @Override
    public void saveContactToDB(List<MyContact> myContactList) {
        mMyContactTable.addContact(myContactList);
    }
    public void updateContactListLastCall()
    {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = null;

        try {
            cursor = contentResolver.query(
                    CallLog.Calls.CONTENT_FILTER_URI,
                    new String[]{CallLog.Calls.NUMBER,CallLog.Calls.DATE},
                    CallLog.Calls.DATE + " > " + lastUpdate +
                            " AND type = " + CallLog.Calls.INCOMING_TYPE +
                            " OR type = " + CallLog.Calls.OUTGOING_TYPE,
                    null,
                    CallLog.Calls.DEFAULT_SORT_ORDER);
            if (cursor != null && cursor.moveToFirst())
            {
                do {
                    for (MyContact m:mMyContacts) {
                        if(m.getNumber().equals(cursor.getString(0)))
                            m.setLastCall(cursor.getInt(1));
                    }
                }while (cursor.moveToNext());
            }

        }finally {
            assert cursor != null;
            cursor.close();
        }

    }
}
