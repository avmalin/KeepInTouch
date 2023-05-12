package com.example.keepintouch.types;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class MyContactTable extends SQLiteOpenHelper {
    //database data

    private static final String DB_NAME = "my_contacts_db.db";
    private static final int DB_VERSION = 3;
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
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts._ID
    };

    private static final String[] FROM_COLUMNS_CALL = {CallLog.Calls.NUMBER, CallLog.Calls.DATE};
    private static long lastDateUpdate = 0;
    public static long getLastDateUpdate() {
        return lastDateUpdate;
    }
    public static Context sContext;

    public static void setLastDateUpdate(long lastDateUpdate) {
        MyContactTable.lastDateUpdate = lastDateUpdate;
    }

    // TODO  constructor, update functions.
    //TODO  update the table if contact if is delete.
   public MyContactTable (Context context)
   {
       super(context, DB_NAME,null,DB_VERSION);
       sContext = context;
   }
   public MyContactTable(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        sContext = context;
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
                + PHONE_COL + " TEXT, "
                + PHOTO_SRC_COL + " TEXT,  "
                + LAST_CALL_COL +  " INTEGER, "
                + PRIORITY_TYPE_COL + " INTEGER)";
        db.execSQL(query);
        Log.i(null,"DataBase has been created.");
    }
    public ArrayList<MyContact> getContactList()
    {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        ArrayList<MyContact> list = new ArrayList<>();
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
                            cursor.getInt(cursor.getColumnIndexOrThrow(CONTACT_ID_COL)),
                            PriorityType.fromInt(cursor.getInt(cursor.getColumnIndexOrThrow(PRIORITY_TYPE_COL))),
                            cursor.getInt(cursor.getColumnIndexOrThrow(LAST_CALL_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(PHONE_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(PHOTO_SRC_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(NAME_COL))));
                }while (cursor.moveToNext());
            }
        }
        finally {
            if (db != null) {
                db.close();
            }
            if (cursor!=null) {
                cursor.close();
            }
        }
        return list;
    }
    public HashMap<Long, MyContact> getContactMap()
    {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        HashMap<Long, MyContact> contactMap = new HashMap<>();
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
                    contactMap.put(cursor.getLong(cursor.getColumnIndexOrThrow(CONTACT_ID_COL)),new MyContact(
                            cursor.getLong(cursor.getColumnIndexOrThrow(CONTACT_ID_COL)),
                            PriorityType.fromInt(cursor.getInt(cursor.getColumnIndexOrThrow(PRIORITY_TYPE_COL))),
                            cursor.getInt(cursor.getColumnIndexOrThrow(LAST_CALL_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(PHONE_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(PHOTO_SRC_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(NAME_COL))));
                }while (cursor.moveToNext());
            }
        }
        finally {
            if ((db != null)) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }
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
        cv.put(NAME_COL, myContact.getName());
        cv.put(PHONE_COL,myContact.getNumber());
        cv.put(PHOTO_SRC_COL, myContact.getPhotoSrc());
        cv.put(LAST_CALL_COL, myContact.getLastCall());
        cv.put(PRIORITY_TYPE_COL, myContact.getPriorityType().toString());
        return cv;
    }

    private long getLastCallById(long contact_id)
    {
        ContentResolver contentResolver = sContext.getContentResolver();
        Cursor cursor = null;
        try{
            String[] projecton = {CallLog.Calls.DATE};
            String[] selection ;
        }
        finally {

        }
        return 0;
    }
    private long getLastCallById(String contact_number) {
        ContentResolver contentResolver = sContext.getContentResolver();
        Cursor cursor = null;
        long lastCallTime = 0;
        String number = null;
        try {
            String[] projection = {CallLog.Calls.DATE};
            String selection = ContactsContract.Contacts._ID + " = ?";
            String[] selectionArgs = {contact_number};

            cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs,CallLog.Calls.DATE + " DESC LIMIT 1");

            if (cursor != null && cursor.moveToFirst()) {
                lastCallTime = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                // Do something with the last call time, e.g. display it in a TextView
            }//todo: fix get last date.

        }
        finally {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        return lastCallTime;
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
        SQLiteDatabase contactDb = null;
        long contact_id = 0;
        HashMap<Long, MyContact> contactMap = getContactMap();
        try{
            contactDb = this.getWritableDatabase();
            cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    FROM_CONTACT_COLUMNS,
                    null,
                    null,
                    null);

            if(cursor != null && cursor.moveToFirst())
            {
                int nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
                int photoIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI);
                int idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
                do {
                    if(contactMap.containsKey(cursor.getLong(idIndex)))
                    {
                        MyContact c = contactMap.get(cursor.getLong(idIndex));//id
                        c.setName(cursor.getString(nameIndex));//name
                        c.setPhotoSrc(cursor.getString(photoIndex));//photo);
                        contactMap.put(c.getContactId(),c);
                    }
                }while (cursor.moveToNext());
            }

            // update LAST CALL
            lastDateUpdate = 0;//TODO: delete me!
            cursor = contentResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    FROM_COLUMNS_CALL,
                    CallLog.Calls.DATE + " > " + lastDateUpdate +
                    " AND type = " + CallLog.Calls.INCOMING_TYPE +
                    " OR type = " + CallLog.Calls.OUTGOING_TYPE,
                    null,
                    CallLog.Calls.DATE + " DESC");
            if (cursor != null && cursor.moveToFirst())
            {
                do {
                    contact_id = getIdByNumber(context, cursor.getString(0));
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
        catch (SQLException e)
        {
            Log.e("DatabaseError", "Error accessing database", e);
        }
        finally {
            if (cursor.moveToLast()) {
                setLastDateUpdate(cursor.getLong(1));//date
            }
            if (contactDb != null && contactDb.isOpen()){
                contactDb.close();
            }
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
    private Long getIdByNumber(Context context, String number) {
       ContentResolver contentResolver = context.getContentResolver();
       Cursor cursor  = null;
       long id = -1;
       try {
           Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));
           cursor = contentResolver.query(uri, new String []{ContactsContract.Contacts._ID},null,null,null);
           /*cursor = contentResolver.query(
                   ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                   new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID},
                   ContactsContract.CommonDataKinds.Phone.NUMBER + " = " + number,
                   null,
                   null);*/
           if (cursor != null && cursor.moveToFirst()) {
               int idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
               id = cursor.getLong(idIndex);
           }
       }
       finally {
           if (cursor != null) {
               cursor.close();
           }
       }
       return id;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DETAILS_TABLE_NAME);
        onCreate(db);
    }

    // this function gets contact map with id and priority, find the last call date and update the table.
    public void updateTableFromMap(Map<Long, MyContact> contactMap) {
        SQLiteDatabase db;
        Cursor cursor = null;
        try{
            db = getWritableDatabase();
            for (MyContact c: contactMap.values()) {
                if(c.getPriorityType() == PriorityType.NEVER)
                {
                    db.delete(CONTACTS_TABLE_NAME,CONTACT_ID_COL + " = " + c.getContactId(),null);
                }
                else {
                    c.setLastCall(getLastCallById(c.getNumber()));// update the last Call because of lastUpdate parameter.
                    ContentValues cv = contactToVc(c);
                    int result = db.update(CONTACTS_TABLE_NAME, cv, CONTACT_ID_COL + " = " + c.getContactId(),null);
                    if (result == 0)
                    {
                        db.insert(CONTACTS_TABLE_NAME,null,cv);
                    }

                }
            }
            db.close();
        }
        catch (Exception e){
            Log.e(null,e.toString());
        }
    }
}
