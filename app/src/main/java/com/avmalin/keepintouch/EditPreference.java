package com.avmalin.keepintouch;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Locale;

public class EditPreference extends AppCompatActivity {
    private TextView tv_whatsappMessage;
    private TextView tv_timeToNotification;
    private SharedPreferences sharedPreferences;
    private String defaultWhatsappMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_edit_preference);

        tv_whatsappMessage = findViewById(R.id.whatsappMessageTextView);
        tv_timeToNotification = findViewById(R.id.timeToNotificationTextView);
        Button resetButton = findViewById(R.id.resetButton);
        LinearLayout whatsappMessageCard = findViewById(R.id.whatsappMessageCard);
        LinearLayout timeToNotificationCard = findViewById(R.id.timeToNotificationCard);


        //set the shared preferences
        sharedPreferences = getSharedPreferences(getString(R.string.pref_name), MODE_PRIVATE);
        //get the default message
        defaultWhatsappMessage = getString(R.string.whatsappMessage);


        //set listeners
        whatsappMessageCard.setOnClickListener(v ->
        {
            showEditDialogWhatsapp();
        });
        timeToNotificationCard.setOnClickListener(v ->{
            showEditDialogTimeToNotification();
        });

        resetButton.setOnClickListener(v -> {
            sharedPreferences.edit().putString(getString(R.string.key_whatsapp_message), defaultWhatsappMessage).apply();
            sharedPreferences.edit().putString(getString(R.string.key_time_to_notification), "12:00").apply();
            loadWhatsappMessage();
            loadTimeToNotification();
            Toast.makeText(EditPreference.this, "הגדרות אופסו", Toast.LENGTH_SHORT).show();
        });


        //set the cards value
        loadWhatsappMessage();
        loadTimeToNotification();

        // TODO: handle edit birthday

    }

    private void showEditDialogWhatsapp() {

        //set the input component
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(sharedPreferences.getString(getString(R.string.key_whatsapp_message), defaultWhatsappMessage));
        input.setSingleLine(false);
        input.setHorizontallyScrolling(false);
        //set the dialog
        new AlertDialog.Builder(this)
                .setTitle("ערוך הודעה")
                .setView(input)
                .setPositiveButton("שמור", (dialog, which) -> {
                    String newMessage = input.getText().toString();
                    sharedPreferences.edit().putString(getString(R.string.key_whatsapp_message), newMessage).apply();
                    loadWhatsappMessage();
                    Toast.makeText(EditPreference.this, "הודעה נשמרה", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void showEditDialogTimeToNotification() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_name), MODE_PRIVATE);
        String savedTime = prefs.getString(getString(R.string.key_time_to_notification), "12:00");
        String[] timeParts = savedTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);


        //set the input component
        TimePickerDialog  input = new TimePickerDialog(this,(view, selectedHour, selectedMinute) -> {
            String time = String.format("%02d:%02d", selectedHour, selectedMinute);
            sharedPreferences.edit().putString(getString(R.string.key_time_to_notification), time).apply();
            loadTimeToNotification();
            Toast.makeText(EditPreference.this, "הזמן נשמר", Toast.LENGTH_SHORT).show();

        }, hour, minute, true);
        input.show();

    }

    private void loadTimeToNotification() {
        String savedMessage = sharedPreferences.getString(getString(R.string.key_time_to_notification), "12:00");
        tv_timeToNotification.setText(savedMessage);
    }


    /**
     * this func set the whatsapp message to the text view
     */
    private void loadWhatsappMessage() {
        String savedMessage = sharedPreferences.getString(getString(R.string.key_whatsapp_message), defaultWhatsappMessage);
        tv_whatsappMessage.setText(shortenText(savedMessage, 30)); // מציג מקוצר
    }

    private String shortenText(String savedMessage, int maxLength) {
        if (savedMessage.length() > maxLength) {
            return savedMessage.substring(0, maxLength) + "...";
        } else {
            return savedMessage;
        }
    }
}
