package com.vdm.virtualdoorman.LogsFragments;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vdm.virtualdoorman.ArrivalsActivity;
import com.vdm.virtualdoorman.LogsAdapters.CustomGuestDeliveryList;
import com.vdm.virtualdoorman.DeliveryLogDetail;
import com.vdm.virtualdoorman.Intercom;
import com.vdm.virtualdoorman.JSONParser;
import com.vdm.virtualdoorman.Login;
import com.vdm.virtualdoorman.MainScreen;
import com.vdm.virtualdoorman.MyBuildingContacts;
import com.vdm.virtualdoorman.R;


//Drawer imports

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;


public class GuestDeliveryLogs extends android.support.v4.app.Fragment {
	ListView list;
	CustomGuestDeliveryList adapter;
	private String[] dateTime=null;
	private String[] reportType=null;
	private String[] operatorName=null;
	private String[] logid=null;
	View view;
	MainScreen mainScreen;
	SharedPreferences loginpref;

	public static final String LOGINPREF = "LoginPrefs";
	String login_guid = null;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view=inflater.inflate(R.layout.fragment_guest_delivery_logs, conteiner, false);


		loginpref = getActivity().getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);

		login_guid = (loginpref.getString("login_guid", ""));

		new getGuestList().execute();

		return view;
	}

				//Checking if the item is in checked state or not, if not make it in checked state


/*
	public boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		}
		return isAvailable;
	}
*/
	class getGuestList extends AsyncTask<String, String, String> {

		private ProgressDialog pDialog;
		JSONObject jsonobj = new JSONObject();
		JSONObject json = new JSONObject();
		JSONParser jsonParser = new JSONParser();

		private  final String url_get_logs_list = "https://portal.virtualdoorman.com/dev/iphone/iphone_requests.php?function=get_delivery_logs&login_guid="
				+ login_guid;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading Log List...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating guest
		 * */
		protected String doInBackground(String... args) {

			 json = jsonParser.makeHttpRequest(url_get_logs_list,
					"GET", jsonobj);
			try {

				JSONArray data = json.getJSONArray("data");
				int size = data.length();
				dateTime = new String[size];
				reportType = new String[size];
				operatorName = new String[size];
				logid=new String[size];
				for (int i = 0; i < data.length(); i++) {

					JSONObject singlelog = data.getJSONObject(i);
				
					dateTime[i] = singlelog.getString("date_time");
					reportType[i] = singlelog.getString("report_type");
					operatorName[i] = singlelog.getString("operator_name");
					logid[i]=singlelog.getString("id");
	 
				}
				adapter = new CustomGuestDeliveryList(getActivity(), dateTime,
						reportType, operatorName,logid);

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

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
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			
			String response = null;
			try {
				response = json.getString("response");
			
			 if(response.equalsIgnoreCase("User authentication failed.")) {
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setMessage("You have been logged out. Please login again to use the application");
				alertDialogBuilder.setTitle("Error");
				alertDialogBuilder.setPositiveButton(
						"OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface arg0,
									int arg1) {
								loginpref = getActivity().getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
								loginpref.edit().remove("login_guid").commit();
								Intent logout = new Intent(getActivity(),
										Login.class);
								logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								logout.putExtra("EXIT", true);
								startActivity(logout);
								//finish();

							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				
			}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			list = (ListView)view. findViewById(R.id.guest_delivery_logs_list);
			list.setAdapter(adapter);

			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					Intent intent = new Intent(getActivity(), DeliveryLogDetail.class);
					intent.putExtra("login_guid", login_guid);
					intent.putExtra("id", logid[position]);
					startActivity(intent);
				}
			});
		
		}

	}

}
