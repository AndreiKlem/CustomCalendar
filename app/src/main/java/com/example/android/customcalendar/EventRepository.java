package com.example.android.customcalendar;

import android.app.Application;

import com.example.android.customcalendar.database.Event;
import com.example.android.customcalendar.database.EventDao;
import com.example.android.customcalendar.database.EventRoomDatabase;

import java.time.LocalDate;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class EventRepository {

    private EventDao mEventDao;

    public EventRepository(Application application) {
        EventRoomDatabase db = EventRoomDatabase.getDatabase(application);
        mEventDao = db.eventDao();
    }

    public Single<Long> insert(Event event) {
        return mEventDao.insert(event);
    }

    public Single<List<Integer>> getDots(LocalDate date) {
        return mEventDao.getDots(date.getYear(), date.getMonthValue());
    }

    public Single<List<Event>> getSelectedDayEvents(LocalDate date) {
        return mEventDao.getSelectedDayEvents(date.getYear(), date.getMonthValue()
                , date.getDayOfMonth());
    }

    public Completable delete(Event event) { return mEventDao.delete(event); }
}
