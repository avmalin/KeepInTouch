package com.example.keepintouch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    private final static String[] FROM_COLUMNS = {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_URI
    };
    private final static int[] TO_IDS = {
            R.id.tv_name,
            R.id.tv_number,
            R.id.iv_image
        };
        ListView contactsList;
        private SimpleCursorAdapter cursorAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.contacts_list_view);
            // check permissions
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            {
               // while (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
               ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
               // TODO: make sure the user grants the required permission
            }
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

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
    //
//            /*{
//            @Override
////            public View getView(int position, View convertView, ViewGroup parent) {
////                //set the item
////
////                if(convertView == null)
////                    convertView = View.inflate(MainActivity.this,R.layout.contacts_list_item,null);
////                //get the component
////                TextView nameView = (TextView) convertView.findViewById(R.id.tv_name);
////                ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_image);
////                TextView phoneView = (TextView) convertView.findViewById(R.id.tv_number);
////                //set contacts
////                //TODO: understand how to send item from cursorAdapter
////                MyContact contact = new MyContact((ContactsContract.Contacts) cursorAdapter.getItem(position));
////                // set the component
////                nameView.setText(contact.getName());
////                imageView.setImageURI(Uri.parse(contact.getPhotoSrc()));
////                phoneView.setText(contact.getNumber());
////
////                return convertView;
////            }*/
//        };
////
            contactsList.setAdapter(cursorAdapter);// TODO add update to the contact cursor evry loaded/flash.

           // CursorJoiner joiner =  new CursorJoiner(cursor,ContactsContract.CommonDataKinds.Id)



    }


}