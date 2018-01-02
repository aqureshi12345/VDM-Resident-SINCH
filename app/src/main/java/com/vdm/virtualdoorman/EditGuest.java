package com.vdm.virtualdoorman;

import com.vdm.virtualdoorman.R;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EditGuest extends Activity {
    private WebView editguest;
    SharedPreferences loginpref;

    public static final String LOGINPREF = "LoginPrefs";
    String login_guid = null;
    String guestID;

    EditText firstName;
    EditText lastName;
    EditText phone;
    Switch check;
    TextView questionText;
    EditText secret_question;
    TextView answer;
    EditText passcode_answer;
    public static final String ADDPERSONPREFERENCES = "AddPersonPrefs";
    SharedPreferences Addpersonpref;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_guest);
        Intent intent = getIntent();
        guestID = intent.getStringExtra("id");
        login_guid = intent.getStringExtra("login_guid");
        RelativeLayout backbutton=(RelativeLayout)findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewGuest.class);
                startActivity(intent);
                finish();
            }
        });
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        phone = (EditText) findViewById(R.id.phone);
        check = (Switch) findViewById(R.id.Switch);
        questionText = (TextView) findViewById(R.id.question);
        secret_question = (EditText) findViewById(R.id.secret_question);
        answer = (TextView) findViewById(R.id.answer);
        passcode_answer = (EditText) findViewById(R.id.passcode_answer);


        TextView btnCreateguest = (TextView) findViewById(R.id.addperson_next);

        // button click event
        btnCreateguest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!check.isChecked()) {

                    if (firstName.getText().toString().length() == 0)
                        firstName.setError("First name is required!");
                    else if (lastName.getText().toString().length() == 0)
                        lastName.setError("Last name is required!");
                    else {
                        String fname = firstName.getText().toString();
                        String lname = lastName.getText().toString();
                        String Phone = phone.getText().toString();
                        String SecQuest = secret_question.getText().toString();
                        String SecAns = passcode_answer.getText().toString();

                        Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = Addpersonpref.edit();
                        editor.putString("guest_id", guestID);
                        editor.putString("first_name", fname);
                        editor.putString("last_name", lname);
                        editor.putString("primary_phone", Phone);
                        editor.putString("secret_question", SecQuest);
                        editor.putString("passcode", SecAns);
                        editor.putString("is_company", "0");
                        editor.commit();
                        Intent permission = new Intent(getApplicationContext(), PermissionType.class);
                        startActivity(permission);
                    }

                } else {
                    if (firstName.getText().toString().length() == 0)
                        firstName.setError("First name is required!");
                    else if (lastName.getText().toString().length() == 0)
                        lastName.setError("Last name is required!");
                    else if (secret_question.getText().toString().length() == 0)
                        secret_question.setError("Secret Question is required!");
                    else if (passcode_answer.getText().toString().length() == 0)
                        passcode_answer.setError("Passcode is required!");
                    else {
                        String fname = firstName.getText().toString();
                        String lname = lastName.getText().toString();
                        String Phone = phone.getText().toString();
                        String SecQuest = secret_question.getText().toString();
                        String SecAns = passcode_answer.getText().toString();

                        Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = Addpersonpref.edit();
                        editor.putString("first_name", fname);
                        editor.putString("guest_id", guestID);
                        editor.putString("first_name", fname);
                        editor.putString("last_name", lname);
                        editor.putString("primary_phone", Phone);
                        editor.putString("secret_question", SecQuest);
                        editor.putString("passcode", SecAns);
                        editor.putString("is_company", "0");
                        editor.commit();
                        Intent permission = new Intent(getApplicationContext(), PermissionType.class);
                        startActivity(permission);

                    }
                }
            }

        });
        new getGuestInfo().execute();
    }

    @Override
    public void onBackPressed() {
        Intent guestList = new Intent(getApplicationContext(), ArrivalsActivity.class);
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
        JSONObject json = new JSONObject();

        private final String url_get_guest_list = "https://portal.virtualdoorman.com/dev/resident-api/guest_detail/";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditGuest.this);
            pDialog.setMessage("Loading Guest Information...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //	pDialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {

            try {
                jsonobj.put("newApi", true);
                jsonobj.put("login_guid", login_guid);
                jsonobj.put("guest_id", guestID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json = jsonParser.makeHttpRequest(url_get_guest_list, "GET", jsonobj);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                String status = json.getString("status");
                JSONArray resultArray = json.getJSONArray("result");
                JSONObject result = resultArray.getJSONObject(0);

                String FirstName = result.getString("FIRST_NAME");
                String LastName = result.getString("LAST_NAME");
                String mobileNo = result.getString("PHONE");
                final String question = result.getString("SECRET_QUESTION");
                if (!question.isEmpty()) {
                    check.setChecked(true);
                } else {
                    check.setChecked(false);
                }
                String code = result.getString("PASSCODE");
                JSONObject permissionObj = result.getJSONObject("permissions");


                check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (!isChecked) {
                            check.setChecked(false);
                            questionText.setVisibility(View.GONE);
                            answer.setVisibility(View.GONE);
                            secret_question.setText("");
                            passcode_answer.setText("");
                            secret_question.setVisibility(View.GONE);
                            passcode_answer.setVisibility(View.GONE);
                        } else {
                            check.setChecked(true);
                            questionText.setVisibility(View.VISIBLE);
                            answer.setVisibility(View.VISIBLE);
                            secret_question.setVisibility(View.VISIBLE);
                            passcode_answer.setVisibility(View.VISIBLE);
                        }

                    }
                });
                final String id = result.getString("guest_id");
                /*try {
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
*/

                //  String permission = result.getString("");
                firstName.setText(FirstName);
                lastName.setText(LastName);
                phone.setText(mobileNo);
                secret_question.setText(question);
                passcode_answer.setText(code);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


}
