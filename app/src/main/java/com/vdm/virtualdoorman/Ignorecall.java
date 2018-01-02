package com.vdm.virtualdoorman;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;

import com.sinch.android.rtc.calling.Call;
import com.vdm.virtualdoorman.AudioPlayer;
import com.vdm.virtualdoorman.BaseActivity;
import com.vdm.virtualdoorman.SinchService;

/**
 * Created by Malik Adil on 6/10/2016.
 */
public class Ignorecall  extends BaseActivity {
    private AudioPlayer mAudioPlayer;
    public Vibrator vibrator;
    private String mCallId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ignoreClicked();

    }

    private void ignoreClicked() {
        NotificationManager mNotificationManager =  (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(001);
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Call call = getSinchServiceInterface().getCall(mCallId);
        if(call!=null) {
            mAudioPlayer.stopRingtone();
            if(vibrator!=null){
                vibrator.cancel();
            }
            finish();
        }
    }
}
