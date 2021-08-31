package com.example.apnacr.teachers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apnacr.Models.Assignment;
import com.example.apnacr.R;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    Context context;
    List<Assignment>assignments;

    public AssignmentAdapter(Context context, List<Assignment> assignments) {
        this.context = context;
        this.assignments = assignments;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_layout,parent,false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        holder.tvDate.setText(assignments.get(position).getSdate()+" to "+assignments.get(position).getLdate());
        holder.tvDes.setText(assignments.get(position).getDesc());
        if(assignments.get(position).getThumbnail()!=null)
        {
            Picasso.get().load(assignments.get(position).getThumbnail())
                        .into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String>list=assignments.get(position).getFiles();
                    if(list!=null&&list.size()>0&&Helper.checkForPermision(context))
                        downloadFile(list.get(0));
                }
            });
        }
        else{
            holder.imageView.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView imageView;
        public TextView tvDes,tvDate;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate=itemView.findViewById(R.id.tv_date);
            tvDes=itemView.findViewById(R.id.tv_desc);
            imageView=itemView.findViewById(R.id.imageView);
        }
    }

    private void downloadFile(String url) {
        Helper.showProgressBar(context,"Downloading...");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(url);
        // StorageReference  islandRef = storageRef.child("file.pdf");

//        File rootPath = new File(Environment.getExternalStorageDirectory(), "file"+System.currentTimeMillis());
//        if(!rootPath.exists()) {
//            rootPath.mkdirs();
//        }



        File apkStorage = new File(context.getExternalFilesDir(null)+"/"+"ApnaCR");//new File(Environment.getExternalStorageDirectory() + "/" + "JanElaaj");


        boolean dirCreat = false;
        //If File is not present create directory

        if (!apkStorage.exists()) {
            dirCreat = apkStorage.mkdir();

        }


        File outputFile = new File(apkStorage, "File_"+System.currentTimeMillis());//Create Output file in Main File

        //Create New File if not present
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



        storageRef.getFile(outputFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Helper.dismissDialog();
                Log.e("firebase ",";local tem file created  created " +outputFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Helper.dismissDialog();
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
            }
        });
    }
}
