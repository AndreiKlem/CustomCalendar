package com.example.android.customcalendar.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.android.customcalendar.R;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        LocalDate date = LocalDate.ofEpochDay(args.getLong("date"));
        int year = date.getYear();
        int month = date.getMonthValue() - 1;
        int day = date.getDayOfMonth();
        return new DatePickerDialog(requireActivity(), R.style.date_picker, this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Bundle result = new Bundle();
        result.putLong("bundleDateKey", LocalDate.of(year, month + 1, dayOfMonth).toEpochDay());
        getParentFragmentManager().setFragmentResult("requestDate", result);
    }
}
