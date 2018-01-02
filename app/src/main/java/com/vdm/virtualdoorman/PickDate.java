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
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PickDate extends Activity {
	public static final String ADDPERSONPREFERENCES = "AddPersonPrefs";
	SharedPreferences Addpersonpref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.date_picker);
		RelativeLayout backbutton=(RelativeLayout)findViewById(R.id.backbutton);
		backbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent= new Intent(getApplicationContext(), PermissionType.class);
				startActivity(intent);
				finish();
			}
		});
		final DatePicker datePickerFrom = (DatePicker) findViewById(R.id.datePickerfrom);
		final DatePicker datePickerTo = (DatePicker) findViewById(R.id.datePickerTo);
		
		TextView next = (TextView) findViewById(R.id.nextdate);
		next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Addpersonpref = getSharedPreferences(ADDPERSONPREFERENCES,
						Context.MODE_PRIVATE);
				Editor editor = Addpersonpref.edit();
				editor.putString("permission_type","date_period");
				editor.putString("start_date",datePickerFrom.getYear()+"/"+ (datePickerFrom.getMonth()+1)+"/"+datePickerFrom.getDayOfMonth());
				editor.putString("end_date",datePickerTo.getYear()+"/"+ (datePickerTo.getMonth()+1)+"/"+datePickerTo.getDayOfMonth());
				editor.commit();
				Intent time = new Intent(getApplicationContext(),
						TimeofDay.class);
				startActivity(time);
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
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
