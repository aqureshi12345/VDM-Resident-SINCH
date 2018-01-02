package com.vdm.virtualdoorman;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.SeekBar.OnSeekBarChangeListener;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallDetails;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static com.vdm.virtualdoorman.R.id.seekbar_unlock;

public class CallScreenActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener,
        OnClickListener  {

    static final String TAG = CallScreenActivity.class.getSimpleName();
    public static final String LOGINPREF = "LoginPrefs";
    //SharedPreferences loginpref;
    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;
    SharedPreferences loginpref;
    String from;
    private String mCallId;
     String callerName;
    String vstation_ID;
    String  EndCause;
    private long mCallStart = 0;
    String mCallDuration;
    //private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;
    SeekBar sb;
    TextView seekbartext;
    ImageButton speaker,mute;
    String speaker_phone="yes";
    String mute_phone ="yes";
    int time_to_delay=0;
    int screenSize;
    AudioManager audioManager;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;
    AudioController audioController;
    Handler handler;
    Handler handler_ignore;
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress > 95) {
            seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
       // seekbartext.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        Log.d("onStopTrackingTouch", "onStopTrackingTouch");

        if (seekBar.getProgress() < 98) {
            seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));
            if(screenSize==Configuration.SCREENLAYOUT_SIZE_LARGE) {
                seekBar.setProgress(0);
            } else{
                seekBar.setProgress(2);
            }

           // Toast.makeText(CallScreenActivity.this, "slide to unlock <80", Toast.LENGTH_LONG).show();
        } else {
            if(screenSize== Configuration.SCREENLAYOUT_SIZE_LARGE) {
                sb.setProgress(100);
            } else{

                sb.setProgress(98);
            }
        seekbartext.setText("Unlocking");
            new DoorUnlock().execute();

           // Toast.makeText(CallScreenActivity.this, "slide to unlock 100", Toast.LENGTH_LONG).show();
        }

    }


    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // updateCallDuration();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        KeyguardManager manager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = manager.newKeyguardLock("abc");
        lock.disableKeyguard();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.callscreen);



        mCallStart = System.currentTimeMillis();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        //callerName=getIntent().getStringExtra("app_name");
        callerName=getIntent().getStringExtra("UserName");
        //vstation_ID = getIntent().getStringExtra(SinchService.vstationID);
        mAudioPlayer = new AudioPlayer(this);
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) CallScreenActivity.this.getSystemService(ns);
        nMgr.cancel(001);
  //      mAudioPlayer.stopRingtone();


        // mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallerName = (TextView) findViewById(R.id.remoteUser);
      //  mCallState = (TextView) findViewById(R.id.callState);
        Button endCallButton = (Button) findViewById(R.id.hangupButton);
//        Button unlockDoor=(Button) findViewById(R.id.unlockButton);
        sb = (SeekBar) findViewById(seekbar_unlock);
        screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

//        if(screenSize==Configuration.SCREENLAYOUT_SIZE_LARGE) {
//            sb.setProgress(0);
//        } else{
//            sb.setProgress(2);
//        }
        seekbartext = (TextView) findViewById(R.id.slider_text_unlock);
        sb.setOnSeekBarChangeListener(this);

//        sb.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//if(sb.getProgress()==0) {
//
//    sb.setProgress(0);
//
//}
//                return true;
//
//           }
//        });

        audioManager = (AudioManager) CallScreenActivity.this.getSystemService(Context.AUDIO_SERVICE);
         speaker=(ImageButton) findViewById(R.id.speaker);
        speaker.setOnClickListener(new OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (speaker_phone.equalsIgnoreCase("yes")) {
                    speaker_phone = "no";
               audioController = getSinchServiceInterface().getAudioController();
                    audioController.disableSpeaker();
                    speaker.setBackground(getResources().getDrawable(R.drawable.speaker_selected));
                }
                else {
                    speaker_phone = "yes";
                  audioController = getSinchServiceInterface().getAudioController();
                    audioController.enableSpeaker();
                    speaker.setBackground(getResources().getDrawable(R.drawable.speaker));
                }

            }
        });
        mute = (ImageButton) findViewById(R.id.mute);
        mute.setOnClickListener(new OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if(mute_phone.equalsIgnoreCase("yes")) {
                    mute_phone="no";
               audioController = getSinchServiceInterface().getAudioController();
                    audioController.mute();
                    mute.setBackground(getResources().getDrawable(R.drawable.mute_selected));
                }
                else
                {
                    mute_phone="yes";
             audioController = getSinchServiceInterface().getAudioController();
                    audioController.unmute();
                    mute.setBackground(getResources().getDrawable(R.drawable.mute));
                }

            }
        });
