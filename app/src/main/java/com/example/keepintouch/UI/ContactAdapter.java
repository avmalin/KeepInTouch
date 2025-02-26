package com.example.keepintouch.UI;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepintouch.R;
import com.example.keepintouch.types.MyContact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private final List<MyContact> contactList;

    public ContactAdapter(List<MyContact> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyContact contact = contactList.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvPriority.setText(contact.getPriorityType().toString());

        //set days
        long date  = contact.getLastCall();
        String lastDate = "";
        if (date > 0) {
            long currentDay = System.currentTimeMillis();
            long days = (currentDay - date)/(1000*60*60*24);// 1 day = 24 hours = 24 * 60 * 60 * 1000 milliseconds
            lastDate = days + " days";
            if(days >  contact.getPriorityType().compValue())
                holder.tvNumber.setTextColor(Color.RED);
        }
        else if (date==0)//if never called
        {
            lastDate = "∞";
            holder.tvNumber.setTextColor(Color.RED);
            holder.tvNumber.setTextSize(24);
        }
        holder.tvNumber.setText(lastDate);
        //set the image
        String photoUri = contact.getPhotoSrc();
        if (photoUri != null) {
            holder.ivImage.setImageURI(Uri.parse(photoUri));
        }


    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
    public Long getContactId(int position){
        return contactList.get(position).getContactId();
    }

    static class ViewHolder  extends RecyclerView.ViewHolder{
        TextView tvNumber;
        TextView tvName;
        //TextView contactIdView;
        TextView tvPriority;
        ImageView ivImage;



        ViewHolder( View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvNumber.setVisibility(View.VISIBLE);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPriority = itemView.findViewById(R.id.tv_priority);
        }
    }
}
