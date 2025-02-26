package com.avmalin.keepintouch.logic;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.avmalin.keepintouch.types.MyContact;
import com.avmalin.keepintouch.types.PriorityType;

import java.util.Date;
import java.util.List;


public class PriorityContactsImpl implements PriorityContacts {
    @Override
    public List<MyContact> getContactsByPriority() {
        return null;
    }

    @Override
    public void updateLastCall(Context context, MyContact contact, Date lastCallDate) {
        final ContentResolver contentResolver = context.getContentResolver();
        try (Cursor cursor = contentResolver.query(
                CallLog.Calls.CONTENT_FILTER_URI,
                new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DATE},
                CallLog.Calls.DATE + " > " + contact.lastUpdate +
                        " AND " + CallLog.Calls.NUMBER + " = " + contact.getNumber() +
                        " AND type = " + CallLog.Calls.INCOMING_TYPE +
                        " OR type = " + CallLog.Calls.OUTGOING_TYPE,
                null,
                CallLog.Calls.DEFAULT_SORT_ORDER)) {
            if (cursor == null || cursor.moveToLast()) {
                contact.setLastCall(cursor.getColumnIndex(CallLog.Calls.DATE));

            }
        }


    }

    @Override
    public void setPriorityType(MyContact contact, PriorityType prioritytype) {
        return;
    }


}
