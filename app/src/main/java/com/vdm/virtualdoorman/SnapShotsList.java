package com.vdm.virtualdoorman;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SnapShotsList extends AppCompatActivity {
    String urlString;
    private ProgressDialog pDialog;
    String[] images;
    String[] TimeDate;
    ArrayList<String> stringArrayList;
    ArrayList<String> stringArrayDateList;
    String uname;
    public static final String LOGINPREF = "LoginPrefs";
    SharedPreferences loginpref;
    String date1;
    String Person_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_shots_list);
        loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
        Person_id = (loginpref.getString("resident_id", ""));
        urlString = "https://portal.virtualdoorman.com/dev/common/libs/slim/snap_shots_list/"+Person_id;
        uname = (loginpref.getString("username", ""));
        new snapShot().execute();

    }

    private ArrayList<ListItem> getListData() {
        ArrayList<ListItem> listMockData = new ArrayList<ListItem>();

        images = stringArrayList.toArray(new String[stringArrayList.size()]);
        TimeDate = stringArrayDateList.toArray(new String[stringArrayDateList.size()]);
       // String[] headlines = getResources().getStringArray(R.array.headline_array);

        for (int i = 0; i < images.length; i++) {
            ListItem newsData = new ListItem();
            String imageUrl = images[i];
            date1 = TimeDate[i];
            newsData.setUrl(imageUrl);

            newsData.setHeadline("SnapShot Take");
            newsData.setReporterName(uname);
            newsData.setDate(date1);
            listMockData.add(newsData);
        }
        return listMockData;
    }


    class snapShot extends AsyncTask<Void, Void, Void> {
        String sResponse = null;

        protected void onPreExecute() {
            super.onPreExecute();
            showDilog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // String sResponse = null;
            try {
                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                connection.setDoOutput(false);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "");
                }
                sResponse = sb.toString();

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            JSONObject mainObject = null;
            JSONArray snapshotslist;
            stringArrayList = new ArrayList<String>();
            stringArrayDateList = new ArrayList<String>();
            try {
                snapshotslist = new JSONArray(sResponse);
                for (int i = 0; i < snapshotslist.length(); i++) {
                    mainObject = snapshotslist.getJSONObject(i);
                    JSONArray snapshotimg = mainObject.getJSONArray("SNAP_SHOTS_LIST");
                    for (int j = 0; j < snapshotimg.length(); j++) {

                        JSONObject  snapshotimgobj = snapshotimg.getJSONObject(j);
                        String img = snapshotimgobj.getString("URL");
                        String TimeDate = snapshotimgobj.getString("TIME_STAMP");
                        stringArrayList.add(img);
                        stringArrayDateList.add(TimeDate);
                    }
                    ArrayList<ListItem> listData = getListData();
                    final ListView listView = (ListView) findViewById(R.id.custom_list);
                    CustomListAdapter ad = new CustomListAdapter(SnapShotsList.this, listData);
                    if (!(ad.isEmpty())) {
                        listView.setAdapter(ad);
                    }
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                            ListItem newsData = (ListItem) listView.getItemAtPosition(position);
                            String Url = newsData.getUrl();
                             String Url2 = Url.replace("thumbnail", "large");
                            Intent terms = new Intent(getApplicationContext(), SingleSnapShot.class);
                            terms.putExtra("Url", Url2);
                            startActivity(terms);

                            //    Toast.makeText(SnapShotsList.this, "Selected :" + " " + newsData, Toast.LENGTH_LONG).show();
                        }
                    });


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            pDialog.dismiss();
        }


    }

    public void showDilog() {
        pDialog = new ProgressDialog(SnapShotsList.this);
        pDialog.setMessage("Loading ....");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

    }
}
