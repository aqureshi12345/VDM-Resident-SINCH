package com.vdm.virtualdoorman;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vdm.virtualdoorman.ArrivalsFragments.GuestDeliveryListContainerFragment;



public class ArrivalsActivity extends AppCompatActivity{

    private static final String TAG = ArrivalsActivity.class.getSimpleName();

    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    DrawerLayout drawer;
    MainScreen mainScreen;
    SharedPreferences loginpref;
    Intent logout;
    public static final String LOGINPREF = "LoginPrefs";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrivals);
        ImageView arrivals=(ImageView)findViewById(R.id.arrivals);
        arrivals.setBackgroundResource(R.drawable.arrivals_s);
        ImageView intercom=(ImageView)findViewById(R.id.intercom);
        intercom.setBackgroundResource(R.drawable.intercom);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new GuestDeliveryListContainerFragment();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
        fragmentTransaction.commit();

        intercom.setOnClickListener(new Button.OnClickListener() {

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
        ImageView logs=(ImageView)findViewById(R.id.logs);
        logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logs_intent=new Intent(getApplicationContext(), logs.class);
                startActivity(logs_intent);
            }
        });

        ImageView buildings=(ImageView)findViewById(R.id.buildings);
        buildings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buidlings_intent=new Intent(getApplicationContext(), MyBuildingContacts.class);
                startActivity(buidlings_intent);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.drawer_header);
        //TextView profileName = (TextView) header.findViewById(R.id.profile_name);
       // profileName.setText("Adele");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                switch (id) {
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
                        startActivity(new Intent(getApplicationContext(), ContactMyVdm.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.about:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(getApplicationContext(), AboutUs.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.logout:
                        final Context context=getApplicationContext();
                        //mainScreen=new MainScreen();

                        if(isNetworkAvailable(context)==true) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ArrivalsActivity.this);
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

                                        }})
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else{
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

                                            Intent reload = new Intent(ArrivalsActivity.this,
                                                    ArrivalsActivity.class);
                                            startActivity(reload);
                                            finish();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                        return true;

                    default:

                }

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container_wrapper, fragment);
                transaction.commit();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arrivals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_person) {
            Intent intent=new Intent(getApplicationContext(), AddPerson.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.add_deliveries) {
            Intent intent=new Intent(getApplicationContext(), AddBusiness.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
