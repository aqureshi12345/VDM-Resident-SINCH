package com.vdm.virtualdoorman.gcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.vdm.virtualdoorman.IncomingCallScreenActivity;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            IncomingCallScreenActivity incoming= new IncomingCallScreenActivity();
            incoming.ignoreClickedfromPowerbutton();
            IncomingCallScreenActivity.getInstance().finish();
        }
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}