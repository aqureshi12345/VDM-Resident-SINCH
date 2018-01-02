package com.vdm.virtualdoorman;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_COMPRESSIONCFG_V30;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_POINT_FRAME;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;

import org.MediaPlayer.PlayM4.Player;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Amna joyia on 3/11/2016.
 */
public class viewBuildingCamera extends Activity implements SurfaceHolder.Callback,
        GestureDetector.OnDoubleTapListener {
    SharedPreferences loginpref;
    public static final String LOGINPREF = "LoginPrefs";
    String DVR_IP;
    int DVR_PORT;
    String DVR_Username;
    String DVR_Paswd;
    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
    private final String 	TAG						= "viewBuildingCamera";
    private int				m_iLogID				= -1;
    private	int 			m_iStartChan 			= 0;				// start channel no
    private int				m_iChanNum				= 0;				//channel number
    private static PlayMultipleSurfaceView [] playView;// = new PlaySurfaceView[4];
    private static PlaySurfaceView [] playSView;// = new PlaySurfaceView[4];
    private static PlaySurfaceView [] playFrameView;
    private int 			m_iPlayID				= -1;				// return by NET_DVR_RealPlay_V30
    private int				m_iPort					= -1;				// play port
    private boolean			m_bNeedDecode			= true;
   private SurfaceView  m_osurfaceView			= null;
    private boolean			m_bMultiPlay			= false;
    private int				m_iPlaybackID			= -1;				// return by NET_DVR_PlayBackByTime
    String camera_name[];
    String camera_channel[] ;
    private int numberOfRows;// = 4;
    private int totalCameras  ;//= numberOfRows * numberOfRows;
        Button one, four,nine,sixteen, screenshot,zoom,zoomOut;
    boolean zooming=false;
    String login_guid ,Person_id;
    File imageFile;
    private static String url_image_upload = "";
   // private static ProgressDialog progressDialog;
   public static final String PREFERENCES = "videoView" ;
    SharedPreferences videoView_sharedpreferences;
    private ProgressDialog pDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_building_camera);
        loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
        login_guid = (loginpref.getString("login_guid", ""));
        Person_id = (loginpref.getString("resident_id", ""));
        url_image_upload = "https://portal.virtualdoorman.com/dev/iphone/iphone_requests.php?function=update_image&login_guid="
                + login_guid+"&snap_shot=1";
       m_osurfaceView = (SurfaceView) findViewById(R.id.Sur_Player);
        videoView_sharedpreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        numberOfRows = (videoView_sharedpreferences.getInt("NumberOfCameras",0));
        if(numberOfRows==0) {
            numberOfRows = 2;
        }
        totalCameras = numberOfRows * numberOfRows;
        one=(Button)findViewById(R.id.one);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopMultiPreview();
                if(totalCameras==1){
                    ((ViewGroup) playSView[0].getParent()).removeView(playSView[0]);
                }else {

                for(int i = 0; i < camera_channel.length; i++) {
                        ((ViewGroup) playView[i].getParent()).removeView(playView[i]);
                    }
                }
                numberOfRows=1;
                totalCameras = numberOfRows * numberOfRows;
                startMultiPreview(0,0);
                if(screenshot.getVisibility()==View.INVISIBLE && zoom.getVisibility()== View.INVISIBLE) {
                    screenshot.setVisibility(View.VISIBLE);
                    zoom.setVisibility(View.VISIBLE);
                }

            }
        });
        four=(Button)findViewById(R.id.two);
        four.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(screenshot.getVisibility()==View.VISIBLE && zoom.getVisibility()== View.VISIBLE) {
                    screenshot.setVisibility(View.INVISIBLE);
                    zoom.setVisibility(View.INVISIBLE);
                }

                if(totalCameras==1){
                    ((ViewGroup) playSView[0].getParent()).removeView(playSView[0]);
                }
                else {
//                    for (int i = 0; i < totalCameras; i++) {
//
//
//                        ((ViewGroup) playView[i].getParent()).removeView(playView[i]);
//
//                    }
                  //  int length=  playFrameView.length;
                    stopMultiPreview();
                    for (int i = 0; i < camera_channel.length; i++) {


                        ((ViewGroup) playView[i].getParent()).removeView(playView[i]);

                    }
                }
                numberOfRows=2;
                totalCameras = numberOfRows * numberOfRows;
              //  drawFrames();
                startMultiPreview(0,4);
               // progressDialog = ProgressDialog.show(viewBuildingCamera.this, "", "Loading...", true);
            }
        });
        nine=(Button)findViewById(R.id.three);
        nine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(screenshot.getVisibility()==View.VISIBLE && zoom.getVisibility()== View.VISIBLE) {
                    screenshot.setVisibility(View.INVISIBLE);
                    zoom.setVisibility(View.INVISIBLE);
                }
                stopMultiPreview();
                if(totalCameras==1){
                    ((ViewGroup) playSView[0].getParent()).removeView(playSView[0]);
                }
                else {
                for(int i = 0; i < camera_channel.length; i++) {

                        ((ViewGroup) playView[i].getParent()).removeView(playView[i]);
                    }
                }
                numberOfRows=3;
                totalCameras = numberOfRows * numberOfRows;
               // drawFrames();
               startMultiPreview(0,9);
            }
        });
        sixteen=(Button)findViewById(R.id.four);
        sixteen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(screenshot.getVisibility()==View.VISIBLE && zoom.getVisibility()== View.VISIBLE) {
                    screenshot.setVisibility(View.INVISIBLE);
                    zoom.setVisibility(View.INVISIBLE);
                }
                stopMultiPreview();
                if(totalCameras==1){
                    ((ViewGroup) playSView[0].getParent()).removeView(playSView[0]);
                }
                else{
                    for(int i = 0; i < camera_channel.length; i++) {

                        ((ViewGroup) playView[i].getParent()).removeView(playView[i]);
                    }
                }
                numberOfRows=4;
                totalCameras = numberOfRows * numberOfRows;
                //drawFrames();
                startMultiPreview(0,16);

            }
        });
        screenshot=(Button)findViewById(R.id.screenshot);
        screenshot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                PlaySurfaceView player= new PlaySurfaceView(viewBuildingCamera.this);
             //   int playId=player.getPlayerID();
               // Test_PTZSelZoomIn(playId);
                takeScreenshot();

            }
        });
        zoom=(Button)findViewById(R.id.zoom);
        zoom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(zooming){
                    zoom.setText("Zoom-In");
                    ZoomOut();
                    zooming=false;
                }
               else {
                    zoom.setText("Zoom-out");
                    Test_PTZSelZoomIn();
                    zooming = true;

                }

            }
        });

        m_osurfaceView.getHolder().addCallback(this);;
        if (!initeSdk()) {
            this.finish();
            return;
        }
        new DVRLogin().execute();


