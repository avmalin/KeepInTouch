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

        return getHebrewDayName(hebrewDay) + " " + getHebrewMonthName(hebrewMonth) + " " + hebrewYear;
    }


    private static String getHebrewMonthName(int month) {
        String[] hebrewMonths = {
                "תשרי", "חשוון", "כסלו", "טבת", "שבט", "אדר א'", "אדר ב'", "ניסן", "אייר", "סיוון", "תמוז", "אב", "אלול"
        };
        return hebrewMonths[month - 1];
    }

    public static String getHebrewDayName(int number) {
        String[] hebrewLetters = {
                "א", "ב", "ג", "ד", "ה", "ו", "ז", "ח", "ט", "י",
                "יא", "יב", "יג", "יד", "טו", "טז", "יז", "יח", "יט", "כ",
                "כא", "כב", "כג", "כד", "כה", "כו", "כז", "כח", "כט", "ל"
        };
        if (number >= 1 && number <= 30) {
            return hebrewLetters[number - 1];
        }
        return String.valueOf(number);
    }
}
