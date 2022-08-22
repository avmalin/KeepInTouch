package com.example.keepintouch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.keepintouch.types.MyContact;
import com.example.keepintouch.types.MyContactTable;

import java.util.ArrayList;
import java.util.Comparator;

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
        private ArrayAdapter<MyContact> adapter = null;
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

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ||ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            {
                //TODO close the program
            }
            else {

                //TODO view to edit contacts priority
                //TODO update the table after every flash
                //showContactActivity();
                myContactTable.updateAllTable(this);
                showMyContactActivity();
            }
    }

    private void showMyContactActivity() {
            SQLiteDatabase db = myContactTable.getReadableDatabase();
            Cursor myContactCursor = null;
            Cursor contactCursor = null;
            contactsList = (ListView) findViewById(R.id.listView);
            ArrayList<MyContact> listContact;
           // Map<Integer,MyContact> contactMap = null;


            //update last call
           listContact = myContactTable.getContactList();

            //sort the contacts.
            listContact.sort(new Comparator<MyContact>() {
                @Override
                public int compare(MyContact o1, MyContact o2) {
                    long i;
                    i = (System.currentTimeMillis() - o1.getLastCall())/ o2.getPriorityType().compValue()-
                            (System.currentTimeMillis() - o2.getLastCall())/ o1.getPriorityType().compValue();
                    return (int)i;
                }});


            //update the contacts
            adapter = new ArrayAdapter<MyContact>(this,R.layout.contacts_list_item,listContact){
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    MyContact contact = getItem(position);
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.contacts_list_item, parent, false);
                    }
                    TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
                    TextView tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
                    ImageView ivView = (ImageView) convertView.findViewById(R.id.iv_image);

                    tvName.setText(contact.getName());
                    tvNumber.setText(contact.getNumber());
                    ivView.setImageURI(Uri.parse(contact.getPhotoSrc()));
                    return convertView;
                }
            };
            contactsList.setAdapter(adapter);


    }

    private void showContactActivity() {
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