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
import android.widget.TextView;
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
import java.util.List;

public class CreateAssignmentDialog extends Dialog {


    private static final String TAG = "CreateAssignmentDialog";
    EditText etDesc;
    TextView selectPdf,startDate,submissionDate;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String start_date,submission_date;
    String filePath;

    public CreateAssignmentDialog(@NonNull Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_assignment_dialog_layout);
        getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.transparent_background));
        getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;


        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        selectPdf=findViewById(R.id.et_select_pdf);
        startDate=findViewById(R.id.tv_start_date);
        submissionDate=findViewById(R.id.tv_submission_date);
        etDesc=findViewById(R.id.et_desc);

        findViewById(R.id.Dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        findViewById(R.id.btn_upload_assignment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc=etDesc.getText().toString().trim();


                if(TextUtils.isEmpty(desc))
                {
                    Toast.makeText(getContext(),"Pl Enter something in description",Toast.LENGTH_LONG).show();
                    return;
                }
                 if(start_date==null||submission_date==null)
                 {
                     Toast.makeText(getContext(),"Select Date",Toast.LENGTH_LONG).show();
                     return;
                 }
                uploadAssignment(desc);

            }
        });
        selectPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }

    private void uploadAssignment(String desc) {
    }




}
