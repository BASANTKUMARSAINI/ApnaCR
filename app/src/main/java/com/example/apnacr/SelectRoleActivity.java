package com.example.apnacr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.apnacr.student.StudentHomeActivity;
import com.example.apnacr.teachers.TeacherHomeActivity;


public class SelectRoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);

        findViewById(R.id.btn_as_student).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SelectRoleActivity.this, StudentHomeActivity.class);
                startActivity(intent);

            }
        });
        findViewById(R.id.btn_as_teacher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SelectRoleActivity.this, TeacherHomeActivity.class);
                startActivity(intent);

            }
        });
    }
}