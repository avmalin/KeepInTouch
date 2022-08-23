package com.example.keepintouch.types;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyContactTable extends SQLiteOpenHelper {
    //database data

    private static final String DB_NAME = "my_contacts_db.db";
    private static final int DB_VERSION = 1;
    public static final String DETAILS_TABLE_NAME = "details_table";
    public static final String TABLE_NAME = "table_name";
    public static final String LAST_UPDATE = "last_update";

    //table data
    public static final String CONTACTS_TABLE_NAME = "my_contacts_table ";
    private static final String ID_COL = "_id";
    public static final String CONTACT_ID_COL = "contact_id";
    public static final String PHONE_COL = "phone";
    public static final String PHOTO_SRC_COL = "photo_src";
    public static final String NAME_COL = "name";
    public static final String LAST_CALL_COL = "last_call_date";
    public static final String PRIORITY_TYPE_COL = "priority_type";
    public static final String PRIORITY_COL = "priority";
    private static final String[] FROM_COLUMNS = {
            CONTACT_ID_COL,
            PRIORITY_TYPE_COL,
            LAST_CALL_COL,
            PHONE_COL,
            PHOTO_SRC_COL,
            NAME_COL};
    private final static String[] FROM_CONTACT_COLUMNS = {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    };
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

    // create empty table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + DETAILS_TABLE_NAME + " ("
            + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TABLE_NAME + "TEXT, "
            + LAST_UPDATE + "INTEGER)";
        db.execSQL(query);

        query = "CREATE TABLE " + CONTACTS_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CONTACT_ID_COL +  " INTEGER, "
                + NAME_COL + " TEXT, "
                + PHONE_COL + " INTEGER, "
                + PHOTO_SRC_COL + "TEXT,  "
                + LAST_CALL_COL +  " INTEGER, "
                + PRIORITY_TYPE_COL + " INTEGER)";
        db.execSQL(query);
        Log.i(null,"DataBase has been created.");
    }
    public ArrayList<MyContact> getContactList()
    {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        ArrayList<MyContact> list = null;
        try {
            db = getReadableDatabase();

            cursor = db.query(
                    CONTACTS_TABLE_NAME,
                    FROM_COLUMNS,
                    null,
                    null,
                    null,
                    null,
                    null);
            if(cursor != null && cursor.moveToFirst())
            {
                do {
                    list.add(new MyContact(
                            cursor.getInt(0),
                            PriorityType.fromInt(cursor.getInt(1)),
                            cursor.getInt(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5)));
                }while (cursor.moveToNext());
            }
        }
        finally {
            db.close();
            cursor.close();
        }
        return list;
    }
    public Map<Integer,MyContact> getContactMap()
    {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Map<Integer,MyContact> contactMap = null;
        try {
            db = getReadableDatabase();

            cursor = db.query(
                    CONTACTS_TABLE_NAME,
                    FROM_COLUMNS,
                    null,
                    null,
                    null,
                    null,
                    null);
            if(cursor != null && cursor.moveToFirst())
            {
                do {
                    contactMap.put(cursor.getInt(0),new MyContact(
                            cursor.getInt(0), //id
                            PriorityType.fromInt(cursor.getInt(1)),//priorityType
                            cursor.getInt(2),//last call
                            cursor.getString(3),//phone number
                            cursor.getString(4),//photo
                            cursor.getString(5)));//name
                }while (cursor.moveToNext());
            }
        }
        finally {
            db.close();
            cursor.close();
        }
        return contactMap;
    }
    //add MyContact to the table.
    public void addContact(MyContact myContact)
    {
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues cv = contactToVc(myContact);
        db.insert(CONTACTS_TABLE_NAME,null,cv);
        db.close();

    }

    private ContentValues contactToVc(MyContact myContact) {
        ContentValues cv = new ContentValues();
        cv.put(CONTACT_ID_COL,myContact.getContactId());
//        cv.put(NAME_COL, myContact.getName());
//        cv.put(PHONE_COL,myContact.getNumber());
//        cv.put(PHOTO_SRC_COL, myContact.getPhotoSrc());
        cv.put(LAST_CALL_COL, myContact.getLastCall());
        cv.put(PRIORITY_TYPE_COL, myContact.getPriorityType().toString());
        return cv;
    }


    private long getLastCallById(int contact_id) {
        return 0; // TODO implement the function
    }

    //update the last_call column
    //recalculation the priority

    /**
     * this function update all contact details and the last call.
     * TODO handle delete contacts.
     * TODO smart update by last update time.
     */
    public void updateAllTable(Context context)
    {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        ContentValues cv;
        List<MyContact> l;
        SQLiteDatabase contactDb = this.getWritableDatabase();
        int contact_id = 0;
        Map<Integer,MyContact> contactMap = getContactMap();
        try{
            cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    FROM_CONTACT_COLUMNS,
                    null,
                    null,
                    null);
            if(cursor != null && cursor.moveToFirst())
                do {
                    if(contactMap.containsKey(cursor.getInt(3)))
                    {
                        MyContact c = contactMap.get(cursor.getInt(3));
                        c.setNumber(cursor.getString(1));//phone number
                        c.setName(cursor.getString(0));//name
                        c.setPhotoSrc(cursor.getString(2));//photo);
                        contactMap.put(c.getContactId(),c);
                    }
                }while (cursor.moveToNext());


            cursor = contentResolver.query(
                    CallLog.Calls.CONTENT_FILTER_URI,
                    new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DATE},
                    CallLog.Calls.DATE + " > " + lastDateUpdate +
                    " AND type = " + CallLog.Calls.INCOMING_TYPE +
                    " OR type = " + CallLog.Calls.OUTGOING_TYPE,
                    null,
                    CallLog.Calls.DEFAULT_SORT_ORDER);
            if (cursor != null && cursor.moveToFirst())
            {
                do {
                    contact_id = getIdByNumber(context, cursor.getInt(0));
                    if (contactMap.containsKey(contact_id))
                    {
                        MyContact c = contactMap.get(contact_id);
                        c.setLastCall(cursor.getLong(1));
                        contactMap.put(contact_id,c);
                    }
                }while (cursor.moveToNext());

            }

            for (MyContact c:contactMap.values()) {
                cv = contactToVc(c);
                contactDb.update(CONTACTS_TABLE_NAME,cv,CONTACT_ID_COL + " = " + c.getContactId(),null);
            }

        }
        finally {
            cursor.moveToLast();
            setLastDateUpdate(cursor.getLong(1));//date
            contactDb.close();
            cursor.close();
        }
        calculationPriority(context);
        for (MyContact c:contactMap.values()) {

        }

    }

    private void calculationPriority(Context context) {

    }

    public void addContact(List<MyContact> myContacts)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int howMatchUpdate=  0;
        
        if (myContacts != null) {
            for (MyContact contact:myContacts) {
                howMatchUpdate = db.update(
                        CONTACTS_TABLE_NAME,
                        contactToVc(contact),
                        CONTACT_ID_COL + " = " + contact.getContactId(),
                        null);
                if(howMatchUpdate == 0)
                    db.insert(CONTACTS_TABLE_NAME,null,contactToVc(contact));
            }
        }
        db.close();
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
           if (cursor != null && cursor.moveToFirst())
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
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        onCreate(db);
    }

    public void updateTableFromMap(Map<Integer, MyContact> contactMap) {
        SQLiteDatabase db;
        try{
            db = getWritableDatabase();
            for (MyContact c: contactMap.values()) {
                if(c.getPriorityType() == PriorityType.NEVER)
                {
                    db.delete(CONTACTS_TABLE_NAME,ID_COL + " = " + c.getContactId(),null);
                }
                else {
                    ContentValues cv = contactToVc(c);
                    db.update(CONTACTS_TABLE_NAME, cv, ID_COL + " = " + c.getContactId(),null);
                }
            }
            db.close();
        }
        catch (Exception e){
            Log.e(null,e.toString());
        }
    }
}
