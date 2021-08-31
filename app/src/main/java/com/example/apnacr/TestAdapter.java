package com.example.apnacr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apnacr.Models.Assignment;
import com.example.apnacr.Models.Test;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

    Context context;
    List<Test>tests;

    public TestAdapter(Context context, List<Test> assignments) {
        this.context = context;
        this.tests = assignments;
    }

    @NonNull
    @Override
    public  TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.test_layout,parent,false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {

        Test test=tests.get(position);
        holder.tvDate.setText(test.getDate());
        holder.tvTime.setText(test.getTime());

        holder.tvTopic.setText(test.getDesc());
        holder.tvMarks.setText(test.getMarks()+"");
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }

    public static class TestViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tvTime,tvDate;
        public View itemView;
        public TextView tvMarks,tvTopic;
        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate=itemView.findViewById(R.id.tv_date);
            tvTime=itemView.findViewById(R.id.tv_time);
            tvMarks=itemView.findViewById(R.id.tv_marks);
            tvTopic=itemView.findViewById(R.id.tv_topic);
            this.itemView=itemView;
        }
    }
}
