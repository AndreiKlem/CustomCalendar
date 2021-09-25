package com.example.android.customcalendar.database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.android.customcalendar.Day;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Future;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Single<Long> insert(Event event);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Event> events);

    @Delete
    Completable delete(Event event);

    @Query("DELETE FROM event_table")
    void delete_all();

    @Query("SELECT * FROM event_table WHERE year IS :year AND month IS :month AND day IS :day")
    Single<List<Event>> getSelectedDayEvents(int year, int month, int day);

    @Query("SELECT day FROM event_table WHERE year IS :year AND month IS :month")
    Single<List<Integer>> getDots(int year, int month);
}
