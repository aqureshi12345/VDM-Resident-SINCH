package com.vdm.virtualdoorman.ArrivalsFragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vdm.virtualdoorman.EditGuest;
import com.vdm.virtualdoorman.Global;
import com.vdm.virtualdoorman.JSONParser;
import com.vdm.virtualdoorman.Login;
import com.vdm.virtualdoorman.ArrivalsActivity;
import com.vdm.virtualdoorman.R;
import com.vdm.virtualdoorman.ArrivalsAdapters.customDeliveryList;
import com.vdm.virtualdoorman.ViewGuest;

import android.support.v4.app.Fragment;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class deliveryList extends Fragment {
    ListView list;
    customDeliveryList adapter;

    Integer[] imageId;
    String[] deliverylist;
    String[] guestId;
    ArrayList<String> deliveries;

    Button[] del;
    Button[] delete;
    SharedPreferences loginpref;
    public static final String LOGINPREF = "LoginPrefs";
    String login_guid = null;
    View view;
    String guest_id;
    Activity context_del;
    public deliveryList(){}
    // ArrayList<String> guestlist = new ArrayList<String>();
    public void deletetList(String id, Activity context2) {
        // TODO Auto-generated method stub
        guest_id = id;
        context_del = context2;
        new deleteDelivery().execute();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_deliveries, container, false);

        getActivity().setTitle("Arrivals");
        loginpref = getActivity().getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);

        login_guid = (loginpref.getString("login_guid", ""));
        new getDeliveryList().execute();



        return view;

    }

	/*@Override
	public void onBackPressed() {
		Intent mainscreen = new Intent(getApplicationContext(),
				MainScreen.class);
		mainscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mainscreen.putExtra("EXIT", true);
		startActivity(mainscreen);
	}*/

    public class getDeliveryList extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        JSONObject json= new JSONObject();

        private final String url_get_guest_list = "https://portal.virtualdoorman.com/dev/iphone/iphone_requests.php?function=get_guests_list&login_guid="
                + login_guid;


        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Loading Guest List..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating guest
         * */
        protected String doInBackground(String... args) {
            JSONObject jsonobj = new JSONObject();
            JSONArray data= new JSONArray();
            JSONObject singleguest=new JSONObject();
            JSONParser jsonParser = new JSONParser();


            json = jsonParser.makeHttpRequest(url_get_guest_list,
                    "GET", jsonobj);
            try {

                data = json.getJSONArray("data");
                Log.e("response is hellois ", json.toString());
                int size = data.length();
                //guestlist = new String[size];
                deliveries=new ArrayList<String>();
                imageId = new Integer[size];
                guestId = new String[size];
                del = new Button[size];
                delete = new Button[size];


                for (int i = 0; i < data.length(); i++) {
                     singleguest = data.getJSONObject(i);

                    if (singleguest.getString("guest_type").equalsIgnoreCase(
                            "person")
                            || singleguest.getString("guest_type")
                            .equalsIgnoreCase("resident")) {

                        singleguest.remove(data.getJSONObject(i).toString());
                        //		guestlist[i]=singleguest.getString("first_name") + " "
                        //				+ singleguest.getString("last_name");
                    } else {
                        deliveries.add(singleguest.getString("first_name") +" " + singleguest.getString("last_name"));
                        imageId[i] = R.drawable.building_icon;
                        guestId[i] = singleguest.getString("id");


                        //		deliveries[i]=singleguest.getString("first_name") + " "
                        //				+ singleguest.getString("last_name");
                    }

                }
                deliverylist=deliveries.toArray(new String[deliveries.size()]);
                adapter = new customDeliveryList(getActivity(), deliverylist,
                        imageId, guestId);

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            // check log cat for response
            //Log.e("Create Response", json.toString());

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
                //Log.e("Guest List", json.toString());
                response = new JSONObject().getString("response");

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
                                    loginpref = new ContextWrapper(getActivity()).getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
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
            list = (ListView)view.findViewById(R.id.delivery_list);
            list.setAdapter(adapter);

//            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view,
//                                        int position, long id) {
//                    Intent intent = new Intent(getActivity(), ViewGuest.class);
//                    intent.putExtra("id", guestId[position]);
//                    startActivity(intent);
//                    //finish();
//                }
//            });

        }

    }

    class deleteDelivery extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;

        JSONObject json;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context_del);
            pDialog.setMessage("Deleting Guest..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating guest
         * */
        protected String doInBackground(String... args) {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonobj = new JSONObject();

            Global LoginGuid;
            LoginGuid = new Global();
            // String login = LoginGuid.g;
            // String login = LoginGuid.loginID;
            String login = LoginGuid.getLoginDetail();
            final String url_del_guest = "https://portal.virtualdoorman.com/dev/iphone/iphone_requests.php?function=delete_visitor&login_guid="
                    + login;
            try {
                jsonobj.put("rid", guest_id);

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            json = jsonParser.makeHttpRequest(url_del_guest, "GET", jsonobj);

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
        protected void onPostExecute(String s) {

            pDialog.dismiss();
            String response = null;
            try {
                response = json.getString("response");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (response.equalsIgnoreCase("Gueset deleted successfully")) {
                Intent intent = new Intent(context_del, ArrivalsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                context_del.startActivity(intent);
                Toast.makeText(context_del, response, Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(context_del, ArrivalsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                context_del.startActivity(intent);
                Toast.makeText(context_del, "Some Error Occured",
                        Toast.LENGTH_SHORT).show();

            }

        }

    }
}
