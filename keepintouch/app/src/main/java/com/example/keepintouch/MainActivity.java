package com.example.keepintouch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    public final static String[] FROM_COLUMNS = {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    };
    public final static int[] TO_IDS = {
            R.id.tv_name,
            R.id.tv_number,
            R.id.iv_image,
            R.id.tv_contact_id
        };
        ListView contactsList;
        private SimpleCursorAdapter cursorAdapter;
        private ArrayAdapter<MyContact> adapter = null;
        private MyContactTable myContactTable;
        private Map<Integer,MyContact> contactMap;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //myContactTable = new MyContactTable(this);
            setContentView(R.layout.contacts_list_view);
            myContactTable = new MyContactTable(this);
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
//            else {
//
//                //TODO view to edit contacts priority
//                //TODO update the table after every flash
//                //showContactActivity();
//                //myContactTable.updateAllTable(this);
//                //showMyContactActivity();
//            }


    }

    private void showMyContactActivity() {
        contactsList = findViewById(R.id.listView);
        ArrayList<MyContact> listContact;
       // Map<Integer,MyContact> contactMap = null;


        //update last call
        myContactTable.updateAllTable(this);
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
                TextView tvName = convertView.findViewById(R.id.tv_name);
                TextView tvNumber = convertView.findViewById(R.id.tv_number);
                ImageView ivView = convertView.findViewById(R.id.iv_image);
                TextView tvId = convertView.findViewById(R.id.tv_contact_id);

                tvName.setText(contact.getName());
                tvNumber.setText(contact.getNumber());
                ivView.setImageURI(Uri.parse(contact.getPhotoSrc()));
                tvId.setText(contact.getContactId());
                return convertView;
            }
        };
        contactsList.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        showMyContactActivity();
    }



    public void openPrioritySet(View view) {
        Intent myIntent = new Intent(MainActivity.this, PrioritySetActivity.class);
        startActivity(myIntent);
    }


}