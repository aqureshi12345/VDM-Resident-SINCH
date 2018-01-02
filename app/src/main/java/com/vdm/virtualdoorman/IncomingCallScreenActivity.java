package com.vdm.virtualdoorman;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.video.VideoCallListener;
import com.vdm.virtualdoorman.gcm.GcmBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IncomingCallScreenActivity extends BaseActivity {

    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    private String mCallId;
    public AudioPlayer mAudioPlayer;
    NotificationCompat.Builder mBuilder;
    String from;
    String  EndCause;
    SharedPreferences loginpref;
    SharedPreferences VstationListpref;
    public static final String VstationListPref = "VstationPrefs";
    String LOGINPREF = "LoginPrefs";
   String remoteUsername;
    int ignor =0;
    String VstationId;
    NotificationManager mNotificationManager;
    public Vibrator vibrator;
    long pattern[] = { 0, 100, 200, 300, 400,500 };
    SharedPreferences ringerPref;
    public static final String RingerPREFS = "RingerPrefs";
    PowerManager pm;
    KeyguardManager km;
    KeyguardManager.KeyguardLock kl;
    PowerManager.WakeLock wl;
    private GcmBroadcastReceiver myReceiver;
    static IncomingCallScreenActivity incomingCall;
    String app_ringer_mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

       // Window window = getWindow();

//        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        km=(KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
//        kl=km.newKeyguardLock("INFO");
//        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE, "INFO");
//        wl.acquire(); //wake up the screen
//        kl.disableKeyguard();// dismiss the keyguard
//        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                |WindowManager.LayoutParams.FLAG_FULLSCREEN
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        super.onCreate(savedInstanceState);
        KeyguardManager manager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = manager.newKeyguardLock("abc");
        lock.disableKeyguard();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.incoming);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        myReceiver = new GcmBroadcastReceiver();
        registerReceiver(myReceiver, filter);
        Button answer = (Button) findViewById(R.id.answerButton);
        incomingCall=this;
        answer.setOnClickListener(mClickListener);
//        Button decline = (Button) findViewById(R.id.declineButton);
//        decline.setOnClickListener(mClickListener);
       Button ignore=(Button) findViewById(R.id.ignoreButton);
        ignore.setOnClickListener(mClickListener);
