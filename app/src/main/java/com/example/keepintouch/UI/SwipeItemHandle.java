package com.example.keepintouch.UI;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeItemHandle extends ItemTouchHelper.SimpleCallback{
    private final ContactAdapter adapter;
    private final Context context;

    public  SwipeItemHandle(Context context,ContactAdapter adapter){
        super(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT);
        this.adapter=adapter;
        this
    }
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
