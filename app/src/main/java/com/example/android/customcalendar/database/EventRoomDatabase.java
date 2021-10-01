package com.example.android.customcalendar.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Event.class}, version = 1, exportSchema = false)
public abstract class EventRoomDatabase extends RoomDatabase {

    public abstract EventDao eventDao();

    private static volatile EventRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static EventRoomDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (EventRoomDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EventRoomDatabase.class, "event_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    EventDao dao = INSTANCE.eventDao();
                    dao.delete_all();
                    LocalDate date = LocalDate.now();
                    List<Event> events = new ArrayList<>();
                    for(int i = 1; i < 30; i++) {
                        date = date.withDayOfMonth((int) ((Math.random() * (date.lengthOfMonth() - 1)) + 1));
                        events.add(new Event("Event " + i, "Description " + i,
                                date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, false));
                    }
                    dao.insertAll(events);
                }
            });
        }
    };
}
