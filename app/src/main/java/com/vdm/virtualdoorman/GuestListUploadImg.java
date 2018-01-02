package com.vdm.virtualdoorman;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vdm.virtualdoorman.R;



import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;


public class GuestListUploadImg extends Activity {
	ListView list;
	customGuestListUploadImg adapter;
	String[] guestlist;
	Integer[] imageId;
	String[] guestId;
	SharedPreferences loginpref;
	public static final String LOGINPREF = "LoginPrefs";
	String login_guid = null;

	// ArrayList<String> guestlist = new ArrayList<String>();
	//
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guest_list);
		loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);

		login_guid = (loginpref.getString("login_guid", ""));
		new getGuestList().execute();

	}

	class getGuestList extends AsyncTask<String, String, String> {

		private ProgressDialog pDialog;
		JSONObject jsonobj = new JSONObject();
		JSONObject json = new JSONObject();
		JSONParser jsonParser = new JSONParser();

		private final String url_get_guest_list = "https://portal.virtualdoorman.com/dev/iphone/iphone_requests.php?function=get_guests_list&login_guid="
				+ login_guid;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GuestListUploadImg.this);
			pDialog.setMessage("Getting Guest List..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating guest
		 * */
		protected String doInBackground(String... args) {

			 json = jsonParser.makeHttpRequest(url_get_guest_list,
					"GET", jsonobj);
			try {

				JSONArray data = json.getJSONArray("data");
				int size = data.length();
				guestlist = new String[size];
				imageId = new Integer[size];
				guestId = new String[size];
				for (int i = 0; i < data.length(); i++) {

					JSONObject singleguest = data.getJSONObject(i);
					guestlist[i] = singleguest.getString("first_name") + " "
							+ singleguest.getString("last_name");
					guestId[i] = singleguest.getString("id");
					if (singleguest.getString("guest_type").equalsIgnoreCase("person" ) ||
							  singleguest.getString("guest_type").equalsIgnoreCase("resident" )) {
							  imageId[i] = R.drawable.doorman_icon; } 
							 else{
								  imageId[i] = R.drawable.building_icon; }
				}
				adapter = new customGuestListUploadImg(GuestListUploadImg.this, guestlist,
						imageId, guestId);

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
		protected void onPostExecute(String res) {
			// dismiss the dialog once done
			pDialog.dismiss();
			String response = null;
			try {
				response = json.getString("response");
			
			 if(response.equalsIgnoreCase("User authentication failed.")) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						GuestListUploadImg.this);
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
			
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			list = (ListView) findViewById(R.id.guestlist);
			list.setAdapter(adapter);

			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					Intent intent = new Intent(GuestListUploadImg.this, UploadImg.class);
					intent.putExtra("id", guestId[position]);
					startActivity(intent);
				}
			});
			
		
	}

	}

}
