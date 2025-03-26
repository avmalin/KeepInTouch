package com.avmalin.keepintouch.android;



import android.content.Context;
import android.content.SharedPreferences;

import androidx.work.*;
import com.avmalin.keepintouch.R;
import com.avmalin.keepintouch.logic.DailyWorker;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WorkManagerHelper {

    public static void scheduleDailyWork(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        String savedTime = sharedPreferences.getString(context.getString(R.string.key_time_to_notification), "12:00");
        String[] timeParts = savedTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar now = Calendar.getInstance();
        Calendar nextRun = Calendar.getInstance();
        nextRun.set(Calendar.HOUR_OF_DAY,hour);
        nextRun.set(Calendar.MINUTE, minute);
        nextRun.set(Calendar.SECOND, 0);

        if (now.after(nextRun)) {
            nextRun.add(Calendar.DAY_OF_MONTH, 1); // אם השעה כבר עברה, נקבע למחר
        }

        long initialDelay = nextRun.getTimeInMillis() - now.getTimeInMillis();

        // הגדרת העבודה עם WorkManager
        WorkRequest workRequest = new PeriodicWorkRequest.Builder(DailyWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS) // הפעלה ראשונה בזמן מחושב
                .setConstraints(
                        new Constraints.Builder()
                                .setRequiresBatteryNotLow(true) // רק אם הסוללה לא חלשה
                                .setRequiresDeviceIdle(false) // גם אם המכשיר בשימוש
                                .build()
                )
                .build();

        // ביטול עבודה קודמת אם יש, ואז להפעיל חדשה
        workManager.cancelAllWorkByTag("dailyWork");
        workManager.enqueueUniquePeriodicWork("dailyWork", ExistingPeriodicWorkPolicy.REPLACE, (PeriodicWorkRequest) workRequest);
    }
}