//        mAudioPlayer = new AudioPlayer(this);
//       mAudioPlayer.getInstance(this);
        loginpref = getSharedPreferences(LOGINPREF,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor call_editor = loginpref.edit();
        call_editor.putString("IncomingCall", "yes");
        call_editor.commit();
        ringerPref = getSharedPreferences(RingerPREFS,
                Context.MODE_PRIVATE);
        app_ringer_mode = (ringerPref.getString("Ringer", ""));
        AudioPlayer.getInstance(this).playRingtone(app_ringer_mode);
//        if(ringer_mode.equalsIgnoreCase("Ring and Vibrate")){
//
//               // mAudioPlayer.playRingtone();
//
//
//        }
//        if(ringer_mode.equalsIgnoreCase("vibrate")){
//            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//            vibrator.vibrate(pattern, 0);
//
//        }

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
//        Log.e(TAG, mCallId);
    }
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
//           ignoreClicked();
//            return true;
//        }
//
//        return super.dispatchKeyEvent(event);
//    }
public static IncomingCallScreenActivity getInstance(){
    return   incomingCall;
}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_POWER:
               ignoreClicked();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP :
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audio.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    AudioManager audioVolume = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioVolume.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    return true;
//            case KeyEvent.KEYCODE_SEARCH:
//                Toast.makeText(this, "Search key released", Toast.LENGTH_SHORT).show();
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_UP:​
//                if(event.isTracking() && !event.isCanceled())
//                    Toast.makeText(this, "Volumen Up released", Toast.LENGTH_SHORT).show();
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                Toast.makeText(this, "Volumen Down released", Toast.LENGTH_SHORT).show();
//                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
      protected void onDestroy(){
       // kl.reenableKeyguard();
        if (myReceiver != null)
        {
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
    //  AudioPlayer.getInstance(this).stopRingtone();
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
        loginpref.edit().remove("IncomingCall").commit();
        super.onDestroy();

    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        AudioPlayer.getInstance(this).stopRingtone();
        // mAudioPlayer.stopRingtone();
        notification();
    }
@Override
public void onBackPressed(){
    notification();
    super.onBackPressed();

}
//    public void notification(){
//        Intent resultIntent;
//        resultIntent=this.getIntent();
//        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        resultIntent.setAction(Intent.ACTION_VIEW);
//        //  resultIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//
//        );
//         mBuilder =  new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.icon175x175)
//                    .setContentTitle("Virtual Doorman-IncomingCall")
//                    .setContentText("Resume Call")
//               //  .setDeleteIntent(getIgnoreCall())
//        .setPriority(Notification.PRIORITY_MAX)
//                .setSound(Uri.parse("android.resource://" + IncomingCallScreenActivity.this.getPackageName() + "/" + R.raw.hangout))
//                 .setAutoCancel(true)
//                 .setContentIntent((resultPendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0)));
//      mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//      mNotificationManager.notify(001, mBuilder.build());
//
//       // .setContentIntent(getDialogPendingIntent())
//    }
    public void notification(){
        Intent resultIntent;
        resultIntent=this.getIntent();
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        resultIntent.setAction(Intent.ACTION_VIEW);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT

        );

        String phone_sitting = AudioPlayer.getInstance(this).mobilesitting();
        if(phone_sitting.equalsIgnoreCase("ring")) {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon175x175)
                    .setContentTitle("Virtual Doorman-IncomingCall")
                    .setContentText("Resume Call")
                            //  .setDeleteIntent(getIgnoreCall())
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSound(Uri.parse("android.resource://" + IncomingCallScreenActivity.this.getPackageName() + "/" + R.raw.hangout))
                    .setAutoCancel(true)
                    .setContentIntent((resultPendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0)));
        }else{
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon175x175)
                    .setContentTitle("Virtual Doorman-IncomingCall")
                    .setContentText("Resume Call")
                            //  .setDeleteIntent(getIgnoreCall())
                    .setPriority(Notification.PRIORITY_MAX)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setVisibility(0)
                    .setAutoCancel(true)
                    .setContentIntent((resultPendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0)));
        }
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());

        // .setContentIntent(getDialogPendingIntent())
    }

//    private PendingIntent getAcceptCall() {
//      //  mAudioPlayer.stopRingtone();
//
//       // finish();
//         return resultPendingIntent;
//    }

    private PendingIntent getIgnoreCall() {
        if(ignor==1){
            mNotificationManager.cancel(001);
            ignoreClicked();
        }
        Intent resultIntent;
        resultIntent = new Intent(this, CallScreenActivity.class);
        resultIntent.putExtra("ignorecall", "ignoreCall");
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        ignor=1;
        return resultPendingIntent;
    }


    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);
            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
            //  String resident_id = (loginpref.getString("resident_id", ""));
            VstationListpref = getSharedPreferences(VstationListPref,
                    Context.MODE_PRIVATE);
            String user=call.getRemoteUserId();
            remoteUsername=(VstationListpref.getString(user, ""));
            remoteUser.setText(remoteUsername);


        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    public void answerClicked() {
        if(vibrator!=null){
            vibrator.cancel();
        }
       AudioPlayer.getInstance(IncomingCallScreenActivity.this).stopRingtone();

        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();

            Intent callintent = new Intent(this, CallScreenActivity.class);
            callintent.putExtra(SinchService.CALL_ID, mCallId);
            callintent.putExtra("UserName", remoteUsername);

            startActivity(callintent);
            finish();
        } else {
            finish();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        if(vibrator!=null){
            vibrator.cancel();
        }
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    @Override
    public void onPause() {

        super.onPause();

    }

    @Override
    public void onResume() {
        if(! AudioPlayer.getInstance(this).isPlaying()){
            AudioPlayer.getInstance(this).playRingtone(app_ringer_mode);
        }
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) IncomingCallScreenActivity.this.getSystemService(ns);
        nMgr.cancel(001);
        super.onResume();

    }

    public void ignoreClickedfromPowerbutton() {

        AudioPlayer.getInstance(this).stopRingtone();
        //mAudioPlayer.stopRingtone();
        if(vibrator!=null){
            vibrator.cancel();
        }
       // this.finish();
    }


    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
           // vibrator.cancel();
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            from=call.getRemoteUserId();
            EndCause= cause.toString();
