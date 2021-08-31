package com.example.apnacr.teachers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.apnacr.Models.Assignment;
import com.example.apnacr.R;
import com.example.apnacr.student.UploadSolutionActivity;
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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentActivity extends AppCompatActivity {
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
    String start_date, submission_date;
    CreateAssignmentDialog dialog;
    AssignmentAdapter assignmentAdapter;
    List<Assignment>assignments=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);


        tvClassName = findViewById(R.id.tv_class_name);
        tvSubjectName = findViewById(R.id.tv_subject);
        imageView = findViewById(R.id.imageView);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        storageReference= FirebaseStorage.getInstance().getReference();
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        crid=getIntent().getStringExtra("crid");

        findViewById(R.id.add_assignment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog=new CreateAssignmentDialog(AssignmentActivity.this);
                dialog.setCancelable(false);
                dialog.show();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Helper.showProgressBar(AssignmentActivity.this,"Fetching...");
        db.collection("assignments").whereEqualTo("crid",crid).whereEqualTo("tid",mAuth.getUid())
                .orderBy("timeMillis", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Helper.dismissDialog();

                if(queryDocumentSnapshots!=null&&queryDocumentSnapshots.size()>0)
                {
                    assignments=new ArrayList<>();
                    for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                    {
                        Assignment assignment=snapshot.toObject(Assignment.class);
                        assignments.add(assignment);
                    }
                    assignmentAdapter=new AssignmentAdapter(AssignmentActivity.this,assignments);
                    recyclerView.setAdapter(assignmentAdapter);
                    assignmentAdapter.notifyDataSetChanged();
                }

            }
        });

    }

    private void uploadAssignment() {
        if(file!=null)
        {
            Bitmap bmp=generateImageFromPdf(file);
            if(bmp==null)
                bmp= BitmapFactory.decodeResource(getResources(),R.drawable.image1);
            uploadFiles(file,bmp);
        }
        else {
            updateData();
        }

    }
    String downloadUrlPdf,downloadUrlThumbnail;
    private void uploadFiles(Uri file, Bitmap bmp) {
        StorageReference riversRef = storageReference.child("pdf/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                            downloadUrlPdf=uri.toString();
                        Log.d(TAG, "onSuccess: "+downloadUrlPdf);
                    }
                });
                uploadThumbnail(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.dismissDialog();
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private void uploadThumbnail(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference child = storageReference.child("thumbnail/"+file.getLastPathSegment());

        UploadTask uploadTask = child.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                 child.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUrlThumbnail=uri.toString();
                        updateData();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.dismissDialog();
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private void updateData() {

        String id=db.collection("assignments").document().getId();
        List<String>files=new ArrayList<>();
        if(downloadUrlPdf!=null)
                files.add(downloadUrlPdf);

        Assignment assignment=new Assignment(crid,id,mAuth.getUid(),start_date,submission_date,files,System.currentTimeMillis(),downloadUrlThumbnail,desc,100);
        db.collection("assignments").document(id).set(assignment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: ");
                Helper.dismissDialog();
                dialog.dismiss();
               onStart();
                Toast.makeText(AssignmentActivity.this,"Uploaded",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.dismissDialog();
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK&&requestCode==1) {
            // Get the Uri of the selected file
            file = data.getData();
            selectPdf.setText(file.getLastPathSegment()+"");
            selectPdf.setBackground(getDrawable(R.drawable.green_hollow_background));
            Log.d(TAG, "onActivityResult: "+file);
        }
    }
    EditText etDesc;
    TextView selectPdf, startDate, submissionDate;
    private Calendar myCalendar;
    private DatePickerDialog dpd;
    public class CreateAssignmentDialog extends Dialog {


        private static final String TAG = "CreateAssignmentDialog";




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


            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            selectPdf = findViewById(R.id.et_select_pdf);
            startDate = findViewById(R.id.tv_start_date);
            submissionDate = findViewById(R.id.tv_submission_date);
            etDesc = findViewById(R.id.et_desc);

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


                    if (TextUtils.isEmpty(desc)) {
                        Toast.makeText(getContext(), "Pl Enter something in description", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (start_date == null || submission_date == null) {
                        Toast.makeText(getContext(), "Select Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Helper.showProgressBar(getContext(),"Uploading...");
                    uploadAssignment();

                }
            });
            selectPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!Helper.checkForPermision(AssignmentActivity.this))
                        return;
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent,1);
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

                    dpd = new DatePickerDialog(AssignmentActivity.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, date, myCalendar
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
            submissionDate.setOnClickListener(new View.OnClickListener() {
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
                            submission_date = sdf.format(myCalendar.getTime());

                            submissionDate.setText(submission_date);
                            submissionDate.setBackground(getDrawable(R.drawable.green_hollow_background));


                        }

                    };

                    dpd = new DatePickerDialog(AssignmentActivity.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, date, myCalendar
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





        public Bitmap generateImageFromPdf(Uri pdfUri) {
            int pageNumber = 0;
            PdfiumCore pdfiumCore = new PdfiumCore(AssignmentActivity.this);
            try {
                //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
                ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(pdfUri, "r");
                PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
                pdfiumCore.openPage(pdfDocument, pageNumber);
                int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
                int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);

                pdfiumCore.closeDocument(pdfDocument); // important!
                return bmp;
            } catch (Exception e) {
                //todo with exception
                Log.d(TAG, "generateImageFromPdf: " + e.getMessage());
                return null;
            }
        }

    }
