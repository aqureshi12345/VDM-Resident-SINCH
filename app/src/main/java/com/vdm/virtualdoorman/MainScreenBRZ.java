package com.vdm.virtualdoorman;

import com.vdm.virtualdoorman.R;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MainScreenBRZ extends Activity {
	ListView list;
	SharedPreferences loginpref;
	public static final String LOGINPREF = "LoginPrefs";
	String[] mainlist = { "Upload A Photo", "View Guest/Delivery Log", "My Account",
			"Order Key Cards/Fobs", "My Building Contacts",
			"Contact My Virtual Doorman" };
	Integer[] imageId = { 
			R.drawable.image3, R.drawable.image4, R.drawable.image5,
			R.drawable.image6, R.drawable.image7, R.drawable.image8
};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		CustomList adapter = new CustomList(MainScreenBRZ.this, mainlist, imageId);
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				  if (mainlist[+position] == "Upload A Photo") {
					Intent intent = new Intent(getApplicationContext(),
							GuestListUploadImg.class);
					startActivity(intent);
					
				} else if (mainlist[+position] == "My Account") {
					Intent myaccount = new Intent(getApplicationContext(),
							MyAccount.class);
					startActivity(myaccount);
				} else if (mainlist[+position] == "Order Key Cards/Fobs") {
					Intent ordercards = new Intent(getApplicationContext(),
							OrderKeyCards.class);
					startActivity(ordercards);
				}
				
				else if (mainlist[+position] == "My Building Contacts") {
					Intent contacts = new Intent(getApplicationContext(),
							MyBuildingContacts.class);
					startActivity(contacts);
				}
				else if (mainlist[+position] == "Contact My Virtual Doorman") {
					Intent intent = new Intent(getApplicationContext(),
							ContactMyVdm.class);
					startActivity(intent);
				}
				else if (mainlist[+position] == "View Guest/Delivery Log") {
					Intent intent = new Intent(getApplicationContext(),
							logs.class);
					startActivity(intent);
				}
				
				/*else
					Toast.makeText(MainScreen.this,
							"You Clicked another place", Toast.LENGTH_SHORT)
							.show();*/
			}
		});
		
		
		 Button btnlogout = (Button) findViewById(R.id.logout);
		 btnlogout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					final Intent serviceIntent = new Intent(getApplicationContext(), SinchService.class);
					stopService(serviceIntent);
					loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);
					loginpref.edit().remove("login_guid").commit();
					Intent logout = new Intent(getApplicationContext(),
							Login.class);
					logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					logout.putExtra("EXIT", true);
					startActivity(logout);
					finish();
				}
			});
	}
	
}
