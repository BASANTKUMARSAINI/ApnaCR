package com.example.apnacr.student.ui.classroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apnacr.R;

import org.w3c.dom.Text;

import java.util.List;
import java.util.zip.CheckedOutputStream;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder> {

List<String>times;

Context context;

    public TimeTableAdapter(List<String> times, Context context) {
        this.times = times;
        this.context = context;
    }

    @NonNull
    @Override
    public TimeTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.time_table_layout,parent,false);

        return new TimeTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableViewHolder holder, int position) {
        String day="Sunday";
        switch (position)
        {
            case 1:
                day="Monday";
                break;
            case 2:
                day="Tuesday";
                break;
            case 3:
                day="Wednesday";
                break;
            case 4:
                day="Thursday";
                break;
            case 5:
                day="Friday";
                break;
            case 6:
                day="Saturday";
                break;

        }
        holder.tvDay.setText(day);
        holder.tvTime.setText(times.get(position)+"");

    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public static class TimeTableViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tvTime,tvDay;
public View itemView;
        public TimeTableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay=itemView.findViewById(R.id.tv_day);
            tvTime=itemView.findViewById(R.id.tv_time);
            this.itemView=itemView;
        }
    }
}
