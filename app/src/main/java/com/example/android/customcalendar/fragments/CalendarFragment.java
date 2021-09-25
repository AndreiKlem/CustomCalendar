package com.example.android.customcalendar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android.customcalendar.R;
import com.example.android.customcalendar.interfaces.OnSwipeRequestListener;
import com.example.android.customcalendar.viewmodels.EventViewModel;
import com.example.android.customcalendar.viewmodels.TodayViewModel;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarFragment extends Fragment implements OnSwipeRequestListener {

    // How many pages
    private static final int PAGES_AMOUNT = 100;

    private static final String TAG = "Calendar fragment";
    private ViewPager2 mPager;
    private MonthPagerAdapter mMonthPagerAdapter;
    private int mCurrentPosition;
    private ViewPager2.OnPageChangeCallback mCallback;
    private TodayViewModel mTodayModel;
    private EventViewModel mEventModel;
    private TextView mYearMonthTextView;

    TextView mDayOneTextView;
    TextView mDayTwoTextView;
    TextView mDayThreeTextView;
    TextView mDayFourTextView;
    TextView mDayFiveTextView;
    TextView mDaySixTextView;
    TextView mDaySevenTextView;
    ImageView mBackImageView;
    ImageView mForwardImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTodayModel = new ViewModelProvider(requireActivity()).get(TodayViewModel.class);
        mEventModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        mMonthPagerAdapter = new MonthPagerAdapter(this);
        mPager = view.findViewById(R.id.month_pager);
        mYearMonthTextView = view.findViewById(R.id.year_month_text);
        mDayOneTextView = view.findViewById(R.id.day_one_textview);
        mDayTwoTextView = view.findViewById(R.id.day_two_textview);
        mDayThreeTextView = view.findViewById(R.id.day_three_textview);
        mDayFourTextView = view.findViewById(R.id.day_four_textview);
        mDayFiveTextView = view.findViewById(R.id.day_five_textview);
        mDaySixTextView = view.findViewById(R.id.day_six_textview);
        mDaySevenTextView = view.findViewById(R.id.day_seven_textview);
        mBackImageView = view.findViewById(R.id.back_arrow);
        mForwardImageView = view.findViewById(R.id.forward_arrow);

        // Preventing strange behavior of the viewpager2 when sometimes after swipe it's not
        // responding on click events
        // mPager.setOffscreenPageLimit(9);

        mPager.setAdapter(mMonthPagerAdapter);
        setCalendarPages();
        setDaysOfWeek();
        setHeader();
        mPager.registerOnPageChangeCallback(mCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position != mCurrentPosition) {
                    swipePage(position - mCurrentPosition);
                }
            }
        });

        mBackImageView.setOnClickListener(v -> onSwipeRequested(-1));
        mForwardImageView.setOnClickListener(v -> onSwipeRequested(1));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPager.unregisterOnPageChangeCallback(mCallback);
    }

    private void setCalendarPages() {
        mTodayModel.initializeItems(PAGES_AMOUNT);
        mCurrentPosition = PAGES_AMOUNT / 2;
        mPager.setCurrentItem(mCurrentPosition, false);
    }

    private void swipePage(int direction) {
        mTodayModel.increaseCurrentMonths(direction);
        mEventModel.setEventsDay(mTodayModel.getToday());
        mCurrentPosition += direction;
        if (mCurrentPosition < 1 || mCurrentPosition > mMonthPagerAdapter.getItemCount() - 2) {
            setCalendarPages();
        }
        setHeader();
    }

    @Override
    public void onSwipeRequested(int direction) {
        swipePage(direction);
        mPager.setCurrentItem(mCurrentPosition, true);
    }

    public void setHeader() {
        mYearMonthTextView.setText(mTodayModel.getYearMonthText());
    }

    private void setDaysOfWeek() {
        mDayOneTextView.setText(DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        mDayTwoTextView.setText(DayOfWeek.TUESDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        mDayThreeTextView.setText(DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        mDayFourTextView.setText(DayOfWeek.THURSDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        mDayFiveTextView.setText(DayOfWeek.FRIDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        mDaySixTextView.setText(DayOfWeek.SATURDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        mDaySevenTextView.setText(DayOfWeek.SUNDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
    }

    class MonthPagerAdapter extends FragmentStateAdapter {

        public MonthPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment month = new MonthFragment();
            Bundle args = new Bundle();
            long date = getItemId(position);
            args.putLong(MonthFragment.DATE, date);
            month.setArguments(args);
            return month;
        }

        @Override
        public int getItemCount() {
            return mTodayModel.getSize();
        }

        @Override
        public long getItemId(int position) {
            return mTodayModel.getId(position);
        }

        @Override
        public boolean containsItem(long itemId) {
            return mTodayModel.contains(itemId);
        }

    }
}
