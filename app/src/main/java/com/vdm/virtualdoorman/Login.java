package com.vdm.virtualdoorman;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import io.fabric.sdk.android.Fabric;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.Toast;

public class Login extends BaseActivity {

	// Progress Dialog
	private ProgressDialog pDialog;
	JSONObject jsonobj = new JSONObject();
	JSONParser jsonParser = new JSONParser();
	String sResponse;
	String username;
	String passwd;
	private  String url_get_login= "https://portal.virtualdoorman.com/dev/iphone/login_iphone.php";
	EditText user_name;
	EditText pass_word;
	Switch remember_me;
	public static final String LOGINPREF = "LoginPrefs";

	SharedPreferences loginpref;

	SharedPreferences ringerPref;
	public static final String RingerPREFS = "RingerPrefs";
	String[] permissions = {"android.permission.INTERNET", "android.permission.VIBRATE","android.permission.MODIFY_AUDIO_SETTINGS","android.permission.READ_PHONE_STATE","android.permission.ACCESS_NETWORK_STATE","android.permission.CAMERA","android.permission.CALL_PHONE","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.RECORD_AUDIO"};
	private static final int PERMISSION_REQUEST_CODE = 1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
		setContentView(R.layout.login);

		if ( !checkCameraPermission() || !checkInternetPermission() || !checkVibrationPermission()|| !checkAudioSettingPermission() || !checkAccessNetworkStatePermission() || !checkPhoneStatePermission() || !checkCallPhonePermission() || !checkWriteExternalStoragePermission()) {
			//Toast.makeText(this,"Permission not Granted",Toast.LENGTH_LONG).show();
			requestPermission();

		}
		Intent intent=getIntent();
		if(intent.hasExtra("EXIT")){
//			if(BaseActivity.getSinchServiceInterface_new()!=null){
//				BaseActivity.getSinchServiceInterface_new().stopClient();
//			}
		}

		// Edit Text
		user_name = (EditText) findViewById(R.id.username);
		pass_word = (EditText) findViewById(R.id.password);
		remember_me = (Switch) findViewById(R.id.Switch);
		remember_me.setChecked(false);
		loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
		String uname = (loginpref.getString("username", ""));
		String pwd = (loginpref.getString("password", ""));
		String checked = (loginpref.getString("Checked", ""));
		ringerPref = getSharedPreferences(RingerPREFS,
				Context.MODE_PRIVATE);
		String ringer_mode = (ringerPref.getString("Ringer", ""));
		if(ringer_mode=="") {
			SharedPreferences.Editor editor = ringerPref.edit();
			editor.putString("Ringer", "ring and vibrate");
			editor.commit();
		}
		if (uname != null && pwd != null && checked != null) {
			user_name.setText(uname);
			pass_word.setText(pwd);
			remember_me.setChecked(true);
		}
// else{
//			user_name.setText("");
//			pass_word.setText("");
//			remember_me.setChecked(false);
//		}

		// Create button
		Button login = (Button) findViewById(R.id.login);

