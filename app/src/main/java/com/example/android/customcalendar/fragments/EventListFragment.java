package com.example.android.customcalendar.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.customcalendar.AlarmReceiver;
import com.example.android.customcalendar.R;
import com.example.android.customcalendar.adapters.EventListAdapter;
import com.example.android.customcalendar.database.Event;
import com.example.android.customcalendar.viewmodels.EventViewModel;
import com.example.android.customcalendar.viewmodels.TodayViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class EventListFragment extends Fragment {

    private DisposableSingleObserver<List<Event>> mDisposable;
    private DisposableCompletableObserver mDisposableCompletable;
    private final String TAG = "Events Fragment";
    private EventListAdapter mAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView eventRecyclerView = view.findViewById(R.id.event_recyclerview);

        mAdapter = new EventListAdapter();
        eventRecyclerView.setAdapter(mAdapter);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        EventViewModel eventModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        TodayViewModel todayModel = new ViewModelProvider(requireActivity()).get(TodayViewModel.class);
        eventModel.getEventsDay().observe(getViewLifecycleOwner(), day -> eventModel
                .getSelectedDayEvents(todayModel.getToday())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDisposable = new DisposableSingleObserver<List<Event>>() {
                    @Override
                    public void onSuccess(@NotNull List<Event> events) {
                        mAdapter.setEvents(events);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                }));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Event event = mAdapter.getEventAt(position);
                mAdapter.deleteEventAt(position);
                if (event.isReminder()) {
                    cancelAlarm(event.getId());
                    Log.i(TAG, "onSwiped: Cancelling reminder...");
                }
                eventModel.deleteEvent(event)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mDisposableCompletable = new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                if (mAdapter.getItemCount() == 0) {
                                    eventModel.removeDotAt(event.getDay());
                                }
                                Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(@NotNull Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        });
            }
        }).attachToRecyclerView(eventRecyclerView);
    }

    public void cancelAlarm(Long id) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent().setClass(requireContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), id.intValue(), intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.dispose();
        if (mDisposableCompletable != null) {
            mDisposableCompletable.dispose();
        }
    }
}