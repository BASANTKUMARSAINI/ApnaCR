package com.example.apnacr.student.ui.classroom;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.BarringInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apnacr.Models.Assignment;
import com.example.apnacr.R;
import com.example.apnacr.student.AssignmentAdapterStudent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class AssignmentFragment extends Fragment {

    private static final String TAG = "AssignmentFragment";
    String crid;
    RecyclerView recyclerView;
    AssignmentAdapterStudent adapterStudent;
    List<Assignment>assignments=new ArrayList<>();
    FirebaseFirestore db;
    public AssignmentFragment(String crid) {
        // Required empty public constructor
        this.crid=crid;
    }
    public AssignmentFragment() {
        // Required empty public constructor

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_assignment, container, false);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));







        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onCreateView: "+crid);
        db=FirebaseFirestore.getInstance();
        db.collection("assignments").whereEqualTo("crid",crid).orderBy("timeMillis", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots!=null&&queryDocumentSnapshots.size()>0)
                {
                    assignments=new ArrayList<>();
                    for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                    {
                        assignments.add(snapshot.toObject(Assignment.class));
                    }
                    Log.d(TAG, "onSuccess: "+assignments.size());
                    adapterStudent=new AssignmentAdapterStudent(getContext(),assignments,crid);
                    recyclerView.setAdapter(adapterStudent);
                    adapterStudent.notifyDataSetChanged();
                }
                Log.d(TAG, "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }
}