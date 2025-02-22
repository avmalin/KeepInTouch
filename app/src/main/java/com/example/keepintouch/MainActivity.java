package com.example.keepintouch;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.keepintouch.android.NotificationManage;
import com.example.keepintouch.types.CalculationContactsTask;
import com.example.keepintouch.types.MyContact;
import com.example.keepintouch.types.MyContactTable;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String[] PERMISSIONS= {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.POST_NOTIFICATIONS};
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
    private AnimationDrawable animationLoading;
    private static int selectedItem =-1;
    private NotificationManage mNotificationManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!hasPermissions(PERMISSIONS))
        {
            Intent myIntent = new Intent(MainActivity.this, RequestPermissionsActivity.class);
            startActivity(myIntent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list_view);
        myContactTable = new MyContactTable(this);

        //init for notifications
        mNotificationManage = NotificationManage.getInstance();
        mNotificationManage.createChannel(this);


// test for set notification on header click
//        TextView tv = findViewById(R.id.tv_header);
//        tv.setOnClickListener((v -> {
//            MyContact c = (MyContact) contactsList.getItemAtPosition(0);
//            mNotificationManage.createNotification(this, c.getContactId(),c.getName(),c.getLastCall(),c.getPriorityType());
//        }));


    }

   
    public void showMyContactActivity() {
        contactsList = findViewById(R.id.listView);
        ArrayList<MyContact> listContact = getMyContactsSort();


        //update the contacts
        adapter = new ArrayAdapter<MyContact>(this,R.layout.contacts_list_item,listContact){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                MyContact contact = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.contacts_list_item, parent, false);
                }
                //def all the box to view a contact card
                TextView tvName = convertView.findViewById(R.id.tv_name);
                TextView tvNumber = convertView.findViewById(R.id.tv_number);
                ImageView ivView = convertView.findViewById(R.id.iv_image);
                TextView tvId = convertView.findViewById(R.id.tv_contact_id);
                TextView tvPriority = convertView.findViewById(R.id.tv_priority);
                LinearLayout call_layout = convertView.findViewById(R.id.call_layout);
                ImageView ivWhatsapp = convertView.findViewById(R.id.iv_whatsapp);
                ImageView ivCall = convertView.findViewById(R.id.iv_call);
                String photoUri = contact.getPhotoSrc();

                //set days
                long date  = contact.getLastCall();
                String lastDate = "";
                if (date > 0) {
                   long currentDay = System.currentTimeMillis();
                   long days = (currentDay - date)/(1000*60*60*24);// 1 day = 24 hours = 24 * 60 * 60 * 1000 milliseconds
                   lastDate = days + " days";
                   if(days >  contact.getPriorityType().compValue())
                       tvNumber.setTextColor(Color.RED);
                }
                else if (date==0)//if never called
                {
                    lastDate = "∞";
                    tvNumber.setTextColor(Color.RED);
                    tvNumber.setTextSize(24);
                }

                //sets properties
                tvName.setText(contact.getName());
                tvNumber.setText(lastDate);
                if (photoUri != null) {
                    ivView.setImageURI(Uri.parse(photoUri));
                }
                tvId.setText(String.valueOf(contact.getContactId()));
                tvPriority.setText(contact.getPriorityType().toString());

                //set on click item
                if (position == selectedItem)
                    call_layout.setVisibility(View.VISIBLE);
                else call_layout.setVisibility(View.GONE);
                convertView.setOnClickListener(v -> {
                    if(position != selectedItem) {
                        selectedItem = position;
                        adapter.notifyDataSetChanged();
                    }
                    else{
                        selectedItem = -1;
                        adapter.notifyDataSetChanged();
                    }
                });

                //set listeners
                ivCall.setOnClickListener((v -> callingByID(contact.getContactId())));
                ivWhatsapp.setOnClickListener((v -> sendWhatsAppById(contact.getContactId())));
                return convertView;
            }
        };
        //contactsList.setOnItemClickListener((parent, view, position, id) -> callingByID(listContact.get(position).getContactId()));
        contactsList.setAdapter(adapter);

    }

    @NonNull
    private ArrayList<MyContact> getMyContactsSort() {
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
        return listContact;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasPermissions(PERMISSIONS))
        {
            showMyContactActivity();
            //init AsyncTask for updating the table
            CalculationContactsTask asyncTask = new CalculationContactsTask(this);
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

    public void callingByID(long contact_id){
        Cursor cursor=null;
        try {
            ContentResolver contentResolver = this.getContentResolver();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
            String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ";
            String[] selectionArgs = {String.valueOf(contact_id)};

            cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNumber = cursor.getString(phoneNumberIndex);
                cursor.close();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
            else
                Toast.makeText(getApplicationContext(), "cant find phone number", Toast.LENGTH_SHORT).show();

        }
        catch (SQLException e) {
            Log.e("DatabaseError", "Error accessing database searching number", e);
        }
        finally {
            if (cursor!=null && !cursor.isClosed())
                cursor.close();
        }
    }
    public void startLoading(){
        ImageView imageLoading =  findViewById(R.id.iv_loading);
        imageLoading.setVisibility(View.VISIBLE);
        animationLoading = (AnimationDrawable) imageLoading.getDrawable();
        animationLoading.start();
    }
    public void stopLoading(){
        animationLoading.stop();
        ImageView imageLoading =findViewById(R.id.iv_loading);
        imageLoading.setVisibility(View.INVISIBLE);
    }
    public void sendWhatsAppById(long contact_id) {


        Cursor cursor =null;
        try{
            ContentResolver contentResolver = this.getContentResolver();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
            String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ";
            String[] selectionArgs = {String.valueOf(contact_id)};

            cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNumber = cursor.getString(phoneNumberIndex);
                String uri = "https://api.whatsapp.com/send?phone=" + phoneNumber;
                uri += "&text=" + URLEncoder.encode(getString(R.string.whatsappMessage), "UTF-8");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                intent.setPackage("com.whatsapp");
                // Start WhatsApp chat
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(getApplicationContext(), "WhatsApp isn't install", Toast.LENGTH_SHORT);
            toast.show();
        }
        finally {
            if (cursor!=null && !cursor.isClosed())
                cursor.close();
        }
    }
}

