package com.example.keepintouch.types;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyContactTable extends SQLiteOpenHelper {
    private static final String DB_NAME = "my_contact_table";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "my_contact";
    private static final String ID_COL = "_id";

    private static final String CONTACT_ID_COL = "contact_id";
    private static final String LAST_CALL_COL = "last_call_date";
    private static final String PRIORITY_TYPE_COL = "priority_type";


    public MyContactTable(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        return;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        return;
    }
}
