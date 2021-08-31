package com.example.apnacr.student.ui.classroom;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apnacr.Models.Test;
import com.example.apnacr.R;
import com.example.apnacr.TestAdapter;
import com.example.apnacr.teachers.TestActivity;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class TestFragment extends Fragment {
    String crid;

    TestAdapter assignmentAdapter;
    List<Test> assignments=new ArrayList<>();
    RecyclerView recyclerView;

    public TestFragment() {
        // Required empty public constructor
    }
    public TestFragment(String crid) {
        // Required empty public constructor
        this.crid=crid;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_test, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Helper.showProgressBar(getContext(),"Fetching...");
        FirebaseFirestore.getInstance().collection("tests").whereEqualTo("crid",crid)
                .orderBy("timeMillis", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Helper.dismissDialog();

                if(queryDocumentSnapshots!=null&&queryDocumentSnapshots.size()>0)
                {
                    assignments=new ArrayList<>();
                    for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                    {
                        Test assignment=snapshot.toObject(Test.class);
                        assignments.add(assignment);
                    }
                    assignmentAdapter=new TestAdapter(getContext(),assignments);
                    recyclerView.setAdapter(assignmentAdapter);
                    assignmentAdapter.notifyDataSetChanged();
                }

            }
        });

    }
}