package com.example.apnacr.teachers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apnacr.Models.Student;
import com.example.apnacr.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    Context context;
    String crid;
    List<String>students;
    public StudentAdapter(Context context, List<String> students,String crid) {
        this.context = context;
        this.students = students;
        this.crid=crid;
    }


    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.student_layout,parent,false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        FirebaseFirestore.getInstance().collection("students").document(students.get(position)).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override

                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot!=null)
                        {
                            Student student=documentSnapshot.toObject(Student.class);
                            holder.tvStudentName.setText(student.getName());
                        }
                    }
                });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,StudentActivity.class);
                intent.putExtra("uid",students.get(position));
                intent.putExtra("crid",crid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static  class  StudentViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tvStudentName;
        public View itemView;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
            tvStudentName=itemView.findViewById(R.id.tv_student_name);
        }
    }

}
