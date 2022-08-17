package com.example.keepintouch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.keepintouch.types.MyContactTable;

public class MainActivity extends AppCompatActivity {

    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    private final static String[] FROM_COLUMNS = {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    };
    private final static int[] TO_IDS = {
            R.id.tv_name,
            R.id.tv_number,
            R.id.iv_image
        };
        ListView contactsList;
        private SimpleCursorAdapter cursorAdapter;
        private MyContactTable myContactTable;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            myContactTable = new MyContactTable(this);
            setContentView(R.layout.contacts_list_view);
            // check permissions
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
            // TODO: make sure the user grants the required permission

            showContactActicity();
            showMyContactActivity();


            // CursorJoiner joiner =  new CursorJoiner(cursor,ContactsContract.CommonDataKinds.Id)



    }

    private void showMyContactActivity() {
            SQLiteDatabase db = myContactTable.getReadableDatabase();
            contactsList = (ListView) findViewById(R.id.listView);
            try {
                Cursor cursor = db.query(
                        myContactTable.TABLE_NAME,
                        new String[]{myContactTable.CONTACT_ID_COL},
                        null,
                        null,
                        null,
                        null,
                        myContactTable.PRIORITY_TYPE_COL + " AND " + myContactTable.LAST_CALL_COL);
                if (cursor == null || cursor.moveToFirst())
                    do{

                    }while (cursor.moveToNext());

            }


            )

    }

    private void showContactActicity() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        contactsList = (ListView) findViewById(R.id.listView);
        String sort =  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "ASC";
        Cursor cursor = getContentResolver().query(uri,null, null,null,sort);
        cursorAdapter =
                new SimpleCursorAdapter(
                        this,
                        R.layout.contacts_list_item,
                        cursor,
                        FROM_COLUMNS,
                        TO_IDS,
                        0);
        contactsList.setAdapter(cursorAdapter);// TODO add update to the contact cursor every loaded/flash.
        cursor.close();
    }


}