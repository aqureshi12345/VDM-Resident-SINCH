package com.vdm.virtualdoorman;

import com.vdm.virtualdoorman.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class PermissionType extends Activity {
	public static final String ADDPERSONPREFERENCES = "AddPersonPrefs";
	SharedPreferences Addpersonpref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.permission_type);



		Button btnAnyDay = (Button) findViewById(R.id.anyday);
		Button btnDatePeriod = (Button) findViewById(R.id.dateperiod);
		Button btnDaysofWeek = (Button) findViewById(R.id.daysofweek);
		RelativeLayout backbutton=(RelativeLayout)findViewById(R.id.backbutton);
		backbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), AddPerson.class);
				startActivity(intent);
			}
		});

		btnAnyDay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES,
						Context.MODE_PRIVATE);
				Editor editor = Addpersonpref.edit();
				editor.putString("permission_type","any_day");
				editor.commit();
				Intent time = new Intent(getApplicationContext(),TimeofDay.class);
				startActivity(time);
			}
		});

		btnDatePeriod.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent dateperiod = new Intent(getApplicationContext(),	PickDate.class);
				startActivity(dateperiod);
			}
		});
		btnDaysofWeek.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent dayofWeek = new Intent(getApplicationContext(), DaysofWeek.class);
				startActivity(dayofWeek);
			}
		});

	}

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
