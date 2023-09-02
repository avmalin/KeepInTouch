package com.example.keepintouch.types;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.keepintouch.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalculationContactsTask extends AsyncTask<MyContactTable,Integer,MyContactTable> {
    private Context mContext;
    private ListView mContactsList;
    public CalculationContactsTask(Context context, ListView contactsList)
    {
        mContext=context;
        mContactsList =contactsList;

    }

    @Override
    protected MyContactTable doInBackground(MyContactTable... myContactTables) {
        myContactTables[0].updateAllTable(mContext);
        return myContactTables[0];
    }

    @Override
    protected void onPostExecute(MyContactTable myContactTable) {
        super.onPostExecute(myContactTable);


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
        ArrayAdapter<MyContact> adapter = new ArrayAdapter<MyContact>(mContext,R.layout.contacts_list_item,listContact){
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
            mContactsList.setAdapter(adapter);
    }
}
