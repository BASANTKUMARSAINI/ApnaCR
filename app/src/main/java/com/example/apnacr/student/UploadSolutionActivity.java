package com.example.apnacr.student;
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
import com.example.apnacr.Models.Solution;
import com.example.apnacr.R;
import com.example.apnacr.teachers.AssignmentAdapter;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

public class UploadSolutionActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String desc;
    private static final String TAG ="UploadSolutionActivity" ;
    Uri file;
    StorageReference storageReference;
    String assid;
String crid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_solution_dialog_layout);







        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();

        selectPdf = findViewById(R.id.et_select_pdf);
        startDate = findViewById(R.id.tv_start_date);
        submissionDate = findViewById(R.id.tv_submission_date);
        etDesc = findViewById(R.id.et_desc);
        assid=getIntent().getStringExtra("assid");
        crid=getIntent().getStringExtra("crid");

        findViewById(R.id.Dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.btn_upload_assignment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desc = etDesc.getText().toString().trim();


                if (TextUtils.isEmpty(desc)) {
                    Toast.makeText(UploadSolutionActivity.this, "Pl Enter something in description", Toast.LENGTH_LONG).show();
                    return;
                }

                Helper.showProgressBar(UploadSolutionActivity.this,"Submiting...");
                uploadAssignment();

            }
        });
        selectPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Helper.checkForPermision(UploadSolutionActivity.this))
                    return;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent,1);
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
        Log.d(TAG, "uploadFiles: ");
        StorageReference riversRef = storageReference.child("pdf/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUrlPdf=uri.toString();
                        uploadThumbnail(bmp);
                        Log.d(TAG, "onSuccess: "+downloadUrlPdf);
                    }
                });
                 
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.dismissDialog();
                Toast.makeText(UploadSolutionActivity.this,"Try without file",Toast.LENGTH_LONG).show();
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
                        Log.d(TAG, "onSuccess: ");
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

        //String id=db.collection("assignments").document(assid).collection("solutions").docu;
        List<String>files=new ArrayList<>();
        if(downloadUrlPdf!=null)
            files.add(downloadUrlPdf);

        Solution solution=new Solution(assid,mAuth.getUid(),mAuth.getUid(),downloadUrlPdf,downloadUrlThumbnail,desc,crid);

        db.collection("assignments").document(assid).collection("solutions").document(mAuth.getUid()).set(solution).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: ");
                Helper.dismissDialog();
                finish();



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.dismissDialog();
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });


        db.collection("solutions").add(solution).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

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






    public Bitmap generateImageFromPdf(Uri pdfUri) {
        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(UploadSolutionActivity.this);
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
