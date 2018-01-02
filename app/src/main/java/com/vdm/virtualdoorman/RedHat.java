package com.vdm.virtualdoorman;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class RedHat extends Activity {
	private WebView redhat;
	SharedPreferences loginpref;

	public static final String LOGINPREF = "LoginPrefs";
	String login_guid = null;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.red_hat_services);
		RelativeLayout backbutton=(RelativeLayout)findViewById(R.id.backbutton);
		backbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ArrivalsActivity.class);
				startActivity(intent);
				finish();
			}
		});
		//Intent intent = getIntent();
		redhat = (WebView) findViewById(R.id.redHat);
		loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);

		login_guid = (loginpref.getString("login_guid", ""));
		redhat
				.loadUrl("https://portal.virtualdoorman.com/dev/iphone/services.php?login_guid="
						+ login_guid);
		redhat.getSettings().setLoadsImagesAutomatically(true);
		redhat.getSettings().setJavaScriptEnabled(true);
		redhat.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
