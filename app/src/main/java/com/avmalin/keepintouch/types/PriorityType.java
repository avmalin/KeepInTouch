package com.avmalin.keepintouch.types;
import com.avmalin.keepintouch.R;

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
    public static PriorityType valueOf(int id)
    {
        if (id == R.id.rb_week) {
            return WEEKLY;
        } else if (id == R.id.rb_month) {
            return MONTHLY;
        } else if (id == R.id.rb_half_year) {
            return HALF_YEAR;
        } else if (id == R.id.rb_year) {
            return YEARLY;
        } else {
            return NEVER;
        }
    }
    public int idOf(){
        if (this == WEEKLY) {
            return R.id.rb_week;
        } else if (this == MONTHLY) {
            return R.id.rb_month;
        } else if (this == HALF_YEAR) {
            return R.id.rb_half_year;
        } else if (this == YEARLY) {
            return R.id.rb_year;
        } else {
            return R.id.never;
        }
    }
    public int compValue() {
        if (this == WEEKLY) return 7;
        else if (this == MONTHLY) return 30;
        else if (this == HALF_YEAR) return 180;
        else if (this == YEARLY) return 360;
        else return 0;
    }
}
