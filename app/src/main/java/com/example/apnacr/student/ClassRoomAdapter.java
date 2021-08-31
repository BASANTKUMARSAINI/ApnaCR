package com.example.apnacr.student;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apnacr.Models.ClassRoom;
import com.example.apnacr.R;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.List;

public class ClassRoomAdapter extends RecyclerView.Adapter<ClassRoomAdapter.ClassRoomViewHolder> {
    Context context;
    List<String>classRooms;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public ClassRoomAdapter(Context context, List<String>classRooms)
    {
        this.classRooms=classRooms;
        this.context=context;
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public ClassRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.classroom_layout,parent,false);
        return new ClassRoomViewHolder(context,view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassRoomViewHolder holder, int position) {
        Drawable drawable=null;

              if(position%3==0)
              {
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                      holder.layout.setBackground(context.getDrawable(R.drawable.image1));
                      drawable=context.getDrawable(R.drawable.image1);
                  }
              }
              else if(position%3==1)
              {
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                      holder.layout.setBackground(context.getDrawable(R.drawable.image2));
                      drawable=context.getDrawable(R.drawable.image2);
                  }
              }
              else{
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                      holder.layout.setBackground(context.getDrawable(R.drawable.image3));
                      drawable=context.getDrawable(R.drawable.image3);
                  }
              }


              db.collection("classrooms").document(classRooms.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                  @Override
                  public void onSuccess(DocumentSnapshot documentSnapshot) {
                      if(documentSnapshot!=null)
                      {
                          ClassRoom classRoom=documentSnapshot.toObject(ClassRoom.class);
                          holder.tvSubject.setText(classRoom.getSubject()+"");
                          holder.tvDate.setText(classRoom.getDate()+"");
                          holder.tvClassName.setText(classRoom.getName()+"");
                          holder.tvClassCode.setText(classRoom.getCnumber()+"");
                          List<String>list=classRoom.getStudents();
                          holder.tvStudents.setText(list.size()+" students");

                          holder.itemView.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View view) {
                                  Intent intent=new Intent(context, StudentClassRoomActivity.class);
                                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                  Log.d("TAG", "onClick: ");
                                  intent.putExtra("classroom",classRoom);

                                  intent.putExtra("image",position%3);
                                  context.startActivity(intent);

                              }
                          });
                      }
                  }
              });

    }

    @Override
    public int getItemCount() {
        return classRooms.size();
    }

    public static class ClassRoomViewHolder extends RecyclerView.ViewHolder
    {
        Context context;
        public TextView tvStudents,tvClassName,tvSubject,tvClassCode,tvDate;
        ConstraintLayout layout;
        public View itemView;
        public ClassRoomViewHolder(Context context,@NonNull View itemView) {
            super(itemView);
            this.context=context;
            tvClassCode=itemView.findViewById(R.id.tv_class_code);
            tvClassName=itemView.findViewById(R.id.tv_class_name);
            tvSubject=itemView.findViewById(R.id.tv_subject);
            tvStudents=itemView.findViewById(R.id.tv_students);
            layout=itemView.findViewById(R.id.layout);
            tvDate=itemView.findViewById(R.id.tv_date);
            this.itemView=itemView;

        }
    }
}
