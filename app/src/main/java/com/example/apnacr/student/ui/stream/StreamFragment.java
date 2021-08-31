package com.example.apnacr.student.ui.stream;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.apnacr.R;
import com.example.apnacr.SplashActivity;
import com.google.firebase.auth.FirebaseAuth;


public class StreamFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_student_stream, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);


        AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle("Settings")
                .setMessage("Coming soon...")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                         dialogInterface.dismiss();

                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

        return root;
    }
}