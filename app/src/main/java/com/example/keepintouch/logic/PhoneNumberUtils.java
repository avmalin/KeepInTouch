package com.example.keepintouch.logic;

import java.util.*;

public class PhoneNumberUtils {

    private static final String COUNTRY_CODE = "+972"; // ניתן לשנות לקוד המדינה שלך
    private static final String LOCAL_PREFIX = "0"; // קידומת מקומית (לישראל זה 0)

    public static ArrayList<String> generatePhoneVariations(List<String> phoneNumbers) {
        ArrayList<String> variations =new ArrayList<String>();

        for (String number : phoneNumbers) {
            String cleanNumber = number.replaceAll("[^0-9+]", ""); // ניקוי רווחים, מקפים וכו'

            if (cleanNumber.startsWith("+")) {
                // מספר עם קידומת בינלאומית
                String localVersion = convertToLocal(cleanNumber);
                variations.add(cleanNumber); // עם קידומת
                variations.add(localVersion); // בלי קידומת
            } else if (cleanNumber.startsWith(LOCAL_PREFIX)) {
                // מספר מקומי
                String internationalVersion = convertToInternational(cleanNumber);
                variations.add(cleanNumber); // בלי קידומת
                variations.add(internationalVersion); // עם קידומת
            } else {
                // מספר לא ברור – מוסיפים את שתי האפשרויות ליתר ביטחון
                variations.add(COUNTRY_CODE + cleanNumber);
                variations.add(cleanNumber);
            }
        }

        return variations;
    }

    private static String convertToLocal(String number) {
        // המרת מספר בינלאומי למקומי: +972543210987 → 0543210987
        if (number.startsWith(COUNTRY_CODE)) {
            return LOCAL_PREFIX + number.substring(COUNTRY_CODE.length());
        }
        return number;
    }

    private static String convertToInternational(String number) {
        // המרת מספר מקומי לבינלאומי: 0543210987 → +972543210987
        if (number.startsWith(LOCAL_PREFIX)) {
            return COUNTRY_CODE + number.substring(1);
        }
        return number;
    }

}

