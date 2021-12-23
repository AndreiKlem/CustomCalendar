package com.example.android.customcalendar.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "event_table")
public class Event {

    @PrimaryKey(autoGenerate = true)
    private long mId;

    @NonNull
    @ColumnInfo(name = "event")
    private final String mEvent;

    @ColumnInfo(name = "description")
    private final String mDescription;

    @ColumnInfo(name = "year")
    private final int mYear;

    @ColumnInfo(name = "month")
    private final int mMonth;

    @ColumnInfo(name = "day")
    private final int mDay;

    @ColumnInfo(name = "hour")
    private final int mHour;

    @ColumnInfo(name = "minutes")
    private final int mMinutes;

    @ColumnInfo(name = "reminder")
    private final boolean mReminder;

    public Event(@NonNull String event, String description, int year, int month, int day, int mHour,
                 int mMinutes, boolean reminder) {
        this.mEvent = event;
        this.mDescription = description;
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
        this.mHour = mHour;
        this.mMinutes = mMinutes;
        this.mReminder = reminder;
    }

    public void setId(long id) {this.mId = id;}

    public long getId() {return this.mId;}

    public String getEvent() {return this.mEvent;}

    public String getDescription() {return  this.mDescription;}

    public int getYear() {return this.mYear;}

    public int getMonth() {return this.mMonth;}

    public int getDay() {return this.mDay;}

    public int getHour() {return this.mHour;}

    public int getMinutes() {return this.mMinutes;}

    public boolean isReminder() {return this.mReminder;}
}
