package com.avmalin.keepintouch.types;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import android.icu.util.HebrewCalendar; // בגרסאות אנדרואיד שתומכות

public class HebrewDateConverter {
    public static String convertToHebrewDate(int year, int month, int day) {
        //
        Calendar gregorianCalendar = new GregorianCalendar(year, month - 1, day);


        HebrewCalendar hebrewCalendar = new HebrewCalendar();
        hebrewCalendar.setTime(gregorianCalendar.getTime());


        int hebrewDay = hebrewCalendar.get(Calendar.DAY_OF_MONTH);
        int hebrewMonth = hebrewCalendar.get(Calendar.MONTH) + 1; // החודשים מתחילים מ-0
        int hebrewYear = hebrewCalendar.get(Calendar.YEAR);

        return hebrewDay + " " + getHebrewMonthName(hebrewMonth) + " " + hebrewYear;
    }


    private static String getHebrewMonthName(int month) {
        String[] hebrewMonths = {
                "תשרי", "חשוון", "כסלו", "טבת", "שבט", "אדר א'", "אדר ב'", "ניסן", "אייר", "סיוון", "תמוז", "אב", "אלול"
        };
        return hebrewMonths[month - 1];
    }
}
