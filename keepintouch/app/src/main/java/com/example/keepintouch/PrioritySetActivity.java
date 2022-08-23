package com.example.keepintouch;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.keepintouch.types.MyContact;
import com.example.keepintouch.types.MyContactTable;
import com.example.keepintouch.types.PriorityType;
import com.example.keepintouch.types.RbFragment;

import java.util.Map;

public class PrioritySetActivity extends AppCompatActivity {

    CursorAdapter cursorAdapter = null;
    MyContactTable myContactTable;
    Map<Integer, MyContact> contactMap;
    int cId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myContactTable = new MyContactTable(this);//TODO check if works



        setContentView(R.layout.activity_priority_set);
        ListView listView = findViewById(R.id.listViewPriority);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (cursorAdapter != null) {
                    RbFragment fragment = new RbFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    Bundle data = new Bundle();

                    Cursor c = (Cursor) cursorAdapter.getItem(position);
                    cId = c.getInt(3);
                    String cName = c.getString(1);

                    data.putInt("id",cId);
                    data.putString("name",cName);

                    fragment.setArguments(data);
                    fragmentTransaction.replace(R.id.contactFragment,fragment).commit();

                }
            }
        });

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = null;
        String sort =  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        try {
            cursor = getContentResolver().query(uri, null, null, null, sort);
            cursorAdapter =
                    new SimpleCursorAdapter(
                            this,
                            R.layout.contacts_list_item,
                            cursor,
                            MainActivity.FROM_COLUMNS,
                            MainActivity.TO_IDS,
                            0);
            listView.setAdapter(cursorAdapter);// TODO add update to the contact cursor every loaded/flash.

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void onRBClicked(View view) {
        PriorityType type;
        switch (view.getId()) {
            case R.id.rb_week:
                type = PriorityType.WEEKLY;
            case R.id.rb_month:
                type = PriorityType.MONTHLY;
            case R.id.rb_half_year:
                type = PriorityType.HALF_YEAR;
            case R.id.rb_year:
                type = PriorityType.YEARLY;
            default:
                type = PriorityType.NEVER;
        }
        contactMap.put(cId,new MyContact(cId, type));
    }

    @Override
    protected void onPause() {
        myContactTable.updateTableFromMap(contactMap);

        super.onPause();
    }
}