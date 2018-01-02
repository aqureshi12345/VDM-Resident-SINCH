package com.vdm.virtualdoorman;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
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

import static com.vdm.virtualdoorman.R.id.seekbar_unlock;

public class OutgoingCallScreen extends BaseActivity implements SeekBar.OnSeekBarChangeListener,
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
    String doorStationID;
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
    int screenSize;
    Handler handler,Busy_handler;
    @Override
    public void onClick(View v) {
        sb.setVisibility(View.VISIBLE);
        sb.setProgress(0);
        //  Confirm.setVisibility(View.INVISIBLE);
        // sb.setBackgroundResource(R.drawable.slider_back);
        Toast.makeText(OutgoingCallScreen.this, "SLIDE TO UNLOCK", Toast.LENGTH_LONG).show();
        seekbartext.setVisibility(View.VISIBLE);
        seekbartext.setText("SLIDE TO UNLOCK");
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

        if (seekBar.getProgress() < 99) {
            seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));
            if(screenSize==Configuration.SCREENLAYOUT_SIZE_LARGE) {
                seekBar.setProgress(0);
            } else{
                seekBar.setProgress(2);
            }

            // Toast.makeText(CallScreenActivity.this, "slide to unlock <80", Toast.LENGTH_LONG).show();
        } else {
            if(screenSize== Configuration.SCREENLAYOUT_SIZE_LARGE) {
                seekBar.setProgress(100);
            } else{

                seekBar.setProgress(98);
            }
            seekbartext.setText("Unlocking");
            new DoorUnlock().execute();

            // Toast.makeText(CallScreenActivity.this, "slide to unlock 100", Toast.LENGTH_LONG).show();
        }

    }


    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            OutgoingCallScreen.this.runOnUiThread(new Runnable() {
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
        setContentView(R.layout.callscreen);

        mAudioPlayer = new AudioPlayer(this);
        // mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallerName = (TextView) findViewById(R.id.remoteUser);
        //  mCallState = (TextView) findViewById(R.id.callState);
        Button endCallButton = (Button) findViewById(R.id.hangupButton);
//        Button unlockDoor=(Button) findViewById(R.id.unlockButton);
        sb = (SeekBar) findViewById(seekbar_unlock);
        seekbartext = (TextView) findViewById(R.id.slider_text_unlock);
        sb.setVisibility(View.INVISIBLE);
        seekbartext.setVisibility(View.INVISIBLE);
        sb.setOnSeekBarChangeListener(this);
        screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
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

        Busy_handler=new Handler(Looper.getMainLooper());
        speaker=(ImageButton) findViewById(R.id.speaker);
        speaker.setOnClickListener(new OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (speaker_phone.equalsIgnoreCase("yes")) {
                    speaker_phone = "no";
                    AudioController audioController = getSinchServiceInterface().getAudioController();
                    audioController.disableSpeaker();
                    speaker.setBackground(getResources().getDrawable(R.drawable.speaker_selected));
                }
                else {
                    speaker_phone = "yes";
                    AudioController audioController = getSinchServiceInterface().getAudioController();
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
                    AudioController audioController = getSinchServiceInterface().getAudioController();
                    audioController.mute();
                    mute.setBackground(getResources().getDrawable(R.drawable.mute_selected));
                }
                else
                {
                    mute_phone="yes";
                    AudioController audioController = getSinchServiceInterface().getAudioController();
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
        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });
        mCallStart = System.currentTimeMillis();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        //callerName=getIntent().getStringExtra("app_name");
        callerName=getIntent().getStringExtra("VSTATION_NAME");
        doorStationID=getIntent().getStringExtra("DoorStation_id");

        final int intervalTime = 120000; // 10 sec
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowConformationDilog();
            }
        }, intervalTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
//    private void ShowConfirmation() {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                            CallScreenActivity.this);
//                    alertDialogBuilder.setMessage("Please confirm to unlock the door");
//                    alertDialogBuilder.setTitle("Door Unlock Confirmation");
//                    alertDialogBuilder.setPositiveButton(
//                            "Unlock Now",
//                            new DialogInterface.OnClickListener() {
//
//                                public void onClick(DialogInterface arg0,
//                                                    int arg1) {
//                                    new DoorUnlock().execute();
//
//                                }
//                            });
//        alertDialogBuilder.setNegativeButton(
//                "Cancel",
//                new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface arg0,
//                                        int arg1) {
//
//
//                    }
//                });
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            mCallerName.setText(callerName);
//            mCallState.setText(call.getState().toString());
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
            String endCallCause=cause.toString();