		// button click event
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if(isNetworkAvailable()==true) {


					if (user_name.getText().toString().length() == 0)
						user_name.setError("Enter Username");
					else if (pass_word.getText().toString().length() == 0)
						pass_word.setError("Enter Password");
					else {
						username = user_name.getText().toString();
						passwd = pass_word.getText().toString();

						if (remember_me.isChecked()) {
							loginpref = getSharedPreferences(LOGINPREF,
									Context.MODE_PRIVATE);
							Editor editor = loginpref.edit();
							editor.putString("username", username);
							editor.putString("password", passwd);
							editor.putString("Checked", "true");
							editor.commit();
						}if (!remember_me.isChecked()){
							loginpref = getSharedPreferences(LOGINPREF,
									Context.MODE_PRIVATE);
							loginpref.edit().remove("username").commit();
							loginpref.edit().remove("password").commit();
							loginpref.edit().remove("Checked").commit();
						}

						new getLogin().execute();

					}
				}
				else{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
					TextView Msg = new TextView(Login.this);
					Msg.setText("Network is not available at the moment. Please try later");
					Msg.setGravity(Gravity.CENTER_HORIZONTAL);
				//	Msg.setMaxHeight(100);
					Msg.setTextSize(18);
					alertDialogBuilder.setView(Msg);
					TextView title = new TextView(Login.this);
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


								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();

				}
			}

		});
	}
	private boolean checkPhoneStatePermission(){
		int result = ContextCompat.checkSelfPermission(Login.this, android.Manifest.permission.READ_PHONE_STATE);
		if (result == PackageManager.PERMISSION_GRANTED){

			return true;

		} else {

			return false;

		}
	}
	private boolean checkInternetPermission() {
		int result = ContextCompat.checkSelfPermission(Login.this, android.Manifest.permission.INTERNET);
		if (result == PackageManager.PERMISSION_GRANTED){

			return true;

		} else {

			return false;

		}
	}
	private boolean checkVibrationPermission() {
		int result = ContextCompat.checkSelfPermission(Login.this, android.Manifest.permission.VIBRATE);
		if (result == PackageManager.PERMISSION_GRANTED){

			return true;

		} else {

			return false;

		}
	}
	private boolean checkAudioSettingPermission() {
		int result = ContextCompat.checkSelfPermission(Login.this, android.Manifest.permission.MODIFY_AUDIO_SETTINGS);
		if (result == PackageManager.PERMISSION_GRANTED){

			return true;

		} else {

			return false;

		}
	}
	private boolean checkAccessNetworkStatePermission() {
		int result = ContextCompat.checkSelfPermission(Login.this, android.Manifest.permission.ACCESS_NETWORK_STATE);
		if (result == PackageManager.PERMISSION_GRANTED){

			return true;

		} else {

			return false;

		}
	}
	private boolean checkCallPhonePermission() {
		int result = ContextCompat.checkSelfPermission(Login.this, android.Manifest.permission.CALL_PHONE);
		if (result == PackageManager.PERMISSION_GRANTED){

			return true;

		} else {

			return false;

		}
	}
	private boolean checkCameraPermission() {
		int result = ContextCompat.checkSelfPermission(Login.this, android.Manifest.permission.CAMERA);
		if (result == PackageManager.PERMISSION_GRANTED){

			return true;

		} else {

			return false;

		}
	}

	private boolean checkWriteExternalStoragePermission() {
		int result = ContextCompat.checkSelfPermission(Login.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (result == PackageManager.PERMISSION_GRANTED){

			return true;

		} else {

			return false;

		}
	}
	private void requestPermission(){


		ActivityCompat.requestPermissions(Login.this, permissions, PERMISSION_REQUEST_CODE);

	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_CODE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED&& grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED && grantResults[6] == PackageManager.PERMISSION_GRANTED && grantResults[7] == PackageManager.PERMISSION_GRANTED && grantResults[8] == PackageManager.PERMISSION_GRANTED ) {
					//Toast.makeText(Login.this,"Permission Granted",Toast.LENGTH_LONG).show();

				} else {
					//Toast.makeText(Login.this,"Permission Denied",Toast.LENGTH_LONG).show();

				}
				break;
		}
	}
	public boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		}
		return isAvailable;
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

