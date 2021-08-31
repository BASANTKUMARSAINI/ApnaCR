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
import com.example.apnacr.R;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TimeTableDialog extends Dialog {


    private static final String TAG = "CreateClassDialog";
    EditText sunday,monday,tuesday,wednesday,thursday,friday,saturday;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    List<String>list=new ArrayList<>();
    String crid;

    public TimeTableDialog(@NonNull Context context,String crid) {

        super(context);
        this.crid=crid;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_table_dialog_layout);
        getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.transparent_background));
        getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;


        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        sunday=findViewById(R.id.sunday);
        monday=findViewById(R.id.monday);
        tuesday=findViewById(R.id.tuesday);
        wednesday=findViewById(R.id.wednesday);
        thursday=findViewById(R.id.thursday);
        friday=findViewById(R.id.friday);
        saturday=findViewById(R.id.saturday);


        findViewById(R.id.Dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        findViewById(R.id.btn_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String one=sunday.getText().toString().trim();
                String two=monday.getText().toString().trim();
                String three=tuesday.getText().toString().trim();
                String four=wednesday.getText().toString().trim();
                String five=thursday.getText().toString().trim();
                String six=friday.getText().toString().trim();
                String seven=saturday.getText().toString().trim();
                list.add(one);
                list.add(two);
                list.add(three);
                list.add(four);
                list.add(five);
                list.add(six);
                list.add(seven);

                HashMap<String , Object>hashMap=new HashMap<>();
                hashMap.put("times",list);
                Helper.showProgressBar(getContext(),"Updating...");
                db.collection("classrooms").document(crid).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                   dismiss();
                   Helper.dismissDialog();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Helper.dismissDialog();
                    }
                });

            }
        });



    }






}
