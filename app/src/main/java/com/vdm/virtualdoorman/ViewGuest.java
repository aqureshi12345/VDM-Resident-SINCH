package com.vdm.virtualdoorman;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewGuest extends AppCompatActivity {
    public static final String LOGINPREF = "LoginPrefs";
    String login_guid = null;
    String guestID ;

    TextView userName;
    TextView mobileValue;
    TextView permissionfromValue;
    TextView secretQuestionValue;
    TextView passcodeValue;
    TextView permissionTimeValue;
    ImageView deleteIcon;
    String guest_id;
    Activity context_del;
    TextView editbtn;

    public void deletetList(String id, Activity context2) {
        // TODO Auto-generated method stub
        guest_id = id;
        context_del = context2;
        new deletetGuest().execute();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_guest);
        Intent intent = getIntent();
        guestID = intent.getStringExtra("id");
        login_guid = intent.getStringExtra("login_guid");
        TextView save=(TextView)findViewById(R.id.editbutton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), EditGuest.class);
                startActivity(intent);

            }
        });
        RelativeLayout backbutton=(RelativeLayout)findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ArrivalsActivity.class);
                startActivity(intent);
            }
        });

        userName = (TextView) findViewById(R.id.username);
        mobileValue = (TextView) findViewById(R.id.mobileValue);
        permissionfromValue = (TextView) findViewById(R.id.permissionfromValue);
        secretQuestionValue = (TextView) findViewById(R.id.questionValue);
        passcodeValue = (TextView) findViewById(R.id.codeValue);
        permissionTimeValue = (TextView) findViewById(R.id.permissionTimeValue);
        deleteIcon = (ImageView) findViewById(R.id.buttonimage);
        editbtn = (TextView) findViewById(R.id.editbutton);
       // editbtn.setOnClickListener();
        //	getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //		getSupportActionBar().setDisplayShowCustomEnabled(true);
        //	getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        //	View view =getSupportActionBar().getCustomView();

        new getGuestInfo().execute();


		/*Intent intent = getIntent();
		editguest = (WebView) findViewById(R.id.webview);
		loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);

		login_guid = (loginpref.getString("login_guid", ""));

		editguest.loadUrl("https://portal.virtualdoorman.com/dev/iphone/update_visitor.php?login_guid="
						+ login_guid
						+ "&guest_id="
						+ intent.getStringExtra("id"));
		editguest.setWebChromeClient(new WebChromeClient());
		editguest.getSettings().setLoadsImagesAutomatically(true);
		editguest.getSettings().setJavaScriptEnabled(true);
		editguest.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);*/

    }
    @Override
    public void onBackPressed() {
        Intent guestList = new Intent(getApplicationContext(),
                ArrivalsActivity.class);
		/*mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mainscreen.putExtra("EXIT", true);*/
        startActivity(guestList);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    class getGuestInfo extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        JSONObject jsonobj = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        JSONObject json= new JSONObject();

        private final String url_get_guest_list = "https://portal.virtualdoorman.com/dev/resident-api/guest_detail/";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewGuest.this);
            pDialog.setMessage("Loading Guest Information...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {

            try {
                jsonobj.put("newApi", true);
                jsonobj.put("login_guid", login_guid);
                jsonobj.put("guest_id",guestID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json = jsonParser.makeHttpRequest(url_get_guest_list,
                    "GET", jsonobj);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            try {
                String status = json.getString("status");
                JSONArray resultArray = json.getJSONArray("result");
                JSONObject result = resultArray.getJSONObject(0);

                String Name = result.getString("FIRST_NAME")+" "+result.getString("LAST_NAME");
                String mobileNo = result.getString("PHONE");
                String question = result.getString("SECRET_QUESTION");
                String code = result.getString("PASSCODE");
                JSONObject permissionObj = result.getJSONObject("permissions");
                String permissionDay = permissionObj.getString("permission_day");
                String permissionTime = permissionObj.getString("permission_time");

                String[] separated = permissionDay.split(",");
                String[] separatedtime = permissionTime.split(",");
                SimpleDateFormat input = new SimpleDateFormat("dd/MM/yy");
                SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy");
                SimpleDateFormat timeinput = new SimpleDateFormat("hh/mm");
                SimpleDateFormat timeoutput = new SimpleDateFormat("hh:mm aaa");
                Date oneWayTripDate = null;
                Date oneWayTripDate2 = null ;

                Date oneWayTripDate3 = null;
                Date oneWayTripDate4 = null ;

                String date1 = null;
                String date2 = null;

                String date3 = null;
                String date4 = null;
                final String id = result.getString("guest_id");
                try {
                    oneWayTripDate = input.parse(separated[0].trim());
                    oneWayTripDate2 = input.parse(separated[1].trim());

                    oneWayTripDate3 = timeinput.parse(separatedtime[0].trim());
                    oneWayTripDate4 = timeinput.parse(separatedtime[1].trim());

                    date1 = output.format(oneWayTripDate);
                    date2 = output.format(oneWayTripDate2);

                    date3 = timeoutput.format(oneWayTripDate3);
                    date4 = timeoutput.format(oneWayTripDate4);




                } catch (ParseException e) {
                    e.printStackTrace();
                }


                //  String permission = result.getString("");
                userName.setText(Name);
                mobileValue.setText(mobileNo);
                secretQuestionValue.setText(question);
                passcodeValue.setText(code);
                permissionfromValue.setText(date3 +" "+ date1);
                permissionTimeValue.setText(date4 +" "+ date2);

                deleteIcon.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // your code here
                        deletetList(id,ViewGuest.this);

                    }
                });
                editbtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // your code here
                        Intent intent = new Intent(getApplicationContext(), EditGuest.class);
                        intent.putExtra("id", id);
                        intent.putExtra("login_guid", login_guid);
                        startActivity(intent);
                        finish();

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    class deletetGuest extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        JSONObject jsonobj = new JSONObject();
        JSONParser jsonParser = new JSONParser();

        JSONObject json;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context_del);
            pDialog.setMessage("Deleting Guest..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating guest
         * */
        protected String doInBackground(String... args) {
            Global LoginGuid;
            LoginGuid = new Global();
            // String login = LoginGuid.g;
            // String login = LoginGuid.loginID;
            String login = LoginGuid.getLoginDetail();
            final String url_del_guest = "https://portal.virtualdoorman.com/dev/iphone/iphone_requests.php?function=delete_visitor&login_guid="
                    + login;
            try {
                jsonobj.put("rid", guest_id);

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            json = jsonParser.makeHttpRequest(url_del_guest, "GET", jsonobj);

            // check log cat for response
            Log.e("Create Response", json.toString());

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
        protected void onPostExecute(String s) {

            pDialog.dismiss();
            String response = null;
            try {
                response = json.getString("response");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (response.equalsIgnoreCase("Gueset deleted successfully")) {
                Intent intent = new Intent(context_del, ArrivalsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                context_del.startActivity(intent);
                Toast.makeText(context_del, response, Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(context_del, EditGuest.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                context_del.startActivity(intent);
                Toast.makeText(getApplicationContext(), "Some Error Occured",
                        Toast.LENGTH_SHORT).show();

            }

        }

    }


}
