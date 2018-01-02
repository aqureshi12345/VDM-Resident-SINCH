package com.vdm.virtualdoorman;

import com.sinch.android.rtc.SinchClient;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

//Drawer imports
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainScreen extends BaseActivity {
	ListView list;
	SharedPreferences loginpref;
	String level;


	String counter;
	public static final String LOGINPREF = "LoginPrefs";
	String[] permissions = {"android.permission.INTERNET", "android.permission.VIBRATE", "android.permission.MODIFY_AUDIO_SETTINGS", "android.permission.READ_PHONE_STATE", "android.permission.ACCESS_NETWORK_STATE", "android.permission.CAMERA", "android.permission.CALL_PHONE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO"};
	Intent logout;
	public static final String VstationListPref = "VstationPrefs";
	SharedPreferences VstationListpref;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isNetworkAvailable(getApplicationContext()) == true) {
			new missedCallCounter().execute();
			new DoorstationList().execute();



		}

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
	@Override
	protected void onServiceConnected() {
		if (!getSinchServiceInterface().isStarted()) {
			loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
			String Caller_id = (loginpref.getString("Caller_id", ""));
			getSinchServiceInterface().startClient(Caller_id);
			Log.d("service++", "connected++");
		}

	}


	public void logout(final Context context) {
		if(getSinchServiceInterface() != null) {
			getSinchServiceInterface().stopClient();
		}
		loginpref = context.getSharedPreferences(LOGINPREF, context.MODE_PRIVATE);
		loginpref.edit().remove("login_guid").commit();

		logout = new Intent(context,
				Login.class);
		logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		logout.putExtra("EXIT", "true");
		Toast.makeText(context,
				"User Logout Successfully", Toast.LENGTH_SHORT).show();
		context.startActivity(logout);
		finish();

	}


	public class DoorstationList extends AsyncTask<String, String, String> {
		JSONObject json = new JSONObject();
		//private ProgressDialog pDialog;
		JSONObject jsonobj = new JSONObject();
		JSONParser jsonParser = new JSONParser();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {

			loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
			String building_id = (loginpref.getString("building_id", ""));
			String resident_id = (loginpref.getString("resident_id", ""));

			final String doorStation_list = "https://portal.virtualdoorman.com/dev/common/libs/slim/vstations_list_all/" + building_id;


			/*
			 * try { jdata.put("data", jsonobj); } catch (JSONException e1) { //
			 * TODO Auto-generated catch block e1.printStackTrace(); }
			 */

			// getting JSON Object
			// Note that create guest url accepts POST method
			Log.e("json is+++++", jsonobj.toString());
			json = jsonParser.makeHttpRequest(doorStation_list, "GET", jsonobj);

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
		 **/
		protected void onPostExecute(String res) {
			// dismiss the dialog once done
			//	pDialog.dismiss();

			// seekBar.isClickable(false);
			try {

				String status = json.getString("status");
				JSONArray data = json.getJSONArray("vstations");
				if (status.equalsIgnoreCase("OK")) {
					int size = data.length();
					String[] doorStationName;
					String[] stationId;
					doorStationName = new String[size];
					stationId = new String[size];
					int i;
					for (i = 0; i < data.length(); i++) {

						JSONObject singleguest = data.getJSONObject(i);
						doorStationName[i] = singleguest.getString("VSTATION_NAME");
						String doorStation_Name = doorStationName[i];
						stationId[i] = singleguest.getString("vstation_id");
						String doorstation_id = stationId[i];
						VstationListpref = getSharedPreferences(VstationListPref,
								Context.MODE_PRIVATE);

						SharedPreferences.Editor editor = VstationListpref.edit();
						editor.putString(doorstation_id, doorStation_Name);
						editor.commit();


					}


				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}





		public class missedCallCounter extends AsyncTask<String, String, String> {

			private ProgressDialog pDialog;
			JSONObject jsonobj = new JSONObject();
			JSONObject json = new JSONObject();
			JSONParser jsonParser = new JSONParser();


			/**
			 * Before starting background thread Show Progress Dialog
			 */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(MainScreen.this);
				pDialog.setMessage("Loading...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				//pDialog.show();
			}

			/**
			 * Creating guest
			 */
			protected String doInBackground(String... args) {
				loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
				String building_id = (loginpref.getString("building_id", ""));
				String apartment_id = (loginpref.getString("apartment_id", ""));
				final String missedCalls = "https://portal.virtualdoorman.com/dev/common/libs/slim/missed_call_count/" + building_id + "/" + apartment_id;

				json = jsonParser.makeHttpRequest(missedCalls,
						"GET", jsonobj);


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
			 **/
			protected void onPostExecute(String file_url) {
				// dismiss the dialog once done
				pDialog.dismiss();
				//imgButton_service.setBackgroundResource(R.drawable.services_s);
				//imgButton_intercom.setBackgroundResource(R.drawable.intercom);
				String response = null;
				try {
					response = json.getString("status");

					if (response.equalsIgnoreCase("OK")) {
						counter = json.getString("missed_call_count");
						Intent intent = getIntent();

						if (intent.getStringExtra("Counter") != null) {
							counter = intent.getStringExtra("Counter");

						}
						loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);

						level = (loginpref.getString("access_level", ""));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent(getApplicationContext(), ArrivalsActivity.class);
				startActivity(intent);
			}
		}
	}

