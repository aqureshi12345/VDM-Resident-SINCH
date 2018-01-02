package com.vdm.virtualdoorman;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

public class AudioPlayer implements Serializable  {

    static final String LOG_TAG = AudioPlayer.class.getSimpleName();
    private static AudioPlayer mAudioPlayer=null;
    private Context mContext;

    private MediaPlayer mPlayer;

    private AudioTrack mProgressTone;

    private final static int SAMPLE_RATE = 16000;
    public Vibrator vibrator;
    long pattern[] = { 0, 100, 200, 300, 400,500 };
    public AudioPlayer(Context context) {
        this.mContext = context.getApplicationContext();
    }

    protected AudioPlayer(Parcel in) {
        pattern = in.createLongArray();
    }

//    public static final Creator<AudioPlayer> CREATOR = new Creator<AudioPlayer>() {
//        @Override
//        public AudioPlayer createFromParcel(Parcel in) {
//            return new AudioPlayer(in);
//        }
//
//        @Override
//        public AudioPlayer[] newArray(int size) {
//            return new AudioPlayer[size];
//        }
//    };

    public void playRingtone(String app_ringer_mode) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        String App_ringtone_sitting = app_ringer_mode;
        // Honour silent mode
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
               if(App_ringtone_sitting.equalsIgnoreCase("Ring and Vibrate")) {
                   mPlayer = new MediaPlayer();
                   mPlayer.setAudioStreamType(AudioManager.STREAM_RING);

                   try {
                       mPlayer.setDataSource(mContext,
                               Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.hangout));
                       mPlayer.prepare();
                   } catch (IOException e) {
                       Log.e(LOG_TAG, "Could not setup media player for ringtone");
                       mPlayer = null;
                       return;
                   }
                   mPlayer.setLooping(true);
                   mPlayer.start();
                   vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                   vibrator.vibrate(pattern, 0);
               }else if(App_ringtone_sitting.equalsIgnoreCase("vibrate")){
                   vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                   vibrator.vibrate(pattern, 0);
               }
                break;

            case AudioManager.RINGER_MODE_VIBRATE:

                vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(pattern, 0);

                break;


        }
    }
    public boolean isPlaying(){
        if(mPlayer!=null) {
            if (mPlayer.isPlaying()) {
                return true;
            }
        }
        return false;

    }
    public void stopRingtone() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if(vibrator!=null){
            vibrator.cancel();
        }
    }

    public void playProgressTone() {
        stopProgressTone();
        try {
            mProgressTone = createProgressTone(mContext);
            mProgressTone.play();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not play progress tone", e);
        }
    }

    public void stopProgressTone() {
        if (mProgressTone != null) {
            mProgressTone.stop();
            mProgressTone.release();
            mProgressTone = null;
        }
    }

    private static AudioTrack createProgressTone(Context context) throws IOException {
        AssetFileDescriptor fd = context.getResources().openRawResourceFd(R.raw.progress_tone);
        int length = (int) fd.getLength();

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STATIC);

        byte[] data = new byte[length];
        readFileToBytes(fd, data);

        audioTrack.write(data, 0, data.length);
        audioTrack.setLoopPoints(0, data.length / 2, 30);

        return audioTrack;
    }

    private static void readFileToBytes(AssetFileDescriptor fd, byte[] data) throws IOException {
        FileInputStream inputStream = fd.createInputStream();

        int bytesRead = 0;
        while (bytesRead < data.length) {
            int res = inputStream.read(data, bytesRead, (data.length - bytesRead));
            if (res == -1) {
                break;
            }
            bytesRead += res;
        }
    }
    public static AudioPlayer getInstance(Context ActivityContext){
        if(mAudioPlayer == null)
        {
            mAudioPlayer =new AudioPlayer(ActivityContext);
        }
        return mAudioPlayer;
    }
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeLongArray(pattern);
//    }

    public String mobilesitting(){
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        String result = "";
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                result = "ring";
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                result = "vibrate";
            case AudioManager.RINGER_MODE_SILENT:
                result = "vibrate";

        }
        return result;
    }
}
