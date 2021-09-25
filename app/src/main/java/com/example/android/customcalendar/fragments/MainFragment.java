package com.example.android.customcalendar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.android.customcalendar.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainFragment extends Fragment {

    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Fragment calendarFragment = new CalendarFragment();
        Fragment eventListFragment = new EventListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.calendar_fragment_container, calendarFragment);
        transaction.replace(R.id.event_list_fragment_container, eventListFragment);
        transaction.setReorderingAllowed(true);
        transaction.commit();

        fab = view.findViewById(R.id.add_event_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_addEventFragment);
            }
        });
    }
}