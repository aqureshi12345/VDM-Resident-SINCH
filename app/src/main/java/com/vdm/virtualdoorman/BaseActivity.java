package com.vdm.virtualdoorman;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

public abstract class BaseActivity extends FragmentActivity implements ServiceConnection {

    private  SinchService.SinchServiceInterface mSinchServiceInterface;
    private static   SinchService.SinchServiceInterface mSinchServiceInterface_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            mSinchServiceInterface_new = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            mSinchServiceInterface_new = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }
    protected static SinchService.SinchServiceInterface getSinchServiceInterface_new() {
        return mSinchServiceInterface_new;
    }

}
