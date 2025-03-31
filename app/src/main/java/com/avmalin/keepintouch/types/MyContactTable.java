package com.avmalin.keepintouch.types;

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

import com.avmalin.keepintouch.android.NotificationManage;
import com.avmalin.keepintouch.logic.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyContactTable extends SQLiteOpenHelper {
    //database data

    private static final String DB_NAME = "my_contacts_db.db";
    private static final int DB_VERSION =10;
    public static final String DETAILS_TABLE_NAME = "details_table";
    public static final String TABLE_NAME = "table_name";
    public static final String LAST_UPDATE = "last_update";

    // ** table data
    // CONTACT TABLE
    public static final String CONTACTS_TABLE_NAME = "my_contacts_table ";
    private static final String ID_COL = "_id";
    public static final String CONTACT_ID_COL = "contact_id";
    public static final String PHONE_COL = "phone";
    public static final String PHOTO_SRC_COL = "photo_src";
    public static final String NAME_COL = "name";
    public static final String LAST_CALL_COL = "last_call_date";
    public static final String PRIORITY_TYPE_COL = "priority_type";
    // NOTIFICATION TABLE
    public static final String NOTIFICATION_TABLE_NAME = "notification_table";
    public static final String NOTIFICATION_ID_COL = "notification_id";
    public static final String LAST_TIME_NOTIFICATION_COL = "last_time_notification";
    public static final String BIRTHDAY_TABLE_NAME = "birthday_table";
    public static final String BIRTHDAY_ID_COL = "_id";
    public static final String BIRTHDAY_COL = "birthday";
    public static final String IS_BIRTHDAY_COL = "is_birthday";


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
            ContactsContract.Contacts._ID,

    };

    private static final String[] FROM_COLUMNS_CALL = {CallLog.Calls.NUMBER, CallLog.Calls.DATE};

    private static final String IS_ANSWERED_CALL =
            "( "+ CallLog.Calls.TYPE+ " = " + CallLog.Calls.INCOMING_TYPE +
                    " OR "+ CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE+")" +
                    " AND " +CallLog.Calls.DURATION+ " IS NOT NULL" +
                    " AND " +CallLog.Calls.DURATION  +" != 0";

    public  Context sContext;



    public MyContactTable(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
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
                + TABLE_NAME + " TEXT, "
                + LAST_UPDATE + " INTEGER)";
        db.execSQL(query);

        query = "CREATE TABLE " + CONTACTS_TABLE_NAME + " ("
                + CONTACT_ID_COL + " INTEGER PRIMARY KEY, "
                + NAME_COL + " TEXT, "
                + PHONE_COL + " TEXT, "
                + PHOTO_SRC_COL + " TEXT,  "
                + LAST_CALL_COL + " INTEGER, "
                + PRIORITY_TYPE_COL + " TEXT)";
        db.execSQL(query);

        query = "CREATE TABLE " + NOTIFICATION_TABLE_NAME + " ("
                + NOTIFICATION_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CONTACT_ID_COL + " INTEGER NOT NULL, "
                + LAST_TIME_NOTIFICATION_COL + " INTEGER,"
                + "FOREIGN KEY (" + CONTACT_ID_COL + ") REFERENCES " + CONTACTS_TABLE_NAME + "(" + CONTACT_ID_COL + ") ON DELETE CASCADE)";
        db.execSQL(query);

        query = "CREATE TABLE " + BIRTHDAY_TABLE_NAME + " ("
                + BIRTHDAY_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CONTACT_ID_COL + " INTEGER UNIQUE NOT NULL, "
                + BIRTHDAY_COL + " TEXT, "
                + IS_BIRTHDAY_COL + " INTEGER,"
                + "FOREIGN KEY (" + CONTACT_ID_COL + ") REFERENCES " + CONTACTS_TABLE_NAME + "(" + CONTACT_ID_COL + ") ON DELETE CASCADE)";
        db.execSQL(query);
        Log.i(null, "DataBase has been created.");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON");
    }

    public ArrayList<MyContact> getContactList() {
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
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(new MyContact(
                            cursor.getInt(cursor.getColumnIndexOrThrow(CONTACT_ID_COL)),
                            PriorityType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(PRIORITY_TYPE_COL))),
                            cursor.getLong(cursor.getColumnIndexOrThrow(LAST_CALL_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(PHONE_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(PHOTO_SRC_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(NAME_COL))));
                } while (cursor.moveToNext());
            }
        } finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public HashMap<Long, MyContact> getContactMap() {
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
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    contactMap.put(cursor.getLong(cursor.getColumnIndexOrThrow(CONTACT_ID_COL)), new MyContact(
                            cursor.getLong(cursor.getColumnIndexOrThrow(CONTACT_ID_COL)),
                            PriorityType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(PRIORITY_TYPE_COL))),
                            cursor.getLong(cursor.getColumnIndexOrThrow(LAST_CALL_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(PHONE_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(PHOTO_SRC_COL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(NAME_COL))));
                } while (cursor.moveToNext());
            }
        } finally {
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


    private ContentValues contactToVc(MyContact myContact) {
        ContentValues cv = new ContentValues();
        cv.put(CONTACT_ID_COL, myContact.getContactId());
        cv.put(NAME_COL, myContact.getName());
        cv.put(PHONE_COL, myContact.getNumber());
        cv.put(PHOTO_SRC_COL, myContact.getPhotoSrc());
        cv.put(LAST_CALL_COL, myContact.getLastCall());
        return cv;
    }

    public Long getLastCallById(long contact_id) {
        ContentResolver contentResolver = sContext.getContentResolver();
        Cursor cursor = null;
        long lastCall = 0;
        List<String> numbersList = new ArrayList<>();
        try {
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ";
                String[] selectionArgs = {String.valueOf(contact_id)};

                cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null); // getting all the contact's numbers if(cursor!=null && cursor.moveToFirst())
            {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        numbersList.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }
            }

        } catch (SQLException e) {
            Log.e("DatabaseError", "Error accessing database", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        if (!numbersList.isEmpty()) {
            numbersList = PhoneNumberUtils.generatePhoneVariations(numbersList);
            lastCall = getLastCallById(numbersList);
        }


        return lastCall;
    }

    private long getLastCallById(List<String> numbersList) {
        ContentResolver contentResolver = sContext.getContentResolver();
        Cursor cursor = null;
        long lastCallTime = 0;

        try {
            String[] projection = {CallLog.Calls.DATE};
            StringBuilder selection =new StringBuilder(CallLog.Calls.NUMBER + " IN (");
            for (int i = 0; i < numbersList.size(); i++) {
                selection.append("?");
                if(i<numbersList.size()-1){
                    selection.append(", ");
                }
            }
            selection.append(") AND " + IS_ANSWERED_CALL);
            String[] selectionArgs = numbersList.toArray(new String[0]);

            cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection, selection.toString(), selectionArgs, CallLog.Calls.DATE + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                lastCallTime = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                // Do something with the last call time, e.g. display it in a TextView
                cursor.close();
            }
            projection = new String[]{CallLog.Calls.NUMBER};

            cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                    Log.d("CallLogNumbers", "Number: " + number);
                }
            }

        } catch (SQLException e) {
            Log.e("DatabaseError", "Error accessing database", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return lastCallTime;
    }

    public long getLastNotificationById(long contact_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        long lastNotification = 0;
        try{
            cursor = db.query(NOTIFICATION_TABLE_NAME,
                    new String[]{LAST_TIME_NOTIFICATION_COL},
                    CONTACT_ID_COL + " = " + contact_id,
                    null,
                    null,
                    null,
                    null);
            if (cursor ==null || !cursor.moveToFirst() )
                return 0;
            lastNotification = cursor.getLong(0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (cursor!=null)
                cursor.close();
        }
        return lastNotification;

    }

    public void setLastNotificationById(long contact_id, long lastNotification) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LAST_TIME_NOTIFICATION_COL, lastNotification);
        //THE id is already in the table
        int result = db.update(NOTIFICATION_TABLE_NAME, cv, CONTACT_ID_COL + " =?" ,new String[]{ String.valueOf(contact_id)});
        //if not in the table
        if (result == 0) {
            cv.put(CONTACT_ID_COL, contact_id);
            db.insert(NOTIFICATION_TABLE_NAME, null, cv);
        }

    }

    //update the last_call column
    //recalculation the priority

    /**
     * this function update all contact details and the last call.
     */
    public void updateAllTable(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        ContentValues cv;
        SQLiteDatabase contactDb = null;
        long contact_id = 0;
        HashMap<Long, MyContact> contactMap = getContactMap();
        long lastUpdate = getLastDateUpdate();
        try {
            contactDb = this.getWritableDatabase();

            //get contacts details
            cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    FROM_CONTACT_COLUMNS,
                    null,
                    null,
                    null);
            //updates names and images 
            if (cursor != null && cursor.moveToFirst()) {
                //getting index to cursor
                int nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
                int photoIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI);
                int idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
                do {
                    if (contactMap.containsKey(cursor.getLong(idIndex))) {
                        MyContact c = contactMap.get(cursor.getLong(idIndex));//id
                        c.setName(cursor.getString(nameIndex));//name
                        c.setPhotoSrc(cursor.getString(photoIndex));//photo);
                        contactMap.put(c.getContactId(), c);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }



            // update LAST CALL
            // get all the call from the last update and find relevant data.
            cursor = contentResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    FROM_COLUMNS_CALL,
                    CallLog.Calls.DATE + " > " + lastUpdate +
                            " AND " + IS_ANSWERED_CALL,
                    null,
                    CallLog.Calls.DATE + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    contact_id = getIdByNumber(context, cursor.getString(0));
                    if (contactMap.containsKey(contact_id)) {
                        MyContact c = contactMap.get(contact_id);
                        c.setLastCall(cursor.getLong(1));//get last call
                        contactMap.put(contact_id, c);
                    }
                } while (cursor.moveToNext());
            cursor.close();
            }
            // update contact table
            for (MyContact c : contactMap.values()) {
                cv = contactToVc(c);
                contactDb.update(CONTACTS_TABLE_NAME, cv, CONTACT_ID_COL + " = " + c.getContactId(), null);
            }

            // TODO: get all contacts birthday

            String[] projection = new String[]{ContactsContract.Data.CONTACT_ID, ContactsContract.CommonDataKinds.Event.START_DATE};
            String selection = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Event.TYPE + " = ?";
            String[] selectionArgs = new String[]{ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                    String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)};
            cursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            if (cursor != null) {
                while (cursor.moveToNext()){
                    Long contactId = cursor.getLong(0);
                    String birthday = cursor.getString(1);
                    if (contactMap.containsKey(contactId)){
                        updateBirthdayById(contactId, birthday);
                    }
                }
            }



            // TODO: update the DB
            setLastDateUpdate(System.currentTimeMillis()); // update the last update time to decrease processing time/
        } catch (SQLException e) {
            Log.e("DatabaseError", "Error accessing database", e);
        } finally {
            if (cursor.moveToLast()) {
                setLastDateUpdate(cursor.getLong(1));//date
            }
            if (contactDb != null && contactDb.isOpen()) {
                contactDb.close();
            }
            cursor.close();
        }
    }

    private void updateBirthdayById(Long contactId, String birthday) {
         ContentValues cv = new ContentValues();
         cv.put(CONTACT_ID_COL, contactId);
         cv.put(BIRTHDAY_COL, birthday);
         getWritableDatabase().insertWithOnConflict(BIRTHDAY_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }


    public void addContact(List<MyContact> myContacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        int howMatchUpdate = 0;

        if (myContacts != null) {
            for (MyContact contact : myContacts) {
                howMatchUpdate = db.update(
                        CONTACTS_TABLE_NAME,
                        contactToVc(contact),
                        CONTACT_ID_COL + " = " + contact.getContactId(),
                        null);
                if (howMatchUpdate == 0)
                    db.insert(CONTACTS_TABLE_NAME, null, contactToVc(contact));
            }
        }
        db.close();
    }

    private Long getIdByNumber(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        long id = -1;
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            cursor = contentResolver.query(uri, new String[]{ContactsContract.Contacts._ID}, null, null, null);
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
        } finally {
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
        db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BIRTHDAY_TABLE_NAME);
        onCreate(db);
    }

    // this function gets contact map with id and priority, find the last call date and update the table.
    public void updateTableFromMap(Map<Long, MyContact> contactMap) {
        SQLiteDatabase db;

        try {
            db = getWritableDatabase();
            for (MyContact c : contactMap.values()) {
                if (c.getPriorityType() == PriorityType.NEVER) {
                    db.delete(CONTACTS_TABLE_NAME, CONTACT_ID_COL + " = " + c.getContactId(), null);
                } else {

                    c.setLastCall(getLastCallById(c.getContactId()));// update the last Call because of lastUpdate parameter.
                    ContentValues cv = contactToVc(c);
                    cv.put(PRIORITY_TYPE_COL,c.getPriorityType().toString());
                    int result = db.update(CONTACTS_TABLE_NAME, cv, CONTACT_ID_COL + " = " + c.getContactId(), null);
                    if (result == 0) {
                        db.insert(CONTACTS_TABLE_NAME, null, cv);
                    }
                    // REMOVE moved to worker
                    //create notification
                    //notificationManage.createNotification(sContext,c.getContactId(),c.getName(),c.getLastCall(),c.getPriorityType());
                }
            }
            db.close();
        } catch (Exception e) {
            Log.e(null, e.toString());
        }
    }

    public int setLastDateUpdate(long lastDateUpdate) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            //get access to database

            ContentValues cv = new ContentValues();
            cv.put(TABLE_NAME, CONTACTS_TABLE_NAME);
            cv.put(LAST_UPDATE, lastDateUpdate);

            int result = db.update(DETAILS_TABLE_NAME, cv, TABLE_NAME + " = '" + CONTACTS_TABLE_NAME + "'", null);
            if (result == 0) {
                db.insert(DETAILS_TABLE_NAME, null, cv);
            }
        } catch (Exception exc) {
            Log.e("update last update-table fail", exc.getMessage());
        }
        return 1;
    }

    public long getLastDateUpdate() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        long lastUpdate = 0;
        try {
            db = this.getReadableDatabase();
            String[] columns = {LAST_UPDATE};
            cursor = db.query(DETAILS_TABLE_NAME,
                    columns,
                    TABLE_NAME + " = '" + CONTACTS_TABLE_NAME + "'",
                    null,
                    null,
                    null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                lastUpdate = cursor.getLong(0);
            }
        } catch (SQLException e) {
            Log.e("get last update table error", e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return lastUpdate;
    }

    public ArrayList<Long> getTodayBirthday() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        ArrayList<Long> listID = new ArrayList<>();
        try {
            db = this.getReadableDatabase();
            cursor = db.query(BIRTHDAY_TABLE_NAME,
                    new String[]{CONTACT_ID_COL, IS_BIRTHDAY_COL},
                    BIRTHDAY_COL + " = DATE('now')",
                    null,
                    null,
                    null,
                    null);
            if (cursor != null)
                while (cursor.moveToNext()) {
                    if(cursor.getInt(1)==1)
                        listID.add(cursor.getLong(0));
                }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }

        }
        return listID;
    }

    public MyContact getContactById(Long id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        MyContact contact = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(CONTACTS_TABLE_NAME,
                    FROM_COLUMNS,
                    CONTACT_ID_COL + " = " + id,
                    null,
                    null,
                    null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                contact = new MyContact(cursor.getLong(0),
                        PriorityType.valueOf(cursor.getString(1)),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4));

            }

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }
            }
        return contact;
    }

    public ArrayList<BirthdayContact> getBirthdayList(){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        ArrayList<BirthdayContact> list = new ArrayList<>();
        try {
            db = this.getReadableDatabase();
            cursor = db.query(BIRTHDAY_TABLE_NAME,
                    new String[]{CONTACT_ID_COL, BIRTHDAY_COL, IS_BIRTHDAY_COL},
                    null,
                    null,
                    null,
                    null,
                    null);
            if (cursor != null)
                while (cursor.moveToNext()) {
                    list.add(new BirthdayContact(cursor.getLong(0), cursor.getString(1), cursor.getInt(2)));
                }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (db != null) {
                db.close();
        }
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;

    }
}
