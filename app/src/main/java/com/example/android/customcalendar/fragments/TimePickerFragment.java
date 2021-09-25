package com.example.android.customcalendar.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.android.customcalendar.R;

import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        int hour = args.getInt("hours", 12);
        int minute = args.getInt("minutes", 0);
        return new TimePickerDialog(requireActivity(), R.style.time_picker,
                this, hour, minute, DateFormat.is24HourFormat(requireContext()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Bundle result = new Bundle();
        result.putInt("bundleHourKey", hourOfDay);
        result.putInt("bundleMinuteKey", minute);
        getParentFragmentManager().setFragmentResult("requestTime", result);
    }
}
