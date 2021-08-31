package com.example.apnacr.student.ui.home;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apnacr.Models.ClassRoom;
import com.example.apnacr.Models.Student;
import com.example.apnacr.R;
import com.example.apnacr.student.ClassRoomAdapter;
import com.example.apnacr.teachers.AssignmentActivity;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment {
    private static final String TAG ="HomeFragment" ;
    FloatingActionButton fab;
    RecyclerView recyclerView;



    FirebaseAuth mAuth;
    FirebaseFirestore db;

    List<String>classRooms=new ArrayList<>();
    ClassRoomAdapter adapter;

    JoinClassDialog dialog;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_student_home, container, false);

        fab=root.findViewById(R.id.join_class);
        recyclerView=root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        getClassRooms();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dialog=new JoinClassDialog(getContext());
                dialog.setCancelable(false);
                dialog.show();
            }
        });
        return root;
    }

    private void getClassRooms() {
        Helper.showProgressBar(getContext(),"Fetching...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Helper.dismissDialog();
            }
        },1000);
        db.collection("students").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null)
                {
                    Student student=documentSnapshot.toObject(Student.class);
                    if(student!=null) {
                        classRooms = student.getClassrooms();
                        if (classRooms == null)
                            classRooms = new ArrayList<>();
                    }
                    adapter=new ClassRoomAdapter(getContext(),classRooms);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "onSuccess: ");
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public class JoinClassDialog extends Dialog {


        private static final String TAG = "CreateAssignmentDialog";


        EditText etClassCode;

        public JoinClassDialog(@NonNull Context context) {
            super(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.join_class_dialog_layout);
            getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.transparent_background));
            getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;


           etClassCode=findViewById(R.id.et_class_code);

           findViewById(R.id.Dialog_close).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   dismiss();
               }
           });
           findViewById(R.id.btn_join_class).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   String cnumber=etClassCode.getText().toString().trim();
                   if(TextUtils.isEmpty(cnumber))
                   {
                       Toast.makeText(getContext(),"Enter valid class code",Toast.LENGTH_LONG).show();
                       return;
                   }
                   if(cnumber.length()!=6)
                   {
                       Toast.makeText(getContext(),"Enter valid class code",Toast.LENGTH_LONG).show();
                       return;
                   }
                   joinClass(cnumber);
               }
           });




        }


    }
    private void joinClass(String cnumber) {
        long code=Integer.parseInt(cnumber);
        Helper.showProgressBar(getContext(),"Joining...");
        db.collection("classrooms").whereEqualTo("cnumber",code).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots!=null&&queryDocumentSnapshots.size()>0)
                {
                    ClassRoom classRoom=null;
                    for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                    {
                        classRoom=snapshot.toObject(ClassRoom.class);
                        break;
                    }
                    if(classRoom!=null)
                    {

                        updateClassRoom(classRoom);

                    }
                    else{
                        Toast.makeText(getContext(),"Invalid Class",Toast.LENGTH_LONG).show();
                        Helper.dismissDialog();
                    }
                }
                else{
                    Toast.makeText(getContext(),"Invalid Class",Toast.LENGTH_LONG).show();
                    Helper.dismissDialog();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Something Went Wrong",Toast.LENGTH_LONG).show();
                Helper.dismissDialog();
            }
        });


    }

    private void updateClassRoom(ClassRoom classRoom) {
        HashMap<String,Object>hashMap=new HashMap<>();
        List<String>students=classRoom.getStudents();
        if(students==null)
            students=new ArrayList<>();
        if(isAlreadyJoined(students,mAuth.getUid()))
        {
            Helper.dismissDialog();
            Toast.makeText(getContext(),"Already Joined",Toast.LENGTH_LONG).show();
            return;
        }
        students.add(mAuth.getUid());
        hashMap.put("students",students);
        db.collection("classrooms").document(classRoom.getId()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: success");
                updateStudent(classRoom);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Something Went Wrong",Toast.LENGTH_LONG).show();
                Helper.dismissDialog();
            }
        });
    }

    private boolean isAlreadyJoined(List<String> students, String uid) {
        for(int i=0;i<students.size();i++)
                if(students.get(i).equals(uid))
                        return true;
                return false;
    }

    private void updateStudent(ClassRoom classRoom) {
        HashMap<String,Object>hashMap=new HashMap<>();
        classRooms.add(classRoom.getId());
        hashMap.put("classrooms",classRooms);

        db.collection("students").document(mAuth.getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: success");
                Helper.dismissDialog();
                dialog.dismiss();
                getClassRooms();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Something Went Wrong",Toast.LENGTH_LONG).show();
                Helper.dismissDialog();
            }
        });

    }
}