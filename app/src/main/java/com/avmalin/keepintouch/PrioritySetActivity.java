package com.avmalin.keepintouch;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.avmalin.keepintouch.types.MyContactTable;
import com.avmalin.keepintouch.types.PriorityType;
import com.avmalin.keepintouch.R;
import com.avmalin.keepintouch.types.MyContact;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class PrioritySetActivity extends AppCompatActivity {

    CursorAdapter cursorAdapter = null;
    ArrayAdapter<MyContact>  arrayAdapter=null;
    Cursor cursor= null;
    ArrayList<MyContact> listContact;
    int selectedItem = -1;
    MyContactTable myContactTable;
    View contactPriority;
    HashMap<Long, MyContact> contactMap;
    int cId;
    public final static String[] FROM_COLUMNS = {
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts._ID
    };
    public final static int[] TO_IDS = {
            R.id.tv_name,
            R.id.iv_image,
            R.id.tv_contact_id
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list_add);


        myContactTable = new MyContactTable(this);
        listContact = myContactTable.getContactList();
        listContact.sort(Comparator.comparing(MyContact::getName));

        //init  element
        ImageView ib_accept = findViewById(R.id.ib_accept);
        ImageView ib_back = findViewById(R.id.ib_back);
        EditText et_search = findViewById(R.id.et_search);
        //imp listener
        ib_accept.setOnClickListener(v -> {
            myContactTable.updateTableFromMap(contactMap);
            finish();
        });
        ib_back.setOnClickListener(v -> finish());
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                performSearch(query);
                selectedItem = -1;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //--handle click on listview to view priority options.
        ListView listView = findViewById(R.id.listViewContacts);
        //init
        contactPriority = null;
        contactMap = new HashMap<>();

        //define on-click item

        performSearch("");
    }

    public void performSearch(String searchString){
        ListView listView = findViewById(R.id.listViewContacts);

        if (!searchString.equals("")) {
            try  {
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,Uri.encode(searchString));
                String sort =  ContactsContract.Contacts.DISPLAY_NAME + " ASC";
                cursor = getContentResolver().query(
                        uri,
                        null,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER + " != 0",
                        null,
                        sort);
                cursorAdapter =
                    new SimpleCursorAdapter(
                            this,
                            R.layout.contacts_list_item,
                            cursor,
                            FROM_COLUMNS,
                            TO_IDS,
                            0) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            if (convertView == null) {
                                convertView = LayoutInflater.from(PrioritySetActivity.this).inflate(R.layout.contacts_list_item, parent, false);
                            }
                            Cursor cursor = getCursor();
                            cursor.moveToPosition(position);
                            ListView listView = findViewById(R.id.listViewContacts);
                            //define on-click item


                            //get the index of the data in the cursor.
                            int nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
                            int photoIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI);
                            int idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
                            boolean isOnList;
                            //set the data into the view item.
                            ((TextView)convertView.findViewById(R.id.tv_name)).setText(cursor.getString(nameIndex));
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
                                radioGroup.setTag(R.id.tv_name, cursor.getString(nameIndex));
                                radioGroup.setTag(R.id.iv_image, cursor.getString(photoIndex));
                                radioGroup.setOnCheckedChangeListener((group, checkedId) ->
                                {
                                    contactMap.put(
                                                (long)group.getTag(R.id.tv_contact_id),
                                                new MyContact(
                                                        (long)group.getTag(R.id.tv_contact_id),
                                                        PriorityType.valueOf(checkedId),
                                                        (String)group.getTag(R.id.tv_name),
                                                        (String)group.getTag(R.id.iv_image)));
                                    boolean ifInList = false;
                                    for (MyContact c:listContact){
                                        if (c.getContactId() == (long)group.getTag(R.id.tv_contact_id))
                                        {
                                            c.setNumber(String.valueOf(checkedId));
                                            ifInList = true;
                                            break;
                                        }
                                        notifyDataSetChanged();
                                    }
                                    if (!ifInList)
                                        listContact.add(new MyContact(
                                                (long)group.getTag(R.id.tv_contact_id),
                                                null,
                                                String.valueOf(checkedId),
                                                (String)group.getTag(R.id.tv_name),
                                                (String) group.getTag(R.id.iv_image)));
                                });

                            }
                            else {
                                convertView.findViewById(R.id.rg_priority1).setVisibility(View.GONE);
                                //define on-click item
                                convertView.setOnClickListener((view) -> {
                                    if (position == selectedItem)
                                        selectedItem = -1;
                                    else selectedItem = position;
                                    if (cursorAdapter!=null)
                                        cursorAdapter.notifyDataSetChanged();
                                    if(arrayAdapter!=null)
                                        arrayAdapter.notifyDataSetChanged();
                                });
                            }
                            return convertView;
                        }
                    };
                listView.setAdapter(cursorAdapter);
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
        /*if search is empty showing my contact to edit*/
        else {
            arrayAdapter =
                    new ArrayAdapter<MyContact>(this,R.layout.contacts_list_item,listContact){
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            if (convertView == null) {//init the listview
                                convertView = LayoutInflater.from(getContext()).inflate(R.layout.contacts_list_item, parent, false);
                            }
                            //inits
                            TextView tvName = convertView.findViewById(R.id.tv_name);
                            ImageView ivView = convertView.findViewById(R.id.iv_image);
                            TextView tvId = convertView.findViewById(R.id.tv_contact_id);
                            TextView tvPriority = convertView.findViewById(R.id.tv_priority);
                            ImageView ivEdit =  convertView.findViewById((R.id.iv_edit));
                            MyContact contact = getItem(position);
                            //sets views
                            tvName.setText(contact.getName());
                            tvId.setText(String.valueOf(contact.getContactId()));
                            String photoUri = contact.getPhotoSrc();
                            if (photoUri != null)
                                ivView.setImageURI(Uri.parse(photoUri));
                            //tvPriority.setText(contact.getPriorityType().toString());//only on all contacts
                            ivEdit.setVisibility(View.VISIBLE);



                            if(contact.getPriorityType() == null)//there isn't prev priority (its new)
                                tvName.setTextColor(ContextCompat.getColor(convertView.getContext(),R.color.colorPrimary));
                            else if (contact.getNumber() == null ||
                                    contact.getPriorityType().idOf() == Integer.decode(contact.getNumber())) // chosen priority equals to prev priority
                                tvName.setTextColor(Color.GRAY);
                            else
                                tvName.setTextColor(Color.RED);


                            //set checkboxes to visible to only selected  item
                            if (position == selectedItem)
                            {
                                RadioGroup radioGroup = convertView.findViewById(R.id.rg_priority1);
                                //set the current priority if not change
                                if (contact.getNumber()==null ||  contact.getNumber().equals("0")) // there isn't checked priority
                                    radioGroup.check(getItem(position).getPriorityType().idOf());
                                else radioGroup.check(Integer.decode(getItem(position).getNumber()));

                                radioGroup.setVisibility(View.VISIBLE);
                                radioGroup.setTag(R.id.tv_contact_id,contact.getContactId());
                                radioGroup.setTag(R.id.tv_name, contact.getName());
                                radioGroup.setTag(R.id.iv_image, contact.getPhotoSrc());
                                radioGroup.setOnCheckedChangeListener((group, checkedId) ->
                                {contactMap.put(
                                            (long) group.getTag(R.id.tv_contact_id),
                                            new MyContact((long) group.getTag(R.id.tv_contact_id),
                                                    PriorityType.valueOf(checkedId),
                                                    (String) group.getTag(R.id.tv_name),
                                                    (String) group.getTag(R.id.iv_image)));
                                    getItem(position).setNumber(String.valueOf(checkedId));



                                    notifyDataSetChanged();
                                });
                                convertView.setOnClickListener(v -> {
                                    selectedItem=-1;
                                    arrayAdapter.notifyDataSetChanged();
                                });

                            }
                            else
                            {
                                convertView.findViewById(R.id.rg_priority1).setVisibility(View.GONE);
                                //define on-click item
                                convertView.setOnClickListener((view) -> {
                                    if (position == selectedItem)
                                        selectedItem = -1;
                                    else selectedItem = position;
                                    if (cursorAdapter!=null)
                                        cursorAdapter.notifyDataSetChanged();
                                    if(arrayAdapter!=null)
                                        arrayAdapter.notifyDataSetChanged();
                                });
                            }

                            return convertView;

                        }
                    };
            listView.setAdapter(arrayAdapter);

        }
    }


    @Override
    protected void onPause() {
        //myContactTable.updateTableFromMap(contactMap);
        super.onPause();
    }


}