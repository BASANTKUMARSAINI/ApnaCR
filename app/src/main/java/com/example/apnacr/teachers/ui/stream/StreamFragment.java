package com.example.apnacr.teachers.ui.stream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.apnacr.R;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

import static org.webrtc.ContextUtils.getApplicationContext;


public class StreamFragment extends Fragment {
EditText et_code;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_teacher_stream, container, false);

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
//        et_code=root.findViewById(R.id.et_code);
//
//
//        URL serverURL;
//        try {
//            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
//            serverURL = new URL("https://meet.jit.si");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Invalid server URL!");
//        }
//        JitsiMeetConferenceOptions defaultOptions
//                = new JitsiMeetConferenceOptions.Builder()
//                .setServerURL(serverURL)
//                // When using JaaS, set the obtained JWT here
//                //.setToken("MyJWT")
//                // Different features flags can be set
//                // .setFeatureFlag("toolbox.enabled", false)
//                // .setFeatureFlag("filmstrip.enabled", false)
//                .setWelcomePageEnabled(false)
//                .build();
//        JitsiMeet.setDefaultConferenceOptions(defaultOptions);
//
//        Log.d("TAG", "onCreateView: "+JitsiMeet.getCurrentConference());
//        registerForBroadcastMessages();
//        root.findViewById(R.id.btn_create_meet).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String meetCode=et_code.getText().toString().trim();
//                if(TextUtils.isEmpty(meetCode))
//                {
//                    Toast.makeText(getContext(),"Enter Meet id",Toast.LENGTH_LONG).show();
//                    return;
//                }
//                onButtonClick(meetCode);
//
//
//            }
//        });
        return root;
    }

//    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            onBroadcastReceived(intent);
//        }
//    };
//
//
//
//
//
//    public void onButtonClick(String text) {
//
//
//        if (text.length() > 0) {
//            // Build options object for joining the conference. The SDK will merge the default
//            // one we set earlier and this one when joining.
//            JitsiMeetConferenceOptions options
//                    = new JitsiMeetConferenceOptions.Builder()
//                    .setRoom(text)
//                    // Settings for audio and video
//                    //.setAudioMuted(true)
//                    //.setVideoMuted(true)
//                    .build();
//            // Launch the new activity with the given options. The launch() method takes care
//            // of creating the required Intent and passing the options.
//            JitsiMeetActivity.launch(getContext(), options);
//            Log.d("TAG", "onCreateView: "+JitsiMeet.getCurrentConference());
//
//        }
//    }
//
//    private void registerForBroadcastMessages() {
//        IntentFilter intentFilter = new IntentFilter();
//
//        /* This registers for every possible event sent from JitsiMeetSDK
//           If only some of the events are needed, the for loop can be replaced
//           with individual statements:
//           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
//                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
//                ... other events
//         */
//        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
//            intentFilter.addAction(type.getAction());
//        }
//
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
//    }
//
//    // Example for handling different JitsiMeetSDK events
//    private void onBroadcastReceived(Intent intent) {
//        if (intent != null) {
//            BroadcastEvent event = new BroadcastEvent(intent);
//
//            switch (event.getType()) {
//                case CONFERENCE_JOINED:
//                    Timber.i("Conference Joined with url%s", event.getData().get("url"));
//                    break;
//                case PARTICIPANT_JOINED:
//                    Timber.i("Participant joined%s", event.getData().get("name"));
//                    break;
//            }
//        }
//    }
//
//    // Example for sending actions to JitsiMeetSDK
//    private void hangUp() {
//        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
//    }
}