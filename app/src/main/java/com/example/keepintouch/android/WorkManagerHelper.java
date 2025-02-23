package com.example.keepintouch.android;

import android.content.Context;
import androidx.work.*;
import com.example.keepintouch.logic.DailyWorker;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WorkManagerHelper {

    public static void scheduleDailyWork(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);

        // חישוב הזמן עד השעה 12:00 הקרובה
        Calendar now = Calendar.getInstance();
        Calendar nextRun = Calendar.getInstance();
        nextRun.set(Calendar.HOUR_OF_DAY, 12);
        nextRun.set(Calendar.MINUTE, 0);
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
