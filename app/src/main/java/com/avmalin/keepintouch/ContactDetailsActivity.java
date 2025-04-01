package com.avmalin.keepintouch;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.avmalin.keepintouch.types.BirthdayContact;
import com.avmalin.keepintouch.types.MyContact;
import com.avmalin.keepintouch.types.MyContactTable;

public class ContactDetailsActivity extends AppCompatActivity {
    //TODO: open
    private ImageView contactImage;
    private TextView contactName;
    private TextView contactPhone;
    private TextView lastCallText;
    private TextView priorityText;
    private TextView birthdayText;
    private ToggleButton birthdayReminder;
    private CardView birthdayCard;

    private MyContactTable myContactTable;
    private long contactID;
    MyContact contact;
    BirthdayContact myBirthdayContact;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        initView();
        contactID = getIntent().getLongExtra("contact_id", -1);
        if (contactID == -1) {
            Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
            finish();

        }
        myContactTable = new MyContactTable(this);
        loadContact();
        displayContact();
    }

    private void displayContact() {
        contactName.setText(contact.getName());
        contactPhone.setText(contact.getNumber());
        priorityText.setText(contact.getPriorityType().toString());


        // set days
        long date  = contact.getLastCall();
        String lastDate = "";
        if (date > 0) {
            Long days = contact.getDays();
            lastDate = "התקשרת לפני " +days + " ימים";
            if(days >  contact.getPriorityType().compValue())
                lastCallText.setTextColor(Color.RED);
        }
        else if (date==0)//if never called
        {
            lastDate = "∞";
            lastCallText.setTextColor(Color.RED);
            lastCallText.setTextSize(24);
        }
        lastCallText.setText(lastDate);

        //set image
        if (contact.getPhotoSrc() != null && !contact.getPhotoSrc().isEmpty()) {
            Uri photoUri = Uri.parse(contact.getPhotoSrc());
            contactImage.setImageURI(photoUri);
        } else {
            // Set default contact image
            contactImage.setImageResource(R.drawable.ic_baseline_person_24);
        }

        //set birthday
        if (myBirthdayContact != null  && myBirthdayContact.getBirthday()!= null) {
            birthdayText.setText(myBirthdayContact.getBirthday());
        } else {
            birthdayText.setText("לא הוגדר");
        }
        birthdayReminder.setChecked(myBirthdayContact.isBirthday());
        updateBirthdayUI();

    }

    private void loadContact() {
        contact = myContactTable.getContactById(contactID);
        myBirthdayContact = myContactTable.birthdayContactById(contactID);
    }

    private void initView() {
        contactImage = findViewById(R.id.contact_image);
        contactName = findViewById(R.id.contact_name);
        contactPhone = findViewById(R.id.contact_phone);
        lastCallText = findViewById(R.id.last_call_text);
        priorityText = findViewById(R.id.priority_text);
        birthdayText = findViewById(R.id.birthday_text);
        birthdayReminder = findViewById(R.id.birthday_reminder_toggle);
        birthdayCard = findViewById(R.id.birthday_card);

        birthdayReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if (myBirthdayContact.getBirthday() == null) {
                        Toast.makeText(getApplicationContext(), "צריך להגדיר תאריך יומולדת לפני", Toast.LENGTH_SHORT).show();
                    } else {
                        myBirthdayContact.setBirthday(1);
                        myContactTable.setBirthday(myBirthdayContact);
                        updateBirthdayUI();
                    }
                }
                else{
                    myBirthdayContact.setBirthday(0);
                    myContactTable.setBirthday(myBirthdayContact);
                    updateBirthdayUI();
                }
            }
        });
        birthdayCard.setOnClickListener((v)->{
            openContactBirthdayEditor();
        });
    }

    private void openContactBirthdayEditor() {

            Intent intent = new Intent(Intent.ACTION_EDIT);
            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
            intent.setDataAndType(contactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
            intent.putExtra("finishActivityOnSaveCompleted", true);
            startActivity(intent);

    }

    private void updateBirthdayUI() {
        if (myBirthdayContact.isBirthday()) {
            birthdayText.setTextColor(getResources().getColor(R.color.text_primary));
            birthdayCard.setCardBackgroundColor(getResources().getColor(R.color.card_enabled));
        } else {
            birthdayText.setTextColor(Color.GRAY);
            birthdayCard.setCardBackgroundColor(getResources().getColor(R.color.card_disabled));
        }
    }
}
