package com.vdm.virtualdoorman.LogsFragments;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vdm.virtualdoorman.ArrivalsActivity;
import com.vdm.virtualdoorman.LogsAdapters.CustomCallLogList;
import com.vdm.virtualdoorman.JSONParser;
import com.vdm.virtualdoorman.R;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.support.v4.app.Fragment;


public class CallLogs extends android.support.v4.app.Fragment {
    ListView list;
    CustomCallLogList adapter;
    private String[] dateTime=null;
    private String[] callFrom=null;
    private String[] recieverName=null;
    private String[] logid=null;
    View view;
     private String[] missedCall=null;
    SharedPreferences VstationListpref;
    public static final String VstationListPref = "VstationPrefs";

    SharedPreferences loginpref;

    public static final String LOGINPREF = "LoginPrefs";
    String login_guid = null;

    public CallLogs(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_call_logs, container, false);
        //setContentView(R.layout.guest_delivery_logs);
        loginpref = getActivity().getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);

        login_guid = (loginpref.getString("login_guid", ""));

        new LogList().execute();
        return view;

    }
     public void onBackPressed(){
         Intent intent = new Intent(getActivity(),
                 ArrivalsActivity.class);
         intent.putExtra("Counter","0");
         startActivity(intent);
         //finish();
    }
    class LogList extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        JSONObject jsonobj = new JSONObject();
        JSONObject json = new JSONObject();
        JSONParser jsonParser = new JSONParser();


        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Log List..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating guest
         * */
        protected String doInBackground(String... args) {
            loginpref = getActivity().getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
            String building_id = (loginpref.getString("building_id", ""));
            String apartment_id = (loginpref.getString("apartment_id", ""));
            final String url_get_call_logs = "https://portal.virtualdoorman.com/dev/common/libs/slim/calls_records/"+building_id+"/"+apartment_id;

            json = jsonParser.makeHttpRequest(url_get_call_logs,
                    "GET",jsonobj);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            try {
                JSONArray data = json.getJSONArray("data");
                int size = data.length();
                dateTime = new String[size];
                callFrom = new String[size];
                recieverName = new String[size];
                missedCall=new String[size];
                logid=new String[size];
                for (int i = 0; i < data.length(); i++) {

                    JSONObject singlelog = data.getJSONObject(i);

                    dateTime[i] = singlelog.getString("START_TIME");
                    String from = singlelog.getString("CALL_FROM");
                    VstationListpref = getActivity().getSharedPreferences(VstationListPref,
                            Context.MODE_PRIVATE);
                    callFrom[i] =(VstationListpref.getString(from, ""));
                    recieverName[i] = singlelog.getString("RESIDENT_NAME");
                    logid[i]=singlelog.getString("ID");
                    missedCall[i]=singlelog.getString("IS_READ");

                }
                adapter = new CustomCallLogList(getActivity(), dateTime,
                        callFrom, recieverName,logid,missedCall);

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



            list = (ListView)view.findViewById(R.id.call_log_list);
            list.setAdapter(adapter);



        }

    }

}
