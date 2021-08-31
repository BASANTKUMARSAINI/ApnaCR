package com.example.apnacr.teachers;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apnacr.Models.ClassRoom;
import com.example.apnacr.R;
import com.example.apnacr.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

import static org.jitsi.meet.sdk.BroadcastEvent.Type.CONFERENCE_JOINED;

public class ClassRoomActivity extends AppCompatActivity {
    TextView tvSubjectName,tvClassName,tvClassCode;
    ImageView imageView;
    RecyclerView recyclerView;

    ClassRoom classRoom;
    int image;

    List<String>students=new ArrayList<>();
    StudentAdapter adapter;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_room);

        tvClassCode=findViewById(R.id.tv_class_code);
        tvClassName=findViewById(R.id.tv_class_name);
        tvSubjectName=findViewById(R.id.tv_subject);
        imageView=findViewById(R.id.imageView);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        classRoom=getIntent().getParcelableExtra("classroom");
        image=getIntent().getIntExtra("image",0);


        URL serverURL;
        try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                // When using JaaS, set the obtained JWT here
                //.setToken("MyJWT")
                // Different features flags can be set
                // .setFeatureFlag("toolbox.enabled", false)
                // .setFeatureFlag("filmstrip.enabled", false)
                .setWelcomePageEnabled(false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        Log.d("TAG", "onCreateView: "+JitsiMeet.getCurrentConference());
        registerForBroadcastMessages();

        setData();
        findViewById(R.id.tv_new_assignment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ClassRoomActivity.this,AssignmentActivity.class);
                intent.putExtra("crid",classRoom.getId());
                startActivity(intent);

            }
        });
        findViewById(R.id.tv_add_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ClassRoomActivity.this,TestActivity.class);
                intent.putExtra("crid",classRoom.getId());
                startActivity(intent);
            }
        });
        findViewById(R.id.tv_add_time_table).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeTableDialog dialog=new TimeTableDialog(ClassRoomActivity.this,classRoom.getId());
                dialog.show();
            }
        });
        findViewById(R.id.tv_new_meet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long meetid= Helper.getRandomNumber();
                AlertDialog.Builder builder=new AlertDialog.Builder(ClassRoomActivity.this);
                builder.setTitle("Meet");
                builder.setMessage("Do you want create meet?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createMeet(meetid+"");
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                     dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });
    }

    private void createMeet(String  meetid) {
        Helper.showProgressBar(ClassRoomActivity.this,"Creating...");
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("url",meetid);
        FirebaseFirestore.getInstance().collection("classrooms").document(classRoom.getId()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Helper.dismissDialog();
                onButtonClick(meetid+"");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.dismissDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter=new StudentAdapter(ClassRoomActivity.this,students,classRoom.getId());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setData() {
        tvClassName.setText(classRoom.getName());
        tvSubjectName.setText(classRoom.getSubject());
        tvClassCode.setText("Class Code:"+classRoom.getCnumber()+" (Use it for join the class)");
        if(image==0)
        {
            imageView.setImageDrawable(getDrawable(R.drawable.image1));
        }
        else if(image==1)
        {
            imageView.setImageDrawable(getDrawable(R.drawable.image2));
        }
        else {

                imageView.setImageDrawable(getDrawable(R.drawable.image3));

        }
        students=classRoom.getStudents();
    }





    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    public void onButtonClick(String text) {

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
            JitsiMeetActivity.launch(this, options);
        }
    }

    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
                ... other events
         */
        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    // Example for handling different JitsiMeetSDK events
    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);

            switch (event.getType()) {
                case CONFERENCE_JOINED:
                    Timber.i("Conference Joined with url%s", event.getData().get("url"));
                    break;
                case PARTICIPANT_JOINED:
                    Timber.i("Participant joined%s", event.getData().get("name"));
                    break;
            }
        }
    }

    // Example for sending actions to JitsiMeetSDK
    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };

}