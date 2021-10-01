package com.example.android.customcalendar.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.customcalendar.Day;
import com.example.android.customcalendar.R;
import com.example.android.customcalendar.adapters.MonthGridAdapter;
import com.example.android.customcalendar.interfaces.OnSwipeRequestListener;
import com.example.android.customcalendar.viewmodels.EventViewModel;
import com.example.android.customcalendar.viewmodels.TodayViewModel;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MonthFragment extends Fragment {

    public static final String DATE = "date";
    private static final String TAG = "MonthFragment";
    private int mOffsetBefore;
    private MonthGridAdapter mAdapter;
    private final ArrayList<Day> mDays = new ArrayList<>();
    private TodayViewModel mTodayModel;
    private EventViewModel mEventModel;
    private OnSwipeRequestListener onSwipeRequestListener;
    private LocalDate mCurrentMonth;
    private DisposableSingleObserver<List<Integer>> mDisposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.month_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GridView grid = view.findViewById(R.id.month_grid);

        // Receiving date from page adapter
        Bundle args = getArguments();
        assert args != null : "date is nor received";
        long date = args.getLong(DATE);

        mTodayModel = new ViewModelProvider(requireActivity()).get(TodayViewModel.class);
        mEventModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        mCurrentMonth = LocalDate.ofEpochDay(date);

        LocalDate firstDay = mCurrentMonth.withDayOfMonth(1);
        LocalDate lastDay = mCurrentMonth.withDayOfMonth(mCurrentMonth.lengthOfMonth());
        mOffsetBefore = firstDay.getDayOfWeek().getValue() - 1;
        int offsetAfter = 7 - lastDay.getDayOfWeek().getValue();
        LocalDate slider = firstDay.minusDays(mOffsetBefore);
        int cellsAmount = mOffsetBefore + lastDay.getDayOfMonth()
                + ((offsetAfter == 0) ? 7 : offsetAfter);

        // Preparing days to fill the grid.
        for (int i = 0; i < cellsAmount; i++) {
            mDays.add(i, new Day(slider.getYear(), slider.getMonthValue(), slider.getDayOfMonth()));
            slider = slider.plusDays(1);
        }

        mAdapter = new MonthGridAdapter(requireContext(), mDays, mCurrentMonth.getYear(), mCurrentMonth.getMonthValue());
        grid.setAdapter(mAdapter);
        setMonthDots();

        // Moving the cursor on the grid according to the user selection.
        grid.setOnItemClickListener((adapterView, view1, pos, l) -> {
            if (pos < mOffsetBefore) {
                onSwipeRequestListener.onSwipeRequested(-1);
            } else if (pos > mOffsetBefore - 1 && pos < mOffsetBefore + mCurrentMonth.lengthOfMonth()) {
                mTodayModel.setCurrentDayInMonth(pos - mOffsetBefore + 1);
                mEventModel.setEventsDay(mTodayModel.getToday());
                setCursor();
            } else {
                onSwipeRequestListener.onSwipeRequested(1);
            }
        });

        mEventModel.getDotRemovePosition().observe(getViewLifecycleOwner(), day -> {
            mAdapter.removeDot(day + mOffsetBefore - 1);
        });
    }

    private void setMonthDots() {
        mEventModel.getDots(mCurrentMonth)
                .map(dots -> {
                    for (int i = 0; i < dots.size(); i++) {
                        dots.set(i, dots.get(i) + mOffsetBefore - 1);
                    }
                    return dots;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDisposable = new DisposableSingleObserver<List<Integer>>() {
                    @Override
                    public void onSuccess(@NotNull List<Integer> eventsDots) {
                        mAdapter.setDots(eventsDots);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    // Store the listener for swiping events.
    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnSwipeRequestListener) {
            onSwipeRequestListener = (OnSwipeRequestListener) getParentFragment();
        } else {
            throw new RuntimeException(getParentFragment().toString()
                    + " must implement OnSwipeRequestListener interface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setCursor();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.dispose();
    }

    private void setCursor() {
        mAdapter.setCursor(mTodayModel.getToday().getDayOfMonth() + mOffsetBefore - 1);
    }
}
