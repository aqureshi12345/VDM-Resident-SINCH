package com.vdm.virtualdoorman;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Drawer imports
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MyBuildingContacts extends BaseActivity {
    ListView list;
    String Superintendent_number;
    //String Manager_number;
    //Drawer Items//
    Intent nav_intent;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String[] activityTitles;
    private DrawerLayout drawer;
    private View navHeader;
    MainScreen mainScreen;
    Intent logout;


    String email_manager;
    String email_report_problem;
    String email_suggestion;
    SharedPreferences loginpref;

    public static final String LOGINPREF = "LoginPrefs";
    String login_guid = null;

    String[] mainlist = { "Order Key Cards/Fob", "Unlock Doors","Call My Superintendent","Email My Building Manager", "Report a Problem", "Suggestions" };
    Integer[] imageId = { R.drawable.image6, R.drawable.doorunlock, R.drawable.phone_icon, R.drawable.mail_icon, R.drawable.problem_icon,R.drawable.suggestion_icon };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_building_contacts);

        ImageView arrivals = (ImageView) findViewById(R.id.arrivals);
        ImageView intercom = (ImageView) findViewById(R.id.intercom);
        ImageView buildings = (ImageView) findViewById(R.id.buildings);
        ImageView logs = (ImageView) findViewById(R.id.logs);

        //Drawer code
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Building Contacts");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        navigationView.bringToFront();
        setUpNavigationView(navigationView);
        loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
        ImageView imgButton_intercom = (ImageView) findViewById(R.id.intercom);
        imgButton_intercom.setOnClickListener(new Button.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                Intent Call_Option = new Intent(getApplicationContext(), Intercom.class);
                startActivity(Call_Option);
                finish();
                //imgButton_intercom.setBackground(getResources().getDrawable(R.drawable.speaker_selected));


            }
        });
        //imgButton_service = (ImageButton) findViewById(R.id.service);
        arrivals = (ImageView) findViewById(R.id.arrivals);
        arrivals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logs_intent = new Intent(getApplicationContext(), ArrivalsActivity.class);
                startActivity(logs_intent);
            }
        });

        logs = (ImageView) findViewById(R.id.logs);
        logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buidlings_intent = new Intent(getApplicationContext(), logs.class);
                startActivity(buidlings_intent);
            }
        });

        login_guid = (loginpref.getString("login_guid", ""));
        CustomList adapter = new CustomList(MyBuildingContacts.this, mainlist, imageId);
        list = (ListView) findViewById(R.id.contactlist);
        list.setAdapter(adapter);


        new getmycontacts().execute();
    }

    //Drawer Method
    private void setUpNavigationView(NavigationView navigationView) {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;

                    case R.id.account:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(getApplicationContext(), MyAccount.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.ringtone:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(getApplicationContext(), RingerMode.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.red_hat_concierge:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(getApplicationContext(), RedHat.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.contacts:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(getApplicationContext(), MyBuildingContacts.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.about:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(getApplicationContext(), TermsandCondition.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.logout:
                        final Context context = getApplicationContext();
                        if (isNetworkAvailable(context) == true) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MyBuildingContacts.this);
                            builder.setMessage("Are you sure you want to Logout?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if(BaseActivity.getSinchServiceInterface_new()!=null){
                                                BaseActivity.getSinchServiceInterface_new().stopClient();
                                            }
                                            logout = new Intent(context,
                                                    Login.class);
                                            logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            logout.putExtra("EXIT", "true");
                                            startActivity(logout);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    getApplicationContext());
                            TextView Msg = new TextView(getApplicationContext());
                            Msg.setText("Network is not available at the moment. Please wait");
                            Msg.setGravity(Gravity.CENTER_HORIZONTAL);
                            //	Msg.setMaxHeight(100);
                            Msg.setTextSize(18);
                            alertDialogBuilder.setView(Msg);
                            TextView title = new TextView(getApplicationContext());
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
                                            Intent mainscreen = new Intent(MyBuildingContacts.this, ArrivalsActivity.class);
                                            startActivity(mainscreen);
                                            finish();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                        return true;

                    default:

                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);


                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
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

    //Drawer method
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Drawer method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.arrivals, menu);
        return true;
    }

    //Drawer method
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    class getmycontacts extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        JSONObject jsonobj = new JSONObject();

        JSONParser jsonParser = new JSONParser();

        private final String url_get_my_contacts = "https://portal.virtualdoorman.com/dev/iphone/iphone_requests.php?function=get_my_contacts&login_guid="
                + login_guid;

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MyBuildingContacts.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            JSONObject json = jsonParser.makeHttpRequest(url_get_my_contacts,  "GET", jsonobj);
            try {

                JSONArray data_superintendent = json
                        .getJSONArray("super_intendent_phone");
                JSONObject superintend = data_superintendent.getJSONObject(0);
                Superintendent_number = superintend.getString("phone");

//				JSONArray data_manager = json
//						.getJSONArray("building_manager_phone");
//				JSONObject manager = data_manager.getJSONObject(0);
//				Manager_number = manager.getString("phone");

                JSONArray data_manager_email = json
                        .getJSONArray("building_manager_email");
                JSONObject manager_email = data_manager_email.getJSONObject(0);
                email_manager = manager_email.getString("email");

                JSONArray report_problem = json
                        .getJSONArray("report_problem_email");
                JSONObject report_problem_email = report_problem
                        .getJSONObject(0);
                email_report_problem = report_problem_email.getString("email");

                JSONArray suggestion = json.getJSONArray("suggestion_email");
                JSONObject emailSuggestion = suggestion.getJSONObject(0);
                email_suggestion = emailSuggestion.getString("email");

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
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    if (mainlist[+position] == "Order Key Cards/Fob") {
                        Intent callIntent = new Intent(getApplicationContext(), OrderKeyCards.class);
                        startActivity(callIntent);
                    }

                    if (mainlist[+position] == "Unlock Doors") {
                        Intent callIntent = new Intent(getApplicationContext(), DoorUnlock.class);
                        startActivity(callIntent);
                    }
                    if (mainlist[+position] == "Call My Superintendent") {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"
                                + Superintendent_number));
                        startActivity(callIntent);
                    }
//					if (mainlist[+position] == "Call My building Manager") {
//
//						Intent callIntent = new Intent(Intent.ACTION_CALL);
//						callIntent.setData(Uri.parse("tel:" + Manager_number));
//						startActivity(callIntent);
//					}

                    if (mainlist[+position] == "Email My Building Manager") {

                        String[] TO = { email_manager };

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.setType("text/plain");

                        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                                "Your subject");
                        emailIntent.putExtra(Intent.EXTRA_TEXT,
                                "Email message goes here");

                        try {
                            startActivity(Intent.createChooser(emailIntent,     "Send mail..."));
                            //finish();
                            Log.i("Finished sending email", "");
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MyBuildingContacts.this,
                                    "There is no email client installed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (mainlist[+position] == "Report a Problem") {

                        String[] TO = { email_report_problem };

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
                            // finish();
                            Log.i("Finished sending email", "");
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MyBuildingContacts.this,
                                    "There is no email client installed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (mainlist[+position] == "Suggestions") {

                        String[] TO = { email_suggestion };

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.setType("text/plain");

                        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                                "Your subject");
                        emailIntent.putExtra(Intent.EXTRA_TEXT,
                                "Email message goes here");

                        try {
                            startActivity(Intent.createChooser(emailIntent,   "Send mail..."));
                            // finish();
                            Log.i("Finished sending email", "");
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MyBuildingContacts.this,
                                    "There is no email client installed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }

    }
}
