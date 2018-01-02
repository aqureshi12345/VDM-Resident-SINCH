//package com.vdm.virtualdoorman;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Vibrator;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//
//import com.sinch.android.rtc.calling.Call;
//
//import java.util.ArrayList;
//
//public class NotificationRecivecall extends BaseActivity {
//    public  AudioPlayer mAudioPlayer;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.notification_recivecall);
//        ArrayList<AudioPlayer> listobj = getIntent().getParcelableArrayListExtra("audioPlayer");
//        mAudioPlayer = listobj.get(0);
//        mAudioPlayer.stopRingtone();
//        finish();
//       /*IncomingCallScreenActivity obj = new IncomingCallScreenActivity();
//        obj.answerClicked();*/
//
//    }
//
//
//
//}
