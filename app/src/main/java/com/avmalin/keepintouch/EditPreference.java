package com.avmalin.keepintouch;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditPreference  extends AppCompatActivity {
    private TextView tv_whatsappMessage;
    private SharedPreferences sharedPreferences;
    private String defaultWhatsappMessage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_edit_preference);

        tv_whatsappMessage = findViewById(R.id.whatsappMessageTextView);
        Button resetButton = findViewById(R.id.resetButton);

        sharedPreferences = getSharedPreferences(getString(R.string.pref_name), MODE_PRIVATE);
        defaultWhatsappMessage = getString(R.string.whatsappMessage);


        loadMessage();

        tv_whatsappMessage.setOnClickListener(v -> {showEditDialog();});

        resetButton.setOnClickListener(v -> {
            sharedPreferences.edit().putString(getString(R.string.key_whatsapp_message), defaultWhatsappMessage).apply();
            loadMessage();
        });

    }

    private void showEditDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(sharedPreferences.getString(getString(R.string.key_whatsapp_message), defaultWhatsappMessage));

        new AlertDialog.Builder(this)
                .setTitle("ערוך הודעה")
                .setView(input)
                .setPositiveButton("שמור", (dialog, which) -> {
                    String newMessage = input.getText().toString();
                    sharedPreferences.edit().putString(getString(R.string.key_whatsapp_message), newMessage).apply();
                    loadMessage();
                })
                .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void loadMessage() {
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
