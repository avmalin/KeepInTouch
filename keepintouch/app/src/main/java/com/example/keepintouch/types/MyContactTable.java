package com.example.keepintouch.types;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Date;

public class MyContactTable extends SQLiteOpenHelper {
    private static final String DB_NAME = "my_contacts_db.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "my_contacts_table ";
    private static final String ID_COL = "_id";

    private static final String CONTACT_ID_COL = "contact_id";
    private static final String LAST_CALL_COL = "last_call_date";
    private static final String PRIORITY_TYPE_COL = "priority_type";
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
                + LAST_CALL_COL +  " TEXT, "
                + PRIORITY_TYPE_COL + " INTEGER)";
        db.execSQL(query);
    }
    public boolean addContact(int contact_id, Date last_call, PriorityType priorityType)
    {
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID_COL,contact_id);
        cv.put(LAST_CALL_COL, last_call.toString());
        cv.put(PRIORITY_TYPE_COL, priorityType.toString());
        long insert = db.insert(TABLE_NAME, null, cv);
        if (insert == -1)
            return false;
        else
            return true;
    }

    public boolean addContact(int contact_id, PriorityType priorityType)
    {
        return addContact(contact_id, getLastCallById(contact_id),priorityType);
    }

    private Date getLastCallById(int contact_id) {

        return null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
