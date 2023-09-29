package com.example.keepintouch.types;
import androidx.annotation.NonNull;

import com.example.keepintouch.R;

public enum PriorityType {
    WEEKLY ,
    MONTHLY,
    HALF_YEAR,
    YEARLY,
    NEVER;
    public static PriorityType fromInt(int x)
    {
        switch (x) {
            case 7:
                return WEEKLY;
            case 30:
                return MONTHLY;
            case 180:
                return HALF_YEAR;
            case 360:
                return YEARLY;
            default:
                return NEVER;
        }
    }
    public static PriorityType valueOf(int id)
    {
        switch (id) {
            case R.id.rb_week:
                return WEEKLY;
            case R.id.rb_month:
                return MONTHLY;
            case R.id.rb_half_year:
                return HALF_YEAR;
            case R.id.rb_year:
                return YEARLY;
            default:
                return NEVER;
        }
    }
    public static PriorityType fromString(String pt){
        switch (pt) {
            case "WEEKLY":
                return WEEKLY;
            case "MONTHLY":
                return MONTHLY;
            case "HALF YEAR":
            case "HALF_YEAR":
                return HALF_YEAR;
            case "YEARLY":
                return YEARLY;
            default:
                return NEVER;
    }
    }
    public int idOf(){
        switch (this){
            case WEEKLY:
                return R.id.rb_week;
            case MONTHLY:
                return R.id.rb_month;
            case HALF_YEAR:
                return R.id.rb_half_year;
            case YEARLY:
                return R.id.rb_year;
            default:
                return R.id.never;
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

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case WEEKLY:
                return "WEEKLY";
            case MONTHLY:
                return "MONTHLY";
            case HALF_YEAR:
                return "HALF YEAR";
            case YEARLY:
                return "YEARLY";
            default:
                return "NEVER";
        }
    }
}
