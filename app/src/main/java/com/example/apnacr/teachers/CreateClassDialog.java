package com.example.apnacr.teachers;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.apnacr.Models.ClassRoom;
import com.example.apnacr.utils.Helper;
import com.example.apnacr.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateClassDialog  extends Dialog {


    private static final String TAG = "CreateClassDialog";
    EditText etClassName,etSubject;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public CreateClassDialog(@NonNull Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_class_dialog_layout);
        getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.transparent_background));
        getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;


        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        etClassName=findViewById(R.id.et_class_name);
        etSubject=findViewById(R.id.et_class_subject);
        findViewById(R.id.Dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        findViewById(R.id.btn_create_class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject=etSubject.getText().toString().trim();
                String name=etClassName.getText().toString().trim();

                if(TextUtils.isEmpty(name))
                {
                    Toast.makeText(getContext(),"Name can'nt be empty",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(subject))
                {
                    Toast.makeText(getContext(),"Subject Name can'nt be empty",Toast.LENGTH_LONG).show();
                    return;
                }
                createClass(name,subject);

            }
        });



    }

    private void createClass(String name, String subject) {
        long cnumber= Helper.getRandomNumber();
        List<String> assignments=new ArrayList<>();
        String tid=mAuth.getUid();
        String date=Helper.parseDateToddMMyyyy( Calendar.getInstance().getTime());
        List<String>students=new ArrayList<>();
        long timeMillis=System.currentTimeMillis();

        String id=db.collection("classrooms").document().getId();
        ClassRoom room=new ClassRoom(assignments,id,tid,subject,name,cnumber,date,students,timeMillis);
        Log.d(TAG, "createClass: "+new Gson().toJson(room));

        Helper.showProgressBar(getContext(),"Creating...");

        db.collection("classrooms").document(id).set(room).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Helper.dismissDialog();
                dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Something Went Wrong",Toast.LENGTH_LONG).show();
                Helper.dismissDialog();
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });



    }
}
