package com.vdm.virtualdoorman;

import com.vdm.virtualdoorman.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;

public class OrderKeyCards extends Activity {
	private WebView editguest;
	SharedPreferences loginpref;

	public static final String LOGINPREF = "LoginPrefs";
	String login_guid = null;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_keycards);
		editguest = (WebView) findViewById(R.id.webview);
		loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);

		login_guid = (loginpref.getString("login_guid", ""));
		editguest
				.loadUrl("https://portal.virtualdoorman.com/dev/iphone/order_access_card.php?login_guid="
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