//            if(endCallCause.equalsIgnoreCase("DENIED")){
//                Busy_handler.post(new Runnable() {
//                    public void run() {
//                AlertDialog.Builder builder = new AlertDialog.Builder(OutgoingCallScreen.this);
//       /* playmedia();*/
//                builder.setMessage("Welcome Station is busy at the moment. Please try later?")
//                        .setCancelable(false)
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                            }
//                        });
//
//                AlertDialog alert = builder.create();
//                alert.show();
//                    }
//                });
//            }
            from=call.getRemoteUserId();
            EndCause= cause.toString();
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            //    new StoreCallDetail().execute();


             // Toast.makeText(OutgoingCallScreen.this, endMsg, Toast.LENGTH_LONG).show();

            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            sb.setVisibility(View.VISIBLE);
            seekbartext.setVisibility(View.VISIBLE);
            //mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();
            mCallStart = System.currentTimeMillis();
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
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
    //    class StoreCallDetail extends AsyncTask<String, String, String> {
//        private static final String CALLRECORDPREFERENCES = "Calrecordpref";
//        JSONObject json = new JSONObject();
//        private ProgressDialog pDialog;
//        JSONObject jsonobj = new JSONObject();
//        JSONParser jsonParser = new JSONParser();
//        SharedPreferences CalRecordPref;
//        final String cal_record = "https://portal.virtualdoorman.com/dev/common/libs/slim/insert_call_record/";
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////            pDialog = new ProgressDialog(CallScreenActivity.this);
////            pDialog.setMessage("Adding Call Record..");
////            pDialog.setIndeterminate(false);
////            pDialog.setCancelable(true);
////           // pDialog.show();
//        }
//        protected String doInBackground(String... args) {
//
//            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
//            String resident_id = (loginpref.getString("resident_id", ""));
//            String building_id = (loginpref.getString("building_id", ""));
//            String apartment_id = (loginpref.getString("apartment_id", ""));
//            String resident_name = (loginpref.getString("resident_name", ""));
//            String to = (loginpref.getString("Caller_id", ""));
//            Log.d("BUILDING ID+++++++",building_id);
//
//            try {
//                jsonobj.put("CALL_ID", mCallId);
//                jsonobj.put("DURATION", mCallDuration);
//                jsonobj.put("CALL_END_REASON", EndCause);
//                jsonobj.put("FROM", from);
//                jsonobj.put("RESIDENT_ID", resident_id);
//                jsonobj.put("BUILDING_ID", building_id);
//                jsonobj.put("APARTMENT_ID", apartment_id);
//                jsonobj.put("RESIDENT_NAME", resident_name);
//                jsonobj.put("TO", to);
//
//
//            } catch (JSONException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
//			/*
//			 * try { jdata.put("data", jsonobj); } catch (JSONException e1) { //
//			 * TODO Auto-generated catch block e1.printStackTrace(); }
//			 */
//
//            // getting JSON Object
//            // Note that create guest url accepts POST method
//            Log.e("json is+++++", jsonobj.toString());
//            json = jsonParser.makeHttpRequest(cal_record, "GET", jsonobj);
//
//            // check log cat for response
//           // Log.e("Create Response", json.toString());
//
//
//            return null;
//        }
//
//        public void onPause() {
//            // super.onPause();
////            if (pDialog != null) {
////                // pDialog.dismiss();
////            }
//        }
//
//        /**
//         * After completing background task Dismiss the progress dialog
//         * **/
//        protected void onPostExecute(String res) {
//            // dismiss the dialog once done
////            pDialog.dismiss();
//            try {
//
//                String status = json.getString("status");
//                String response = json.getString("message");
//                if (status.equalsIgnoreCase("OK")) {
////                    Toast.makeText(getApplicationContext(), response,
////                            Toast.LENGTH_LONG).show();
////                    Intent mainscreen = new Intent(getApplicationContext(),
////                            MainScreen.class);
////                    mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                    mainscreen.putExtra("EXIT", true);
////                    startActivity(mainscreen);
//                }
////                else if(response.equalsIgnoreCase("User authentication failed.")) {
////
////                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
////                            CallScreenActivity.this);
////                    alertDialogBuilder.setMessage("You have been logged out. Please login again to use the application");
////                    alertDialogBuilder.setTitle("Error");
////                    alertDialogBuilder.setPositiveButton(
////                            "OK",
////                            new DialogInterface.OnClickListener() {
////
////                                public void onClick(DialogInterface arg0,
////                                                    int arg1) {
////                                    loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
////                                    loginpref.edit().remove("login_guid").commit();
////                                    Intent logout = new Intent(getApplicationContext(),
////                                            Login.class);
////                                    logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                                    logout.putExtra("EXIT", true);
////                                    startActivity(logout);
////                                    finish();
////
////                                }
////                            });
////                    AlertDialog alertDialog = alertDialogBuilder.create();
////                    alertDialog.show();
////                }
//
//                else {
////                    Toast.makeText(getApplicationContext(),
////                            "Some Error Occured", Toast.LENGTH_SHORT).show();
//                    Intent mainscreen = new Intent(getApplicationContext(),
//                            MainScreen.class);
//                    mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    mainscreen.putExtra("EXIT", true);
//                    startActivity(mainscreen);
//
//                }
//
//            } catch (Exception e) {
//
//                Log.e(e.getClass().getName(), e.getMessage(), e);
//            }
//
//        }
//    }
    class DoorUnlock extends AsyncTask<String, String, String> {
        private static final String CALLRECORDPREFERENCES = "Calrecordpref";
        JSONObject json = new JSONObject();
        private ProgressDialog pDialog;
        JSONObject jsonobj = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        SharedPreferences CalRecordPref;
        //final String door_unlock = "https://portal.virtualdoorman.com/dev/common/libs/slim/unlock_door/11/158";
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
            String door_unlock = "https://portal.virtualdoorman.com/dev/common/libs/slim/unlock_door/"+building_id+"/"+doorStationID;


//            try {
//                jsonobj.put("CALL_ID", mCallId);
//                jsonobj.put("DURATION", mCallDuration);
//                jsonobj.put("CALL_END_REASON", EndCause);
//                jsonobj.put("FROM", from);
//                jsonobj.put("RESIDENT_ID", resident_id);
//                jsonobj.put("BUILDING_ID", building_id);
//                jsonobj.put("APARTMENT_ID", apartment_id);
//                jsonobj.put("RESIDENT_NAME", resident_name);
//                jsonobj.put("TO", to);
//
//
//            } catch (JSONException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
			/*
			 * try { jdata.put("data", jsonobj); } catch (JSONException e1) { //
			 * TODO Auto-generated catch block e1.printStackTrace(); }
			 */

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

                   int time_to_delay = json.getInt("pulse_time");
                    int delay_time = 0;
                    delay_time = time_to_delay * 1000;
                    seekbartext.setText("Unlocked");

                    Timer seekbarTimer = new Timer();
                    seekbarTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                                        sb.setProgress(0);
                                    } else {
                                        sb.setProgress(2);
                                    }
                                    seekbartext.setText("SLIDE TO UNLOCK");
                                }
                            });
                        }
                    }, delay_time);
