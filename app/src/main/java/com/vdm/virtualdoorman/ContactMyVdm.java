package com.vdm.virtualdoorman;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vdm.virtualdoorman.R;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class ContactMyVdm extends Activity {
	ListView list;
	String emailvdm;
	String vdm_number ;
	SharedPreferences loginpref;

	public static final String LOGINPREF = "LoginPrefs";
	String login_guid = null;

	String[] mainlist = { "Call My Virtual Doorman", "Email My Virtual Doorman" };
	Integer[] imageId = { R.drawable.phone_icon, R.drawable.mail_icon

	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_my_vdm);
		loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
		RelativeLayout backbutton=(RelativeLayout)findViewById(R.id.backbutton);
		backbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), ArrivalsActivity.class);
				startActivity(intent);
			}
		});

		login_guid = (loginpref.getString("login_guid", ""));
		CustomList adapter = new CustomList(ContactMyVdm.this, mainlist,
				imageId);
		list = (ListView) findViewById(R.id.myvdmlist);
		list.setAdapter(adapter);
		new getmycontacts().execute();
	}

	class getmycontacts extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		JSONObject jsonobj = new JSONObject();
		JSONObject json = new JSONObject();

		JSONParser jsonParser = new JSONParser();

		private final String url_get_my_contacts = "https://portal.virtualdoorman.com/dev/iphone/iphone_requests.php?function=get_my_contacts&login_guid="
				+ login_guid;

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ContactMyVdm.this);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			JSONObject json = jsonParser.makeHttpRequest(url_get_my_contacts,
					"GET", jsonobj);
			try {
				JSONArray number = json
						.getJSONArray("vdm_phone");
				JSONObject vdm_no = number.getJSONObject(0);
				 vdm_number = vdm_no.getString("phone");


				JSONArray vdm_email = json.getJSONArray("vdm_email");
				JSONObject email_vdm = vdm_email.getJSONObject(0);
				emailvdm = email_vdm.getString("email");

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
						ContactMyVdm.this);
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
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			

			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					if (mainlist[+position] == "Call My Virtual Doorman") {

						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:"
								+ vdm_number));
						startActivity(callIntent);
					}

					if (mainlist[+position] == "Email My Virtual Doorman") {

						String[] TO = { emailvdm };

						Intent emailIntent = new Intent(Intent.ACTION_SEND);
						emailIntent.setData(Uri.parse("mailto:"));
						emailIntent.setType("text/plain");

						emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
						emailIntent.putExtra(Intent.EXTRA_SUBJECT,
								"Your subject");
						emailIntent.putExtra(Intent.EXTRA_TEXT,
								"Email message goes here");

						try {
							startActivity(Intent.createChooser(emailIntent,
									"Send mail..."));
							finish();
							Log.i("Finished sending email...", "");
						} catch (android.content.ActivityNotFoundException ex) {
							Toast.makeText(ContactMyVdm.this,
									"There is no email client installed.",
									Toast.LENGTH_SHORT).show();
						}
					}

				}
			});
		}

	}
}
