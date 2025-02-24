package com.example.keepintouch.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.keepintouch.MainActivity;
import com.example.keepintouch.types.MyContactTable;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncTableBackgroundTask {
    private final Context mContext;
    private final WeakReference<MainActivity> activityReference;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public SyncTableBackgroundTask(MainActivity activity) {
        this.mContext = activity;
        this.activityReference = new WeakReference<>(activity);
    }

    public void execute(MyContactTable myContactTable) {
        MainActivity activity = activityReference.get();
        if (activity != null) {
            activity.startLoading();
        }
        executorService.execute(() -> {
            // background func
            myContactTable.updateAllTable(mContext);

            // update UI
            mainHandler.post(() -> {
                MainActivity activityRef = activityReference.get();
                if (activityRef != null) {
                    activityRef.showMyContactActivity();
                    activityRef.stopLoading();
                }
            });
        });
    }
}

