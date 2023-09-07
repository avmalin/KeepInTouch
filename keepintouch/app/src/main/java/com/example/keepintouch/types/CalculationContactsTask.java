package com.example.keepintouch.types;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;

import com.example.keepintouch.MainActivity;

import java.lang.ref.WeakReference;

public class CalculationContactsTask extends AsyncTask<MyContactTable,Integer,MyContactTable> {
    private final Context mContext;
    private WeakReference<MainActivity> activityReference;
    private AnimationDrawable animationLoading;
    public CalculationContactsTask(MainActivity activity)
    {
        mContext=activity;
        activityReference = new WeakReference<>(activity);
    }

    @Override
    protected MyContactTable doInBackground(MyContactTable... myContactTables) {
        myContactTables[0].updateAllTable(mContext);
        return myContactTables[0];
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //start animation loading
        MainActivity activity = activityReference.get();
        if (activity!=null) {
            activity.startLoading();
        }
    }

    @Override
    protected void onPostExecute(MyContactTable myContactTable) {
        super.onPostExecute(myContactTable);
            MainActivity activity = activityReference.get();
            if (activity!=null) {
                activity.showMyContactActivity();
                activity.stopLoading();
            }
            //stop animation loading and invisible

    }
}
