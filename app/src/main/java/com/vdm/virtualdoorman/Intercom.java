package com.vdm.virtualdoorman;

import com.sinch.android.rtc.calling.Call;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;


//Drawer imports
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.support.v4.app.ActivityCompat;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Intercom extends BaseActivity implements ServiceConnection {
    ListView list;

    //Drawer Items//

    private NavigationView navigationView;
    private Toolbar toolbar;
    private String[] activityTitles;
    private DrawerLayout drawer;
    private View navHeader;

    private  SinchService.SinchServiceInterface mSinchServiceInterface;
    SharedPreferences loginpref;
    public static final String LOGINPREF = "LoginPrefs";
    String[] vstationList;
    String[] doorstationID;
    Intent nav_intent;
    Integer[] imageId;
    String[] vstationId;
    Intent logout;
    MainScreen mainScreen;
    CustomVstationList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_option);

        ImageView arrivals=(ImageView)findViewById(R.id.arrivals);
        ImageView intercom=(ImageView)findViewById(R.id.intercom);
        ImageView buildings=(ImageView)findViewById(R.id.buildings);
        ImageView logs=(ImageView)findViewById(R.id.logs);



        //Drawer code
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Intercom");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        navigationView.bringToFront();
        setUpNavigationView(navigationView);


        arrivals= (ImageView) findViewById(R.id.arrivals);
        arrivals.setOnClickListener(new Button.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                Intent Call_Option = new Intent(getApplicationContext(), ArrivalsActivity.class);
                startActivity(Call_Option);
                finish();
                //imgButton_intercom.setBackground(getResources().getDrawable(R.drawable.speaker_selected));


            }
        });
        //imgButton_service = (ImageButton) findViewById(R.id.service);
        logs=(ImageView)findViewById(R.id.logs);
        logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logs_intent=new Intent(getApplicationContext(), logs.class);
                startActivity(logs_intent);
            }
        });

        buildings=(ImageView)findViewById(R.id.buildings);
        buildings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buidlings_intent=new Intent(getApplicationContext(), MyBuildingContacts.class);
                startActivity(buidlings_intent);
            }
        });

        getApplicationContext().bindService(new Intent(this, SinchService.class), Intercom.this,
                BIND_AUTO_CREATE);
        new vstationList().execute();




//        Button btnoperator = (Button) findViewById(R.id.operator);
//        btnoperator.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                new callOperator().execute();
//            }
//        });
//
//        Button btnvstation = (Button) findViewById(R.id.Vstation);
//        btnvstation.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Call call = getSinchServiceInterface().callUser("vdm-11-25-2");
//                String callId = call.getCallId();
//                Intent callScreen = new Intent(Intercom.this, CallScreenActivity.class);
//                callScreen.putExtra(SinchService.CALL_ID, callId);
//                startActivity(callScreen);
//            }
//        });
    }
    //Drawer Method
    private void setUpNavigationView(NavigationView navigationView) {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;

                    case R.id.account:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(getApplicationContext(), MyAccount.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.ringtone:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(getApplicationContext(), RingerMode.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.red_hat_concierge:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(getApplicationContext(), RedHat.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.contacts:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(getApplicationContext(), ContactMyVdm.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.about:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(getApplicationContext(), AboutUs.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.logout:
                        final Context context=getApplicationContext();
                        if(isNetworkAvailable(context)==true) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Intercom.this);
                            builder.setMessage("Are you sure you want to Logout?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if(BaseActivity.getSinchServiceInterface_new()!=null){
                                                BaseActivity.getSinchServiceInterface_new().stopClient();
                                            }
                                            logout = new Intent(context,
                                                    Login.class);
                                            logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            logout.putExtra("EXIT", "true");
                                            startActivity(logout);
                                        }})
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else{
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    getApplicationContext());
                            TextView Msg = new TextView(getApplicationContext());
                            Msg.setText("Network is not available at the moment. Please wait");
                            Msg.setGravity(Gravity.CENTER_HORIZONTAL);
                            //	Msg.setMaxHeight(100);
                            Msg.setTextSize(18);
                            alertDialogBuilder.setView(Msg);
                            TextView title = new TextView(getApplicationContext());
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

                                            Intent mainscreen = new Intent(Intercom.this,
                                                    ArrivalsActivity.class);
                                            startActivity(mainscreen);
                                            finish();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                        return true;

                    default:

                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);



                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
    //Drawer method
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //Drawer method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.arrivals, menu);
        return true;
    }
    //Drawer method
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (SinchService.class.getName().equals(name.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) service;
            onServiceConnected();
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected  SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }


    //    class callOperator extends AsyncTask<String, String, String> {
