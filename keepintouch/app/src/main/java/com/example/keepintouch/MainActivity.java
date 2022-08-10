package com.example.keepintouch;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_URI
    };
    private final static int[] TO_IDS = {
            android.R.id.text1
    };
    ListView contactsList;

    private SimpleCursorAdapter cursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list_view);
        contactsList = (ListView) findViewById(R.id.listView);
        cursorAdapter = new SimpleCursorAdapter(this,R.layout.contacts_list_item,null,FROM_COLUMNS,TO_IDS,0){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }

        };
        contactsList.setAdapter(cursorAdapter);


    }


}