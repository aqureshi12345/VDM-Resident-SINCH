package com.vdm.virtualdoorman;

import org.json.JSONException;
import org.json.JSONObject;

import com.vdm.virtualdoorman.Login.getLogin;
import com.vdm.virtualdoorman.TimeofDay.CreateNewguest;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TermsandCondition extends Activity {
	private WebView terms;
	String login_guid = null;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_conditions);
		// Intent intent = getIntent();
		terms = (WebView) findViewById(R.id.webview);
		Intent intent = getIntent();
		login_guid = intent.getStringExtra("login_guid");

		terms.loadUrl("https://portal.virtualdoorman.com/dev/owner/terms_and_conditions.php?login_guid="
				+ login_guid);
		terms.getSettings().setLoadsImagesAutomatically(true);
		terms.getSettings().setJavaScriptEnabled(true);
		terms.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		final Button accept = (Button) findViewById(R.id.accept);
		 Handler handler = new Handler(); 
		    handler.postDelayed(new Runnable() { 
		         public void run() { 
		              accept.setVisibility(View.VISIBLE); 
		         } 
		    },10000); 
		// button click event
		accept.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				new AcceptTerms().execute();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class AcceptTerms extends AsyncTask<String, String, String> {
		JSONObject json = new JSONObject();
		private ProgressDialog pDialog;
		JSONObject jsonobj = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		public static final String ADDPERSONPREFERENCES = "AddPersonPrefs";
		SharedPreferences Addpersonpref;

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(TermsandCondition.this);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating guest
		 * */
		protected String doInBackground(String... args) {

			Log.e("json is+++++++++++------", jsonobj.toString());
			String url_terms_conditions = "https://portal.virtualdoorman.com/dev/iphone/iphone_requests.php?function=agree_terms_and_conditions&login_guid="
					+ login_guid;
			json = jsonParser.makeHttpRequest(url_terms_conditions, "GET",
					jsonobj);

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

				String response = json.getString("response");
				if (response.equalsIgnoreCase("User authentication failed.")) {

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							TermsandCondition.this);
					alertDialogBuilder
							.setMessage("You have been logged out. Please login again to use the application");
					alertDialogBuilder.setTitle("Error");
					alertDialogBuilder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
										int arg1) {
									Intent logout = new Intent(
											getApplicationContext(),
											Login.class);
									logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									logout.putExtra("EXIT", true);
									startActivity(logout);
									finish();

								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				} else if (response
						.equalsIgnoreCase("Terms and Conditions accepted Successfully")) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							TermsandCondition.this);
					TextView Msg = new TextView(TermsandCondition.this);
					Msg.setText(" You have successfully accepted updated terms. Please login again to use Virtual Doorman App.");
					Msg.setGravity(Gravity.CENTER_HORIZONTAL);
					Msg.setMaxHeight(100);
					Msg.setTextSize(15);
					alertDialogBuilder.setView(Msg);
					TextView title = new TextView(TermsandCondition.this);
					title.setText("Terms and Conditions accepted");
					title.setPadding(10, 10, 10, 10);
					title.setGravity(Gravity.CENTER);
					title.setTextColor(Color.BLACK);
					title.setTextSize(20);
					alertDialogBuilder.setCustomTitle(title);
					alertDialogBuilder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
										int arg1) {
									Intent terms = new Intent(
											getApplicationContext(),
											Login.class);
									startActivity(terms);

								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}

				else {
					Toast.makeText(getApplicationContext(),
							"Some error occurred while processing. Please try again or contact your Virtual Doorman.", Toast.LENGTH_SHORT).show();
					Intent mainscreen = new Intent(getApplicationContext(),
							ArrivalsActivity.class);
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