//        unlockDoor.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShowConfirmation();
//            }
//        });
        final int intervalTime = 120000; // 10 sec
       handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowConformationDilog();
            }
        }, intervalTime);
        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

    }
    public void notification(){
        Intent resultIntent;
        resultIntent=this.getIntent();
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        resultIntent.setAction(Intent.ACTION_VIEW);
        //  resultIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT

        );
        mBuilder =  new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon175x175)
                .setContentTitle("Ongoing-Call Virtual Doorman")
                .setContentText("Resume Call")
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setVibrate (new long[0])
                .setContentIntent((resultPendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0)));
        mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(002, mBuilder.build());

        // .setContentIntent(getDialogPendingIntent())
    }
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        // mAudioPlayer.stopRingtone();
        notification();
    }
    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);

        if (call != null) {
//            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
//            callerName=(loginpref.getString(call.getRemoteUserId(), ""));
            if(call.getRemoteUserId().contains("operator")){
                sb.setVisibility(View.INVISIBLE);
                seekbartext.setVisibility(View.INVISIBLE);

            }
            mCallerName.setText(callerName);
            call.addCallListener(new SinchCallListener());
            if (call.getState() == CallState.ESTABLISHED) {
                addVideoViews();
            }
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDurationTask.cancel();
        mTimer.cancel();
        removeVideoViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
        if (getSinchServiceInterface() != null) {
            onServiceConnected();
        }

    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void endCall() {

        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
//        CallEndCause cause = call.getDetails().getEndCause();
//        EndCause= cause.toString();
        if (call != null) {
            call.hangup();
        }

        finish();
    }
    @Override
public void onDestroy(){
    super.onDestroy();
        handler.removeCallbacksAndMessages(null);
//        if(  mute_phone.equalsIgnoreCase("yes")){
//            audioController.unmute();
//
//        }
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
}
    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        if (mCallStart > 0) {
             mCallDuration = (formatTimespan(System.currentTimeMillis() - mCallStart));
        }
    }

    private void addVideoViews() {
        final VideoController vc = getSinchServiceInterface().getVideoController();

        if (vc != null) {
//            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
//            localView.addView(vc.getLocalView());
//            localView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    vc.toggleCaptureDevicePosition();
//                }
//            });

            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.addView(vc.getRemoteView());
        }
    }

    private void removeVideoViews() {
        VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.removeView(vc.getRemoteView());

//            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
//           localView.removeView(vc.getLocalView());
        }
    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {

            updateCallDuration();
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            from=call.getRemoteUserId();
                EndCause= cause.toString();
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            cancelNotification(CallScreenActivity.this, 002);
            String endMsg = "Call ended: " + call.getDetails().toString();
        //    new StoreCallDetail().execute();


          //  Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();

            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
           if( call.getRemoteUserId().contains("operator")) {
               Log.d(TAG, "Call from operator");
           } else{
               vstation_ID = call.getHeaders().get("vstationId");
           }
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();
            mCallStart = System.currentTimeMillis();
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
            //callerName= call.getHeaders().get("vstationName");
           // mCallerName.setText(callerName);
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.d(TAG, "Video track added");
            addVideoViews();
        }
        @Override
        public void onVideoTrackPaused(Call call) {

        }

        @Override
        public void onVideoTrackResumed(Call call) {

        }
    }

    class DoorUnlock extends AsyncTask<String, String, String> {
        private static final String CALLRECORDPREFERENCES = "Calrecordpref";
        JSONObject json = new JSONObject();
        private ProgressDialog pDialog;
        JSONObject jsonobj = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        SharedPreferences CalRecordPref;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(CallScreenActivity.this);
//            pDialog.setMessage("Adding Call Record..");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//           // pDialog.show();
        }
        protected String doInBackground(String... args) {

            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
            String building_id = (loginpref.getString("building_id", ""));
            String door_unlock = "https://portal.virtualdoorman.com/dev/common/libs/slim/unlock_door/"+building_id+"/"+vstation_ID;

            // getting JSON Object
            // Note that create guest url accepts POST method
            Log.e("json is+++++", jsonobj.toString());
            json = jsonParser.makeHttpRequest(door_unlock, "GET", jsonobj);

            // check log cat for response
            // Log.e("Create Response", json.toString());


            return null;
        }

        public void onPause() {
            // super.onPause();
//            if (pDialog != null) {
//                // pDialog.dismiss();
//            }
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String res) {
            // dismiss the dialog once done
//            pDialog.dismiss();
            try {

                String doorUnlock_status = json.getString("status");
                String response = json.getString("message");
                if (doorUnlock_status.equalsIgnoreCase("OK")) {

                    time_to_delay=json.getInt("pulse_time");
                    int delay_time=0;
                    delay_time = time_to_delay * 1000;
                    seekbartext.setText("Unlocked");

                    Timer seekbarTimer = new Timer();
                    seekbarTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if(screenSize==Configuration.SCREENLAYOUT_SIZE_LARGE) {
                                        sb.setProgress(0);
                                    } else{
                                        sb.setProgress(2);
                                    }
                                    seekbartext.setText("SLIDE TO UNLOCK");
                                }
                            });
                        }
                    }, delay_time);
                    // DoorstationList list=new DoorstationList();

                }
                else{
                    if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                        sb.setProgress(0);
                    } else {
                        sb.setProgress(2);
                    }
                    seekbartext.setText("SLIDE TO UNLOCK");
                    Toast.makeText(getApplicationContext(), response,
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }
    public void ShowConformationDilog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CallScreenActivity.this);
        builder.setMessage("Do you want to continue the call?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        increaseCalltime();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        endCall();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        final int intervalTime = 10000; // 10 sec
        handler_ignore = new Handler();
        handler_ignore.postDelayed(new Runnable() {
            @Override
            public void run() {

                endCall();
            }
        }, intervalTime);


    }

    public void increaseCalltime(){
        handler_ignore.removeCallbacksAndMessages(null);
        final int intervalTime = 60000; // 10 sec
         handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowConformationDilog();
            }
        }, intervalTime);

    }
}
