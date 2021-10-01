package com.example.android.customcalendar;

public class Day {

    private final int mYear;
    private final int mMonth;
    private final int mDayOfMonth;

    public Day(int year, int month, int day) {
        this.mYear = year;
        this.mMonth = month;
        this.mDayOfMonth = day;
    }

    public int getDayOfMonth() {
        return mDayOfMonth;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() { return mYear; }
}
