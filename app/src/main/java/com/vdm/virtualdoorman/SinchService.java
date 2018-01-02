package com.vdm.virtualdoorman;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.video.VideoController;


public class SinchService extends Service {


    public static final String CALL_ID = "CALL_ID";
    public static final String RemoteUserName ="remoteUsername";
    public static final String vstationID ="ID";
    static final String TAG = SinchService.class.getSimpleName();
    private static  String APP_KEY;// ="c8deb5a8-99ba-4828-a495-0176fe799298";
    private static  String APP_SECRET;// = "kT4VJAoxAUaZ8yPdZuUX1w==" ;
    private static  String ENVIRONMENT;//="sandbox.sinch.com" ;
    private SinchServiceInterface mSinchServiceInterface = new SinchServiceInterface();
    private SinchClient mSinchClient;
    private String mUserId;
    SharedPreferences loginpref;
    private PersistedSettings mSettings;
    String CallId;
    final String LOGINPREF = "LoginPrefs";
    private StartFailedListener mListener;

    @Override
    public void onCreate() {

        super.onCreate();
        mSettings = new PersistedSettings(getApplicationContext());
    }
//    @Override
//    public boolean onUnbind(Intent intent) {
//        mSinchClient.stopListeningOnActiveConnection();
//        mSinchClient.removeSinchClientListener(new MySinchClientListener());
//        mSinchClient.terminate();
//        Toast.makeText(this, "Service unbind", Toast.LENGTH_LONG).show();
//        mSinchClient = null;
//        return false;
//    }

  @Override
 public void onDestroy() {
     if (mSinchClient != null && mSinchClient.isStarted()) {
       //  mSinchClient.unregisterManagedPush();
         mSinchClient.terminate();
     }
     super.onDestroy();
 }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//
////        loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
////         CallId = (loginpref.getString("Caller_id", ""));
////        loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
////        CallId = (loginpref.getString("Caller_id", ""));
////         APP_KEY =(loginpref.getString("APP_KEY", ""));
////       APP_SECRET =(loginpref.getString("APP_SECRET", ""));
////        ENVIRONMENT = (loginpref.getString("ENVIRONMENT", ""));
//     //   mSettings.setUsername(mUserId);
//      //  start(CallId);
//
//        return super.onStartCommand(intent, flags, startId);
//    }
    private void start(String userName) {
        if (mSinchClient == null) {
            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
           // CallId = (loginpref.getString("Caller_id", ""));
            APP_KEY =(loginpref.getString("APP_KEY", ""));
            APP_SECRET =(loginpref.getString("APP_SECRET", ""));
            ENVIRONMENT = (loginpref.getString("ENVIRONMENT", ""));
            mUserId = userName;
            mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(userName)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT).build();
            mSinchClient.setSupportCalling(true);
//            mSinchClient.setSupportActiveConnectionInBackground(true);
     //  mSinchClient.startListeningOnActiveConnection();
         mSinchClient.setSupportManagedPush(true);

           mSinchClient.checkManifest();

            mSinchClient.addSinchClientListener(new MySinchClientListener());
            mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
            mSinchClient.start();
            Log.e("+++++++", "startedddddddd");
        }
    }

    private void stop() {
        if (mSinchClient != null) {

            mSinchClient.terminate();
            mSinchClient = null;

        }
    }

    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    @Override
    public IBinder onBind(Intent intent) {
        intent.setFlags(START_STICKY);
        return mSinchServiceInterface;
    }

    public class SinchServiceInterface extends Binder {

        public Call callPhoneNumber(String phoneNumber) {
            return mSinchClient.getCallClient().callPhoneNumber(phoneNumber);
        }
        public Call callUser(String userId) {
            return mSinchClient.getCallClient().callUser(userId);
        }
        public Call callUserVideo(String userId) {
            return mSinchClient.getCallClient().callUserVideo(userId);
        }

        public String getUserName() {
            return mUserId;
        }

        public boolean isStarted() {
            return SinchService.this.isStarted();
        }

        public void startClient(String userName) {
            if (mSinchClient == null) {
                mSettings.setUsername(userName);
                start(userName);
            }
        }

        public void stopClient() {
            mSinchClient.unregisterManagedPush();
          //  mSinchClient.terminateGracefully();
          //  mSinchClient.stopListeningOnActiveConnection();
            mSinchClient = null;
          // stop();

        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public Call getCall(String callId) {
            return mSinchClient.getCallClient().getCall(callId);
        }

        public VideoController getVideoController() {
            return mSinchClient.getVideoController();
        }

        public AudioController getAudioController() {
            return mSinchClient.getAudioController();
        }

        public NotificationResult relayRemotePushNotificationPayload(Intent intent) {
//            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
//             CallId = (loginpref.getString("Caller_id", ""));
//            if (CallId != null) {
                if (mSinchClient == null && !mSettings.getUsername().isEmpty()) {
                    Log.d("push++++++=","Push---------");
                    start(mSettings.getUsername());
                } else if (mSinchClient == null && mSettings.getUsername().isEmpty()) {
                    Log.e(TAG, "Can't start a SinchClient as no username is available, unable to relay push.");
                    return null;
                }
                return mSinchClient.relayRemotePushNotificationPayload(intent);
//            } else {
//
//                Log.d("calling ID++++", "null++++++");
//                return null;
//            }
        }
    }

    public interface StartFailedListener {

        void onStartFailed(SinchError error);

        void onStarted();
    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {
            if (mListener != null) {
                mListener.onStartFailed(error);
            }
            mSinchClient.terminate();
            mSinchClient = null;
        }

        @Override
        public void onClientStarted(SinchClient client) {
            Log.d(TAG, "SinchClient started");
            if (mListener != null) {
                mListener.onStarted();
            }
        }

        @Override
        public void onClientStopped(SinchClient client) {
            Log.d(TAG, "SinchClient stopped");
        }

        @Override
        public void onLogMessage(int level, String area, String message) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    break;
                case Log.ERROR:
                    Log.e(area, message);
                    break;
                case Log.INFO:
                    Log.i(area, message);
                    break;
                case Log.VERBOSE:
                    Log.v(area, message);
                    break;
                case Log.WARN:
                    Log.w(area, message);
                    break;
            }
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient client,
                ClientRegistration clientRegistration) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Log.d(TAG, "Incoming call");
//            if (call.getRemoteUserId().contains("operator")){
//                Intent intent = new Intent(SinchService.this, IncomingCallScreenActivity.class);
//                intent.putExtra(CALL_ID, call.getCallId());
//                intent.putExtra(RemoteUserName,call.getRemoteUserId());
//                intent.putExtra(vstationID,"operator");
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                SinchService.this.startActivity(intent);
//            }
//            else {

                Intent intent = new Intent(SinchService.this, IncomingCallScreenActivity.class);
                intent.putExtra(CALL_ID, call.getCallId());
//                intent.putExtra(RemoteUserName, call.getHeaders().get("vstationName"));
//                intent.putExtra(vstationID, call.getHeaders().get("vstationId"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SinchService.this.startActivity(intent);
//            }
            //call.getCallId()

        }
    }
    private class PersistedSettings {

        private SharedPreferences mStore;

        private static final String PREF_KEY = "Sinch";

        public PersistedSettings(Context context) {
            mStore = context.getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        }

        public String getUsername() {
            return mStore.getString("Username", "");
        }

        public void setUsername(String username) {
            SharedPreferences.Editor editor = mStore.edit();
            editor.putString("Username", username);
            editor.commit();
        }
    }

}
