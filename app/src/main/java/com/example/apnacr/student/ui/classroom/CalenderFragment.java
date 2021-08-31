package com.example.apnacr.student.ui.classroom;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apnacr.Models.ClassRoom;
import com.example.apnacr.R;
import com.example.apnacr.teachers.ClassRoomActivity;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.BroadcastReceiver;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.webrtc.ContextUtils.getApplicationContext;


public class CalenderFragment extends Fragment {


    String crid;
    List<String >times=new ArrayList<>();
    TimeTableAdapter adapter;
    public CalenderFragment(String crid) {
        this.crid=crid;
        // Required empty public constructor
    }
    public CalenderFragment()
    {

    }


  RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_calender, container, false);



        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        view.findViewById(R.id.btn_joinMeet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.showProgressBar(getContext(),"Joining...");
                FirebaseFirestore.getInstance().collection("classrooms").document(crid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot!=null)
                        {
                            ClassRoom classRoom=documentSnapshot.toObject(ClassRoom.class);
                            if(classRoom!=null)
                            {

                                if(classRoom.getUrl()!=null)
                                {
                                    joinMeet(classRoom.getUrl());
                                }
                            }
                            else{
                                Helper.dismissDialog();
                                Toast.makeText(getContext(),"Something Went Wrong",Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Helper.dismissDialog();
                            Toast.makeText(getContext(),"Something Went Wrong",Toast.LENGTH_LONG).show();

                        }
                        Helper.dismissDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    Helper.dismissDialog();
                        Toast.makeText(getContext(),"Something Went Wrong",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Helper.showProgressBar(getContext(),"Fetching...");
        FirebaseFirestore.getInstance().collection("classrooms").document(crid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null)
                {
                    ClassRoom classRoom=documentSnapshot.toObject(ClassRoom.class);
                    if(classRoom!=null)
                    {
                        times=classRoom.getTimes();
                    }
                    if(times==null)
                        times=new ArrayList<>();
                    adapter=new TimeTableAdapter(times,getContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                Helper.dismissDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.dismissDialog();
            }
        });
    }

    private void joinMeet(String text) {
        if (text.length() > 0) {
            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.
            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(text)
                    // Settings for audio and video
                    //.setAudioMuted(true)
                    //.setVideoMuted(true)
                    .build();
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            JitsiMeetActivity.launch(getContext(), options);
        }
    }















    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }



}