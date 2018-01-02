//
//package com.vdm.virtualdoorman;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.media.AudioManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.sinch.android.rtc.PushPair;
//import com.sinch.android.rtc.calling.Call;
//import com.sinch.android.rtc.calling.CallEndCause;
//import com.sinch.android.rtc.video.VideoCallListener;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.List;
//
//public class OperatorIncomingCallScreenActivity extends BaseActivity {
//
//    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
//    private String mCallId;
//    private AudioPlayer mAudioPlayer;
//    String from;
//    String  EndCause;
//    SharedPreferences loginpref;
//    String LOGINPREF = "LoginPrefs";
//    String remoteUsername;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.incoming);
//
//        Button answer = (Button) findViewById(R.id.answerButton);
//        answer.setOnClickListener(mClickListener);
////        Button decline = (Button) findViewById(R.id.declineButton);
////        decline.setOnClickListener(mClickListener);
//        Button ignore=(Button) findViewById(R.id.ignoreButton);
//        ignore.setOnClickListener(mClickListener);
//
//        mAudioPlayer = new AudioPlayer(this);
//        mAudioPlayer.playRingtone();
//        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
//        remoteUsername=getIntent().getStringExtra(SinchService.RemoteUserName);
//
//    }
//
//    @Override
//    protected void onServiceConnected() {
//        Call call = getSinchServiceInterface().getCall(mCallId);
//        if (call != null) {
//            call.addCallListener(new SinchCallListener());
//            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);
//            remoteUser.setText(remoteUsername);
//
//        } else {
//            Log.e(TAG, "Started with invalid callId, aborting");
//            finish();
//        }
//    }
//
//    private void answerClicked() {
//        mAudioPlayer.stopRingtone();
//        Call call = getSinchServiceInterface().getCall(mCallId);
//        if (call != null) {
//            call.answer();
//            Intent intent = new Intent(this, CallScreenActivity.class);
//            intent.putExtra(SinchService.CALL_ID, mCallId);
//            intent.putExtra(SinchService.RemoteUserName, remoteUsername);
//            startActivity(intent);
//        } else {
//            finish();
//        }
//    }
//
//    private void declineClicked() {
//        mAudioPlayer.stopRingtone();
//        Call call = getSinchServiceInterface().getCall(mCallId);
//        if (call != null) {
//            call.hangup();
//        }
//        finish();
//    }
//
//
//    private class SinchCallListener implements VideoCallListener {
//
//        @Override
//        public void onCallEnded(Call call) {
//
//            CallEndCause cause = call.getDetails().getEndCause();
//            Log.d(TAG, "Call ended. Reason: " + cause.toString());
//            from=call.getRemoteUserId();
//            EndCause= cause.toString();
////            Toast.makeText(getApplicationContext(), EndCause,
////                    Toast.LENGTH_LONG).show();
//            // mAudioPlayer.stopProgressTone();
//            mAudioPlayer.stopRingtone();
//            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
//            String endMsg = "Call ended: " + call.getDetails().toString();
//            new StoreCallDetail().execute();
//        }
//
//        @Override
//        public void onCallEstablished(Call call) {
//            Log.d(TAG, "Call established");
//        }
//
//        @Override
//        public void onCallProgressing(Call call) {
//            Log.d(TAG, "Call progressing");
//        }
//
//        @Override
//        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
//            // Send a push through your push provider here, e.g. GCM
//        }
//
//        @Override
//        public void onVideoTrackAdded(Call call) {
//            // Display some kind of icon showing it's a video call
//        }
//    }
//
//    private OnClickListener mClickListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.answerButton:
//                    answerClicked();
//                    break;
////                case R.id.declineButton:
////                    declineClicked();
////                    break;
//                case R.id.ignoreButton:
//                    ignoreClicked();
//                    break;
//            }
//        }
//    };
//
//    private void ignoreClicked() {
//        // CallEndCause cause = call.getDetails().getEndCause();
//        // Log.d(TAG, "Call ended, cause: " + cause.toString());
//        mAudioPlayer.stopRingtone();
//        finish();
//    }
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
////            pDialog = new ProgressDialog(IncomingCallScreenActivity.this);
////            pDialog.setMessage("Adding Call Record..");
////            pDialog.setIndeterminate(false);
////            pDialog.setCancelable(true);
////            pDialog.show();
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
//                jsonobj.put("DURATION", "");
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
//            // Log.e("Create Response", json.toString());
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
//            // pDialog.dismiss();
//            try {
//
//                String status = json.getString("status");
//                String response = json.getString("message");
//                if (status.equalsIgnoreCase("OK")) {
//                    if(from.equalsIgnoreCase("vdm.operator")){
//                        Log.d("Call From",from);
//                    }
//                    else {
////                    Toast.makeText(getApplicationContext(), response,
////                            Toast.LENGTH_LONG).show();
//                        Intent mainscreen = new Intent(getApplicationContext(),
//                                MainScreen.class);
//                        startActivity(mainscreen);
//                    }
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
//}
