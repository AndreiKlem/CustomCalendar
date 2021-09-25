package com.example.android.customcalendar;

public class Day {

    private int mMonth;
    private int mDayOfMonth;
    private boolean mHasEvent;

    public Day(int month, int day, boolean hasEvent) {
        this.mMonth = month;
        this.mDayOfMonth = day;
        this.mHasEvent = hasEvent;
    }

    public int getDayOfMonth() {
        return mDayOfMonth;
    }

    public void setDayOfMonth(int mDayOfMonth) {
        this.mDayOfMonth = mDayOfMonth;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public boolean isHasEvent() {
        return mHasEvent;
    }

    public void setHasEvent(boolean mHasEvent) {
        this.mHasEvent = mHasEvent;
    }
}
