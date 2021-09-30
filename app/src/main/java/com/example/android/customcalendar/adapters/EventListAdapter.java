package com.example.android.customcalendar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.example.android.customcalendar.R;
import com.example.android.customcalendar.database.Event;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<Event> mEventList = new ArrayList<>();
    private final OnEditClickListener mListener;
    final RecyclerView mEventRecyclerView;

    public EventListAdapter(OnEditClickListener listener, RecyclerView eventRecyclerView) {
        this.mListener = listener;
        this.mEventRecyclerView = eventRecyclerView;
    }

    @NonNull
    @Override
    public EventListAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eventlist_item, parent, false);
        return new EventViewHolder(mItemView);
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


    public interface OnEditClickListener {
        void onEditClick(Event event, int position);
    }

    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mEventTitle;
        public View mItemView;
        public TextView mDescription;
        public ImageView mEditImage;

        public EventViewHolder(View itemView) {
            super(itemView);
            mEventTitle = itemView.findViewById(R.id.title_textview);
            mDescription = itemView.findViewById(R.id.description_textview);
            mEditImage = itemView.findViewById(R.id.edit_event_imageview);
            this.mItemView = itemView;
            itemView.setOnClickListener(this);
            mEditImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int mPosition = getLayoutPosition();
            if (mPosition != RecyclerView.NO_POSITION) {
                if (view == mItemView) {
                    if (mEditImage.getVisibility() == View.GONE) {
                        mDescription.setMaxLines(3);
                        TransitionManager.beginDelayedTransition(mEventRecyclerView, new AutoTransition());
                        mEditImage.setVisibility(View.VISIBLE);
                    } else {
                        mDescription.setMaxLines(1);
                        TransitionManager.beginDelayedTransition(mEventRecyclerView, new AutoTransition());
                        mEditImage.setVisibility(View.GONE);
                    }
                } else if (view == mEditImage) {
                    Event event = getEventAt(mPosition);
                    mListener.onEditClick(event, mPosition);
                }
            }
        }
    }
}
