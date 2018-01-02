package com.vdm.virtualdoorman;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.util.Log;

class CreateNewguest extends AsyncTask<String, String, String> {

	private ProgressDialog pDialog;
	private static final String TAG_MESSAGE = "message";
	JSONObject jsonobj = new JSONObject();
	JSONParser jsonParser = new JSONParser();
	private Context context;
	 public static final String ADDPERSONPREFERENCES = "AddPersonPrefs" ;
	 SharedPreferences Addpersonpref;
	 private static String url_create_guest = "https://portal.virtualdoorman.com/dev/iphone/iphone_requests.php?function=add_visitor&login_guid=12345";
	 
	 public CreateNewguest(Context context) {
		    this.context = context;
		}
	/**
	 * Before starting background thread Show Progress Dialog
	 * */
	@Override
	 
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Creating guest...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}

	/**
	 * Creating guest
	 * */
	protected String doInBackground(String... args) {
		
	//	 Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES, context.MODE_PRIVATE);
		String fname = (Addpersonpref.getString("first_name",""));
		String lname = (Addpersonpref.getString("last_name",""));
		String Phone = (Addpersonpref.getString("primary_phone",""));
		String SecQuest=(Addpersonpref.getString("secret_question",""));
		String SecAns=(Addpersonpref.getString("passcode",""));

		try {
			jsonobj.put("first_name", fname);
			jsonobj.put("last_name", lname);
			jsonobj.put("primary_phone", Phone);
			jsonobj.put("secret_question",SecQuest);
			jsonobj.put("passcode",SecAns);
			jsonobj.put("permission_type","any_day");
			jsonobj.put("permission_type_time","any_time");
			jsonobj.put("is_company","0");
			 
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*try {
			jdata.put("data", jsonobj);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		

		
		
		// getting JSON Object
		// Note that create guest url accepts POST method
		Log.e("json is+++++++++++------", jsonobj.toString());
		JSONObject json = jsonParser.makeHttpRequest(url_create_guest,
				"GET",jsonobj );
		
		// check log cat for response
		Log.e("Create Response", json.toString());

		// check for success tag
		try {
			String message = json.getString(TAG_MESSAGE);

			if (message == "Guest successfully added.") {
				// successfully created guest
				Log.e("Insert+++++++++", "successfulll__----------");
//				Intent i = new Intent(getApplicationContext(), AllguestsActivity.class);
//				startActivity(i);
				
				// closing this screen
				//finish();
	} else {
				// failed to create guest				}
		} }
		catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
	

	public void onPause() {
	//super.onPause();
	   if(pDialog != null){
	      // pDialog.dismiss();
	   }
	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	protected void onPostExecute(String file_url) {
		// dismiss the dialog once done
		pDialog.dismiss();
		
//		Intent permission = new Intent(getApplicationContext(), Permission_Type.class);
//		startActivity(permission);
	}

}
