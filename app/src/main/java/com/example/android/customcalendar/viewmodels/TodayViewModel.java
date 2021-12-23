package com.example.android.customcalendar.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TodayViewModel extends AndroidViewModel {

    private LocalDate mToday;
    private ArrayList<Long> mItems = new ArrayList<>();

    public TodayViewModel(@NonNull Application application) {
        super(application);
    }

    public void initializeItems(int amount) {
        if (mToday == null) {
            mToday = LocalDate.now();
        } else if (!mItems.isEmpty()) {
            mItems.clear();
        }
        int middle = amount / 2;
        for (int i = 0; i < amount; i++) {
            mItems.add(i, mToday.plusMonths(i - middle).toEpochDay());
        }
    }

    public void setToday(long date) {
        mToday = LocalDate.ofEpochDay(date);
    }

    public void increaseCurrentMonths(int m) {
        mToday = mToday.plusMonths(m);
    }

    public void setCurrentDayInMonth(int d) {
        mToday = mToday.withDayOfMonth(d);
    }

    public LocalDate getToday() {
        return mToday;
    }

    public int getSize() {
        return mItems.size();
    }

    public long getId(int position) {
        return mItems.get(position);
    }

    public boolean contains(long itemId) {
        return mItems.contains(itemId);
    }

    public String getYearMonthText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL yyyy");
        return mToday.format(formatter);
    }

    public String getDateText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        return mToday.format(formatter);
    }
}
