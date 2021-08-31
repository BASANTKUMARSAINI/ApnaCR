package com.example.apnacr.teachers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apnacr.Models.Assignment;
import com.example.apnacr.Models.Solution;
import com.example.apnacr.R;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.AssignmentViewHolder> {

    Context context;
    List<Solution>assignments;

    public SolutionAdapter(Context context, List<Solution> assignments) {
        this.context = context;
        this.assignments = assignments;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.solution_layout,parent,false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Solution solution=assignments.get(position);
        holder.tvDes.setText(assignments.get(position).getDesc()+"");
        if(assignments.get(position).getThumbnail()!=null)
        {
            Picasso.get().load(assignments.get(position).getThumbnail())
                        .into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"Downloading...",Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            holder.imageView.setVisibility(View.GONE);
        }

        if(solution.getMarks()!=-100)
        {
            holder.tvStatus.setText("Checked");
            holder.etMarks.setEnabled(false);
            holder.etMarks.setText(solution.getMarks()+"");
            holder.btnUpload.setVisibility(View.GONE);

        }
        else{
            holder.tvStatus.setText("UnChecked");


            holder.btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String marks=holder.etMarks.getText().toString().trim();
                    if(TextUtils.isEmpty(marks))
                    {
                        Toast.makeText(context,"Enter marks",Toast.LENGTH_LONG).show();
                        return;
                    }
                    uploadMarks(Integer.parseInt(marks),solution);
                }
            });
        }


    }

    private void uploadMarks(int parseInt, Solution solution) {
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("marks",parseInt);
        Helper.showProgressBar(context,"Updating...");
        FirebaseFirestore.getInstance().collection("assignments").document(solution.getAssid())
                .collection("solutions").document(solution.getId()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Helper.dismissDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.dismissDialog();
                Log.d("TAG", "onFailure: "+e.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView imageView;
        public TextView tvDes,tvStatus;
        public EditText etMarks;
        public Button btnUpload;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus=itemView.findViewById(R.id.tv_status);
            etMarks=itemView.findViewById(R.id.tv_marks);
            btnUpload=itemView.findViewById(R.id.btn_marks);
            tvDes=itemView.findViewById(R.id.tv_desc);
            imageView=itemView.findViewById(R.id.imageView);
        }
    }
}