//        JSONObject json = new JSONObject();
//        private ProgressDialog pDialog;
//        JSONObject jsonobj = new JSONObject();
//        JSONParser jsonParser = new JSONParser();
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(Intercom.this);
//            pDialog.setMessage("Calling Operator..");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
//        }
//        protected String doInBackground(String... args) {
//
//            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
//            String resident_id = (loginpref.getString("resident_id", ""));
//
//            final String cal_operator = "https://portal.virtualdoorman.com/dev/common/libs/slim/operator_to_resident_call/"+resident_id;
//
//
//			/*
//			 * try { jdata.put("data", jsonobj); } catch (JSONException e1) { //
//			 * TODO Auto-generated catch block e1.printStackTrace(); }
//			 */
//
//            // getting JSON Object
//            // Note that create guest url accepts POST method
//            Log.e("json is+++++", jsonobj.toString());
//            json = jsonParser.makeHttpRequest(cal_operator, "GET", jsonobj);
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
//            pDialog.dismiss();
//            try {
//
//                String status = json.getString("status");
//                String response = json.getString("message");
//                if (status.equalsIgnoreCase("OK")) {
//                    Toast.makeText(getApplicationContext(), response,
//                            Toast.LENGTH_LONG).show();
//
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
//                    Toast.makeText(getApplicationContext(),
//                            "Some Error Occured", Toast.LENGTH_SHORT).show();
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
    class vstationList extends AsyncTask<String, String, String> {
        JSONObject json = new JSONObject();
        private ProgressDialog pDialog;
        JSONObject jsonobj = new JSONObject();
        JSONParser jsonParser = new JSONParser();

        //ImageView imgButton_intercom=(ImageView) findViewById(R.id.intercom);
        //ImageView imgButton_service=(ImageView) findViewById(R.id.service);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Intercom.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
            String building_id = (loginpref.getString("building_id", ""));

            final String vstation_list = "https://portal.virtualdoorman.com/dev/common/libs/slim/vstations_list/" + building_id;


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
            try {

                String status = json.getString("status");
                JSONArray data = json.getJSONArray("vstations");
                if (status.equalsIgnoreCase("OK")) {
                    int size = data.length();
                    vstationList = new String[size];
                    imageId = new Integer[size];
                    doorstationID = new String[size];
                    vstationId = new String[size];

                    for (int i = 0; i < data.length(); i++) {

                        JSONObject vstation_List = data.getJSONObject(i);
                        vstationList[i] = vstation_List.getString("VSTATION_NAME");
                        vstationId[i] = vstation_List.getString("vstation_id");
                        doorstationID[i] = vstation_List.getString("DOORSTATION_ID");
                        imageId[i] = R.drawable.phone_icon;


                    }
                    adapter = new CustomVstationList(Intercom.this, vstationList,
                            imageId, vstationId);


                }

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

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
            list = (ListView) findViewById(R.id.Vstatiolist);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (vstationId[position].equalsIgnoreCase("vdm.operator")) {
                        loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
                        String vdm_operator_number = (loginpref.getString("operator_phoneNo", ""));
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + vdm_operator_number));
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(callIntent);
                    } else {
                        Log.e("vstaionid+++++++++===================", vstationId[position]);
                        Call call = getSinchServiceInterface().callUserVideo(vstationId[position]);
                        String callId = call.getCallId();
                        Intent callScreen = new Intent(Intercom.this, OutgoingCallScreen.class);
                        callScreen.putExtra(SinchService.CALL_ID, callId);
                        callScreen.putExtra("VSTATION_NAME", vstationList[position]);
                        callScreen.putExtra("DoorStation_id", doorstationID[position]);
                        startActivity(callScreen);
                    }

                }
            });

//            try {
//
//                String status = json.getString("status");
//                JSONArray vstations= new JSONArray();
//                vstations=json.getJSONArray("vstations");
//                if (status.equalsIgnoreCase("OK")) {
//                    for(int n = 0; n < vstations.length(); n++)
//                    {
//                        JSONObject object = vstations.getJSONObject(n);
//                        final String vstation_name=object.getString("VSTATION_NAME");
//                        final String vstation_id=object.getString("vstation_id");
//
//                        LinearLayout layout=(LinearLayout) findViewById(R.id.list_of_vstation);
//                        Button vstation=new Button (Intercom.this);
//                        vstation.setText(vstation_name);
//                        vstation.setBackground(getResources().getDrawable(R.drawable.mybutton));
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                              350,
//                                LinearLayout.LayoutParams.WRAP_CONTENT);
//                        params.setMargins(0, 0, 0, 30);
//                        vstation.setLayoutParams(params);
//                        layout.addView(vstation);
//                        vstation.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//             if(vstation_id.equalsIgnoreCase("vdm.operator")){
//                    new callOperator().execute();
//                } else {
//                    Call call = getSinchServiceInterface().callUser(vstation_id);
//                    String callId = call.getCallId();
//                    Intent callScreen = new Intent(Intercom.this, CallScreenActivity.class);
//                    callScreen.putExtra(SinchService.CALL_ID, callId);
//                    startActivity(callScreen);
//                }
//            }
//        });
//
//                    }
//
//                }
//                else {
//                    Toast.makeText(getApplicationContext(),
//                            "Some Error Occured", Toast.LENGTH_SHORT).show();
//
//                }
//
//            } catch (Exception e) {
//
//                Log.e(e.getClass().getName(), e.getMessage(), e);
//            }
//



        }
    }
}
