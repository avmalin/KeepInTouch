package com.example.keepintouch.types;

public enum PriorityType {
    WEEKLY ,
    MONTHLY,
    HALF_YEAR,
    YEARLY,
    NEVER;
    public static PriorityType fromInt(int x)
    {
        switch (x) {
            case 0:
                return WEEKLY;
            case 1:
                return MONTHLY;
            case 2:
                return HALF_YEAR;
            case 3:
                return YEARLY;
            default:
                return NEVER;
        }
    }
    public int compValue() {
        switch (this) {
            case WEEKLY:
                return 7;
            case MONTHLY:
                return 30;
            case HALF_YEAR:
                return 180;
            case YEARLY:
                return 360;
            default:
                return 0;
        }
    }
}
