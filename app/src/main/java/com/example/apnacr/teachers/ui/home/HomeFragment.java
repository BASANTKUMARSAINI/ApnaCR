package com.example.apnacr.teachers.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apnacr.Models.ClassRoom;
import com.example.apnacr.R;
import com.example.apnacr.teachers.ClassRoomAdapter;
import com.example.apnacr.teachers.CreateClassDialog;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private static final String TAG ="HomeFragment" ;
    FloatingActionButton fab;
    RecyclerView recyclerView;

    ClassRoomAdapter adapter;
    List<ClassRoom>classRooms;

    FirebaseAuth mAuth;
    FirebaseFirestore db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        fab=root.findViewById(R.id.create_class);
        recyclerView=root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateClassDialog dialog=new CreateClassDialog(getContext());
                dialog.setCancelable(false);
                dialog.show();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Helper.showProgressBar(getContext(),"Fetching...");
        db.collection("classrooms").whereEqualTo("tid",mAuth.getUid()).orderBy("timeMillis", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots!=null&&queryDocumentSnapshots.size()>0)
                        {
                            classRooms=new ArrayList<>();
                            for(DocumentSnapshot s:queryDocumentSnapshots)
                            {
                                classRooms.add(s.toObject(ClassRoom.class));
                            }
                            adapter=new ClassRoomAdapter(getContext(),classRooms);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "onSuccess: set");
                        }
                        Log.d(TAG, "onSuccess: out");
                        Helper.dismissDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.dismissDialog();
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }
}