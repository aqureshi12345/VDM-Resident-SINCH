package com.vdm.virtualdoorman;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;

import org.MediaPlayer.PlayM4.Player;

@SuppressLint("NewApi")
public class PlaySurfaceView extends SurfaceView implements Callback {

    private final String TAG = "PlaySurfaceView";
    private int m_iWidth = 0;
    private int m_iHeight = 0;
    public int m_iPreviewHandle = -1;
    private int m_iPort = -1;
    private boolean m_bSurfaceCreated = false;
    private static final int INVALID_POINTER_ID = -1;
    public Bitmap mMyChracter;
    private float mPosX;
    private float mPosY;
    private SurfaceHolder holder;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    int surface_width,surface_height;
    View currentView;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private float focusX;
    private float focusY;

    private float lastFocusX = -1;
    private float lastFocusY = -1;

    static final int IMG_WIDTH = 640;
    static final int IMG_HEIGHT = 480;

    static final int IMAGE_X_POS = 560;
    static final int IMAGE_Y_POS = 20;

    Matrix matrix;
    float sy;
    float sx;
    public static Context context;
    Paint paint = new Paint();
    public PlaySurfaceView(viewBuildingCamera building_camera_view) {
        super((Context) building_camera_view);
        // TODO Auto-generated constructor stub
        getHolder().addCallback(this);
      // holder=getHolder();
        mScaleDetector = new ScaleGestureDetector(building_camera_view, new ScaleListener());


    }





    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        System.out.println("surfaceChanged");
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        m_bSurfaceCreated = true;
        holder=arg0;
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        if (-1 == m_iPort) {
            return;
        }
        Surface surface = arg0.getSurface();
        if (true == surface.isValid()) {
            if (false == Player.getInstance().setVideoWindow(m_iPort, 0, arg0)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        m_bSurfaceCreated = false;
        if (-1 == m_iPort) {
            return;
        }
        if (true == arg0.getSurface().isValid()) {
            if (false == Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.setMeasuredDimension(m_iWidth - 1, m_iHeight - 1);
    }

    //	public void setSingleParam( int nScreenSize,int HeightPixels)
//	{
//		m_iWidth = nScreenSize*2;
//		m_iHeight = HeightPixels*2;
////		m_iWidth = nScreenSize / 3;
////		m_iHeight = (m_iWidth * 3) / 2;
//	}
    public void setParam(int nScreenSize, int HeightPixels, int rows) {
         surface_width = nScreenSize;
        surface_height=HeightPixels;
        m_iWidth = nScreenSize / rows;
        m_iHeight = HeightPixels / rows;
//		m_iWidth = nScreenSize / 4;
//		m_iHeight = (m_iWidth * 3) / 2;
    }

    public int getCurWidth() {
        return m_iWidth;
    }

    public int getCurHeight() {
        return m_iHeight;
    }

    public int startPreview(int iUserID, int iChan)
    {
        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        if (fRealDataCallBack == null)
        {
            Log.e(TAG, "fRealDataCallBack object is failed!");
            return iUserID;
        }
        Log.i(TAG, "preview channel:" + iChan);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = iChan;
        previewInfo.dwStreamType = 0; //substream
        previewInfo.bBlocked = 1;
        // HCNetSDK start preview
        m_iPreviewHandle = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iUserID, previewInfo, fRealDataCallBack);
        if (m_iPreviewHandle < 0)
        {

            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        return 1;
    }
    public int getCurrentPort(){

        return m_iPort;
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            // float x = detector.getFocusX();
            // float y = detector.getFocusY();

            lastFocusX = -1;
            lastFocusY = -1;

            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            focusX = detector.getFocusX();
            focusY = detector.getFocusY();

            if (lastFocusX == -1)
                lastFocusX = focusX;
            if (lastFocusY == -1)
                lastFocusY = focusY;

            mPosX += (focusX - lastFocusX);
            mPosY += (focusY - lastFocusY);
            Log.v("Hi Zoom", "Factor:"  + mScaleFactor);
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 2.0f));

            lastFocusX = focusX;
            lastFocusY = focusY;

            invalidate();
            return true;
        }

    }



