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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.android.customcalendar.AlarmReceiver;
import com.example.android.customcalendar.R;
import com.example.android.customcalendar.database.Event;
import com.example.android.customcalendar.viewmodels.EventViewModel;
import com.example.android.customcalendar.viewmodels.TodayViewModel;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AddEventFragment extends Fragment {

    private static final String DATE_REQUEST_CODE = "requestDate";
    public static final String TIME_REQUEST_CODE = "requestTime";
    private static final String TAG = "AddEventFragment";

    private EditText mEventTitle;
    private EditText mDescriptionText;
    private TextView mDateTextView;
    private TextView mTimeTextView;
    private LocalTime mTime;
    private TodayViewModel mTodayModel;
    private EventViewModel mEventModel;
    private boolean mReminderFlag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTodayModel = new ViewModelProvider(requireActivity()).get(TodayViewModel.class);
        mEventModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        ImageView cancelImageView = view.findViewById(R.id.cancel_imageview);
        ImageView saveImageView = view.findViewById(R.id.save_imageview);
        mEventTitle = view.findViewById(R.id.event_name_edittext);
        mDescriptionText = view.findViewById(R.id.description_edittext);
        mDateTextView = view.findViewById(R.id.date_textview);
        mTimeTextView = view.findViewById(R.id.time_textview);
        TextView headerTextView = view.findViewById(R.id.header_textview);
        String header = getArguments().getString(MainFragment.HEADER_EXTRA);
        headerTextView.setText(header);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        mEventTitle.requestFocus();

        if (header.equals(getResources().getString(R.string.edit_event))) {
            Event event = mEventModel.getEvent();
            mEventTitle.setText(event.getEvent());
            mDescriptionText.setText(event.getDescription());
            cancelImageView.setImageResource(R.drawable.ic_delete);
        }
        mDateTextView.setText(mTodayModel.getDateText());
        mDateTextView.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            Bundle args = new Bundle();
            long date = mTodayModel.getToday().toEpochDay();
            args.putLong("date", date);
            datePicker.setArguments(args);
            fragmentManager.setFragmentResultListener(DATE_REQUEST_CODE, getViewLifecycleOwner(),
                    (requestKey, result) -> {
                        long selectedDate = result.getLong("bundleDateKey");
                        mTodayModel.setToday(selectedDate);
                        mDateTextView.setText(mTodayModel.getDateText());
                    });
            datePicker.show(fragmentManager, "datePicker");
        });

        mTimeTextView.setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment();
            Bundle args = new Bundle();
            mTime = LocalTime.now();
            args.putInt("hours", mTime.getHour());
            args.putInt("minutes", mTime.getMinute());
            timePicker.setArguments(args);
            fragmentManager.setFragmentResultListener(TIME_REQUEST_CODE, getViewLifecycleOwner(),
                    (requestKey, result) -> {
                        int hour = result.getInt("bundleHourKey");
                        int minute = result.getInt("bundleMinuteKey");
                        mTime = LocalTime.of(hour, minute);
                        mTimeTextView.setText(mTime.toString());
                        mReminderFlag = true;
                    });
            timePicker.show(fragmentManager, "timePicker");
        });

        cancelImageView.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_addEventFragment_to_mainFragment));

        saveImageView.setOnClickListener(v -> {
            String eventTitle = mEventTitle.getText().toString();
            String description = mDescriptionText.getText().toString().trim();
            if (eventTitle.trim().isEmpty()) {
                mEventTitle.getText().clear();
                Toast.makeText(requireContext(), R.string.title_insert_request, Toast.LENGTH_SHORT).show();
            } else {
                LocalDate date = mTodayModel.getToday();
                Toast.makeText(requireContext(),
                        header.equals(getResources().getString(R.string.add_event)) ?
                                R.string.event_created : R.string.event_edited,
                        Toast.LENGTH_SHORT).show();
                mEventModel.insert(new Event(eventTitle, description, date.getYear(),
                        date.getMonthValue(), date.getDayOfMonth(),
                        mTime != null? mTime.getHour() : 0,
                        mTime != null ? mTime.getMinute() : 0,
                        mReminderFlag))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new DisposableSingleObserver<Long>() {
                            @Override
                            public void onSuccess(@NotNull Long id) {
                                if (mReminderFlag && getZonedDateTime().isAfter(ZonedDateTime.now())) {
                                    createNotification(id, eventTitle, description);
                                }
                            }

                            @Override
                            public void onError(@NotNull Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        });
                Navigation.findNavController(v)
                        .navigate(R.id.action_addEventFragment_to_mainFragment);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mEventTitle.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEventTitle, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEventTitle.getWindowToken(), 0);
    }

    public void createNotification(Long id, String title, String description) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent().setClass(requireContext(), AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), id.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, getZonedDateTime().toInstant().toEpochMilli(), pendingIntent);
    }

    private ZonedDateTime getZonedDateTime() {
        LocalDate date = mTodayModel.getToday();
        LocalDateTime localDateTime = LocalDateTime.of(date.getYear(), date.getMonthValue(),
                date.getDayOfMonth(), mTime.getHour(), mTime.getMinute(), 0);
        return localDateTime.atZone(ZoneId.systemDefault());
    }
}