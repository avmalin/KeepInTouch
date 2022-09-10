package com.example.keepintouch;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
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
    MyContactTable myContactTable;
    View contactPriority;
    HashMap<Integer, MyContact> contactMap;
    int cId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority_set);
        myContactTable = new MyContactTable(this);//TODO check if works
        ListView listView = findViewById(R.id.listViewPriority);
        RadioGroup radioGroup = findViewById(R.id.rg_priority);
        contactPriority=null;
        contactMap = new HashMap<>();

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (contactPriority != null) {
                    contactPriority.setVisibility(View.GONE);
                }
                contactPriority = view.findViewById(R.id.contactPriority);
                contactPriority.setVisibility(View.VISIBLE);
                /* by fragment  -- not use
                // RbFragment fragment = new RbFragment(PrioritySetActivity.this);
                //FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //Bundle data = new Bundle();

                //Cursor c = (Cursor) cursorAdapter.getItem(position);
                //cId = c.getInt(3);
                //String cName = c.getString(1);

                //data.putInt("id",cId);
                //data.putString("name",cName);

                //fragment.setArguments(data);
                //fragmentTransaction.replace(R.id.contactFragment,fragment).commit();
                 */
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                contactPriority.setVisibility(View.GONE);
                contactPriority = null;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (contactPriority != null) {
                    contactPriority.setVisibility(View.GONE);
                }
                contactPriority = view.findViewById(R.id.contactPriority);
                contactPriority.setVisibility(View.VISIBLE);
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
                            0){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);
                            RadioGroup rg = v.findViewById(R.id.rg_priority);
                            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    int rbId = group.getCheckedRadioButtonId();
                                    View parent = (View)group.getParent().getParent();
                                    TextView textView = (TextView)parent.findViewById(R.id.tv_contact_id);
                                    int cId = Integer.parseInt(textView.getText().toString());
                                    PriorityType type;

                                    switch (rbId){
                                        case R.id.rb_week:
                                            type = PriorityType.WEEKLY;
                                            break;
                                        case R.id.rb_month:
                                            type = PriorityType.MONTHLY;
                                            break;
                                        case R.id.rb_half_year:
                                            type = PriorityType.HALF_YEAR;
                                            break;
                                        case R.id.rb_year:
                                            type = PriorityType.YEARLY;
                                            break;
                                        default:
                                            type = PriorityType.NEVER;
                                    }
                                    contactMap.put(cId, new MyContact(cId,type));

                                }
                            });
                            return v;
                        }
                    };
            listView.setAdapter(cursorAdapter);// TODO add update to the contact cursor every loaded/flash.

        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        /*
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int rbId = group.getCheckedRadioButtonId();
                View parent = (View)group.getParent();
                int cId = Integer.parseInt(((TextView)parent.findViewById(R.id.tv_contact_id)).getText().toString());
                PriorityType type;

                switch (rbId){
                    case R.id.rb_week:
                        type = PriorityType.WEEKLY;
                        break;
                    case R.id.rb_month:
                        type = PriorityType.MONTHLY;
                        break;
                    case R.id.rb_half_year:
                        type = PriorityType.HALF_YEAR;
                        break;
                    case R.id.rb_year:
                        type = PriorityType.YEARLY;
                        break;
                    default:
                        type = PriorityType.NEVER;
                }
                contactMap.put(cId, new MyContact(cId,type));
            }
        });*/
    }


    @Override
    protected void onPause() {
        myContactTable.updateTableFromMap(contactMap);
        super.onPause();
    }


}