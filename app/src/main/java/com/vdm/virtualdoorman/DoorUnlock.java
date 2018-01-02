package com.vdm.virtualdoorman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.calling.Call;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.transform.Result;

/**
 * Created by Amna joyia on 12/1/2015.
 */
public class DoorUnlock  extends Activity  {
    //implements SeekBar.OnSeekBarChangeListener,View.OnClickListener

    SharedPreferences loginpref;
    public static final String LOGINPREF = "LoginPrefs";
    //TextView seekbartext;
    String[] doorStationList;
    String[] stationId;
    int[] unlockTime;
    int screenSize;
    String doorUnlock_status;
    int time_to_delay;
   TextView seekbarTextGlobal;
    SeekBar seekbarGlobal;
    RelativeLayout backbutton;
    String VstationNameGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.door_unlock);
        new DoorstationList().execute();
         screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
         backbutton=(RelativeLayout)findViewById(R.id.backbutton);
         backbutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(getApplicationContext(), MyBuildingContacts.class);
                 startActivity(intent);
                 finish();
             }
         });

//        String toastMsg;
//        switch(screenSize) {
//            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
//                toastMsg = "XtraLarge screen";
//                break;
//            case Configuration.SCREENLAYOUT_SIZE_LARGE:
//                toastMsg = "Large screen";
//                break;
//            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
//                toastMsg = "Normal screen";
//                break;
//            case Configuration.SCREENLAYOUT_SIZE_SMALL:
//                toastMsg = "Small screen";
//                break;
//            default:
//                toastMsg = "Screen size is neither large, normal or small";
//        }
//        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_logout) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    class UnlockDoor extends AsyncTask<String, String, String> {
    //    private static final String CALLRECORDPREFERENCES = "Calrecordpref";
        JSONObject json = new JSONObject();
        private ProgressDialog pDialog;
        JSONObject jsonobj = new JSONObject();
        JSONParser jsonParser = new JSONParser();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DoorUnlock.this);
            pDialog.setMessage("Unlocking Door...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... params) {
            String door_id=params[0];
            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
            String building_id = (loginpref.getString("building_id", ""));
            final String door_unlock = "https://portal.virtualdoorman.com/dev/common/libs/slim/unlock_door/"+building_id+"/"+door_id;
//
            // Note that create guest url accepts POST method
            Log.e("json is+++++", jsonobj.toString());
            json = jsonParser.makeHttpRequest(door_unlock, "GET", jsonobj);

            // check log cat for response
            // Log.e("Create Response", json.toString());


            return null;
        }

        public void onPause() {
            // super.onPause();
            if (pDialog != null) {
                 pDialog.dismiss();
            }
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String res) {
            // dismiss the dialog once done
          pDialog.dismiss();
            try {

                doorUnlock_status = json.getString("status");
                String response = json.getString("message");
                if (doorUnlock_status.equalsIgnoreCase("OK")) {
                    seekbarTextGlobal.setText("");
                    seekbarTextGlobal.setText("Unlocked");
                     time_to_delay=json.getInt("pulse_time");
                    Timer seekbarTimer = new Timer();
                    seekbarTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                                        seekbarGlobal.setProgress(0);
                                    } else {
                                        seekbarGlobal.setProgress(2);
                                    }
                                    seekbarTextGlobal.setText(VstationNameGlobal);
                                }
                            });
                        }
                    }, time_to_delay * 1000);
                   // DoorstationList list=new DoorstationList();

                }
                else{
                    if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                        seekbarGlobal.setProgress(0);
                    } else {
                        seekbarGlobal.setProgress(2);
                    }
                    seekbarTextGlobal.setText(VstationNameGlobal);
                    Toast.makeText(getApplicationContext(), response,
                            Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {

                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }

    public String getResponse() {
       return doorUnlock_status;
    }
}


    class DoorstationList extends AsyncTask<String, String, String> {
        JSONObject json = new JSONObject();
        private ProgressDialog pDialog;
        JSONObject jsonobj = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DoorUnlock.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected String doInBackground(String... args) {

            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
            String building_id = (loginpref.getString("building_id", ""));
            String resident_id=(loginpref.getString("resident_id", ""));

            final String vstation_list = "https://portal.virtualdoorman.com/dev/common/libs/slim/unlock_door_list/"+building_id+"/"+resident_id;


			/*
			 * try { jdata.put("data", jsonobj); } catch (JSONException e1) { //
			 * TODO Auto-generated catch block e1.printStackTrace(); }
			 */

            // getting JSON Object
            // Note that create guest url accepts POST method
            Log.e("json is+++++", jsonobj.toString());
            json = jsonParser.makeHttpRequest(vstation_list, "GET", jsonobj);

            // check log cat for response
            // Log.e("Create Response", json.toString());


            return null;
        }

        public void onPause() {
            // super.onPause();
//            if (pDialog != null) {
//                // pDialog.dismiss();
//            }
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String res) {
            // dismiss the dialog once done
           pDialog.dismiss();

           // seekBar.isClickable(false);
            try {

                String status = json.getString("status");
                JSONArray data = json.getJSONArray("vstations");
                if(status.equalsIgnoreCase("OK")){
                    int size = data.length();
                    doorStationList = new String[size];
                    stationId = new String[size];
                    unlockTime =new int[size];
                     int i;
                    for (i = 0; i < data.length(); i++) {

                        JSONObject singleguest = data.getJSONObject(i);
                        doorStationList[i] = singleguest.getString("DOORSTATION_NAME") ;
                        final String doorStation_Name= doorStationList[i];
                        stationId[i] = singleguest.getString("DOORSTATION_ID");
                       final String doorstation_id= stationId[i];
                        unlockTime[i]=singleguest.getInt("DOOR_UNLOCK_TIME");
                        final int time_to_unlock= unlockTime[i];
                        LinearLayout layout=(LinearLayout) findViewById(R.id.seekbarHolder);
                        RelativeLayout relativeLayout= new RelativeLayout(DoorUnlock.this);
                        RelativeLayout.LayoutParams dimensions = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                     //   dimensions.setMargins(20,0,0,0);
                        relativeLayout.setLayoutParams(dimensions);

                        SeekBar seekBar = new SeekBar(DoorUnlock.this);
                        seekBar.setMax(100);
                        Drawable thumb = getResources().getDrawable(R.drawable.thumb);
                        seekBar.setThumb(thumb);

                       // seekBar.setBackground(getResources().getDrawable(R.drawable.vdm_bar));
                        seekBar.setBackgroundColor(getResources().getColor(R.color.greyish));

                        RelativeLayout.LayoutParams params = null;

                        if(screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE){
                            params = new RelativeLayout.LayoutParams(
                                    300,
                                    35);
                            seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));

                        }
                       else if(screenSize==Configuration.SCREENLAYOUT_SIZE_LARGE ||screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE){
                            params = new RelativeLayout.LayoutParams(
                                   300,
                                    50);
                            seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));
                        } else {
                            params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));

                             seekBar.setProgress(2);
                          //  seekBar.setProgressDrawable(getDrawable(R.drawable.thumb));

                        }
                         params.setMargins(50, 50, 50, 30);
                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        params.addRule(RelativeLayout.CENTER_VERTICAL);
                        seekBar.setLayoutParams(params);

                      seekBar.setProgressDrawable(new ColorDrawable(Color.TRANSPARENT));

                        final TextView seekbartext = new TextView(DoorUnlock.this);
                        RelativeLayout.LayoutParams parameters = new RelativeLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                      // parameters.setMargins(0, 20, 0, 0);
                        seekbartext.setText(doorStationList[i]);
                        seekbartext.setTextSize(12);
                        parameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        parameters.addRule(RelativeLayout.CENTER_VERTICAL);
                        seekbartext.setLayoutParams(parameters);
                        seekbartext.setTextColor(Color.DKGRAY);

                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                            public void onClick(View v) {
//
                            }


                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (progress > 97) {

                                    seekBar.setThumb(getResources().getDrawable(R.drawable.thumb));
                                }
                            }


                            public void onStartTrackingTouch(SeekBar seekBar) {
                                // seekbartext.setVisibility(View.INVISIBLE);

                            }


                            public void onStopTrackingTouch(final SeekBar seekBar) {
                                Log.d("onStopTrackingTouch", "onStopTrackingTouch");
                                seekbarTextGlobal = seekbartext;
                                if (seekBar.getProgress() < 98) {
                                    if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                                        seekBar.setProgress(0);
                                    } else {
                                        seekBar.setProgress(2);
                                    }
                                    //  Toast.makeText(CallScreenActivity.this, "slide to unlock <80", Toast.LENGTH_LONG).show();
                                } else {
                                    if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE||screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                                        seekBar.setProgress(100);
                                    } else {

                                        seekBar.setProgress(98);
                                    }
                                    seekbartext.setText("Unlocking");

                                    try {
                                        seekbarGlobal=seekBar;
                                        VstationNameGlobal=doorStation_Name;
                                        String result = new UnlockDoor().execute(doorstation_id).get();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }

                                }


                            }
//                            }

                        });
                        relativeLayout.addView(seekBar);
                        relativeLayout.addView(seekbartext);
                        layout.addView(relativeLayout);

                    }


                }

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }



        }
    }

}

