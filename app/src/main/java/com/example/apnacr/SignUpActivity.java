package com.example.apnacr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apnacr.Models.Student;
import com.example.apnacr.Models.Teacher;
import com.example.apnacr.Models.User;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    EditText etUserName,etEmail,etPassword;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        etEmail=findViewById(R.id.et_email);
        etUserName=findViewById(R.id.et_user_name);
        etPassword=findViewById(R.id.et_password);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        findViewById(R.id.tv_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);

            }
        });

        findViewById(R.id.btn_signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password=etPassword.getText().toString().trim();
                String username=etUserName.getText().toString().trim();
                String email=etEmail.getText().toString().trim();
                if(TextUtils.isEmpty(username))
                {
                    Toast.makeText(SignUpActivity.this,"User name Can'nt be Empty",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(SignUpActivity.this,"Email Can'nt be Empty",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(SignUpActivity.this,"Password Can'nt be Empty",Toast.LENGTH_LONG).show();
                    return;
                }
                isAlreadyTaken(username,email,password);
               // signUp(username,email,password);
            }
        });
    }

    private void signUp(String username, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "createUserWithEmail:success");
                            updateData(username,email,password);
                            Intent intent=new Intent(SignUpActivity.this,SelectRoleActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                        Helper.dismissDialog();
                    }
                });
    }

    private void updateData(String username, String email, String password) {
        User user=new User(username,email,password);
        db.collection("users").document(mAuth.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        Student student=new Student(mAuth.getUid(),username,new ArrayList<>());
        db.collection("students").document(mAuth.getUid()).set(student).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        Teacher teacher=new Teacher(mAuth.getUid(),username,new ArrayList<>());
        db.collection("teachers").document(mAuth.getUid()).set(teacher).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    private void isAlreadyTaken(String username,String email,String password) {
        Helper.showProgressBar(SignUpActivity.this,"Logging...");
        db.collection("users").whereEqualTo("name",username).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots!=null&&queryDocumentSnapshots.size()>0)
                {

                        Toast.makeText(SignUpActivity.this,"User name is already taken...please use different name",Toast.LENGTH_LONG).show();
                         Helper.dismissDialog();

                }
                else{
                    signUp(username,email,password);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
                signUp(username,email,password);

            }
        });
    }
}