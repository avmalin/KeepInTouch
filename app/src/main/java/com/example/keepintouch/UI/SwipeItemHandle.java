package com.example.keepintouch.UI;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepintouch.MainActivity;
import com.example.keepintouch.R;
import com.example.keepintouch.types.MyContact;

import java.net.URLEncoder;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class SwipeItemHandle extends ItemTouchHelper.SimpleCallback{
    private final ContactAdapter adapter;
    private final Context context;

    public  SwipeItemHandle(Context context,ContactAdapter adapter){
        super(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT);
        this.adapter = adapter;
        this.context = context;
    }
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.RIGHT){
            sendWhatsAppById(adapter.getContactId(viewHolder.getLayoutPosition()));

        } else if (direction == ItemTouchHelper.LEFT) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + "0521234567")); // החלף למספר הרצוי
            context.startActivity(intent);

        }
        adapter.notifyItemChanged(viewHolder.getLayoutPosition() );


    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                //left swipe
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(recyclerView.getContext(), R.color.blue_light))
                .addSwipeLeftActionIcon(R.drawable.baseline_call_24)
                //right swipe
                .addSwipeRightBackgroundColor(ContextCompat.getColor(recyclerView.getContext(),R.color.person_color))
                .addSwipeRightActionIcon(R.drawable.whatsapp_logo_wine)
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
    public void sendWhatsAppById(long contact_id) {


        Cursor cursor =null;
        try{
            ContentResolver contentResolver = context.getContentResolver();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
            String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ";
            String[] selectionArgs = {String.valueOf(contact_id)};

            cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNumber = cursor.getString(phoneNumberIndex);
                phoneNumber = phoneNumber.replaceAll("[^0-9+]", "");
                phoneNumber = phoneNumber.replace("+972", "0");
                String uri = "https://api.whatsapp.com/send?phone=" + phoneNumber;
                uri += "&text=" +Uri.encode(context.getString(R.string.whatsappMessage));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                intent.setPackage("com.whatsapp");
                // Start WhatsApp chat
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(context, "WhatsApp isn't install", Toast.LENGTH_SHORT);
            toast.show();
        }
        finally {
            if (cursor!=null && !cursor.isClosed())
                cursor.close();
        }
    }
    public void sendCallById(long contact_id) {


        Cursor cursor =null;
        try{
            ContentResolver contentResolver = context.getContentResolver();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
            String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ";
            String[] selectionArgs = {String.valueOf(contact_id)};

            cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNumber = cursor.getString(phoneNumberIndex);
                phoneNumber = phoneNumber.replaceAll("[^0-9+]", "");
                phoneNumber = phoneNumber.replace("+972", "0");
                String uri = "https://api.whatsapp.com/send?phone=" + phoneNumber;
                uri += "&text=" +Uri.encode(context.getString(R.string.whatsappMessage));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                intent.setPackage("com.whatsapp");
                // Start WhatsApp chat
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(context, "WhatsApp isn't install", Toast.LENGTH_SHORT);
            toast.show();
        }
        finally {
            if (cursor!=null && !cursor.isClosed())
                cursor.close();
        }
    }
}
