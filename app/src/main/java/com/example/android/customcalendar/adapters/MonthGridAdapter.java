package com.example.android.customcalendar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.android.customcalendar.Day;
import com.example.android.customcalendar.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MonthGridAdapter extends BaseAdapter {

    private final ArrayList<Day> mDays;
    private final Context mContext;
    private final int mCurrentMonth;
    private final int mOffsetColor;
    private HashSet<Integer> mDots = new HashSet<>();
    private int mCursorPosition;

    public MonthGridAdapter(Context context, ArrayList<Day> days, int currentMonth) {
        super();
        this.mContext = context;
        this.mDays = days;
        this.mCurrentMonth = currentMonth;
        this.mOffsetColor = ContextCompat.getColor(context, R.color.color_offset);
    }

    @Override
    public int getCount() {
        return mDays.size();
    }

    @Override
    public Object getItem(int position) {
        return mDays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.grid_cell, viewGroup, false);

            final TextView textView = convertView.findViewById(R.id.grid_cell_textview);
            final ImageView imageView = convertView.findViewById(R.id.dot_imageview);

            final ViewHolder viewHolder = new ViewHolder(textView, imageView);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.textView.setText(String.valueOf(mDays.get(position).getDayOfMonth()));
        viewHolder.imageView.setVisibility(View.INVISIBLE);

        convertView.setBackground(null);

        // Gray out other months days
        if(mDays.get(position).getMonth() != mCurrentMonth) {
            viewHolder.textView.setTextColor(mOffsetColor);
        }

        // Draw the cursor
        if(mCursorPosition == position) {
            convertView.setBackgroundResource(R.drawable.cursor);
        }

        // Set events dots for the month
        if(mDots != null) {
            if(mDots.contains(position)) {
                viewHolder.imageView.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    public void removeDot(int value) {
        mDots.remove(value);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(TextView textView, ImageView imageView) {
            this.textView = textView;
            this.imageView = imageView;
        }
    }

    public void setDots(List<Integer> dots) {
        this.mDots.addAll(dots);
        notifyDataSetChanged();
    }

    public void setCursor(int position) {
        this.mCursorPosition = position;
        notifyDataSetChanged();
    }
}