//	@Override
//	public void onStartFailed(SinchError error) {
//
//	}
//
//	@Override
//	public void onStarted() {
//
//	}
	@Override
	protected void onServiceConnected() {
		Log.d("serviceLOgin++", "connected++");

	}


	class getLogin extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = null;
			HttpResponse response = null;

			try {
				httppost = new HttpPost(url_get_login);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", username));
				nameValuePairs.add(new BasicNameValuePair("passwd", passwd));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				response = httpclient.execute(httppost);
			} catch (UnsupportedEncodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Execute HTTP Post Request
			try {

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));

				sResponse = reader.readLine();

				Log.e("after htttp response", sResponse);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
			final JSONObject JResponse;
			String call_id = null;
			try {
				JResponse = new JSONObject(sResponse);
				String res = JResponse.toString();
				if (res.contains("terms_not_agree")) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							Login.this);
					TextView Msg = new TextView(Login.this);
					Msg.setText("Terms of use has been updated.Please agree to updated terms to use Virtual Doorman App");
					Msg.setGravity(Gravity.CENTER_HORIZONTAL);
					Msg.setMaxHeight(100);
					Msg.setTextSize(15);
					alertDialogBuilder.setView(Msg);
					TextView title = new TextView(Login.this);
					title.setText("Terms Updated");
					title.setPadding(10, 10, 10, 10);
					title.setGravity(Gravity.CENTER);
					title.setTextColor(Color.BLACK);
					title.setTextSize(20);
					alertDialogBuilder.setCustomTitle(title);
					alertDialogBuilder.setPositiveButton(
							"View Terms & Conditions",
							new DialogInterface.OnClickListener() {

								String lg = JResponse.getString("login_guid");

								public void onClick(DialogInterface arg0,
										int arg1) {
									Intent terms = new Intent(
											getApplicationContext(),
											TermsandCondition.class);
									terms.putExtra("login_guid", lg);
									startActivity(terms);

								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}

				String is_valid = JResponse.getString("is_valid");
				if (is_valid.equalsIgnoreCase("true")) {

					String access_level=JResponse.getString("usr_level_current");
					if(access_level.equalsIgnoreCase("BRZ"))
					{
						String operator_phone=JResponse.getString("vdm_phone_number");
						String APP_KEY=JResponse.getString("SINCH_APP_KEY");
						String APP_SECRET=JResponse.getString("SINCH_APP_SECRET");
						String ENVIRONMENT=JResponse.getString("SINCH_ENVIRONMENT");
						String apartment_id=JResponse.getString("apt");
						String building_id=JResponse.getString("bld");
						String resident_name=JResponse.getString("name");
						String resident_id=JResponse.getString("uid");

						 call_id="vdm-"+building_id+"-"+apartment_id;
						String lg = JResponse.getString("login_guid");
						loginpref = getSharedPreferences(LOGINPREF,
								Context.MODE_PRIVATE);
						Editor editor = loginpref.edit();
						editor.putString("login_guid", lg);
						editor.putString("access_level", "BRZ");
						editor.putString("Caller_id",call_id );
						editor.putString("apartment_id",apartment_id );
						editor.putString("building_id",building_id );
						editor.putString("operator_phoneNo",operator_phone );
						editor.putString("resident_name",resident_name );
						editor.putString("resident_id",resident_id );
						editor.putString("APP_KEY",APP_KEY );
						editor.putString("APP_SECRET",APP_SECRET );
						editor.putString("ENVIRONMENT",ENVIRONMENT );
						editor.commit();
						Global loginId;
						loginId = new Global();
						loginId.setLoginDetail(lg);
//						final Intent serviceIntent = new Intent(getApplicationContext(), SinchService.class);
//						startService(serviceIntent);

					}
					else{
						String operator_phone=JResponse.getString("vdm_phone_number");
						String APP_KEY=JResponse.getString("SINCH_APP_KEY");
						String APP_SECRET=JResponse.getString("SINCH_APP_SECRET");
						String ENVIRONMENT=JResponse.getString("SINCH_ENVIRONMENT");
						String apartment_id=JResponse.getString("apt");
						String building_id=JResponse.getString("bld");
						String resident_name=JResponse.getString("name");
						String resident_id=JResponse.getString("uid");
						 call_id="vdm-"+building_id+"-"+apartment_id;
						String lg = JResponse.getString("login_guid");
						loginpref = getSharedPreferences(LOGINPREF,
								Context.MODE_PRIVATE);
						Editor editor = loginpref.edit();
						editor.putString("login_guid", lg);
						editor.putString("access_level", "other");
						editor.putString("Caller_id",call_id );
						editor.putString("apartment_id",apartment_id );
						editor.putString("building_id",building_id );
						editor.putString("operator_phoneNo",operator_phone );
						editor.putString("resident_name",resident_name );
						editor.putString("resident_id",resident_id );
						editor.putString("APP_KEY",APP_KEY );
						editor.putString("APP_SECRET", APP_SECRET);
						editor.putString("ENVIRONMENT",ENVIRONMENT );
						editor.commit();
						Global loginId;
						loginId = new Global();
						loginId.setLoginDetail(lg);
//						getApplicationContext().bindService(new Intent(Login.this, SinchService.class), Login.this,
//								BIND_AUTO_CREATE);
//
////						final Intent serviceIntent = new Intent(getApplicationContext(), SinchService.class);
////						startService(serviceIntent);
//					Intent main = new Intent(getApplicationContext(),
//							MainScreen.class);
//					//main.putExtra("access_level", "other");
//					startActivity(main);
//					finish();

				}
					getSinchServiceInterface().startClient(call_id);
					Intent arrivals = new Intent(getApplicationContext(),
							MainScreen.class);
					startActivity(arrivals);
					finish();
				}else {
					Toast.makeText(Login.this, "Wrong Username or password",
							Toast.LENGTH_SHORT).show();

				}



			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