//                Toast.makeText(getApplicationContext(), "Door has been Unlocked",
//                           Toast.LENGTH_LONG).show();
//                String response = json.getString("message");
//                if (status.equalsIgnoreCase("OK")) {
////                    Toast.makeText(getApplicationContext(), response,
////                            Toast.LENGTH_LONG).show();
//                    Intent mainscreen = new Intent(getApplicationContext(),
//                            MainScreen.class);
//                    mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    mainscreen.putExtra("EXIT", true);
//                    startActivity(mainscreen);
//                }
//                else if(response.equalsIgnoreCase("User authentication failed.")) {
//
//                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                            CallScreenActivity.this);
//                    alertDialogBuilder.setMessage("You have been logged out. Please login again to use the application");
//                    alertDialogBuilder.setTitle("Error");
//                    alertDialogBuilder.setPositiveButton(
//                            "OK",
//                            new DialogInterface.OnClickListener() {
//
//                                public void onClick(DialogInterface arg0,
//                                                    int arg1) {
//                                    loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
//                                    loginpref.edit().remove("login_guid").commit();
//                                    Intent logout = new Intent(getApplicationContext(),
//                                            Login.class);
//                                    logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    logout.putExtra("EXIT", true);
//                                    startActivity(logout);
//                                    finish();
//
//                                }
//                            });
//                    AlertDialog alertDialog = alertDialogBuilder.create();
//                    alertDialog.show();
//                }

//                else {
////                    Toast.makeText(getApplicationContext(),
////                            "Some Error Occured", Toast.LENGTH_SHORT).show();
//                    Intent mainscreen = new Intent(getApplicationContext(),
//                            MainScreen.class);
//                    mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    mainscreen.putExtra("EXIT", true);
//                    startActivity(mainscreen);
//
//                }
//

                } else{
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
        AlertDialog.Builder builder = new AlertDialog.Builder(OutgoingCallScreen.this);
       /* playmedia();*/
        builder.setMessage("Do you want to continue the call?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        handler.removeCallbacksAndMessages(null);
                        increaseCalltime();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        endCall();
                        handler.removeCallbacksAndMessages(null);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        final int intervalTime = 10000; // 10 sec
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                endCall();
            }
        }, intervalTime);


    }

    public void increaseCalltime(){
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
