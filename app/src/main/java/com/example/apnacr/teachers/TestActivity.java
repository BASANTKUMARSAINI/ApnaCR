package com.example.apnacr.teachers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apnacr.Models.Assignment;
import com.example.apnacr.Models.Test;
import com.example.apnacr.R;
import com.example.apnacr.TestAdapter;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "AssignmentActivity";
    TextView tvSubjectName, tvClassName;
    ImageView imageView;
    RecyclerView recyclerView;
    Uri file;
    StorageReference storageReference;
    String desc;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String crid;
    String start_date;
    EditText etTime,etMarks;
    CreateTestDialog dialog;
    TestAdapter assignmentAdapter;
    List<Test>assignments=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        tvClassName = findViewById(R.id.tv_class_name);
        tvSubjectName = findViewById(R.id.tv_subject);
        imageView = findViewById(R.id.imageView);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etMarks=findViewById(R.id.et_marks);
        etTime=findViewById(R.id.et_time);

        storageReference= FirebaseStorage.getInstance().getReference();
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        crid=getIntent().getStringExtra("crid");

        findViewById(R.id.add_assignment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog=new CreateTestDialog(TestActivity.this);
                dialog.setCancelable(false);
                dialog.show();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Helper.showProgressBar(TestActivity.this,"Fetching...");
        db.collection("tests").whereEqualTo("crid",crid)
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
                    assignmentAdapter=new TestAdapter(TestActivity.this,assignments);
                    recyclerView.setAdapter(assignmentAdapter);
                    assignmentAdapter.notifyDataSetChanged();
                }

            }
        });

    }

    private void uploadAssignment() {

            updateData();


    }


    private void updateData() {

        String id=db.collection("tests").document().getId();
        Test test=new Test(id,crid,mAuth.getUid(),start_date,new ArrayList<>(),new ArrayList<>(),desc,time,System.currentTimeMillis(),
                Integer.parseInt(marks));

        db.collection("tests").document(id).set(test).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: ");
                Helper.dismissDialog();
                dialog.dismiss();
               onStart();
                Toast.makeText(TestActivity.this,"Uploaded",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.dismissDialog();
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }


    EditText etDesc;
    TextView   startDate;
    private Calendar myCalendar;
    private DatePickerDialog dpd;
    String time,marks;
    public class CreateTestDialog extends Dialog {


        private static final String TAG = "CreateAssignmentDialog";




        public CreateTestDialog(@NonNull Context context) {
            super(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.create_test_dialog_layout);
            getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.transparent_background));
            getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;


            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();


            startDate = findViewById(R.id.tv_start_date);
            etMarks=findViewById(R.id.et_marks);
            etTime=findViewById(R.id.et_time);
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
                     desc = etDesc.getText().toString().trim();
                     marks=etMarks.getText().toString().trim();
                     time=etTime.getText().toString().trim();


                    if (TextUtils.isEmpty(desc)) {
                        Toast.makeText(getContext(), "Pl Enter something in description", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (start_date == null) {
                        Toast.makeText(getContext(), "Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(TextUtils.isEmpty(marks))
                    {
                        Toast.makeText(getContext(), "Marks", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(TextUtils.isEmpty(time))
                    {
                        Toast.makeText(getContext(), "Time", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Helper.showProgressBar(getContext(),"Uploading...");
                    uploadAssignment();

                }
            });


            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    myCalendar = Calendar.getInstance();
                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            // TODO Auto-generated method stub
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            String myFormat = "dd-MM-yyyy"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                            start_date = sdf.format(myCalendar.getTime());
                            startDate.setText(start_date);
                            startDate.setBackground(getDrawable(R.drawable.green_hollow_background));

                        }

                    };

                    dpd = new DatePickerDialog(TestActivity.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));//.show();
                    dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.DATE, 700);
                    Date newDate = cal.getTime();
                    dpd.getDatePicker().setMaxDate(newDate.getTime());
                    dpd.show();



                }
            });



        }
    }








    }
