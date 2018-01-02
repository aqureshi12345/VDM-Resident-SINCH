package com.vdm.virtualdoorman;

import com.vdm.virtualdoorman.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;


public class DeliveryLogDetail extends Activity {
	private WebView editguest;
	SharedPreferences loginpref;

	public static final String LOGINPREF = "LoginPrefs";
	String login_guid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delivery_log_detail);
		editguest = (WebView) findViewById(R.id.webview);
		loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
		Intent intent=getIntent();
		login_guid = (loginpref.getString("login_guid", ""));
		String abc="https://portal.virtualdoorman.com/dev/iphone/delivery_log_detail.php?log_id="+intent.getStringExtra("id")+"&login_guid="
		+ login_guid;
		editguest
				.loadUrl("https://portal.virtualdoorman.com/dev/iphone/delivery_log_detail.php?log_id="+intent.getStringExtra("id")+"&login_guid="
						+ login_guid);
		editguest.getSettings().setLoadsImagesAutomatically(true);
		editguest.getSettings().setJavaScriptEnabled(true);
		editguest.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