//        try
//        {
//
//
//        }
//        catch (Exception err)
//        {
//            Log.e(TAG, "error: " + err.toString());
//        }


    }

    private RealPlayCallBack getRealPlayerCbf()
    {
        RealPlayCallBack cbf = new RealPlayCallBack()
        {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize)
            {
                // player channel 1
                viewBuildingCamera.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }
    private void startSinglePreview(int chan)
    {
        if (m_iPlaybackID >= 0) {
            Log.i(TAG, "Please stop palyback first");
            return ;
        }
        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        if (fRealDataCallBack == null)
        {
            Log.e(TAG, "fRealDataCallBack object is failed!");
            return ;
        }
        Log.i(TAG, "m_iStartChan:" +m_iStartChan);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan;
        previewInfo.dwStreamType = 0; //substream
        previewInfo.bBlocked = 1;
        // HCNetSDK start preview
        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID, previewInfo, fRealDataCallBack);
        if (m_iPlayID < 0)
        {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        Log.i(TAG, "NetSdk Play sucess ***********************3***************************");

    }
    private void stopSinglePreview()
    {
        if ( m_iPlayID < 0)
        {
            Log.e(TAG, "m_iPlayID < 0");
            return;
        }

        //  net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID))
        {
            Log.e(TAG, "StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        m_iPlayID = -1;
        stopSinglePlayer();
    }
    private void stopSinglePlayer()
    {
        Player.getInstance().stopSound();
        // player stop play
        if (!Player.getInstance().stop(m_iPort))
        {
            Log.e(TAG, "stop is failed!");
            return;
        }

        if(!Player.getInstance().closeStream(m_iPort))
        {
            Log.e(TAG, "closeStream is failed!");
            return;
        }
        if(!Player.getInstance().freePort(m_iPort))
        {
            Log.e(TAG, "freePort is failed!" + m_iPort);
            return;
        }
        m_iPort = -1;
    }
    public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode)
    {
        if(!m_bNeedDecode)
        {
            //   Log.i(TAG, "iPlayViewNo:" + iPlayViewNo + ",iDataType:" + iDataType + ",iDataSize:" + iDataSize);
        }
        else
        {
            if(HCNetSDK.NET_DVR_SYSHEAD == iDataType)
            {
                if(m_iPort >= 0)
                {
                    return;
                }
                m_iPort = Player.getInstance().getPort();
                if(m_iPort == -1)
                {
                    Log.e(TAG, "getPort is failed with: " + Player.getInstance().getLastError(m_iPort));
                    return;
                }
                Log.i(TAG, "getPort succ with: " + m_iPort);
                if (iDataSize > 0)
                {
                    if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
                    {
                        Log.e(TAG, "setStreamOpenMode failed");
                        return;
                    }
                    if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2*1024*1024)) //open stream
                    {
                        Log.e(TAG, "openStream failed");
                        return;
                    }
                    if (!Player.getInstance().play(m_iPort, m_osurfaceView.getHolder()))
                    {
                        Log.e(TAG, "play failed");
                        return;
                    }
                    if(!Player.getInstance().playSound(m_iPort))
                    {
                        Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
                        return;
                    }
                }
            }
            else
            {
                if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize))
                {
//		    		Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
                    for(int i = 0; i < 4000 && m_iPlaybackID >=0 ; i++)
                    {
                        if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize))
                            Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
                        else
                            break;
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        }
                    }
                }

            }
        }

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(pDialog!=null) {
            pDialog.dismiss();
        }
        if(camera_channel!=null) {
            stopMultiPreview();

            m_bMultiPlay = false;
            if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
                Log.e(TAG, " NET_DVR_Logout is failed!");
                return;
            }
            //  m_oLoginBtn.setText("Login");
            m_iLogID = -1;
        }
        videoView_sharedpreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = videoView_sharedpreferences.edit();

        editor.putInt("NumberOfCameras", numberOfRows);
        editor.commit();
     //   finish();
    }
    @Override
    public void onBackPressed(){
        finish();

        super.onBackPressed();

    }
    //@Override
    public void surfaceCreated(SurfaceHolder holder) {
        m_osurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created" + m_iPort);
        if (-1 == m_iPort)
        {
            return;
        }
        Surface surface = holder.getSurface();
        if (true == surface.isValid()) {
            if (false == Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }
//    @Override
//    protected void Draw(Canvas canvas)
//    {
//
//        canvas.drawColor(Color.RED);
//    }
    //@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    //@Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
        if (-1 == m_iPort)
        {
            return;
        }
        if (true == holder.getSurface().isValid()) {
            if (false == Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putInt("m_iPort", m_iPort);
//        super.onSaveInstanceState(outState);
//        Log.i(TAG, "onSaveInstanceState");
//    }
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        m_iPort = savedInstanceState.getInt("m_iPort");
//        super.onRestoreInstanceState(savedInstanceState);
//        Log.i(TAG, "onRestoreInstanceState");
//    }
// init sdk
    private boolean initeSdk()
    {
        //init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e("CameraViewr++", "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
        return true;
    }
    private int loginDevice()
    {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30)
        {

            Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
              return -1;
        }

        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(DVR_IP, DVR_PORT, DVR_Username, DVR_Paswd, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0)
        {
            Log.e(TAG, "NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
             return -1;
        }
//        if(m_oNetDvrDeviceInfoV30.byChanNum > 0)
//        {
////            NET_DVR_CONFIG config=new NET_DVR_CONFIG();
////            m_iStartChan=HCNetSDK.getInstance().NET_DVR_GetDVRConfig(iLogID, HCNetSDK.getInstance().NET_DVR_GET_IPPARACFG_V40, 33,config);
//           m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
//            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
//        }
//        else
        if(m_oNetDvrDeviceInfoV30.byIPChanNum > 0)
        {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
          //  m_iStartChan = 0;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");

        return iLogID;
    }


    private ExceptionCallBack getExceptiongCbf()
    {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack()
        {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle)
            {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }

//    private void drawFrames() {
//
//        DisplayMetrics metric = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int i;
//
//        playFrameView = new PlaySurfaceView[totalCameras];
//        int leftIndex = 0;
//        // SingleplayView=null;
//        int camera_count=0;
//        for(i = 0; i < totalCameras  ; i++) {
//            if (playFrameView[i] == null) {
//                playFrameView[i] = new PlaySurfaceView(this);
//
//                playFrameView[i].setParam(m_osurfaceView.getWidth(), m_osurfaceView.getHeight(), numberOfRows);
//
//                // FrameLayout frameLayout= new FrameLayout()
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                        FrameLayout.LayoutParams.MATCH_PARENT,
//                        FrameLayout.LayoutParams.MATCH_PARENT);
//                final ImageView header = (ImageView) findViewById(R.id.headerImage);
//
//                params.topMargin = (int) (Math.floor((double) (i / numberOfRows)) * playFrameView[i].getCurHeight());//+header.getHeight();
////                if(params.topMargin==0){
////                    params.topMargin=header.getHeight();
////                }
//                params.leftMargin = leftIndex * playFrameView[i].getCurWidth();
//
//                leftIndex++;
//
//                if (leftIndex % numberOfRows == 0)
//                    leftIndex = 0;
//                params.gravity = Gravity.START | Gravity.LEFT;
//                addContentView(playFrameView[i], params);
//                playView[i].setBackgroundResource(R.drawable.framelayout_border);
//                playView[i].getBackground().setAlpha(40);
//                playView[i].setPadding(5, 5, 5, 5);
//                playView[i].setZOrderOnTop(true);
//
//            }
//        }
//    }
    private void startMultiPreview(int c , int v)
    {
      if(c==0) {
          DisplayMetrics metric = new DisplayMetrics();
          getWindowManager().getDefaultDisplay().getMetrics(metric);
          int i;
          if (totalCameras > 1) {
              playView = new PlayMultipleSurfaceView[totalCameras];
              int leftIndex = 0;
              // SingleplayView=null;
              int camera_count = 0;
              Log.d(TAG, "startMultiPreview totalCameras" + totalCameras);
              for (i = 0; i < camera_channel.length && camera_count < totalCameras; i++) {
                  if (playView[i] == null) {
                      playView[i] = new PlayMultipleSurfaceView(this);
                      playView[i].setParam(m_osurfaceView.getWidth(), m_osurfaceView.getHeight(), numberOfRows);
                      // FrameLayout frameLayout= new FrameLayout()
                      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                              FrameLayout.LayoutParams.MATCH_PARENT,
                              FrameLayout.LayoutParams.MATCH_PARENT);
                      final ImageView header = (ImageView) findViewById(R.id.headerImage);
                      params.topMargin = (int) (Math.floor((double) (i / numberOfRows)) * playView[i].getCurHeight());//+header.getHeight();
//                if(params.topMargin==0){
//                    params.topMargin=header.getHeight();
//                }

                      params.leftMargin = leftIndex * playView[i].getCurWidth();

                      leftIndex++;

                      if (leftIndex % numberOfRows == 0)
                          leftIndex = 0;
                      params.gravity = Gravity.START | Gravity.LEFT;
                      addContentView(playView[i], params);
                      playView[i].setBackgroundResource(R.drawable.framelayout_border);
                      playView[i].getBackground().setAlpha(40);
                      playView[i].setPadding(5, 5, 5, 5);
                      playView[i].setZOrderOnTop(true);
                      playView[i].setClickable(true);
                      playView[i].setFocusable(false);

//                if(i==0){
//                    playView[i].startPreview(m_iLogID, 1);
//                }
//                else {
                      playView[i].startPreview(m_iLogID, Integer.parseInt(camera_channel[i]));
                      /////frame Rate Control code
//                NET_DVR_COMPRESSIONCFG_V30 struCompress = new NET_DVR_COMPRESSIONCFG_V30();
//                if(!HCNetSDK.getInstance().NET_DVR_GetDVRConfig(m_iLogID, HCNetSDK.NET_DVR_GET_COMPRESSCFG_V30, Integer.parseInt(camera_channel[i]), struCompress))
//                {
//                    Log.e(TAG, "NET_DVR_GET_COMPRESSCFG_V30 failed with error code:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
//                }
//                else
//                {
//                    Log.i(TAG, "NET_DVR_GET_COMPRESSCFG_V30 succ");
//                }
//                //set substream resolution to cif
//                struCompress.struNetPara.dwVideoBitrate = 2;
//                // struCompress.struNetPara.byResolution = 12;
//                if(!HCNetSDK.getInstance().NET_DVR_SetDVRConfig(m_iLogID, HCNetSDK.NET_DVR_SET_COMPRESSCFG_V30, Integer.parseInt(camera_channel[i]), struCompress))
//                {
//                    Log.e(TAG, "NET_DVR_SET_COMPRESSCFG_V30 failed with error code:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
//                }
//                else
//                {
//                    Log.i(TAG, "NET_DVR_SET_COMPRESSCFG_V30 succ");
//                }


                      camera_count++;
                      // }

                      // SingleplayView= playView[i];
                      final int j = i;

                      playView[i].setOnClickListener(new View.OnClickListener() {
                          public void onClick(View v) {
                              // int channel = 0;
                              int k = j;
                              stopMultiPreview();
                              if (totalCameras == 1) {
                                  ((ViewGroup) playView[0].getParent()).removeView(playView[0]);
                              } else {

                                  for (int i = 0; i < camera_channel.length; i++) {
                                      ((ViewGroup) playView[i].getParent()).removeView(playView[i]);
                                  }
                              }
                              numberOfRows = 1;
                              totalCameras = numberOfRows * numberOfRows;
                              startMultiPreview(1,k);
                              if (screenshot.getVisibility() == View.INVISIBLE && zoom.getVisibility() == View.INVISIBLE) {
                                  screenshot.setVisibility(View.VISIBLE);
                                  zoom.setVisibility(View.VISIBLE);
                              }
                          }
                      });

                  }

              }
              //  m_iPlayID = SingleplayView.m_iPreviewHandle;
              //progressDialog.dismiss();
              m_iPlayID = playView[0].m_iPreviewHandle;
          } else {

              playSView = new PlaySurfaceView[totalCameras];
              int leftIndex = 0;
              int camera_count = 0;
              Log.d(TAG, "startMultiPreview totalCameras" + totalCameras);
              for (i = 0; i < camera_channel.length && camera_count < totalCameras; i++) {
                  if (playSView[i] == null) {
                      playSView[i] = new PlaySurfaceView(this);
                      playSView[i].setParam(m_osurfaceView.getWidth(), m_osurfaceView.getHeight(), numberOfRows);
                      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                              FrameLayout.LayoutParams.MATCH_PARENT,
                              FrameLayout.LayoutParams.MATCH_PARENT);
                      final ImageView header = (ImageView) findViewById(R.id.headerImage);
                      params.topMargin = (int) (Math.floor((double) (i / numberOfRows)) * playSView[i].getCurHeight());//+header.getHeight();
                      params.leftMargin = leftIndex * playSView[i].getCurWidth();
                      leftIndex++;
                      if (leftIndex % numberOfRows == 0)
                          leftIndex = 0;
                      params.gravity = Gravity.START | Gravity.LEFT;
                      addContentView(playSView[i], params);
                      playSView[i].setBackgroundResource(R.drawable.framelayout_border);
                      playSView[i].getBackground().setAlpha(40);
                      playSView[i].setPadding(5, 5, 5, 5);
                      playSView[i].setZOrderOnTop(true);
                      playSView[i].setClickable(true);
                      playSView[i].startPreview(m_iLogID, Integer.parseInt(camera_channel[i]));

                      camera_count++;

                      final int j = i;

                  }

              }
              //  m_iPlayID = SingleplayView.m_iPreviewHandle;
              //progressDialog.dismiss();
              m_iPlayID = playSView[0].m_iPreviewHandle;
          }
      }else{
          int i;
          playSView = new PlaySurfaceView[totalCameras];
          int leftIndex = 0;
          int camera_count = 0;
          Log.d(TAG, "startMultiPreview totalCameras" + totalCameras);
          for (i = 0; i < camera_channel.length && camera_count < totalCameras; i++) {
              if (playSView[i] == null) {
                  playSView[i] = new PlaySurfaceView(this);
                  playSView[i].setParam(m_osurfaceView.getWidth(), m_osurfaceView.getHeight(), numberOfRows);
                  FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                          FrameLayout.LayoutParams.MATCH_PARENT,
                          FrameLayout.LayoutParams.MATCH_PARENT);
                  final ImageView header = (ImageView) findViewById(R.id.headerImage);
                  params.topMargin = (int) (Math.floor((double) (i / numberOfRows)) * playSView[i].getCurHeight());//+header.getHeight();
                  params.leftMargin = leftIndex * playSView[i].getCurWidth();
                  leftIndex++;
                  if (leftIndex % numberOfRows == 0)
                      leftIndex = 0;
                  params.gravity = Gravity.START | Gravity.LEFT;
                  addContentView(playSView[i], params);
                  playSView[i].setBackgroundResource(R.drawable.framelayout_border);
                  playSView[i].getBackground().setAlpha(40);
                  playSView[i].setPadding(5, 5, 5, 5);
                  playSView[i].setZOrderOnTop(true);
                  playSView[i].setClickable(true);
                  playSView[i].startPreview(m_iLogID, Integer.parseInt(camera_channel[v]));

                  camera_count++;

                  final int j = i;

              }

          }
          //  m_iPlayID = SingleplayView.m_iPreviewHandle;
          //progressDialog.dismiss();
          m_iPlayID = playSView[0].m_iPreviewHandle;
      }
    }


    public void ZoomOut(){
        m_iPort= playView[0].getCurrentPort();
//        Player.MPRect srcRect = new Player.MPRect();
//        srcRect.left =m_osurfaceView.getHeight() / 2;
//        srcRect.top = m_osurfaceView.getHeight() / 2;
//        srcRect.bottom = 10;
//        srcRect.right = 10;
        Player.getInstance().setDisplayRegion(m_iPort, 0, null, null,1);
    }
    public  void Test_PTZSelZoomIn()
	{
        m_iPort= playView[0].getCurrentPort();
        Player.MPRect srcRect = new Player.MPRect();
        srcRect.left =70;
        srcRect.top = 70;
        srcRect.bottom = m_osurfaceView.getHeight();
        srcRect.right = m_osurfaceView.getHeight();
        Player.getInstance().setDisplayRegion(m_iPort, 0, srcRect, null,1);


//      //  m_osurfaceView.
//        Display metric = getWindowManager().getDefaultDisplay();
//        Point mdispSize = new Point();
//        metric.getSize(mdispSize);
//		NET_DVR_POINT_FRAME strPointFrame = new NET_DVR_POINT_FRAME();
//    	strPointFrame.xTop = 0;
//    	strPointFrame.yTop = 0;
//    	strPointFrame.xBottom =  m_osurfaceView.getHeight();
//    	strPointFrame.yBottom = m_osurfaceView.getHeight();
//    	if(!HCNetSDK.getInstance().NET_DVR_PTZSelZoomIn(iPreviewID, strPointFrame))
//		{
//    		System.out.println("NET_DVR_PTZSelZoomIn!" + " err: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
//		}
//    	else
//    	{
//    		System.out.println("NET_DVR_PTZSelZoomIn! succeed");
//    	}
	}
    private void takeScreenshot() {

        m_iPort= playView[0].getCurrentPort();
        Log.e(TAG, String.valueOf(m_iPort));
        try {
            if (m_iPort < 0) {
                Log.e(TAG, "please start preview first");
                return;
            }
            Player.MPInteger stWidth = new Player.MPInteger();
            Player.MPInteger stHeight = new Player.MPInteger();
            if (!Player.getInstance().getPictureSize(m_iPort, stWidth, stHeight)) {
                Log.e(TAG, "getPictureSize failed with error code:" + Player.getInstance().getLastError(m_iPort));
                return;
            }
            int nSize =  stWidth.value * stHeight.value*3/2;
            byte[] picBuf = new byte[nSize*2];
            Player.MPInteger stSize = new Player.MPInteger();
           // getJPEG(int nPort, byte[] pJpeg, int nBufSize, MPInteger pJpegSize) ;
            if (!Player.getInstance().getJPEG(m_iPort, picBuf, nSize * 2, stSize)) {
                Log.e(TAG, "getBMP failed with error code:" + Player.getInstance().getLastError(m_iPort));
              //  return;
            }

            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
            String date = sDateFormat.format(new Date());
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + date + ".jpeg";
            FileOutputStream file = new FileOutputStream(mPath);
            file.write(picBuf, 0, stSize.value);
            file.close();
             imageFile = new File(mPath);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(imageFile); //out is your output file
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            } else {
                sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));
            }
           // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            new ImageUploadTask().execute();
           // openScreenshot(imageFile);
        }
        catch (Exception err)
        {
            Log.e(TAG, "error: " + err.toString());
        }


//        Date now = new Date();
//        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
//
//        try {
//            // image naming and path  to include sd card  appending name you choose for file
//            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
//
//            // create bitmap screen capture
//            View v1 = findViewById(R.id.widget_Play).getRootView();
//            v1.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//            v1.setDrawingCacheEnabled(false);
//
//            File imageFile = new File(mPath);
//
//            FileOutputStream outputStream = new FileOutputStream(imageFile);
//            int quality = 100;
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//            outputStream.flush();
//            outputStream.close();
//
//            openScreenshot(imageFile);
//        } catch (Throwable e) {
//            // Several error may come out with file handling or OOM
//            e.printStackTrace();
//        }
    }
    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
