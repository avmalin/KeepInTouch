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

import com.example.keepintouch.types.CalculationContactsTask;
import com.example.keepintouch.types.MyContact;
import com.example.keepintouch.types.MyContactTable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String[] PERMISSIONS= {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG};
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
        if (!hasPermissions(PERMISSIONS))
        {
            Intent myIntent = new Intent(MainActivity.this, RequestPermissionsActivity.class);
            startActivity(myIntent);
        }
        super.onCreate(savedInstanceState);
        //myContactTable = new MyContactTable(this);
        setContentView(R.layout.contacts_list_view);
        myContactTable = new MyContactTable(this);

    }

    private void showMyContactActivity() {
        contactsList = findViewById(R.id.listView);
        ArrayList<MyContact> listContact;
       // Map<Integer,MyContact> contactMap = null;

       listContact = myContactTable.getContactList();

        //sort the contacts.
        listContact.sort((o1, o2) -> {
            long o1T,o2T, i;
            o1T = System.currentTimeMillis() - o1.getLastCall();//calc how match time from last call of o1
            o2T = System.currentTimeMillis() - o2.getLastCall();//calc how match time from last call of o2
            i = o2T/o2.getPriorityType().compValue() - o1T/o1.getPriorityType().compValue();//oT div by mount of day according to priority
            return (int)i;
        });


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
                TextView tvPriority = convertView.findViewById(R.id.tv_priority);

                long date  = contact.getLastCall();
                String lastDate = "";
                 //TODO: convert millisecond into date
                if (date > 0) {
                    Instant instant = Instant.ofEpochMilli(date);
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    lastDate = formatter.format(localDateTime);
                }

                tvName.setText(contact.getName());
                tvNumber.setText(lastDate);
                String photoUri = contact.getPhotoSrc();
                if (photoUri != null) {
                    ivView.setImageURI(Uri.parse(photoUri));
                }
                tvId.setText(String.valueOf(contact.getContactId()));
                tvPriority.setText(contact.getPriorityType().toString());
                return convertView;
            }
        };
        contactsList.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasPermissions(PERMISSIONS))
        {
            showMyContactActivity();
            //init AsyncTask for updating the table
            CalculationContactsTask asyncTask = new CalculationContactsTask(this,findViewById(R.id.listView), findViewById(R.id.iv_loading));
            asyncTask.execute(myContactTable);


        }
    }


    //fixme: fix the intent
    public void openPrioritySet(View view) {
        Intent myIntent = new Intent(MainActivity.this, PrioritySetActivity.class);
        startActivity(myIntent);
    }
    public boolean hasPermissions (String[] permissions)
    {
        if (permissions!= null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }

}