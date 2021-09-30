package com.example.android.customcalendar.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.android.customcalendar.EventRepository;
import com.example.android.customcalendar.database.Event;

import java.time.LocalDate;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class EventViewModel extends AndroidViewModel {

    private EventRepository mRepository;
    private MutableLiveData<LocalDate> mEventsDay = new MutableLiveData<>();
    private MutableLiveData<Integer> mRemovePosition = new MutableLiveData<>();
    private Event mEvent;

    public EventViewModel(Application application) {
        super(application);
        mRepository = new EventRepository(application);
        mEventsDay.setValue(LocalDate.now());
    }

    public Single<Long> insert(Event event) {
        return mRepository.insert(event);
    }

    public Single<List<Integer>> getDots(LocalDate date) { return mRepository.getDots(date); }

    public void setEventsDay(LocalDate day) {
        mEventsDay.setValue(day);
    }

    public MutableLiveData<LocalDate> getEventsDay() { return mEventsDay; }

    public Single<List<Event>> getSelectedDayEvents(LocalDate date) {
        return mRepository.getSelectedDayEvents(date);
    }

    public Completable deleteEvent(Event event) { return mRepository.delete(event);}

    public MutableLiveData<Integer> getDotRemovePosition() { return mRemovePosition; }

    public void removeDotAt(int i) { mRemovePosition.setValue(i);}

    public void setEvent(Event event) { mEvent = event; }

    public Event getEvent() { return mEvent; }
}
