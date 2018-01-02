package com.vdm.virtualdoorman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends BaseActivity{
    SharedPreferences loginpref;
    public static final String LOGINPREF = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    // code to hide notification bar
    	/* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    	            WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.add_guest_delivery);
        loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
        if(loginpref.contains("login_guid")){
//            if(loginpref.contains("IncomingCall")){
//                Intent IncominCall = new Intent(getApplicationContext(),
//                        IncomingCallScreenActivity.class);
//                startActivity(IncominCall);
//                finish();
//            } else {
                Intent arrivals = new Intent(getApplicationContext(),
                        MainScreen.class);
                startActivity(arrivals);
                finish();
         //   }
        } else {
            Intent Login = new Intent(getApplicationContext(),
                    Login.class);
            startActivity(Login);
            finish();
        }

    }
//    public void logout(final Context context) {
//        if (getSinchServiceInterface() != null) {
//        getSinchServiceInterface().stopClient();
//        }
//        loginpref = context.getSharedPreferences(LOGINPREF, context.MODE_PRIVATE);
//        loginpref.edit().remove("login_guid").commit();
//        Intent logout;
//        logout = new Intent(context,
//                Login.class);
//        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        logout.putExtra("EXIT", "true");
//        Toast.makeText(context,
//                "User Logout Successfully", Toast.LENGTH_SHORT).show();
//        context.startActivity(logout);
//        finish();
//
//    }
//    @Override
//    protected void onServiceConnected() {
//        if (!getSinchServiceInterface().isStarted()) {
//            loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
//            String Caller_id = (loginpref.getString("Caller_id", ""));
//            getSinchServiceInterface().startClient(Caller_id);
//            Log.d("service++", "connected++");
//        }
//
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
