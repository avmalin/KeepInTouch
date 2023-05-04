package com.example.keepintouch;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keepintouch.types.MyContact;
import com.example.keepintouch.types.MyContactTable;
import com.example.keepintouch.types.PriorityType;

import java.util.HashMap;

public class PrioritySetActivity extends AppCompatActivity {

    CursorAdapter cursorAdapter = null;
    Cursor cursor= null;
    int selectedItem = -1;
    MyContactTable myContactTable;
    View contactPriority;
    HashMap<Long, MyContact> contactMap;
    int cId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority_set);



        myContactTable = new MyContactTable(this);//TODO check if works
        ListView listView = findViewById(R.id.listViewPriority);
//        RadioGroup radioGroup = findViewById(R.id.rg_priority);


        //init
        contactPriority=null;
        contactMap = new HashMap<>();

        //define on-click item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = position;
                cursorAdapter.notifyDataSetChanged();

            }
        });
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String sort =  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        try  {
            cursor = getContentResolver().query(uri, null, null, null, sort);
            cursorAdapter =
                    new SimpleCursorAdapter(
                            this,
                            R.layout.contacts_list_item,
                            cursor,
                            MainActivity.FROM_COLUMNS,
                            MainActivity.TO_IDS,
                            0) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            if (convertView == null) {
                                convertView = LayoutInflater.from(PrioritySetActivity.this).inflate(R.layout.contacts_list_item, parent, false);
                            }
                            Cursor cursor = getCursor();
                            cursor.moveToPosition(position);
                            ListView listView = findViewById(R.id.listViewPriority);

                            //get the index of the data in the cursor.
                            int nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                            int phoneIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            int photoIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                            int idIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);

                            //set the data into the view item.
                            ((TextView)convertView.findViewById(R.id.tv_name)).setText(cursor.getString(nameIndex));
                            ((TextView)convertView.findViewById(R.id.tv_number)).setText(cursor.getString(phoneIndex));
                            String photoUri = cursor.getString(photoIndex);
                            if (photoUri != null) {
                                ((ImageView) convertView.findViewById(R.id.iv_image)).setImageURI(Uri.parse(photoUri));
                            }
                            //set checkboxes to visible to only selected  item
                            if (position == selectedItem)
                            {
                                RadioGroup radioGroup = convertView.findViewById(R.id.rg_priority1);
                                radioGroup.setVisibility(View.VISIBLE);
                                radioGroup.setTag(R.id.tv_contact_id,cursor.getLong(idIndex));
                                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                                        contactMap.put((long)group.getTag(R.id.tv_contact_id),new MyContact((long)group.getTag(R.id.tv_contact_id), PriorityType.valueOf(checkedId),(String)group.getTag(R.id.tv_number),(String)group.getTag(R.id.tv_name),(String)group.getTag(R.id.iv_image)));
                                    }
                                });
                            }
                            else convertView.findViewById(R.id.rg_priority1).setVisibility(View.GONE);
                            return convertView;
                        }
                    };
            listView.setAdapter(cursorAdapter);

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    @Override
    protected void onPause() {
        myContactTable.updateTableFromMap(contactMap);
        super.onPause();
    }


}