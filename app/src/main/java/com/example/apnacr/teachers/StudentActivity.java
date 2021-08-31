package com.example.apnacr.teachers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.hardware.lights.LightsManager;
import android.os.Bundle;

import com.example.apnacr.Models.Solution;
import com.example.apnacr.Models.Student;
import com.example.apnacr.R;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends AppCompatActivity {
    String crid,uid;
    SolutionAdapter adapter;
    List<Solution>solutions=new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        crid=getIntent().getStringExtra("crid");
        uid=getIntent().getStringExtra("uid");



    }

    @Override
    protected void onStart() {
        super.onStart();
        Helper.showProgressBar(this,"Fetching...");
        FirebaseFirestore.getInstance().collection("solutions").whereEqualTo("crid",crid).whereEqualTo("uid",uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots!=null&&queryDocumentSnapshots.size()>0)
                {
                    solutions=new ArrayList<>();
                    for(DocumentSnapshot s:queryDocumentSnapshots)
                    {
                        solutions.add(s.toObject(Solution.class));
                    }
                    adapter=new SolutionAdapter(StudentActivity.this,solutions);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                Helper.dismissDialog();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.dismissDialog();
            }
        });
    }
}