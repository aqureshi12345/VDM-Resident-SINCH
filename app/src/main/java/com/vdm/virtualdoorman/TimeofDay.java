package com.vdm.virtualdoorman;


import org.json.JSONException;
import org.json.JSONObject;

import com.vdm.virtualdoorman.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TimeofDay extends Activity {
	public static final String ADDPERSONPREFERENCES = "AddPersonPrefs";
	SharedPreferences Addpersonpref,loginpref;
	
	public static final String LOGINPREF = "LoginPrefs";
	 String login_guid = "";
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_of_day);
		RelativeLayout backbutton=(RelativeLayout)findViewById(R.id.backbutton);
		backbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), PermissionType.class);
				startActivity(intent);
				finish();
			}
		});
		Button btnAnyTime = (Button) findViewById(R.id.anytime);
		Button btnSpecifiedTime = (Button) findViewById(R.id.specifiedtime);
		 loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
		 
			
		 login_guid = (loginpref.getString("login_guid", ""));
		btnAnyTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES,
						Context.MODE_PRIVATE);
				Editor editor = Addpersonpref.edit();
				editor.putString("permission_type_time", "any_time");
				editor.commit();

				String guest_id = (Addpersonpref.getString("guest_id", ""));
				if(guest_id.isEmpty()){
					new CreateNewguest().execute();
				}else{
					new Updateguest().execute();
				}

			//	new CreateNewguest().execute();

			}
		});
		btnSpecifiedTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent time = new Intent(getApplicationContext(), PickTime.class);
				startActivity(time);
			
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class CreateNewguest extends AsyncTask<String, String, String> {
		JSONObject json = new JSONObject();
		private ProgressDialog pDialog;
		JSONObject jsonobj = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		public static final String ADDPERSONPREFERENCES = "AddPersonPrefs";
		SharedPreferences Addpersonpref;


		final String url_create_guest = "https://portal.virtualdoorman.com/dev/resident-api/add_resident_guest";

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(TimeofDay.this);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating guest
		 * */
		protected String doInBackground(String... args) {

			Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES,
					Context.MODE_PRIVATE);
			String fname = (Addpersonpref.getString("first_name", ""));
			String lname = (Addpersonpref.getString("last_name", ""));
			String Phone = (Addpersonpref.getString("primary_phone", ""));
			String SecQuest = (Addpersonpref.getString("secret_question", ""));
			String SecAns = (Addpersonpref.getString("passcode", ""));
			String is_company = (Addpersonpref.getString("is_company", ""));
			String permission_type = (Addpersonpref.getString("permission_type", ""));
			String permission_type_time = (Addpersonpref.getString("permission_type_time", ""));
			if (permission_type == "date_period") {
				String start_date = (Addpersonpref.getString("start_date", ""));
				String end_date = (Addpersonpref.getString("end_date", ""));
				try {
					jsonobj.put("start_date", start_date);
					jsonobj.put("end_date", end_date);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				permission_type = "Each Day";
			}
			if(permission_type_time.equalsIgnoreCase("any_time")){
				permission_type_time = "Any Time";
			}

			try {
				jsonobj.put("newApi", true);
				jsonobj.put("login_guid", login_guid);
				jsonobj.put("first_name", fname);
				jsonobj.put("last_name", lname);
				jsonobj.put("phone", Phone);
				jsonobj.put("notes", "Some Note Here");
				jsonobj.put("cell_phone", Phone);
				jsonobj.put("alt_phone", Phone);
				jsonobj.put("work_phone", Phone);
				jsonobj.put("secret_question", SecQuest);
				jsonobj.put("passcode", SecAns);
				jsonobj.put("email", "");
				jsonobj.put("permission_type_day", permission_type);
				jsonobj.put("permission_day", "");
				jsonobj.put("permission_type_time", permission_type_time);
				jsonobj.put("permission_time", "");
				jsonobj.put("is_company", is_company);

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		/*
		 * try { jdata.put("data", jsonobj); } catch (JSONException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); }
		 */

			// getting JSON Object
			// Note that create guest url accepts POST method
			Log.e("json is+++++", jsonobj.toString());
			json = jsonParser.makeHttpRequest(url_create_guest, "POST", jsonobj);

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
		protected void onPostExecute(String res) {
			// dismiss the dialog once done
			pDialog.dismiss();
			try {

				String response = json.getString("status");
				if (response.equalsIgnoreCase("SUCCESS")) {
					Toast.makeText(getApplicationContext(), response,
							Toast.LENGTH_LONG).show();
					Intent mainscreen = new Intent(getApplicationContext(),
							ArrivalsActivity.class);
					mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mainscreen.putExtra("EXIT", true);
					startActivity(mainscreen);
				}
				else if(response.equalsIgnoreCase("FAIL")) {

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							TimeofDay.this);
					alertDialogBuilder.setMessage("You have been logged out. Please login again to use the application");
					alertDialogBuilder.setTitle("Error");
					alertDialogBuilder.setPositiveButton(
							"OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
													int arg1) {
									loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
									loginpref.edit().remove("login_guid").commit();
									Intent logout = new Intent(getApplicationContext(),
											Login.class);
									logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									logout.putExtra("EXIT", true);
									startActivity(logout);
									finish();

								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}

				else {
					Toast.makeText(getApplicationContext(),
							"Some Error Occured", Toast.LENGTH_SHORT).show();
					Intent mainscreen = new Intent(getApplicationContext(),
							MainScreen.class);
					mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mainscreen.putExtra("EXIT", true);
					startActivity(mainscreen);

				}

			} catch (Exception e) {

				Log.e(e.getClass().getName(), e.getMessage(), e);
			}

		}

	}

	class Updateguest extends AsyncTask<String, String, String> {
		JSONObject json = new JSONObject();
		private ProgressDialog pDialog;
		JSONObject jsonobj = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		public static final String ADDPERSONPREFERENCES = "AddPersonPrefs";
		SharedPreferences Addpersonpref;


		final String url_create_guest = "https://portal.virtualdoorman.com/dev/resident-api/update_resident_guest";

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(TimeofDay.this);
			pDialog.setMessage("Updating Guest...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating guest
		 * */
		protected String doInBackground(String... args) {

			Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES,	Context.MODE_PRIVATE);

			String guest_id = (Addpersonpref.getString("guest_id", ""));
			String fname = (Addpersonpref.getString("first_name", ""));
			String lname = (Addpersonpref.getString("last_name", ""));
			String Phone = (Addpersonpref.getString("primary_phone", ""));
			String SecQuest = (Addpersonpref.getString("secret_question", ""));
			String SecAns = (Addpersonpref.getString("passcode", ""));
			String is_company = (Addpersonpref.getString("is_company", ""));
			String permission_type = (Addpersonpref.getString("permission_type", ""));
			String permission_type_time = (Addpersonpref.getString("permission_type_time", ""));
			String days_of_week = (Addpersonpref.getString("days_of_week", ""));
			String start_date="";
			String end_date = "";
			String permission_day = "";
			if (permission_type == "date_period") {
				 start_date = (Addpersonpref.getString("start_date", ""));
				 end_date = (Addpersonpref.getString("end_date", ""));
				permission_day=start_date+","+end_date;
				 try {
					jsonobj.put("start_date", start_date);
					jsonobj.put("end_date", end_date);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(permission_type.equalsIgnoreCase("days_of_week")){
				permission_type = "Week Days";
				permission_day = days_of_week;
			}else{
				permission_type = "Each Day";
			}
			if(permission_type_time.equalsIgnoreCase("any_time")){
				permission_type_time = "Any Time";
			}

			try {

				jsonobj.put("newApi", true);
				jsonobj.put("login_guid", login_guid);
				jsonobj.put("guest_id", guest_id);
				jsonobj.put("first_name", fname);
				jsonobj.put("last_name", lname);
				jsonobj.put("phone", Phone);
				jsonobj.put("notes", "Some Notes Here");
				jsonobj.put("cell_phone", Phone);
				jsonobj.put("alt_phone", Phone);
				jsonobj.put("work_phone", Phone);
				jsonobj.put("email", "");
				jsonobj.put("secret_question", SecQuest);
				jsonobj.put("passcode", SecAns);
				jsonobj.put("permission_type_day", permission_type);
				jsonobj.put("permission_day", permission_day);
				jsonobj.put("permission_type_time", permission_type_time);
				jsonobj.put("permission_time", "");
				jsonobj.put("is_company", is_company);

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		/*
		 * try { jdata.put("data", jsonobj); } catch (JSONException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); }
		 */

			// getting JSON Object
			// Note that create guest url accepts POST method
			Log.e("json is+++++", jsonobj.toString());
			json = jsonParser.makeHttpRequest(url_create_guest, "POST", jsonobj);

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
		protected void onPostExecute(String res) {
			// dismiss the dialog once done
			pDialog.dismiss();
			try {

				String response = json.getString("status");
				if (response.equalsIgnoreCase("SUCCESS")) {
					Toast.makeText(getApplicationContext(), response,
							Toast.LENGTH_LONG).show();
					Intent mainscreen = new Intent(getApplicationContext(),
							MainScreen.class);
					mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mainscreen.putExtra("EXIT", true);
					startActivity(mainscreen);
				}
				else if(response.equalsIgnoreCase("FAIL")) {

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							TimeofDay.this);
					alertDialogBuilder.setMessage("You have been logged out. Please login again to use the application");
					alertDialogBuilder.setTitle("Error");
					alertDialogBuilder.setPositiveButton(
							"OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
													int arg1) {
									loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
									loginpref.edit().remove("login_guid").commit();
									Intent logout = new Intent(getApplicationContext(),
											Login.class);
									logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									logout.putExtra("EXIT", true);
									startActivity(logout);
									finish();

								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}

				else {
					Toast.makeText(getApplicationContext(),
							"Some Error Occured", Toast.LENGTH_SHORT).show();
					Intent mainscreen = new Intent(getApplicationContext(),
							MainScreen.class);
					mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mainscreen.putExtra("EXIT", true);
					startActivity(mainscreen);

				}

			} catch (Exception e) {

				Log.e(e.getClass().getName(), e.getMessage(), e);
			}

		}

	}


}