//        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(getPath(uri),
//                MediaStore.Images.Thumbnails.MINI_KIND);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
//    public static String getPath(Uri uri) {
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor =managedQuery(uri, projection, null, null, null);
//        int column_index = cursor
//                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
    private void stopMultiPreview()
    { if(totalCameras==1){
        playView[0].stopPreview();
    } else {
        int i;
        for (i = 0; i < camera_channel.length; i++) {
            playView[i].stopPreview();
        }
    }
    }
    private void Preview() {
        try
        {
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(viewBuildingCamera.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            if(m_iLogID < 0)
            {
                Log.e(TAG,"please login on device first");
                return ;
            }
            if(m_bNeedDecode)
            {
                if(m_iChanNum > 1)//preview more than a channel
                {
//                    if(!m_bMultiPlay)
//                    {
                    for(int i=0; i< camera_channel.length;i++){
                        Log.d(TAG+"--",camera_channel[i]);
                    }
                        startMultiPreview(0,0);
                        m_bMultiPlay = true;
//                    }
//                    else
//                    {
//                       // stopMultiPreview();
//                        //m_bMultiPlay = false;
//                        Log.d("ELSE CASE", "else++++++++");
//                    }
                }
                else	//preivew a channel
                {
                    if(m_iPlayID < 0)
                    {
                        startSinglePreview(1);
                }
                    else {
                        stopSinglePreview();
                    }
                }
            }
            else
            {

            }
        }
        catch (Exception err)
        {
            Log.e(TAG, "error: " + err.toString());
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    public void setPort(int port) {
        this.m_iPort = port;
    }

    class DVRLogin extends AsyncTask<String, String, String> {


        JSONObject jsonobj = new JSONObject();
        JSONObject json = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        JSONArray DVR = new JSONArray();
        JSONArray camera_info=new JSONArray();


        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(viewBuildingCamera.this);
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Creating guest
         * */
        protected String doInBackground(String... args) {
            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
            String building_id = (loginpref.getString("building_id", ""));
            final String url_camera_view = "https://portal.virtualdoorman.com/dev/common/libs/slim/building_dvr/"+building_id;

            json = jsonParser.makeHttpRequest(url_camera_view,
                    "GET",jsonobj);



            // check log cat for response
            Log.e("Cameras log entry : ", json.toString());

            return null;
        }

        public void onPause() {
            // super.onPause();
            if (pDialog != null) {
                // pDialog.dismiss();
            }
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if(pDialog!=null) {
                pDialog.dismiss();
            }

            String status = null;
            Log.e("API test response", json.toString());
            try {
                status = json.getString("status");


                if(status.equalsIgnoreCase("OK")) {


                    DVR=json.getJSONArray("DVR");
                   // int size = DVR.length();

                    camera_info=json.getJSONArray("CAMERAS");

                    camera_name = new String[camera_info.length()];
                     camera_channel = new String[camera_info.length()];
                    for(int i=0;i<camera_info.length(); i++){
                        JSONObject SingleCamera=camera_info.getJSONObject(i);
                        camera_name[i]=SingleCamera.getString("CAMERA_NAME");
                        camera_channel[i]=SingleCamera.getString("CAMERA_INDEX");
                    }

                        JSONObject singleguest = DVR.getJSONObject(0);
                        DVR_IP = singleguest.getString("DVR_IP") ;
                       DVR_PORT=singleguest.getInt("DVR_PORT");
                        DVR_Username=singleguest.getString("DVR_USER");
                        DVR_Paswd=singleguest.getString("DVR_PASSWORD");


                    if(m_iLogID < 0)
                    {
                        // login on the device
                        m_iLogID = loginDevice();
                        if (m_iLogID < 0)
                        {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    viewBuildingCamera.this);
                            TextView Msg = new TextView(viewBuildingCamera.this);
                            Msg.setText("No video is available at the moment.");
                            Msg.setGravity(Gravity.CENTER_HORIZONTAL);
                            //	Msg.setMaxHeight(100);
                            Msg.setTextSize(18);
                            alertDialogBuilder.setView(Msg);
                            TextView title = new TextView(viewBuildingCamera.this);
                            title.setText("Information");
                            title.setPadding(10, 10, 10, 10);
                            title.setGravity(Gravity.CENTER);
                            title.setTextColor(Color.BLACK);
                            title.setTextSize(20);
                            alertDialogBuilder.setCustomTitle(title);
                            alertDialogBuilder.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {

                                            Intent mainscreen = new Intent(getApplicationContext(),
                                                    ArrivalsActivity.class);
                                            startActivity(mainscreen);

                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            Log.e(TAG, "This device logins failed!");
                            return;
                        }
                        // get instance of exception callback and set
                        ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                        if (oexceptionCbf == null)
                        {
                            Log.e(TAG, "ExceptionCallBack object is failed!");
                            return ;
                        }

                        if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf))
                        {
                            Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
                            return;
                        }

//                m_oLoginBtn.setText("Logout");
                        Log.i(TAG, "Login sucess ****************************1***************************");
                        Preview();
                      }
//                    else
//                    {
//                        // whether we have logout
//
//                    }



//
//                    }
//                    DVR.getString(0);


                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



//            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view,
//                                        int position, long id) {
//
//                    Intent intent = new Intent(callLogs.this, DeliveryLogDetail.class);
//                    intent.putExtra("id", logid[position]);
//                    startActivity(intent);
//                }
//            });

        }

    }

    public void Cleanup()
    {
        // release player resource

        Player.getInstance().freePort(m_iPort);
        m_iPort = -1;

        // release net SDK resource
        HCNetSDK.getInstance().NET_DVR_Cleanup();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:

                stopSinglePlayer();
                Cleanup();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            default:
                break;
        }

        return true;
    }
    class ImageUploadTask extends AsyncTask<Void, Void, String> {
        String sResponse = null;

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(viewBuildingCamera.this);
            pDialog.setMessage("Uploading Photo..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... unsued) {
            // String sResponse = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();

                HttpPost httppost = new HttpPost(url_image_upload);
                MultipartEntity entity = new MultipartEntity();

                entity.addPart("person_id", Person_id);
                entity.addPart("files", imageFile);
                httppost.setEntity(entity);
                HttpResponse response = httpClient.execute(httppost,
                        localContext);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent(), "UTF-8"));

                sResponse = reader.readLine();

            } catch (Exception e) {
                e.printStackTrace();

            }
            return sResponse;

        }

        @Override
        protected void onProgressUpdate(Void... unsued) {

        }

        @Override
        protected void onPostExecute(String sResponse) {
            pDialog.dismiss();
            try {
                JSONObject JResponse = new JSONObject(sResponse);
                String response = JResponse.getString("response");
                if (response
                        .equalsIgnoreCase("Image has been uploaded successfully")) {
                    Toast.makeText(getApplicationContext(), response,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Some Error Occured", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error at server side",
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
    }
}