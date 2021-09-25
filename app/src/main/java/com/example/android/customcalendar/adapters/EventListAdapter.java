package com.example.android.customcalendar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.customcalendar.R;
import com.example.android.customcalendar.database.Event;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<Event> mEventList = new ArrayList<>();

    @NonNull
    @Override
    public EventListAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eventlist_item, parent, false);
        return new EventViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull EventListAdapter.EventViewHolder holder, int position) {
        String title = mEventList.get(position).getEvent();
        String description = mEventList.get(position).getDescription();
        holder.mEventTitle.setText(title);
        holder.mDescription.setText(description);
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    public void setEvents(List<Event> events) {
        this.mEventList = events;
        notifyDataSetChanged();
    }

    public Event getEventAt(int position) {
        return mEventList.get(position);
    }

    public void deleteEventAt(int position) {
        mEventList.remove(position);
        notifyItemRemoved(position);
    }

    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mEventTitle;
        public final TextView mDescription;
        final EventListAdapter mAdapter;

        public EventViewHolder(View itemView, EventListAdapter adapter) {
            super(itemView);
            mEventTitle = itemView.findViewById(R.id.title_textview);
            mDescription = itemView.findViewById(R.id.description_textview);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int mPosition = getLayoutPosition();
            String element = mEventList.get(mPosition).getEvent();
            Toast.makeText(view.getContext(), element + "Clicked!", Toast.LENGTH_SHORT).show();
        }
    }
}
