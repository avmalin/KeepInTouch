package com.avmalin.keepintouch;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avmalin.keepintouch.UI.ContactAdapter;
import com.avmalin.keepintouch.UI.SwipeItemHandle;
import com.avmalin.keepintouch.android.NotificationManage;
import com.avmalin.keepintouch.android.SyncTableBackgroundTask;
import com.avmalin.keepintouch.android.WorkManagerHelper;
import com.avmalin.keepintouch.types.MyContactTable;
import com.avmalin.keepintouch.types.MyContact;

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
    RecyclerView contactsListView;
    private SimpleCursorAdapter cursorAdapter;
    private ContactAdapter contactAdapter = null;
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

        contactsListView = findViewById(R.id.listView);
        contactsListView.setLayoutManager(new LinearLayoutManager(this));

        mNotificationManage = NotificationManage.getInstance();
        mNotificationManage.createChannel(this);


        //set works to manage notification
        WorkManagerHelper.scheduleDailyWork(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasPermissions(PERMISSIONS))
        {
            showMyContactActivity();
            //init AsyncTask for updating the table
            new SyncTableBackgroundTask(this).execute(myContactTable);



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

    public void showMyContactActivity() {
        contactsListView = findViewById(R.id.listView);
        ArrayList<MyContact> listContact = getMyContactsSort();


        //update the contacts
        ContactAdapter contactAdapter = new ContactAdapter(listContact);
        contactsListView.setAdapter(contactAdapter);

        ItemTouchHelper itemTouchHelper =  new ItemTouchHelper(new SwipeItemHandle(this,contactAdapter));
        itemTouchHelper.attachToRecyclerView(contactsListView);


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
}