    public void stopPreview() {
        HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPreviewHandle);
        stopPlayer();
    }

    private void stopPlayer() {
        Player.getInstance().stopSound();
        // player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            Log.e(TAG, "stop is failed!");
            return;
        }

        if (!Player.getInstance().closeStream(m_iPort)) {
            Log.e(TAG, "closeStream is failed!");
            return;
        }
        if (!Player.getInstance().freePort(m_iPort)) {
            Log.e(TAG, "freePort is failed!" + m_iPort);
            return;
        }
        m_iPort = -1;
    }

    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
                processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }

    private void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        //   Log.i(TAG, "iPlayViewNo:" + iPlayViewNo + ",iDataType:" + iDataType + ",iDataSize:" + iDataSize);
        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
            if (m_iPort >= 0) {
                return;
            }
            m_iPort = Player.getInstance().getPort();
            if (m_iPort == -1) {
                Log.e(TAG, "getPort is failed with: " + Player.getInstance().getLastError(m_iPort));
                return;
            }
            Log.i(TAG, "getPort succ with: " + m_iPort);
            if (iDataSize > 0) {
                if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
                {
                    Log.e(TAG, "setStreamOpenMode failed");
                    return;
                }
                if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) //open stream
                {
                    Log.e(TAG, "openStream failed");
                    return;
                }
                while (!m_bSurfaceCreated) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Log.i(TAG, "wait 100 for surface, handle:" + iPlayViewNo);
                }

                if (!Player.getInstance().play(m_iPort, getHolder())) {
                    Log.e(TAG, "play failed,error:" + Player.getInstance().getLastError(m_iPort));
                    return;
                }
                if (!Player.getInstance().playSound(m_iPort)) {
                    Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
                    return;
                }
            }
        } else {
            if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
                Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.

        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {

                final float x = ev.getX() / mScaleFactor;
                final float y = ev.getY() / mScaleFactor;
                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = ev.getPointerId(0);

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex) / mScaleFactor;
                final float y = ev.getY(pointerIndex) / mScaleFactor;

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {

                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;
                    mPosX += dx;
                    mPosY += dy;

                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {

              Log.e(TAG,"OnTouch+++++++++++");
               // mActivePointerId = INVALID_POINTER_ID;
                return false;
              //  break;

            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = (ev.getAction() &    MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex) / mScaleFactor;
                    mLastTouchY = ev.getY(newPointerIndex) / mScaleFactor;
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }
    public void onDraw (Canvas canvas) {

        super.onDraw(canvas);
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        // canvas.save();
        Player.MPRect struRect = new Player.MPRect();
       float scale_factor = mScaleFactor;
//
//        int new_top = (int)((scale_factor * -1 * surface_height) + surface_height);
//        int new_left = (int)((scale_factor * -1 * surface_width)+ surface_width);
//
//
//        int new_right = (int)(scale_factor * surface_width);
//        int new_bottom = (int)(scale_factor * surface_height);
//
////        new_left = new_top ;
////        new_bottom = new_right;
//        struRect.left =  (int) (focusX/new_left);//(int)(struRect.left * mScaleFactor-1);//(int)(focusX/mScaleFactor);
//        struRect.top = (int) (focusY/new_top);//(int)(struRect.top * mScaleFactor-1); //(int)(focusY/mScaleFactor);
//
//        struRect.right = new_right;//(int)(struRect.right * mScaleFactor);//(int)focusX;
//        struRect.bottom =  new_bottom;//(int)(struRect.bottom * mScaleFactor);//(int)focusY;
        struRect.left = (int)(focusX/scale_factor);
        struRect.top = (int)(focusY/scale_factor);
        struRect.right = (int)focusX;
        struRect.bottom = (int)focusY;
        if(!Player.getInstance().setDisplayRegion(m_iPort, 0, struRect, getHolder(), 1))
        {
            Log.e("Preview onDraw", "setDisplayRegion failed");
        }
        canvas.scale(mScaleFactor, mScaleFactor, focusX, focusY);
        canvas.translate(mPosX, mPosY);
       // canvas.drawText("HikVision", 120, 240, paint);
        // canvas.restore();
    }
}
