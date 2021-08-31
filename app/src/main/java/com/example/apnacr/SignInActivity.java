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

import com.example.apnacr.Models.User;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInActivity extends AppCompatActivity {
    EditText etUser,etPassword;
    String TAG="SignInActivity";
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        etUser=findViewById(R.id.et_user);
        etPassword=findViewById(R.id.et_password);

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        findViewById(R.id.tv_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password=etPassword.getText().toString().trim();
                String user=etUser.getText().toString().trim();

                if(TextUtils.isEmpty(user))
                {
                    Toast.makeText(SignInActivity.this,"User name Can'nt be Empty",Toast.LENGTH_LONG).show();
                    return;
                }


                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(SignInActivity.this,"Password Can'nt be Empty",Toast.LENGTH_LONG).show();
                    return;
                }

                signIn(user,password);
            }
        });

    }

    private void signIn(String user, String password) {
        Helper.showProgressBar(SignInActivity.this,"Logging...");
        if(isEmail(user))
        {
            signInWithEmail(user,password);
        }
        else{
            signInWithUserName(user,password);
        }
    }

    private boolean isEmail(String user) {
        if(user.length()<=10)
            return false;
        String last=user.substring(user.length()-10);
        Log.d(TAG, "isEmail: "+last);//@yahoo.com
        if(last.equals("@gmail.com")||last.equals("@yahoo.com"))
           return true;

        return  false;
    }

    private void signInWithUserName(String userName, String password) {
            db.collection("users").whereEqualTo("name",userName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots!=null&&queryDocumentSnapshots.size()>0)
                    {
                        User user=new User("xyz","xyz@gmail.com","123456");
                        for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                        {
                            user=snapshot.toObject(User.class);
                            break;
                        }
                        signInWithEmail(user.getEmail(),user.getPassword());
                    }
                    else{
                        Helper.dismissDialog();
                        Toast.makeText(SignInActivity.this, "User does not exists",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "signInWithEmail:failure"+e.getMessage());
                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    Helper.dismissDialog();
                }
            });
    }

    private void signInWithEmail(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent=new Intent(SignInActivity.this,SelectRoleActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        Helper.dismissDialog();
                    }
                });

    }
}