//            Toast.makeText(getApplicationContext(), EndCause,
//                    Toast.LENGTH_LONG).show();
           // mAudioPlayer.stopProgressTone();
            if(vibrator!=null){
                vibrator.cancel();
            }
           AudioPlayer.getInstance(IncomingCallScreenActivity.this).stopRingtone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            cancelNotification(IncomingCallScreenActivity.this,001);
            new StoreCallDetail().execute();
        }

        @Override
        public void onCallEstablished(Call call) {

            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {

         //   VstationId= call.getHeaders().get("vstationId");
            Log.e(TAG, "Call progressing+++++++++++++");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            // Display some kind of icon showing it's a video call
        }
        @Override
        public void onVideoTrackPaused(Call call) {

        }

        @Override
        public void onVideoTrackResumed(Call call) {

        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    answerClicked();
                    break;
//                case R.id.declineButton:
//                    declineClicked();
//                    break;
                case R.id.ignoreButton:
                    ignoreClicked();
                    break;
            }
        }
    };

    public void ignoreClicked() {

       // Call call = getSinchServiceInterface().getCall(mCallId);
        //if(call!=null) {
        AudioPlayer.getInstance(this).stopRingtone();
            //mAudioPlayer.stopRingtone();
            if(vibrator!=null){
                vibrator.cancel();
           }
            finish();
       // }
    }
    class StoreCallDetail extends AsyncTask<String, String, String> {
        private static final String CALLRECORDPREFERENCES = "Calrecordpref";
        JSONObject json = new JSONObject();
        private ProgressDialog pDialog;
        JSONObject jsonobj = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        SharedPreferences CalRecordPref;
        final String cal_record = "https://portal.virtualdoorman.com/dev/common/libs/slim/insert_call_record/";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(IncomingCallScreenActivity.this);
//            pDialog.setMessage("Adding Call Record..");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
        }
        protected String doInBackground(String... args) {

            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
            String resident_id = (loginpref.getString("resident_id", ""));
            String building_id = (loginpref.getString("building_id", ""));
            String apartment_id = (loginpref.getString("apartment_id", ""));
            String resident_name = (loginpref.getString("resident_name", ""));
            String to = (loginpref.getString("Caller_id", ""));
            Log.d("BUILDING ID+++++++",building_id);

            try {
                jsonobj.put("CALL_ID", mCallId);
                jsonobj.put("DURATION", "");
                jsonobj.put("CALL_END_REASON", EndCause);
                jsonobj.put("FROM", from);
                jsonobj.put("RESIDENT_ID", resident_id);
                jsonobj.put("BUILDING_ID", building_id);
                jsonobj.put("APARTMENT_ID", apartment_id);
                jsonobj.put("RESIDENT_NAME", resident_name);
                jsonobj.put("TO", to);


            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
			/*
			 * try { jdata.put("data", jsonobj); } catch (JSONException e1) { //
			 * TODO Auto-generated catch block e1.printStackTrace(); }
			 */

            // getting JSON Object
            // Note that create guest url accepts POST method
            Log.e("json is+++++", jsonobj.toString());
            json = jsonParser.makeHttpRequest(cal_record, "GET", jsonobj);

            // check log cat for response
            // Log.e("Create Response", json.toString());


            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String res) {
            // dismiss the dialog once done
          // pDialog.dismiss();
            try {

                String status = json.getString("status");
                String response = json.getString("message");
                if (status.equalsIgnoreCase("OK")) {
                    if(from.equalsIgnoreCase("vdm.operator")){
                        Log.d("Call From",from);
                    }
                    else {
//                    Toast.makeText(getApplicationContext(), response,
//                            Toast.LENGTH_LONG).show();
//                        Intent mainscreen = new Intent(getApplicationContext(),
//                                MainScreen.class);
//                        startActivity(mainscreen);
                        finish();
                    }
                }

                else {
//                    Toast.makeText(getApplicationContext(),
//                            "Some Error Occured", Toast.LENGTH_SHORT).show();
                    Intent arrivals = new Intent(getApplicationContext(),
                            ArrivalsActivity.class);
                    arrivals.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    arrivals.putExtra("EXIT", true);
                    startActivity(arrivals);

                }

            } catch (Exception e) {

                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }
}
