package com.example.keepintouch.types;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CallLog;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

public class MyContactTable extends SQLiteOpenHelper {
    private static final String DB_NAME = "my_contacts_db.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "my_contacts_table ";
    private static final String ID_COL = "_id";

    public static final String CONTACT_ID_COL = "contact_id";
    public static final String LAST_CALL_COL = "last_call_date";
    public static final String PRIORITY_TYPE_COL = "priority_type";
    private static long lastDateUpdate = 0;

    public static long getLastDateUpdate() {
        return lastDateUpdate;
    }

    public static void setLastDateUpdate(long lastDateUpdate) {
        MyContactTable.lastDateUpdate = lastDateUpdate;
    }

    // TODO  constructor, update functions.
    //TODO  update the table if contact if is delete.
   public MyContactTable (Context context)
   {
       super(context, DB_NAME,null,DB_VERSION);
   }
    public MyContactTable(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CONTACT_ID_COL +  " INTEGER, "
                + LAST_CALL_COL +  " INTEGER, "
                + PRIORITY_TYPE_COL + " INTEGER)";
        db.execSQL(query);
    }
    public boolean addContact(int contact_id, long last_call, PriorityType priorityType)
    {
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID_COL,contact_id);
        cv.put(LAST_CALL_COL, last_call);
        cv.put(PRIORITY_TYPE_COL, priorityType.toString());
        long insert = db.insert(TABLE_NAME, null, cv);
        db.close();
        if (insert == -1)
            return false;
        else
            return true;
    }

    public boolean addContact(int contact_id, PriorityType priorityType)
    {
        return addContact(contact_id, getLastCallById(contact_id),priorityType);
    }

    private long getLastCallById(int contact_id) {
        return 0; // TODO implement the function
    }

    //update the last_call column
    //recalculation the priority
    public void updateAllTable(Context context)
    {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor callCursor = null;
        SQLiteDatabase contactDb = this.getWritableDatabase();
        int contact_id = 0;
        try{
            callCursor = contentResolver.query(
                    CallLog.Calls.CONTENT_FILTER_URI,
                    new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DATE},
                    CallLog.Calls.DATE + " > " + lastDateUpdate +
                    " AND type = " + CallLog.Calls.INCOMING_TYPE +
                    " OR type = " + CallLog.Calls.OUTGOING_TYPE,
                    null,
                    CallLog.Calls.DEFAULT_SORT_ORDER);
            if (callCursor == null || callCursor.moveToFirst())
            {
                do {
                    contact_id = getIdByNumber(context, callCursor.getInt(0));
                    ContentValues cv = new ContentValues();
                    cv.put(LAST_CALL_COL,callCursor.getLong(1));//date
                    contactDb.update(
                            TABLE_NAME,
                            cv,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contact_id,
                            null);
                }while (callCursor.moveToNext());

            }

        }
        finally {
            callCursor.moveToLast();
            setLastDateUpdate(callCursor.getLong(1));//date
            contactDb.close();
            callCursor.close();
        }
        calculationPriority(context);

    }

    private void calculationPriority(Context context) {

    }

    private int getIdByNumber(Context context, int number) {
       ContentResolver contentResolver = context.getContentResolver();
       Cursor cursor  = null;
       int id = -1;
       try {
           cursor = contentResolver.query(
                   ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                   new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID},
                   ContactsContract.CommonDataKinds.Phone.NUMBER + " = " + number,
                   null,
                   null);
           if (cursor == null || cursor.moveToFirst())
               id = cursor.getInt(0);
       }
       finally {
           cursor.close();
       }
       return id;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
